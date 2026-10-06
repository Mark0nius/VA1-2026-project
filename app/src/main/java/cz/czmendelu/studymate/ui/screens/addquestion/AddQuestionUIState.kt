package cz.czmendelu.studymate.ui.screens.addquestion

data class AddQuestionUIState(
    val questionText: String = "",
    val answerText: String = "",
    val questionType: String = "FLASHCARD",
    val imagePath: String? = null,
    val correctAnswer: Boolean = true,

    val questionTextError: String? = null,
    val answerTextError: String? = null,

    val isEditMode: Boolean = false,
    val saved: Boolean = false
)