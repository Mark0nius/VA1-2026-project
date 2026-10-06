package cz.czmendelu.studymate.ui.screens.createtest

data class CreateTestUIState(
    val testName: String = "",
    val testType: String = "FLASHCARDS",
    val questionCount: String = "10",
    val isTimed: Boolean = false,
    val timeLimitSeconds: String = "",

    val testNameError: String? = null,
    val questionCountError: String? = null,
    val timeLimitError: String? = null,

    val saved: Boolean = false
)