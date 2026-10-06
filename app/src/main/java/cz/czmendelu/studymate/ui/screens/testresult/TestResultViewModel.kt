package cz.czmendelu.studymate.ui.screens.testresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.czmendelu.studymate.database.repository.IQuestionRepository
import cz.czmendelu.studymate.database.repository.ITestResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestResultViewModel @Inject constructor(
    private val testResultRepository: ITestResultRepository,
    private val questionRepository: IQuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestResultUIState())
    val uiState: StateFlow<TestResultUIState> = _uiState.asStateFlow()

    private val resultId: Long =
        savedStateHandle.get<Long>("resultId") ?: -1L

    init {
        loadResult()
    }

    private fun loadResult() {
        if (resultId == -1L) {
            return
        }

        viewModelScope.launch {
            val result = testResultRepository.getTestResultById(resultId)

            result?.let { testResult ->
                val answers = testResultRepository.getAnswersByResult(resultId)

                val answerDetails = answers.map { answer ->
                    val question = questionRepository.getQuestionById(answer.questionId)

                    TestAnswerDetail(
                        questionText = question?.questionText.orEmpty(),
                        userAnswer = answer.userAnswer,
                        correctAnswer = answer.correctAnswer,
                        isCorrect = answer.isCorrect
                    )
                }

                _uiState.update {
                    it.copy(
                        testId = testResult.testId,
                        subjectId = testResult.subjectId,
                        scorePercent = testResult.scorePercent,
                        correctCount = testResult.correctCount,
                        wrongCount = testResult.wrongCount,
                        totalQuestions = testResult.totalQuestions,
                        answers = answerDetails
                    )
                }
            }
        }
    }
}