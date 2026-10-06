package cz.czmendelu.studymate.ui.screens.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.czmendelu.studymate.database.repository.ISubjectRepository
import cz.czmendelu.studymate.database.tables.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val subjectRepository: ISubjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectsUIState())
    val uiState: StateFlow<SubjectsUIState> = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            subjectRepository.getAllSubjects().collect { subjects ->
                _uiState.update {
                    it.copy(subjects = subjects)
                }
            }
        }
    }

    fun onNewSubjectNameChanged(value: String) {
        _uiState.update {
            it.copy(newSubjectName = value)
        }
    }

    fun showAddDialog() {
        _uiState.update {
            it.copy(isAddDialogVisible = true)
        }
    }

    fun hideAddDialog() {
        _uiState.update {
            it.copy(
                isAddDialogVisible = false,
                newSubjectName = ""
            )
        }
    }

    fun addSubject() {
        val name = _uiState.value.newSubjectName.trim()


        if (name.isEmpty()) {
            return
        }

        viewModelScope.launch {
            subjectRepository.insertSubject(
                Subject(
                    name = name
                )
            )

            hideAddDialog()
        }
    }

    fun showEditDialog(subject: Subject) {
        _uiState.update {
            it.copy(
                editedSubject = subject,
                editedSubjectName = subject.name,
                isEditDialogVisible = true
            )
        }
    }

    fun onEditedSubjectNameChanged(value: String) {
        _uiState.update {
            it.copy(editedSubjectName = value)
        }
    }

    fun hideEditDialog() {
        _uiState.update {
            it.copy(
                editedSubject = null,
                editedSubjectName = "",
                isEditDialogVisible = false
            )
        }
    }

    fun updateSubject() {
        val subject = _uiState.value.editedSubject ?: return
        val newName = _uiState.value.editedSubjectName.trim()

        if (newName.isEmpty()) {
            return
        }

        viewModelScope.launch {
            subjectRepository.updateSubject(
                subject.copy(
                    name = newName
                )
            )

            hideEditDialog()
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(subject)
        }
    }
}
