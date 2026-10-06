package cz.czmendelu.studymate.navigation

import androidx.navigation.NavHostController

class NavigationRouterImpl(
    private val navController: NavHostController
) : INavigationRouter {

    override fun navigateToSubjects() {
        navController.navigate(ScreenDestination.Subjects.route) {
            popUpTo(ScreenDestination.Splash.route) {
                inclusive = true
            }
        }
    }

    override fun navigateToSettings() {
        navController.navigate(ScreenDestination.Settings.route)
    }

    override fun navigateToSubjectDetail(subjectId: Long) {
        navController.navigate(
            ScreenDestination.SubjectDetail.createRoute(subjectId)
        )
    }

    override fun navigateToSubjectDetailFromResult(subjectId: Long) {
        navController.navigate(
            ScreenDestination.SubjectDetail.createRoute(subjectId)
        ) {
            popUpTo(ScreenDestination.Subjects.route)
            launchSingleTop = true
        }
    }

    override fun navigateToAddQuestion(subjectId: Long) {
        navController.navigate(
            ScreenDestination.AddQuestion.createRoute(subjectId)
        )
    }

    override fun navigateToEditQuestion(subjectId: Long, questionId: Long) {
        navController.navigate(
            ScreenDestination.AddQuestion.createEditRoute(
                subjectId = subjectId,
                questionId = questionId
            )
        )
    }

    override fun navigateToCreateTest(subjectId: Long) {
        navController.navigate(
            ScreenDestination.CreateTest.createRoute(subjectId)
        )
    }

    override fun navigateToTest(testId: Long) {
        navController.navigate(
            ScreenDestination.TestScreen.createRoute(testId)
        )
    }

    override fun navigateToTestResult(resultId: Long) {
        navController.navigate(
            ScreenDestination.TestResult.createRoute(resultId)
        )
    }

    override fun returnBack() {
        navController.popBackStack()
    }
}
