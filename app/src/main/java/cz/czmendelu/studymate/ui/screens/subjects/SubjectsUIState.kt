package cz.czmendelu.studymate.ui.screens.subjects

import cz.czmendelu.studymate.database.tables.Subject

data class SubjectsUIState(
    val subjects: List<Subject> = emptyList(),

    val newSubjectName: String = "",
    val isAddDialogVisible: Boolean = false,

    val editedSubject: Subject? = null,
    val editedSubjectName: String = "",
    val isEditDialogVisible: Boolean = false
)