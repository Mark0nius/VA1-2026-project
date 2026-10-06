package cz.czmendelu.studymate.ui.screens.test

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.czmendelu.studymate.database.repository.IQuestionRepository
import cz.czmendelu.studymate.database.repository.ITestRepository
import cz.czmendelu.studymate.database.repository.ITestResultRepository
import cz.czmendelu.studymate.database.tables.Question
import cz.czmendelu.studymate.database.tables.TestAnswer
import cz.czmendelu.studymate.database.tables.TestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val testRepository: ITestRepository,
    private val questionRepository: IQuestionRepository,
    private val testResultRepository: ITestResultRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestUIState())
    val uiState: StateFlow<TestUIState> = _uiState.asStateFlow()

    private val testId: Long =
        savedStateHandle.get<Long>("testId") ?: -1L

    private var allSubjectQuestions: List<Question> = emptyList()

    private val savedAnswers = mutableMapOf<Long, SavedTestAnswer>()

    private var timerJob: Job? = null

    init {
        loadTest()
    }

    private fun loadTest() {
        if (testId == -1L) {
            return
        }

        viewModelScope.launch {
            val test = testRepository.getTestById(testId)

            if (test == null) {
                return@launch
            }

            val testQuestions = testRepository.getTestQuestions(testId)

            allSubjectQuestions =
                questionRepository.getQuestionsBySubject(test.subjectId).first()

            val orderedQuestions = testQuestions
                .sortedBy {
                    it.position
                }
                .mapNotNull { testQuestion ->
                    allSubjectQuestions.firstOrNull {
                        it.id == testQuestion.questionId
                    }
                }

            val filteredQuestions = getQuestionsForTestType(
                questions = orderedQuestions,
                testType = test.testType
            )

            _uiState.update {
                it.copy(
                    test = test,
                    questions = filteredQuestions,
                    currentQuestionIndex = 0,
                    showAnswer = false,
                    userTextAnswer = "",
                    selectedYesNoAnswer = null,
                    selectedImageAnswer = null,
                    remainingSeconds = if (test.isTimed) {
                        test.timeLimitSeconds
                    } else {
                        null
                    },
                    finished = false,
                    resultId = null
                )
            }

            if (test.isTimed) {
                startTimer()
            }
        }
    }

    private fun getQuestionsForTestType(
        questions: List<Question>,
        testType: String
    ): List<Question> {
        return when (testType) {
            "IMAGE_RECOGNITION",
            "CHOOSE_CORRECT_IMAGE" -> {
                questions.filter {
                    it.imagePath != null
                }
            }

            else -> {
                questions
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)

                val remainingSeconds = _uiState.value.remainingSeconds

                if (remainingSeconds == null) {
                    return@launch
                }

                if (remainingSeconds <= 1) {
                    _uiState.update {
                        it.copy(remainingSeconds = 0)
                    }

                    finishTest()
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        remainingSeconds = remainingSeconds - 1
                    )
                }
            }
        }
    }

    fun toggleAnswer() {
        _uiState.update {
            it.copy(showAnswer = !it.showAnswer)
        }
    }

    fun onUserTextAnswerChanged(value: String) {
        _uiState.update {
            it.copy(userTextAnswer = value)
        }
    }

    fun onYesNoAnswerSelected(value: Boolean) {
        _uiState.update {
            it.copy(selectedYesNoAnswer = value)
        }
    }

    fun onImageAnswerSelected(value: String) {
        _uiState.update {
            it.copy(selectedImageAnswer = value)
        }
    }

    fun getImageOptions(): List<Question> {
        val currentQuestion = _uiState.value.currentQuestion ?: return emptyList()

        // Správný obrázek se doplní o několik náhodných ze stejného předmětu.
        val otherQuestionsWithImages = allSubjectQuestions
            .filter {
                it.id != currentQuestion.id && it.imagePath != null
            }
            .shuffled()
            .take(3)

        val options = mutableListOf<Question>()

        options.add(currentQuestion)
        options.addAll(otherQuestionsWithImages)

        return options
            .distinctBy {
                it.imagePath
            }
            .filter {
                it.imagePath != null
            }
            .shuffled()
    }

    fun previousQuestion() {
        val state = _uiState.value

        if (state.currentQuestionIndex <= 0) {
            return
        }

        saveCurrentAnswer()

        _uiState.update {
            it.copy(
                currentQuestionIndex = it.currentQuestionIndex - 1,
                showAnswer = false,
                userTextAnswer = "",
                selectedYesNoAnswer = null,
                selectedImageAnswer = null
            )
        }

        restoreAnswerForCurrentQuestion()
    }

    fun nextQuestion() {
        val state = _uiState.value

        if (state.questions.isEmpty()) {
            return
        }

        saveCurrentAnswer()

        if (state.currentQuestionIndex >= state.questions.lastIndex) {
            finishTest()
            return
        }

        _uiState.update {
            it.copy(
                currentQuestionIndex = it.currentQuestionIndex + 1,
                showAnswer = false,
                userTextAnswer = "",
                selectedYesNoAnswer = null,
                selectedImageAnswer = null
            )
        }

        restoreAnswerForCurrentQuestion()
    }

    private fun saveCurrentAnswer() {
        val state = _uiState.value
        val test = state.test ?: return
        val question = state.currentQuestion ?: return
        val questionId = question.id ?: return

        // Odpověď se ukládá podle ID otázky, aby šla obnovit
        val userAnswer = getCurrentUserAnswer(
            testType = test.testType,
            state = state
        )

        val isCorrect = isCurrentAnswerCorrect(
            testType = test.testType,
            question = question,
            selectedYesNoAnswer = state.selectedYesNoAnswer,
            selectedImageAnswer = state.selectedImageAnswer,
            userAnswer = userAnswer
        )

        savedAnswers[questionId] = SavedTestAnswer(
            questionId = questionId,
            userAnswer = userAnswer,
            correctAnswer = getCorrectAnswerForQuestion(
                testType = test.testType,
                question = question
            ),
            isCorrect = isCorrect
        )
    }

    private fun restoreAnswerForCurrentQuestion() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val questionId = question.id ?: return
        val savedAnswer = savedAnswers[questionId] ?: return

        when (state.test?.testType) {
            "IMAGE_RECOGNITION" -> {
                _uiState.update {
                    it.copy(userTextAnswer = savedAnswer.userAnswer)
                }
            }

            "CHOOSE_CORRECT_IMAGE" -> {
                _uiState.update {
                    it.copy(selectedImageAnswer = savedAnswer.userAnswer)
                }
            }

            "YES_NO" -> {
                _uiState.update {
                    it.copy(selectedYesNoAnswer = savedAnswer.userAnswer == "true")
                }
            }
        }
    }

    private fun getCurrentUserAnswer(
        testType: String,
        state: TestUIState
    ): String {
        return when (testType) {
            "IMAGE_RECOGNITION" -> {
                state.userTextAnswer.trim()
            }

            "CHOOSE_CORRECT_IMAGE" -> {
                state.selectedImageAnswer.orEmpty()
            }

            "YES_NO" -> {
                state.selectedYesNoAnswer?.toString().orEmpty()
            }

            else -> {
                "reviewed"
            }
        }
    }

    private fun getCorrectAnswerForQuestion(
        testType: String,
        question: Question
    ): String {
        return when (testType) {
            "CHOOSE_CORRECT_IMAGE" -> {
                question.imagePath.orEmpty()
            }

            "YES_NO" -> {
                question.correctAnswer?.toString().orEmpty()
            }

            else -> {
                question.answerText
            }
        }
    }

    private fun isCurrentAnswerCorrect(
        testType: String,
        question: Question,
        selectedYesNoAnswer: Boolean?,
        selectedImageAnswer: String?,
        userAnswer: String
    ): Boolean {
        return when (testType) {
            "IMAGE_RECOGNITION" -> {
                userAnswer.equals(
                    question.answerText.trim(),
                    ignoreCase = true
                )
            }

            "CHOOSE_CORRECT_IMAGE" -> {
                selectedImageAnswer == question.imagePath
            }

            "YES_NO" -> {
                selectedYesNoAnswer == question.correctAnswer
            }

            else -> {
                true
            }
        }
    }

    fun finishTest() {
        timerJob?.cancel()

        saveCurrentAnswer()

        val state = _uiState.value
        val test = state.test ?: return

        viewModelScope.launch {
            val totalQuestions = state.questions.size

            val correctCount = savedAnswers.values.count {
                it.isCorrect
            }

            val wrongCount = totalQuestions - correctCount

            val scorePercent = if (totalQuestions == 0) {
                0.0
            } else {
                correctCount.toDouble() / totalQuestions.toDouble() * 100.0
            }

            val resultId = testResultRepository.insertTestResult(
                TestResult(
                    testId = test.id ?: testId,
                    subjectId = test.subjectId,
                    scorePercent = scorePercent,
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    totalQuestions = totalQuestions
                )
            )

            val testAnswers = savedAnswers.values.map { savedAnswer ->
                TestAnswer(
                    resultId = resultId,
                    questionId = savedAnswer.questionId,
                    userAnswer = savedAnswer.userAnswer,
                    correctAnswer = savedAnswer.correctAnswer,
                    isCorrect = savedAnswer.isCorrect
                )
            }

            testResultRepository.insertTestAnswers(testAnswers)

            _uiState.update {
                it.copy(
                    finished = true,
                    resultId = resultId
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private data class SavedTestAnswer(
        val questionId: Long,
        val userAnswer: String,
        val correctAnswer: String,
        val isCorrect: Boolean
    )
}
