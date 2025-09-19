package com.ayaan.incompletion.presentation.nearestbusstop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.repository.BusesForStopRepository
import com.ayaan.incompletion.data.repository.BusesForStopResult
import com.ayaan.incompletion.data.model.BusForStop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BusesForStopUiState(
    val isLoading: Boolean = false,
    val buses: List<BusForStop> = emptyList(),
    val errorMessage: String? = null,
    val selectedStopId: String? = null
)

@HiltViewModel
class BusesForStopViewModel @Inject constructor(
    private val busesForStopRepository: BusesForStopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusesForStopUiState())
    val uiState: StateFlow<BusesForStopUiState> = _uiState.asStateFlow()

    fun getBusesForStop(stopId: String) {
        viewModelScope.launch {
            busesForStopRepository.getBusesForStop(stopId).collect { result ->
                when (result) {
                    is BusesForStopResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null,
                            selectedStopId = stopId
                        )
                    }
                    is BusesForStopResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            buses = result.data,
                            errorMessage = null
                        )
                    }
                    is BusesForStopResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            buses = emptyList(),
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

    fun retry() {
        _uiState.value.selectedStopId?.let { stopId ->
            getBusesForStop(stopId)
        }
    }
}
