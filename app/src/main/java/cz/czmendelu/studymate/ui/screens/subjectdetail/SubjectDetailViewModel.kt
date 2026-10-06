package cz.czmendelu.studymate.ui.screens.subjectdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.czmendelu.studymate.ai.GeminiQuestionImportService
import cz.czmendelu.studymate.database.repository.IQuestionRepository
import cz.czmendelu.studymate.database.repository.ISubjectRepository
import cz.czmendelu.studymate.database.repository.ITestRepository
import cz.czmendelu.studymate.database.repository.ITestResultRepository
import cz.czmendelu.studymate.database.tables.Question
import cz.czmendelu.studymate.database.tables.Test
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubjectDetailViewModel @Inject constructor(
    private val subjectRepository: ISubjectRepository,
    private val questionRepository: IQuestionRepository,
    private val testRepository: ITestRepository,
    private val testResultRepository: ITestResultRepository,
    private val geminiQuestionImportService: GeminiQuestionImportService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectDetailUIState())
    val uiState: StateFlow<SubjectDetailUIState> = _uiState.asStateFlow()

    private val subjectId: Long =
        savedStateHandle.get<Long>("subjectId") ?: -1L

    init {
        if (subjectId != -1L) {
            loadSubject()
            loadQuestions()
            loadTests()
            loadResults()
        }
    }

    private fun loadSubject() {
        viewModelScope.launch {
            val subject = subjectRepository.getSubjectById(subjectId)

            _uiState.update {
                it.copy(
                    subject = subject
                )
            }
        }
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            questionRepository.getQuestionsBySubject(subjectId).collect { questions ->
                _uiState.update {
                    it.copy(
                        questions = questions
                    )
                }
            }
        }
    }

    private fun loadTests() {
        viewModelScope.launch {
            testRepository.getTestsBySubject(subjectId).collect { tests ->
                _uiState.update {
                    it.copy(
                        tests = tests
                    )
                }
            }
        }
    }

    private fun loadResults() {
        viewModelScope.launch {
            testResultRepository.getResultsBySubject(subjectId).collect { results ->
                _uiState.update {
                    it.copy(
                        results = results
                    )
                }
            }
        }
    }

    fun selectTab(tab: SubjectDetailTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab
            )
        }
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch {
            questionRepository.deleteQuestion(question)
        }
    }

    fun deleteTest(test: Test) {
        viewModelScope.launch {
            testRepository.deleteTest(test)
        }
    }

    fun showAiImportDialog() {
        _uiState.update {
            it.copy(
                showAiImportDialog = true,
                aiImportError = null
            )
        }
    }

    fun hideAiImportDialog() {
        if (_uiState.value.isImportingQuestions) {
            return
        }

        _uiState.update {
            it.copy(
                showAiImportDialog = false,
                aiSourceText = "",
                aiImportError = null
            )
        }
    }

    fun onAiSourceTextChanged(value: String) {
        _uiState.update {
            it.copy(
                aiSourceText = value,
                aiImportError = null
            )
        }
    }

    fun importQuestionsFromAi() {
        val sourceText = _uiState.value.aiSourceText.trim()
        if (sourceText.isBlank()) {
            _uiState.update {
                it.copy(
                    aiImportError = "Text for import is empty."
                )
            }

            return
        }

        if (subjectId == -1L) {
            _uiState.update {
                it.copy(
                    aiImportError = "Subject was not found."
                )
            }

            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isImportingQuestions = true,
                    aiImportError = null
                )
            }

            try {
                val generatedQuestions = geminiQuestionImportService.generateQuestions(
                    subjectId = subjectId,
                    sourceText = sourceText
                )

                generatedQuestions.forEach { question ->
                    questionRepository.insertQuestion(question)
                }

                _uiState.update {
                    it.copy(
                        showAiImportDialog = false,
                        aiSourceText = "",
                        isImportingQuestions = false,
                        aiImportError = null,
                        selectedTab = SubjectDetailTab.QUESTIONS
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isImportingQuestions = false,
                        aiImportError = exception.message ?: "AI import failed."
                    )
                }
            }
        }
    }
}
