package com.ayaan.incompletion.data.repository

import android.util.Log
import com.ayaan.incompletion.data.api.RouteApiService
import com.ayaan.incompletion.data.model.NearestBusStopResponse
import com.ayaan.incompletion.data.location.LocationService
import com.ayaan.incompletion.data.model.NearestBusStopRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

sealed class NearestBusStopResult {
    data class Success(val data: NearestBusStopResponse) : NearestBusStopResult()
    data class Error(val message: String) : NearestBusStopResult()
    object Loading : NearestBusStopResult()
}

@Singleton
class NearestBusStopRepository @Inject constructor(
    private val apiService: RouteApiService,
    private val locationService: LocationService
) {
    suspend fun test(): Response<String> {
        val response = apiService.testEndpoint()
        return response
    }

    fun getNearestBusStops(): Flow<NearestBusStopResult> = flow {
        emit(NearestBusStopResult.Loading)

        try {
            // Get user's current location
            val location = locationService.getUserLocation()

            // Force use of Bangalore coordinates for testing since emulator gives California coordinates
            // which the backend doesn't recognize
            val latitude = 12.9098849
            val longitude = 77.5644359

//            val request = NearestBusStopRequest(
//                userLat = location?.latitude ?: latitude,
//                userLon = location?.longitude ?: longitude
//            )
val request= NearestBusStopRequest(
                userLat = latitude,
                userLon = longitude
    )
            // Send GET request with request body
            val response = apiService.getNearestBusStops(request)

            if (response.isSuccessful) {
                response.body()?.let { busStopResponse ->
                    emit(NearestBusStopResult.Success(busStopResponse))
                } ?: emit(NearestBusStopResult.Error("No data received from server"))
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "No bus stops found nearby"
                    500 -> "Server error - please try again later"
                    else -> "Failed to get nearest bus stops: ${response.message()}"
                }
                emit(NearestBusStopResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("NearestBusStopRepository", "Network error: ${e.message}", e)
            emit(NearestBusStopResult.Error("Network error: ${e.localizedMessage ?: "Please check your connection"}"))
        }
    }
}
