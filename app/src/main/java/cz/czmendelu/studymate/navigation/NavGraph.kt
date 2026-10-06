package cz.czmendelu.studymate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cz.czmendelu.studymate.ui.screens.addquestion.AddQuestionScreen
import cz.czmendelu.studymate.ui.screens.createtest.CreateTestScreen
import cz.czmendelu.studymate.ui.screens.settings.SettingsScreen
import cz.czmendelu.studymate.ui.screens.splash.SplashScreen
import cz.czmendelu.studymate.ui.screens.subjectdetail.SubjectDetailScreen
import cz.czmendelu.studymate.ui.screens.subjects.SubjectsScreen
import cz.czmendelu.studymate.ui.screens.test.TestScreen
import cz.czmendelu.studymate.ui.screens.testresult.TestResultScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val navigationRouter = remember {
        NavigationRouterImpl(navController)
    }

    NavHost(
        navController = navController,
        startDestination = ScreenDestination.Splash.route
    ) {
        composable(
            route = ScreenDestination.Splash.route
        ) {
            SplashScreen(
                navigationRouter = navigationRouter
            )
        }

        composable(
            route = ScreenDestination.Subjects.route
        ) {
            SubjectsScreen(
                navigationRouter = navigationRouter
            )
        }

        composable(
            route = ScreenDestination.Settings.route
        ) {
            SettingsScreen(
                navigationRouter = navigationRouter
            )
        }

        composable(
            route = ScreenDestination.SubjectDetail.route,
            arguments = listOf(
                navArgument("subjectId") {
                    type = NavType.LongType
                }
            )
        ) {
            SubjectDetailScreen(
                navigationRouter = navigationRouter
            )
        }

        composable(
            route = ScreenDestination.AddQuestion.route,
            arguments = listOf(
                navArgument("subjectId") {
                    type = NavType.LongType
                },
                navArgument("questionId") {
                    type = NavType.LongType
                    // Hodnota -1 znamená vytvoření nové otázky, jinak se otázka edituje.
                    defaultValue = -1L
                }
            )
        ) {
            AddQuestionScreen(
                navigationRouter = navigationRouter
            )
        }

        composable(
            route = ScreenDestination.CreateTest.route,
            arguments = listOf(
                navArgument("subjectId") {
                    type = NavType.LongType
                }
            )
        ) {
            CreateTestScreen(
                navigationRouter = navigationRouter
            )
        }

        composable(
            route = ScreenDestination.TestScreen.route,
            arguments = listOf(
                navArgument("testId") {
                    type = NavType.LongType
                }
            )
        ) {
            TestScreen(
                navigationRouter = navigationRouter
            )
        }

        composable(
            route = ScreenDestination.TestResult.route,
            arguments = listOf(
                navArgument("resultId") {
                    type = NavType.LongType
                }
            )
        ) {
            TestResultScreen(
                navigationRouter = navigationRouter
            )
        }
    }
}
