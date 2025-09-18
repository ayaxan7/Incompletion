package com.ayaan.incompletion.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.repository.RouteRepository
import com.ayaan.incompletion.data.repository.RouteResult
import com.ayaan.incompletion.data.model.RouteResponse
import com.ayaan.incompletion.data.model.BusStopData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RouteSelectionUiState(
    val isLoading: Boolean = false,
    val routeResponse: RouteResponse? = null,
    val errorMessage: String? = null,
    val selectedSourceId: String? = null,
    val selectedDestinationId: String? = null,
    val selectedSourceName: String? = null,
    val selectedDestinationName: String? = null
)

@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteSelectionUiState())
    val uiState: StateFlow<RouteSelectionUiState> = _uiState.asStateFlow()

    // Get bus stop options with names
    val busStopOptions = BusStopData.busStopOptions

    fun updateSourceSelection(stopId: String) {
        val stopName = BusStopData.getStopName(stopId)
        _uiState.value = _uiState.value.copy(
            selectedSourceId = stopId,
            selectedSourceName = stopName,
            routeResponse = null,
            errorMessage = null
        )
        checkAndFetchRoutes()
    }

    fun updateDestinationSelection(stopId: String) {
        val stopName = BusStopData.getStopName(stopId)
        _uiState.value = _uiState.value.copy(
            selectedDestinationId = stopId,
            selectedDestinationName = stopName,
            routeResponse = null,
            errorMessage = null
        )
        checkAndFetchRoutes()
    }

    fun swapSourceAndDestination() {
        val currentSourceId = _uiState.value.selectedSourceId
        val currentDestinationId = _uiState.value.selectedDestinationId
        val currentSourceName = _uiState.value.selectedSourceName
        val currentDestinationName = _uiState.value.selectedDestinationName

        _uiState.value = _uiState.value.copy(
            selectedSourceId = currentDestinationId,
            selectedDestinationId = currentSourceId,
            selectedSourceName = currentDestinationName,
            selectedDestinationName = currentSourceName,
            routeResponse = null,
            errorMessage = null
        )
        checkAndFetchRoutes()
    }

    private fun checkAndFetchRoutes() {
        val sourceId = _uiState.value.selectedSourceId
        val destinationId = _uiState.value.selectedDestinationId

        if (!sourceId.isNullOrEmpty() && !destinationId.isNullOrEmpty()) {
            fetchCommonRoutes(sourceId, destinationId)
        }
    }

    private fun fetchCommonRoutes(sourceId: String, destinationId: String) {
        viewModelScope.launch {
            routeRepository.getCommonRoutes(sourceId, destinationId).collect { result ->
                when (result) {
                    is RouteResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is RouteResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            routeResponse = result.data,
                            errorMessage = null
                        )
                    }
                    is RouteResult.Error -> {
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

    fun clearSelections() {
        _uiState.value = RouteSelectionUiState()
    }
}
