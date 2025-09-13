package com.ayaan.incompletion.data.repository

import com.ayaan.incompletion.data.PlaceSuggestion
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val placesClient: PlacesClient
) {

    suspend fun getBusStationSuggestions(query: String): List<PlaceSuggestion> {
        return try {
            // Assume you have user's location (lat/lng)
            val userLat = 12.9716  // get from FusedLocationProviderClient in real code
            val userLng = 77.5946

            val bounds = RectangularBounds.newInstance(
                LatLng(userLat - 0.40, userLng - 0.40),
                LatLng(userLat + 0.40, userLng + 0.40)
            )

            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setLocationBias(bounds)
                .setCountry("IN")
                .setTypesFilter(listOf("bus_station", "transit_station"))
                .build()

            val response = placesClient.findAutocompletePredictions(request).await()

            response.autocompletePredictions.map { prediction ->
                PlaceSuggestion(
                    placeId = prediction.placeId,
                    primaryText = prediction.getPrimaryText(null).toString(),
                    secondaryText = prediction.getSecondaryText(null).toString(),
                    description = prediction.getFullText(null).toString()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPlaceSuggestions(query: String): List<PlaceSuggestion> {
        return try {
            // Assume you have user's location (lat/lng)
            val userLat = 12.9716  // get from FusedLocationProviderClient in real code
            val userLng = 77.5946

            val bounds = RectangularBounds.newInstance(
                LatLng(userLat - 0.40, userLng - 0.40),
                LatLng(userLat + 0.40, userLng + 0.40)
            )

            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setLocationBias(bounds)
                .build()

            val response = placesClient.findAutocompletePredictions(request).await()

            response.autocompletePredictions.map { prediction ->
                PlaceSuggestion(
                    placeId = prediction.placeId,
                    primaryText = prediction.getPrimaryText(null).toString(),
                    secondaryText = prediction.getSecondaryText(null).toString(),
                    description = prediction.getFullText(null).toString()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
