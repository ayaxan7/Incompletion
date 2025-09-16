package com.ayaan.incompletion.presentation.nearestbusstop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.model.NearestBusStopResponse
import com.ayaan.incompletion.data.repository.NearestBusStopRepository
import com.ayaan.incompletion.data.repository.NearestBusStopResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NearestBusStopUiState(
    val isLoading: Boolean = false,
    val busStopsResponse: NearestBusStopResponse? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class NearestBusStopViewModel @Inject constructor(
    private val repository: NearestBusStopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearestBusStopUiState())
    val uiState: StateFlow<NearestBusStopUiState> = _uiState.asStateFlow()

    fun findNearestBusStops() {
        viewModelScope.launch {
            repository.getNearestBusStops().collect { result ->
                when (result) {
                    is NearestBusStopResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is NearestBusStopResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            busStopsResponse = result.data,
                            errorMessage = null
                        )
                    }
                    is NearestBusStopResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearResults() {
        _uiState.value = NearestBusStopUiState()
    }
}
