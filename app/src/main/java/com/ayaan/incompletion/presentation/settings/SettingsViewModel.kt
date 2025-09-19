package com.ayaan.incompletion.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.preferences.LanguagePreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing language settings and preferences
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languagePreferencesRepository: LanguagePreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _selectedLanguageCode = MutableStateFlow(LanguagePreferencesRepository.DEFAULT_LANGUAGE_CODE)
    val selectedLanguageCode: StateFlow<String> = _selectedLanguageCode.asStateFlow()

    init {
        loadLanguagePreference()
    }

    /**
     * Loads the current language preference from DataStore
     */
     fun loadLanguagePreference() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                languagePreferencesRepository.languageCode
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to load language preference: ${exception.message}"
                        )
                    }
                    .collect { languageCode ->
                        _selectedLanguageCode.value = languageCode
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load language preference: ${exception.message}"
                )
            }
        }
    }

    /**
     * Updates the selected language preference
     * @param languageCode - ISO 639-1 language code (e.g., "en", "hi", "pa")
     */
    fun updateLanguagePreference(languageCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                languagePreferencesRepository.saveLanguageCode(languageCode)
                _selectedLanguageCode.value = languageCode
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    languageUpdated = true
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to save language preference: ${exception.message}"
                )
            }
        }
    }

    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Clears the language updated flag
     */
    fun clearLanguageUpdatedFlag() {
        _uiState.value = _uiState.value.copy(languageUpdated = false)
    }

    /**
     * Resets all language preferences to default
     */
    fun resetLanguagePreferences() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                languagePreferencesRepository.clearLanguagePreferences()
                _selectedLanguageCode.value = LanguagePreferencesRepository.DEFAULT_LANGUAGE_CODE
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    languageUpdated = true
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to reset language preferences: ${exception.message}"
                )
            }
        }
    }
}

/**
 * UI state for the Settings screen
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val languageUpdated: Boolean = false
)
