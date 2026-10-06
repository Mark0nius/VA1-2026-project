package cz.czmendelu.studymate.ui.screens.subjects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import cz.czmendelu.studymate.database.tables.Subject
import cz.czmendelu.studymate.localization.LocalAppStrings
import cz.czmendelu.studymate.navigation.INavigationRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    navigationRouter: INavigationRouter,
    viewModel: SubjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    var subjectToDelete by remember {
        mutableStateOf<Subject?>(null)
    }

    Scaffold(
        bottomBar = {
            SubjectsBottomBar(
                onSubjectsClick = {
                    navigationRouter.navigateToSubjects()
                },
                onAddClick = {
                    viewModel.showAddDialog()
                },
                onSettingsClick = {
                    navigationRouter.navigateToSettings()
                },
                subjectsText = strings.subjects,
                settingsText = strings.settings
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = strings.subjects,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 20.dp)
            )

            if (uiState.subjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.subjectListEmpty,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(uiState.subjects) { subject ->
                        SubjectItem(
                            subject = subject,
                            tapToOpenText = strings.tapToOpen,
                            onClick = {
                                subject.id?.let {
                                    navigationRouter.navigateToSubjectDetail(it)
                                }
                            },
                            onEditClick = {
                                viewModel.showEditDialog(subject)
                            },
                            onDeleteClick = {
                                subjectToDelete = subject
                            }
                        )
                    }
                }
            }
        }

        if (uiState.isAddDialogVisible) {
            SubjectNameDialog(
                title = strings.createNewSubject,
                subjectName = uiState.newSubjectName,
                subjectNameLabel = strings.subjectName,
                confirmText = strings.create,
                cancelText = strings.cancel,
                onSubjectNameChanged = {
                    viewModel.onNewSubjectNameChanged(it)
                },
                onConfirm = {
                    viewModel.addSubject()
                },
                onDismiss = {
                    viewModel.hideAddDialog()
                }
            )
        }

        if (uiState.isEditDialogVisible) {
            SubjectNameDialog(
                title = strings.editSubject,
                subjectName = uiState.editedSubjectName,
                subjectNameLabel = strings.subjectName,
                confirmText = strings.save,
                cancelText = strings.cancel,
                onSubjectNameChanged = {
                    viewModel.onEditedSubjectNameChanged(it)
                },
                onConfirm = {
                    viewModel.updateSubject()
                },
                onDismiss = {
                    viewModel.hideEditDialog()
                }
            )
        }

        subjectToDelete?.let { subject ->
            AlertDialog(
                onDismissRequest = {
                    subjectToDelete = null
                },
                title = {
                    Text(
                        text = strings.confirmDeleteTitle,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        text = strings.confirmDeleteSubjectMessage,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteSubject(subject)
                            subjectToDelete = null
                        }
                    ) {
                        Text(
                            text = strings.delete,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            subjectToDelete = null
                        }
                    ) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SubjectNameDialog(
    title: String,
    subjectName: String,
    subjectNameLabel: String,
    confirmText: String,
    cancelText: String,
    onSubjectNameChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            OutlinedTextField(
                value = subjectName,
                onValueChange = onSubjectNameChanged,
                label = {
                    Text(
                        text = subjectNameLabel,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = confirmText,
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
private fun SubjectsBottomBar(
    onSubjectsClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    subjectsText: String,
    settingsText: String
) {
    Surface(
        tonalElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = onSubjectsClick,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = subjectsText,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        label = {
                            Text(
                                text = subjectsText,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(96.dp))

                    NavigationBarItem(
                        selected = false,
                        onClick = onSettingsClick,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = settingsText,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        label = {
                            Text(
                                text = settingsText,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(66.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add subject",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun SubjectItem(
    subject: Subject,
    tapToOpenText: String,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = tapToOpenText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onEditClick
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit subject",
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete subject",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
