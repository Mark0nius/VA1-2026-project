package cz.czmendelu.studymate.navigation

sealed class ScreenDestination(val route: String) {

    data object Splash : ScreenDestination("splash")

    data object Subjects : ScreenDestination("subjects")

    data object Settings : ScreenDestination("settings")

    data object SubjectDetail : ScreenDestination("subjectDetail/{subjectId}") {
        fun createRoute(subjectId: Long): String {
            return "subjectDetail/$subjectId"
        }
    }

    data object AddQuestion : ScreenDestination("addQuestion/{subjectId}?questionId={questionId}") {
        fun createRoute(subjectId: Long): String {
            return "addQuestion/$subjectId?questionId=-1"
        }

        fun createEditRoute(subjectId: Long, questionId: Long): String {
            return "addQuestion/$subjectId?questionId=$questionId"
        }
    }

    data object CreateTest : ScreenDestination("createTest/{subjectId}") {
        fun createRoute(subjectId: Long): String {
            return "createTest/$subjectId"
        }
    }

    data object TestScreen : ScreenDestination("testScreen/{testId}") {
        fun createRoute(testId: Long): String {
            return "testScreen/$testId"
        }
    }

    data object TestResult : ScreenDestination("testResult/{resultId}") {
        fun createRoute(resultId: Long): String {
            return "testResult/$resultId"
        }
    }
}