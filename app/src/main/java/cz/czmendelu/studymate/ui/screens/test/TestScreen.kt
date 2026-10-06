package cz.czmendelu.studymate.ui.screens.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cz.czmendelu.studymate.localization.LocalAppStrings
import cz.czmendelu.studymate.navigation.INavigationRouter
import cz.czmendelu.studymate.ui.screens.test.components.ChooseCorrectImageContent
import cz.czmendelu.studymate.ui.screens.test.components.FlashcardQuestionContent
import cz.czmendelu.studymate.ui.screens.test.components.ImageRecognitionContent
import cz.czmendelu.studymate.ui.screens.test.components.TestBottomControls
import cz.czmendelu.studymate.ui.screens.test.components.TestHeader
import cz.czmendelu.studymate.ui.screens.test.components.YesNoQuestionContent

@Composable
fun TestScreen(
    navigationRouter: INavigationRouter,
    viewModel: TestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    LaunchedEffect(uiState.finished, uiState.resultId) {
        val resultId = uiState.resultId

        if (uiState.finished && resultId != null) {
            navigationRouter.navigateToTestResult(resultId)
        }
    }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            TestHeader(
                progressText = "${strings.questionCounter} ${uiState.currentQuestionIndex + 1}/${uiState.questions.size}",
                progress = uiState.progress,
                timerText = uiState.timerText,
                hasQuestions = uiState.questions.isNotEmpty(),
                onCloseClick = {
                    navigationRouter.returnBack()
                }
            )

            val question = uiState.currentQuestion

            if (question == null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = strings.loadingTest,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                when (uiState.test?.testType) {

                    "YES_NO" -> {
                        YesNoQuestionContent(
                            questionText = question.questionText,
                            answerText = question.answerText,
                            selectedAnswer = uiState.selectedYesNoAnswer,
                            yesText = strings.yes,
                            noText = strings.no,
                            proposedAnswerText = strings.proposedAnswer,
                            onAnswerClick = {
                                viewModel.onYesNoAnswerSelected(it)
                            }
                        )
                    }

                    "IMAGE_RECOGNITION" -> {
                        ImageRecognitionContent(
                            question = question,
                            userAnswer = uiState.userTextAnswer,
                            yourAnswerText = strings.yourAnswer,
                            confirmText = strings.confirm,
                            noImageSelectedText = strings.noImageSelected,
                            onUserAnswerChanged = {
                                viewModel.onUserTextAnswerChanged(it)
                            },
                            onConfirmClick = {
                                viewModel.nextQuestion()
                            }
                        )
                    }

                    "CHOOSE_CORRECT_IMAGE" -> {
                        ChooseCorrectImageContent(
                            question = question,
                            options = viewModel.getImageOptions(),
                            selectedImageAnswer = uiState.selectedImageAnswer,
                            selectedText = strings.selected,
                            imageText = strings.image,
                            noImageOptionsText = strings.noImageOptionsAvailable,
                            onImageSelected = {
                                viewModel.onImageAnswerSelected(it)
                            }
                        )
                    }

                    else -> {
                        FlashcardQuestionContent(
                            questionText = question.questionText,
                            answerText = question.answerText,
                            showAnswer = uiState.showAnswer,
                            tapToShowAnswerText = strings.tapCardToShowAnswer,
                            tapToShowQuestionText = strings.tapCardToShowQuestion,
                            onCardClick = {
                                viewModel.toggleAnswer()
                            }
                        )
                    }
                }
            }

            TestBottomControls(
                isFirstQuestion = uiState.isFirstQuestion,
                hasQuestions = uiState.questions.isNotEmpty(),
                finishText = strings.finishTest,
                onPreviousClick = {
                    viewModel.previousQuestion()
                },
                onNextClick = {
                    viewModel.nextQuestion()
                },
                onFinishClick = {
                    viewModel.finishTest()
                }
            )
        }
    }
}