package cz.czmendelu.studymate.ui.screens.subjectdetail

import cz.czmendelu.studymate.database.tables.Question
import cz.czmendelu.studymate.database.tables.Subject
import cz.czmendelu.studymate.database.tables.Test
import cz.czmendelu.studymate.database.tables.TestResult

data class SubjectDetailUIState(
    val subject: Subject? = null,
    val questions: List<Question> = emptyList(),
    val tests: List<Test> = emptyList(),
    val results: List<TestResult> = emptyList(),

    val selectedTab: SubjectDetailTab = SubjectDetailTab.TESTS,

    val showAiImportDialog: Boolean = false,
    val aiSourceText: String = "",
    val isImportingQuestions: Boolean = false,
    val aiImportError: String? = null
)