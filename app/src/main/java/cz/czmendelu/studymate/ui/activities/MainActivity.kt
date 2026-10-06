package cz.czmendelu.studymate.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.czmendelu.studymate.datastore.SettingsDataStore
import cz.czmendelu.studymate.localization.LocalAppStrings
import cz.czmendelu.studymate.localization.appStrings
import cz.czmendelu.studymate.localization.toAppLanguage
import cz.czmendelu.studymate.navigation.NavGraph
import cz.czmendelu.studymate.ui.theme.StudyMateTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkModeEnabled by settingsDataStore.darkModeFlow.collectAsState(
                initial = false
            )

            val selectedLanguage by settingsDataStore.languageFlow.collectAsState(
                initial = "English"
            )

            val strings = appStrings(selectedLanguage.toAppLanguage())

            StudyMateTheme(
                darkTheme = darkModeEnabled
            ) {
                CompositionLocalProvider(
                    LocalAppStrings provides strings
                ) {
                    NavGraph()
                }
            }
        }
    }
}