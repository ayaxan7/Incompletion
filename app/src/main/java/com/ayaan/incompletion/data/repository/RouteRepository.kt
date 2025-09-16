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

    fun getCommonRoutes(sourceId: String, destinationId: String): Flow<RouteResult> = flow {
        emit(RouteResult.Loading)

        try {
            // Validate input
            if (sourceId.isBlank() || destinationId.isBlank()) {
                emit(RouteResult.Error("Both source and destination stop IDs are required"))
                return@flow
            }

            if (sourceId == destinationId) {
                emit(RouteResult.Error("Source and destination stops cannot be the same"))
                return@flow
            }

            // Validate stop ID format (S1 to S25)
            if (!isValidStopId(sourceId) || !isValidStopId(destinationId)) {
                emit(RouteResult.Error("Stop IDs must be in the format S1 to S25"))
                return@flow
            }

            val request = RouteRequest(sourceId, destinationId)
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
                            apiError.message ?: "Invalid request"
                        } catch (e: Exception) {
                            "Invalid request"
                        }
                    }
                    404 -> "No common routes found for this selection"
                    500 -> "Server error - please try again later"
                    else -> "Failed to get routes: ${response.message()}"
                }
                emit(RouteResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(RouteResult.Error("Network error: ${e.localizedMessage ?: "Please check your connection"}"))
        }
    }

    private fun isValidStopId(stopId: String): Boolean {
        return stopId.matches(Regex("^S([1-9]|1[0-9]|2[0-5])$"))
    }
}
