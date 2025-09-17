package com.ayaan.incompletion.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.local.entity.FavoriteRoute
import com.ayaan.incompletion.data.repository.FavoriteRouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteRouteViewModel @Inject constructor(
    private val repository: FavoriteRouteRepository
) : ViewModel() {

    private val _favoriteRoutes = MutableStateFlow<List<FavoriteRoute>>(emptyList())
    val favoriteRoutes: StateFlow<List<FavoriteRoute>> = _favoriteRoutes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFavoriteRoutes()
    }

    private fun loadFavoriteRoutes() {
        viewModelScope.launch {
            repository.getAllFavoriteRoutes().collect { routes ->
                _favoriteRoutes.value = routes
            }
        }
    }

    fun addFavoriteRoute(
        sourceName: String,
        sourceId: String,
        destinationName: String,
        destinationId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addFavoriteRoute(sourceName, sourceId, destinationName, destinationId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFavoriteRoute(favoriteRoute: FavoriteRoute) {
        viewModelScope.launch {
            repository.removeFavoriteRoute(favoriteRoute)
        }
    }

    suspend fun isFavoriteRoute(sourceId: String, destinationId: String): Boolean {
        return repository.isFavoriteRoute(sourceId, destinationId)
    }
}