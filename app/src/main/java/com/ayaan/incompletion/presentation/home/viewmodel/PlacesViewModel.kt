package com.ayaan.incompletion.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.PlaceSuggestion
import com.ayaan.incompletion.data.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<PlaceSuggestion>>(emptyList())
    val suggestions: StateFlow<List<PlaceSuggestion>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getBusStationSuggestions(query: String) {
        if (query.length <= 2) {
            _suggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = placesRepository.getBusStationSuggestions(query)
                _suggestions.value = results
            } catch (e: Exception) {
                _suggestions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPlaceSuggestions(query: String) {
        if (query.length <= 2) {
            _suggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = placesRepository.getPlaceSuggestions(query)
                _suggestions.value = results
            } catch (e: Exception) {
                e.printStackTrace()
                _suggestions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}
