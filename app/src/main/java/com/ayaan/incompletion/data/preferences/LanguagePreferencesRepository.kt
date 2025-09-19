package com.ayaan.incompletion.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing language preferences using DataStore
 */
@Singleton
class LanguagePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LANGUAGE_CODE_KEY = stringPreferencesKey("language_code")
        const val DEFAULT_LANGUAGE_CODE = "en"
    }

    /**
     * Gets the current language code as a Flow
     * @return Flow<String> - Language code with default value "en"
     */
    val languageCode: Flow<String> = dataStore.data
        .catch { exception ->
            // If there's an error reading preferences, emit empty preferences
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[LANGUAGE_CODE_KEY] ?: DEFAULT_LANGUAGE_CODE
        }

    /**
     * Saves the selected language code to DataStore
     * @param languageCode - ISO 639-1 language code (e.g., "en", "hi", "pa")
     */
    suspend fun saveLanguageCode(languageCode: String) {
        try {
            dataStore.edit { preferences ->
                preferences[LANGUAGE_CODE_KEY] = languageCode
            }
        } catch (exception: Exception) {
            // Log error or handle it appropriately
            throw LanguagePreferenceException("Failed to save language preference", exception)
        }
    }

    /**
     * Gets the current language code synchronously (for initialization)
     * @return String - Current language code or default
     */
    suspend fun getCurrentLanguageCode(): String {
        return try {
            dataStore.data.map { preferences ->
                preferences[LANGUAGE_CODE_KEY] ?: DEFAULT_LANGUAGE_CODE
            }.catch {
                emit(DEFAULT_LANGUAGE_CODE)
            }.let { flow ->
                var result = DEFAULT_LANGUAGE_CODE
                flow.collect { result = it }
                result
            }
        } catch (exception: Exception) {
            DEFAULT_LANGUAGE_CODE
        }
    }

    /**
     * Clears all language preferences
     */
    suspend fun clearLanguagePreferences() {
        try {
            dataStore.edit { preferences ->
                preferences.remove(LANGUAGE_CODE_KEY)
            }
        } catch (exception: Exception) {
            throw LanguagePreferenceException("Failed to clear language preferences", exception)
        }
    }
}

/**
 * Custom exception for language preference operations
 */
class LanguagePreferenceException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
