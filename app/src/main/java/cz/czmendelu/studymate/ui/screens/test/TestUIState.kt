package cz.czmendelu.studymate.ui.screens.test

import cz.czmendelu.studymate.database.tables.Question
import cz.czmendelu.studymate.database.tables.Test

data class TestUIState(
    val test: Test? = null,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,

    val showAnswer: Boolean = false,

    val userTextAnswer: String = "",
    val selectedYesNoAnswer: Boolean? = null,
    val selectedImageAnswer: String? = null,

    val remainingSeconds: Int? = null,

    val finished: Boolean = false,
    val resultId: Long? = null
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = questions.isNotEmpty() && currentQuestionIndex == questions.lastIndex

    val progress: Float
        get() = if (questions.isEmpty()) {
            0f
        } else {
            (currentQuestionIndex + 1).toFloat() / questions.size.toFloat()
        }

    val timerText: String?
        get() = remainingSeconds?.let { seconds ->
            val minutes = seconds / 60
            val restSeconds = seconds % 60
            "%02d:%02d".format(minutes, restSeconds)
        }
}