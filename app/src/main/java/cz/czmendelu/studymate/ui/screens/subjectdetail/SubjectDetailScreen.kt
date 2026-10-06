package cz.czmendelu.studymate.ui.screens.subjectdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cz.czmendelu.studymate.database.tables.Question
import cz.czmendelu.studymate.database.tables.Test
import cz.czmendelu.studymate.database.tables.TestResult
import cz.czmendelu.studymate.localization.AppStrings
import cz.czmendelu.studymate.localization.LocalAppStrings
import cz.czmendelu.studymate.navigation.INavigationRouter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    navigationRouter: INavigationRouter,
    viewModel: SubjectDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    var questionToDelete by remember {
        mutableStateOf<Question?>(null)
    }

    var testToDelete by remember {
        mutableStateOf<Test?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.subject?.name ?: strings.subjects,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigationRouter.returnBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            OutlinedButton(
                onClick = {
                    uiState.subject?.id?.let {
                        navigationRouter.navigateToAddQuestion(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = strings.addQuestion,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.showAiImportDialog()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = strings.importQuestionsAi,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    uiState.subject?.id?.let {
                        navigationRouter.navigateToCreateTest(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = strings.createTest,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedTab == SubjectDetailTab.TESTS,
                    onClick = {
                        viewModel.selectTab(SubjectDetailTab.TESTS)
                    },
                    label = {
                        Text(
                            text = strings.tests,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = uiState.selectedTab == SubjectDetailTab.QUESTIONS,
                    onClick = {
                        viewModel.selectTab(SubjectDetailTab.QUESTIONS)
                    },
                    label = {
                        Text(
                            text = strings.questions,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = uiState.selectedTab == SubjectDetailTab.RESULTS,
                    onClick = {
                        viewModel.selectTab(SubjectDetailTab.RESULTS)
                    },
                    label = {
                        Text(
                            text = strings.results,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState.selectedTab) {
                SubjectDetailTab.TESTS -> {
                    TestsList(
                        tests = uiState.tests,
                        strings = strings,
                        onStartClick = { test ->
                            test.id?.let {
                                navigationRouter.navigateToTest(it)
                            }
                        },
                        onDeleteClick = { test ->
                            testToDelete = test
                        }
                    )
                }

                SubjectDetailTab.QUESTIONS -> {
                    QuestionsList(
                        questions = uiState.questions,
                        hasImageText = strings.hasImage,
                        onEditClick = { question ->
                            val subjectId = uiState.subject?.id
                            val questionId = question.id

                            if (subjectId != null && questionId != null) {
                                navigationRouter.navigateToEditQuestion(
                                    subjectId = subjectId,
                                    questionId = questionId
                                )
                            }
                        },
                        onDeleteClick = { question ->
                            questionToDelete = question
                        }
                    )
                }

                SubjectDetailTab.RESULTS -> {
                    ResultsList(
                        results = uiState.results,
                        strings = strings,
                        onResultClick = { result ->
                            result.id?.let {
                                navigationRouter.navigateToTestResult(it)
                            }
                        }
                    )
                }
            }
        }

        questionToDelete?.let { question ->
            ConfirmDeleteDialog(
                title = strings.confirmDeleteTitle,
                message = strings.confirmDeleteQuestionMessage,
                deleteText = strings.delete,
                cancelText = strings.cancel,
                onConfirm = {
                    viewModel.deleteQuestion(question)
                    questionToDelete = null
                },
                onDismiss = {
                    questionToDelete = null
                }
            )
        }

        testToDelete?.let { test ->
            ConfirmDeleteDialog(
                title = strings.confirmDeleteTitle,
                message = strings.confirmDeleteTestMessage,
                deleteText = strings.delete,
                cancelText = strings.cancel,
                onConfirm = {
                    viewModel.deleteTest(test)
                    testToDelete = null
                },
                onDismiss = {
                    testToDelete = null
                }
            )
        }

        if (uiState.showAiImportDialog) {
            AiImportDialog(
                strings = strings,
                sourceText = uiState.aiSourceText,
                isImporting = uiState.isImportingQuestions,
                errorMessage = uiState.aiImportError,
                onSourceTextChanged = {
                    viewModel.onAiSourceTextChanged(it)
                },
                onImportClick = {
                    viewModel.importQuestionsFromAi()
                },
                onDismiss = {
                    viewModel.hideAiImportDialog()
                }
            )
        }
    }
}

@Composable
private fun AiImportDialog(
    strings: AppStrings,
    sourceText: String,
    isImporting: Boolean,
    errorMessage: String?,
    onSourceTextChanged: (String) -> Unit,
    onImportClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isImporting) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = strings.aiImportTitle,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = strings.aiImportDescription,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = sourceText,
                    onValueChange = onSourceTextChanged,
                    label = {
                        Text(
                            text = strings.sourceText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    minLines = 6,
                    maxLines = 10,
                    enabled = !isImporting,
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                if (isImporting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = strings.importingQuestions,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onImportClick,
                enabled = !isImporting && sourceText.isNotBlank()
            ) {
                Text(
                    text = strings.importQuestions,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isImporting
            ) {
                Text(
                    text = strings.cancel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@Composable
private fun ConfirmDeleteDialog(
    title: String,
    message: String,
    deleteText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = deleteText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = cancelText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@Composable
private fun TestsList(
    tests: List<Test>,
    strings: AppStrings,
    onStartClick: (Test) -> Unit,
    onDeleteClick: (Test) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tests) { test ->

            val testDescription = buildString {
                append(test.testType)
                append(" • ")
                append(test.questionCount)
                append(" ")
                append(strings.questions.lowercase())

                if (test.isTimed) {
                    append(" • ")
                    append(strings.timedTest)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = test.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = testDescription,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                onStartClick(test)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start"
                            )
                        }

                        IconButton(
                            onClick = {
                                onDeleteClick(test)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionsList(
    questions: List<Question>,
    hasImageText: String,
    onEditClick: (Question) -> Unit,
    onDeleteClick: (Question) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(questions) { question ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = question.questionText,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = question.answerText,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (question.imagePath != null) {
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = hasImageText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = {
                                onEditClick(question)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }

                        IconButton(
                            onClick = {
                                onDeleteClick(question)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultsList(
    results: List<TestResult>,
    strings: AppStrings,
    onResultClick: (TestResult) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results) { result ->
            Card(
                onClick = {
                    onResultClick(result)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${strings.score}: ${result.scorePercent.roundToInt()} %",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${strings.correct}: ${result.correctCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "${strings.incorrect}: ${result.wrongCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "${result.correctCount} / ${result.totalQuestions}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = strings.tapToOpen,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}