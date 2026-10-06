package cz.czmendelu.studymate.ui.screens.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cz.czmendelu.studymate.localization.LocalAppStrings
import cz.czmendelu.studymate.navigation.INavigationRouter

@Composable
fun SettingsScreen(
    navigationRouter: INavigationRouter,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    Scaffold(
        bottomBar = {
            SettingsBottomBar(
                subjectsText = strings.subjects,
                settingsText = strings.settings,
                onSubjectsClick = {
                    navigationRouter.navigateToSubjects()
                },
                onAddClick = {
                    navigationRouter.navigateToSubjects()
                },
                onSettingsClick = {
                    navigationRouter.navigateToSettings()
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text(
                text = strings.settings,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = strings.language,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (uiState.selectedLanguage == "English") {
                            Button(
                                onClick = {
                                    viewModel.onLanguageChanged("English")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = "English ✓",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    viewModel.onLanguageChanged("English")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = "English",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        if (uiState.selectedLanguage == "Čeština") {
                            Button(
                                onClick = {
                                    viewModel.onLanguageChanged("Čeština")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = "Čeština ✓",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    viewModel.onLanguageChanged("Čeština")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = "Čeština",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = strings.darkMode,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = if (uiState.darkModeEnabled) {
                                    strings.enabled
                                } else {
                                    strings.disabled
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Switch(
                            checked = uiState.darkModeEnabled,
                            onCheckedChange = {
                                viewModel.onDarkModeChanged(it)
                            }
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = strings.about,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.version,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = uiState.versionName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.build,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = uiState.buildNumber,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "StudyMate",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = strings.slogan,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = strings.madeForStudents,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsBottomBar(
    subjectsText: String,
    settingsText: String,
    onSubjectsClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
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
                        selected = false,
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
                        selected = true,
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