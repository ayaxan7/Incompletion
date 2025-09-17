package com.ayaan.incompletion.data.repository

import com.ayaan.incompletion.data.api.RouteApiService
import com.ayaan.incompletion.data.model.BusesForStopRequest
import com.ayaan.incompletion.data.model.BusesForStopResponse
import com.ayaan.incompletion.data.model.ApiError
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class BusesForStopResult {
    data class Success(val data: BusesForStopResponse) : BusesForStopResult()
    data class Error(val message: String) : BusesForStopResult()
    object Loading : BusesForStopResult()
}
@Singleton
class BusesForStopRepository @Inject constructor(
    private val routeApiService: RouteApiService,
    private val gson: Gson
) {
    fun getBusesForStop(stopId: String): Flow<BusesForStopResult> = flow {
        emit(BusesForStopResult.Loading)

        try {
            // Validate input
            if (stopId.isBlank()) {
                emit(BusesForStopResult.Error("Stop ID is required"))
                return@flow
            }

            val request = BusesForStopRequest(stopId = stopId)
            val response = routeApiService.getBusesForStop(request)

            if (response.isSuccessful) {
                response.body()?.let { busesData ->
                    emit(BusesForStopResult.Success(busesData))
                } ?: emit(BusesForStopResult.Error("No buses found for this stop"))
            } else {
                val errorMessage = when (response.code()) {
                    400 -> {
                        try {
                            val errorBody = response.errorBody()?.string()
                            if (!errorBody.isNullOrEmpty()) {
                                val apiError = gson.fromJson(errorBody, ApiError::class.java)
                                apiError.message ?: "Invalid request"
                            } else {
                                "Invalid request"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "Invalid request"
                        }
                    }
                    404 -> "Stop not found"
                    500 -> "Server error. Please try again later."
                    else -> "Failed to get buses for stop. Please try again."
                }
                emit(BusesForStopResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is java.net.UnknownHostException -> "No internet connection"
                is java.net.SocketTimeoutException -> "Request timeout. Please try again."
                else -> "An unexpected error occurred: ${e.message}"
            }
            e.printStackTrace()
            emit(BusesForStopResult.Error(errorMessage))
        }
    }
}
