package cz.czmendelu.studymate.ui.screens.testresult

data class TestResultUIState(
    val testId: Long? = null,
    val subjectId: Long? = null,
    val scorePercent: Double = 0.0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val totalQuestions: Int = 0,
    val answers: List<TestAnswerDetail> = emptyList()
)

data class TestAnswerDetail(
    val questionText: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
)