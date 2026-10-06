package cz.czmendelu.studymate.ui.screens.createtest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.czmendelu.studymate.database.repository.IQuestionRepository
import cz.czmendelu.studymate.database.repository.ITestRepository
import cz.czmendelu.studymate.database.tables.Question
import cz.czmendelu.studymate.database.tables.Test
import cz.czmendelu.studymate.database.tables.TestQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Kontroluje, jestli má předmět dost vhodných otázek pro vybraný typ testu,
 * náhodně je vybere a uloží vazbu mezi testem a otázkami.
 */
@HiltViewModel
class CreateTestViewModel @Inject constructor(
    private val testRepository: ITestRepository,
    private val questionRepository: IQuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTestUIState())
    val uiState: StateFlow<CreateTestUIState> = _uiState.asStateFlow()

    private val subjectId: Long =
        savedStateHandle.get<Long>("subjectId") ?: -1L

    val testTypes = listOf(
        "FLASHCARDS",
        "YES_NO",
        "IMAGE_RECOGNITION",
        "CHOOSE_CORRECT_IMAGE"
    )

    fun onTestNameChanged(value: String) {
        _uiState.update {
            it.copy(
                testName = value,
                testNameError = null
            )
        }
    }

    fun onTestTypeChanged(value: String) {
        _uiState.update {
            it.copy(
                testType = value,
                questionCountError = null
            )
        }
    }

    fun onQuestionCountChanged(value: String) {
        // Do počtu otázek se povolují jen čísla
        if (value.all { it.isDigit() }) {
            _uiState.update {
                it.copy(
                    questionCount = value,
                    questionCountError = null
                )
            }
        }
    }

    fun onTimedChanged(value: Boolean) {
        _uiState.update {
            it.copy(
                isTimed = value,
                timeLimitError = null
            )
        }
    }

    fun onTimeLimitChanged(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update {
                it.copy(
                    timeLimitSeconds = value,
                    timeLimitError = null
                )
            }
        }
    }

    fun createTest() {
        val state = _uiState.value

        val questionCount = state.questionCount.toIntOrNull()
        val timeLimit = state.timeLimitSeconds.toIntOrNull()

        val testNameError =
            if (state.testName.isBlank()) {
                "Enter test name"
            } else {
                null
            }

        val questionCountError =
            if (questionCount == null || questionCount <= 0) {
                "Enter valid number of questions"
            } else {
                null
            }

        val timeLimitError =
            if (state.isTimed && (timeLimit == null || timeLimit <= 0)) {
                "Enter valid time limit"
            } else {
                null
            }

        if (
            testNameError != null ||
            questionCountError != null ||
            timeLimitError != null ||
            subjectId == -1L
        ) {
            _uiState.update {
                it.copy(
                    testNameError = testNameError,
                    questionCountError = questionCountError,
                    timeLimitError = timeLimitError
                )
            }
            return
        }

        viewModelScope.launch {
            val allQuestions =
                questionRepository.getQuestionsBySubject(subjectId).first()

            if (allQuestions.isEmpty()) {
                _uiState.update {
                    it.copy(
                        questionCountError = "This subject has no questions"
                    )
                }
                return@launch
            }

            val availableQuestions = getAvailableQuestionsForTestType(
                questions = allQuestions,
                testType = state.testType
            )

            // Testy s obrázky mohou používat jen otázky, které obrázek mají.
            if (availableQuestions.isEmpty()) {
                _uiState.update {
                    it.copy(
                        questionCountError = getEmptyQuestionsMessage(state.testType)
                    )
                }
                return@launch
            }

            if (state.testType == "CHOOSE_CORRECT_IMAGE" && availableQuestions.size < 2) {
                _uiState.update {
                    it.copy(
                        questionCountError = "Choose correct image test needs at least 2 questions with images"
                    )
                }
                return@launch
            }

            if (availableQuestions.size < questionCount!!) {
                _uiState.update {
                    it.copy(
                        questionCountError = "Only ${availableQuestions.size} suitable questions available"
                    )
                }
                return@launch
            }

            val selectedQuestions = availableQuestions
                .shuffled()
                .take(questionCount)

            //uloží test, aby vzniklo  ID pro vazební tabulku.
            val testId = testRepository.insertTest(
                Test(
                    subjectId = subjectId,
                    name = state.testName.trim(),
                    testType = state.testType,
                    questionCount = selectedQuestions.size,
                    isTimed = state.isTimed,
                    timeLimitSeconds = if (state.isTimed) {
                        timeLimit
                    } else {
                        null
                    }
                )
            )

            val testQuestions = selectedQuestions.mapIndexed { index, question ->
                TestQuestion(
                    testId = testId,
                    questionId = question.id ?: 0L,
                    position = index
                )
            }

            testRepository.insertTestQuestions(testQuestions)

            _uiState.update {
                it.copy(saved = true)
            }
        }
    }

    private fun getAvailableQuestionsForTestType(
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

    private fun getEmptyQuestionsMessage(testType: String): String {
        return when (testType) {
            "IMAGE_RECOGNITION" -> {
                "Image recognition test needs questions with images"
            }

            "CHOOSE_CORRECT_IMAGE" -> {
                "Choose correct image test needs questions with images"
            }

            else -> {
                "This subject has no suitable questions"
            }
        }
    }
}
