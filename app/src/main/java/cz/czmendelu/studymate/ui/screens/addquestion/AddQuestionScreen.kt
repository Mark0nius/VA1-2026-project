package cz.czmendelu.studymate.ui.screens.addquestion

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import cz.czmendelu.studymate.localization.LocalAppStrings
import cz.czmendelu.studymate.navigation.INavigationRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionScreen(
    navigationRouter: INavigationRouter,
    viewModel: AddQuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    var expanded by remember {
        mutableStateOf(false)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (exception: SecurityException) {
                exception.printStackTrace()
            }

            viewModel.onImageSelected(it.toString())
        }
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            navigationRouter.returnBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) {
                            strings.editQuestion
                        } else {
                            strings.addQuestion
                        },
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = uiState.questionText,
                onValueChange = {
                    viewModel.onQuestionTextChanged(it)
                },
                label = {
                    Text(
                        text = strings.question,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                minLines = 3,
                isError = uiState.questionTextError != null,
                supportingText = {
                    uiState.questionTextError?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )

            OutlinedTextField(
                value = uiState.answerText,
                onValueChange = {
                    viewModel.onAnswerTextChanged(it)
                },
                label = {
                    Text(
                        text = strings.answer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 2,
                isError = uiState.answerTextError != null,
                supportingText = {
                    uiState.answerTextError?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = uiState.questionType,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = strings.questionType,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    viewModel.questionTypes.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = {
                                viewModel.onQuestionTypeChanged(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (uiState.questionType == "YES_NO") {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = strings.correctYesNoAnswer,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = uiState.correctAnswer,
                                onClick = {
                                    viewModel.onCorrectAnswerChanged(true)
                                },
                                label = {
                                    Text(
                                        text = strings.yes,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )

                            FilterChip(
                                selected = !uiState.correctAnswer,
                                onClick = {
                                    viewModel.onCorrectAnswerChanged(false)
                                },
                                label = {
                                    Text(
                                        text = strings.no,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    imagePickerLauncher.launch(
                        arrayOf("image/*")
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                Text(
                    text = strings.addImageFromGallery,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (uiState.imagePath != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = strings.selectedImage,
                            style = MaterialTheme.typography.titleSmall
                        )

                        AsyncImage(
                            model = uiState.imagePath,
                            contentDescription = strings.selectedImage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = uiState.imagePath ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        OutlinedButton(
                            onClick = {
                                viewModel.removeImage()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = strings.removeImage,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.saveQuestion()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                Text(
                    text = if (uiState.isEditMode) {
                        strings.saveChanges
                    } else {
                        strings.saveQuestion
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}