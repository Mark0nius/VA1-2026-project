package cz.czmendelu.studymate.ui.screens.settings

data class SettingsUIState(
    val selectedLanguage: String = "English",
    val darkModeEnabled: Boolean = false,
    val versionName: String = "",
    val buildNumber: String = ""
)