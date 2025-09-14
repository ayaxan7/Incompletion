package com.ayaan.incompletion.presentation.bookings.viewmodel

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

data class RouteFinderUiState(
    val isLoading: Boolean = false,
    val routeResponse: RouteResponse? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class RouteFinderViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteFinderUiState())
    val uiState: StateFlow<RouteFinderUiState> = _uiState.asStateFlow()

    fun findRoutes(startStopId: String, destinationStopId: String) {
        viewModelScope.launch {
            routeRepository.getCommonRoutes(startStopId, destinationStopId).collect { result ->
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
                            isRefreshing = false,
                            routeResponse = result.data,
                            errorMessage = null
                        )
                    }
                    is RouteResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun refreshRoutes(startStopId: String, destinationStopId: String) {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        findRoutes(startStopId, destinationStopId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearResults() {
        _uiState.value = RouteFinderUiState()
    }
}
