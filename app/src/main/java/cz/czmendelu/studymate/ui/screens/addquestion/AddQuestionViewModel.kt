package cz.czmendelu.studymate.ui.screens.addquestion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.czmendelu.studymate.database.repository.IQuestionRepository
import cz.czmendelu.studymate.database.tables.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Podle předaného questionId rozlišuje, jestli se zakládá nová otázka,
 * nebo se načítá existující otázka k editaci.
 */
@HiltViewModel
class AddQuestionViewModel @Inject constructor(
    private val questionRepository: IQuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddQuestionUIState())
    val uiState: StateFlow<AddQuestionUIState> = _uiState.asStateFlow()

    private val subjectId: Long =
        savedStateHandle.get<Long>("subjectId") ?: -1L

    private val questionId: Long =
        savedStateHandle.get<Long>("questionId") ?: -1L

    val questionTypes = listOf(
        "FLASHCARD",
        "YES_NO",
        "IMAGE_RECOGNITION",
        "CHOOSE_CORRECT_IMAGE"
    )

    init {
        loadQuestionForEdit()
    }

    private fun loadQuestionForEdit() {
        if (questionId == -1L) {
            // -1 je výchozí hodnota pro režim nové otázky.
            return
        }

        viewModelScope.launch {
            val question = questionRepository.getQuestionById(questionId)

            question?.let { loadedQuestion ->
                _uiState.update {
                    it.copy(
                        questionText = loadedQuestion.questionText,
                        answerText = loadedQuestion.answerText,
                        questionType = loadedQuestion.questionType,
                        imagePath = loadedQuestion.imagePath,
                        correctAnswer = loadedQuestion.correctAnswer ?: true,
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun onQuestionTextChanged(value: String) {
        _uiState.update {
            it.copy(
                questionText = value,
                questionTextError = null
            )
        }
    }

    fun onAnswerTextChanged(value: String) {
        _uiState.update {
            it.copy(
                answerText = value,
                answerTextError = null
            )
        }
    }

    fun onQuestionTypeChanged(value: String) {
        _uiState.update {
            it.copy(questionType = value)
        }
    }

    fun onCorrectAnswerChanged(value: Boolean) {
        _uiState.update {
            it.copy(correctAnswer = value)
        }
    }

    fun onImageSelected(imagePath: String) {
        _uiState.update {
            it.copy(imagePath = imagePath)
        }
    }

    fun removeImage() {
        _uiState.update {
            it.copy(imagePath = null)
        }
    }

    fun saveQuestion() {
        val state = _uiState.value

        val questionTextError =
            if (state.questionText.isBlank()) {
                "Enter question"
            } else {
                null
            }

        val answerTextError =
            if (state.answerText.isBlank()) {
                "Enter answer"
            } else {
                null
            }

        if (
            questionTextError != null ||
            answerTextError != null ||
            subjectId == -1L
        ) {
            _uiState.update {
                it.copy(
                    questionTextError = questionTextError,
                    answerTextError = answerTextError
                )
            }
            return
        }

        viewModelScope.launch {
            val question = Question(
                id = if (state.isEditMode) questionId else null,
                subjectId = subjectId,
                questionText = state.questionText.trim(),
                answerText = state.answerText.trim(),
                questionType = state.questionType,
                imagePath = state.imagePath,
                // používá jen u otázek ano/ne.
                correctAnswer = if (state.questionType == "YES_NO") {
                    state.correctAnswer
                } else {
                    null
                }
            )

            if (state.isEditMode && questionId != -1L) {
                questionRepository.updateQuestion(question)
            } else {
                questionRepository.insertQuestion(question)
            }

            _uiState.update {
                it.copy(saved = true)
            }
        }
    }
}
