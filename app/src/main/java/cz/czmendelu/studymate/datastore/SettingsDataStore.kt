package cz.czmendelu.studymate.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.settingsDataStore by preferencesDataStore(
    name = "settings"
)

class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // Klíče, pod kterými jsou uložené hodnoty v Preferences DataStore.
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Flow průběžně vrací aktuální jazyk aplikace a automaticky reaguje na změny.
    val languageFlow: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "English"
    }

    // Flow průběžně vrací nastavení tmavého režimu.
    val darkModeFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    suspend fun setLanguage(language: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }
}
