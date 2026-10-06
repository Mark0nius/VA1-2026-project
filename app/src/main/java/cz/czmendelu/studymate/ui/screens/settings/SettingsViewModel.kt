package cz.czmendelu.studymate.ui.screens.settings

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.czmendelu.studymate.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUIState(
            versionName = getVersionName(),
            buildNumber = getVersionCode()
        )
    )

    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsDataStore.languageFlow,
                settingsDataStore.darkModeFlow
            ) { language, darkMode ->
                Pair(language, darkMode)
            }.collect { settings ->
                _uiState.update {
                    it.copy(
                        selectedLanguage = settings.first,
                        darkModeEnabled = settings.second,
                        versionName = getVersionName(),
                        buildNumber = getVersionCode()
                    )
                }
            }
        }
    }

    fun onLanguageChanged(language: String) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(language)
        }
    }

    fun onDarkModeChanged(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(enabled)
        }
    }

    private fun getVersionName(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )

            packageInfo.versionName ?: "1.0"
        } catch (exception: Exception) {
            "1.0"
        }
    }

    private fun getVersionCode(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )

            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                // Starší Android verze nemají longVersionCode, proto se použije původní API.
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            versionCode.toString()
        } catch (exception: Exception) {
            "1"
        }
    }
}
