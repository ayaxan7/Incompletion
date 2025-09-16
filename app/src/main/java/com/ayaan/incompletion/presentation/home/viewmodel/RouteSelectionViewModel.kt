package com.ayaan.incompletion.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.repository.RouteRepository
import com.ayaan.incompletion.data.repository.RouteResult
import com.ayaan.incompletion.data.model.RouteResponse
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
    val selectedDestinationId: String? = null
)

@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteSelectionUiState())
    val uiState: StateFlow<RouteSelectionUiState> = _uiState.asStateFlow()

    // Generate bus stop options (S1 to S25)
    val busStopOptions = (1..25).map { "S$it" }

    fun updateSourceSelection(sourceId: String) {
        _uiState.value = _uiState.value.copy(
            selectedSourceId = sourceId,
            routeResponse = null,
            errorMessage = null
        )
        checkAndFetchRoutes()
    }

    fun updateDestinationSelection(destinationId: String) {
        _uiState.value = _uiState.value.copy(
            selectedDestinationId = destinationId,
            routeResponse = null,
            errorMessage = null
        )
        checkAndFetchRoutes()
    }

    fun swapSourceAndDestination() {
        val currentSource = _uiState.value.selectedSourceId
        val currentDestination = _uiState.value.selectedDestinationId

        _uiState.value = _uiState.value.copy(
            selectedSourceId = currentDestination,
            selectedDestinationId = currentSource,
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
