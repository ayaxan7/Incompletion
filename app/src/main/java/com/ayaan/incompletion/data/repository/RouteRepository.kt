package com.ayaan.incompletion.data.repository

import com.ayaan.incompletion.data.api.RouteApiService
import com.ayaan.incompletion.data.model.RouteRequest
import com.ayaan.incompletion.data.model.RouteResponse
import com.ayaan.incompletion.data.model.ApiError
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class RouteResult {
    data class Success(val data: RouteResponse) : RouteResult()
    data class Error(val message: String) : RouteResult()
    object Loading : RouteResult()
}

@Singleton
class RouteRepository @Inject constructor(
    private val routeApiService: RouteApiService,
    private val gson: Gson
) {

    fun getCommonRoutes(startStopId: String, destinationStopId: String): Flow<RouteResult> = flow {
        emit(RouteResult.Loading)

        try {
            // Validate input
            if (startStopId.isBlank() || destinationStopId.isBlank()) {
                emit(RouteResult.Error("Both source and destination stop IDs are required"))
                return@flow
            }

            if (startStopId == destinationStopId) {
                emit(RouteResult.Error("Source and destination stops cannot be the same"))
                return@flow
            }

            val request = RouteRequest(startStopId, destinationStopId)
            val response = routeApiService.getCommonRoutes(request)

            if (response.isSuccessful) {
                response.body()?.let { routeResponse ->
                    emit(RouteResult.Success(routeResponse))
                } ?: emit(RouteResult.Error("No data received from server"))
            } else {
                val errorMessage = when (response.code()) {
                    400 -> {
                        try {
                            val errorBody = response.errorBody()?.string()
                            val apiError = gson.fromJson(errorBody, ApiError::class.java)
                            apiError.error
                        } catch (parseException: Exception) {
                            "Invalid request parameters"
                        }
                    }
                    404 -> "Stop not found"
                    500 -> "Server error - please try again later"
                    else -> "Failed to get routes: ${response.message()}"
                }
                emit(RouteResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(RouteResult.Error("Network error: ${e.localizedMessage ?: "Please check your connection"}"))
        }
    }
}
