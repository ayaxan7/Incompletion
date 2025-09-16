package com.ayaan.incompletion.presentation.routedetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.repository.RouteRepository
import com.ayaan.incompletion.data.repository.SingleRouteResult
import com.ayaan.incompletion.data.model.getRoute.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RouteDetailsUiState(
    val isLoading: Boolean = false,
    val routes: List<Routes> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class RouteDetailsViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteDetailsUiState())
    val uiState: StateFlow<RouteDetailsUiState> = _uiState.asStateFlow()

    fun getRouteDetails(routeNumber: String) {
        viewModelScope.launch {
            routeRepository.getRoutes(routeNumber).collect { result ->
                when (result) {
                    is SingleRouteResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is SingleRouteResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            routes = result.data,
                            errorMessage = null
                        )
                    }
                    is SingleRouteResult.Error -> {
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

    fun refreshRouteDetails(routeNumber: String) {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        getRouteDetails(routeNumber)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearResults() {
        _uiState.value = RouteDetailsUiState()
    }
}
