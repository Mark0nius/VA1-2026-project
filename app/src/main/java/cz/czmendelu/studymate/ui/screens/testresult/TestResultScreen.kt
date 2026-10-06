package cz.czmendelu.studymate.ui.screens.testresult

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cz.czmendelu.studymate.localization.AppStrings
import cz.czmendelu.studymate.localization.LocalAppStrings
import cz.czmendelu.studymate.navigation.INavigationRouter
import kotlin.math.roundToInt

@Composable
fun TestResultScreen(
    navigationRouter: INavigationRouter,
    viewModel: TestResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ResultSummaryCard(
                scorePercent = uiState.scorePercent,
                correctCount = uiState.correctCount,
                wrongCount = uiState.wrongCount,
                totalQuestions = uiState.totalQuestions,
                strings = strings
            )
        }

        item {
            Text(
                text = strings.answerDetails,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(uiState.answers) { answer ->
            AnswerDetailCard(
                answer = answer,
                strings = strings
            )
        }

        item {
            ResultButtons(
                testId = uiState.testId,
                subjectId = uiState.subjectId,
                strings = strings,
                navigationRouter = navigationRouter
            )
        }
    }
}

@Composable
private fun ResultSummaryCard(
    scorePercent: Double,
    correctCount: Int,
    wrongCount: Int,
    totalQuestions: Int,
    strings: AppStrings
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(82.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = strings.studySessionComplete,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "${scorePercent.roundToInt()} %",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "$correctCount / $totalQuestions",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${strings.correct}: $correctCount",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${strings.incorrect}: $wrongCount",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AnswerDetailCard(
    answer: TestAnswerDetail,
    strings: AppStrings
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = answer.questionText,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (answer.isCorrect) {
                    strings.correct
                } else {
                    strings.incorrect
                },
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "${strings.yourAnswer}: ${formatAnswer(answer.userAnswer, strings)}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${strings.correctAnswer}: ${formatAnswer(answer.correctAnswer, strings)}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ResultButtons(
    testId: Long?,
    subjectId: Long?,
    strings: AppStrings,
    navigationRouter: INavigationRouter
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedButton(
            onClick = {
                testId?.let {
                    navigationRouter.navigateToTest(it)
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(58.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )

            Text(
                text = strings.studyAgain,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Button(
            onClick = {
                subjectId?.let {
                    navigationRouter.navigateToSubjectDetailFromResult(it)
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(58.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )

            Text(
                text = strings.backToSubject,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

private fun formatAnswer(
    value: String,
    strings: AppStrings
): String {
    return when (value.lowercase()) {
        "true" -> strings.yes
        "false" -> strings.no
        "" -> strings.notAnswered
        else -> value
    }
}
