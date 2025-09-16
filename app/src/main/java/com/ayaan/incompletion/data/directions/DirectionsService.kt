package com.ayaan.incompletion.data.directions

import android.util.Log
import com.ayaan.incompletion.BuildConfig
import com.google.android.gms.maps.model.LatLng
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.google.maps.model.Unit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectionsService @Inject constructor() {

    private val geoApiContext = GeoApiContext.Builder()
        .apiKey(BuildConfig.GMAPS_API_KEY)
        .build()

    suspend fun getDirections(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng> = emptyList()
    ): DirectionsResult? = withContext(Dispatchers.IO) {
        try {
            val directionsApiRequest = DirectionsApi.newRequest(geoApiContext)
                .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.TRANSIT)
                .units(Unit.METRIC)

            // Add waypoints if provided
            if (waypoints.isNotEmpty()) {
                val waypointArray = waypoints.map {
                    com.google.maps.model.LatLng(it.latitude, it.longitude)
                }.toTypedArray()
                directionsApiRequest.waypoints(*waypointArray)
            }

            val result = directionsApiRequest.await()
            Log.d("DirectionsService", "Directions API response received")
            result
        } catch (e: Exception) {
            Log.e("DirectionsService", "Error getting directions: ${e.message}", e)
            null
        }
    }

    suspend fun getBusRouteDirections(
        stops: List<LatLng>
    ): List<LatLng> = withContext(Dispatchers.IO) {
        if (stops.size < 2) return@withContext stops

        val allRoutePoints = mutableListOf<LatLng>()

        try {
            // Get directions between consecutive stops
            for (i in 0 until stops.size - 1) {
                val origin = stops[i]
                val destination = stops[i + 1]

                val directionsResult = getDirections(origin, destination)

                directionsResult?.routes?.firstOrNull()?.legs?.forEach { leg ->
                    leg.steps?.forEach { step ->
                        val polylinePoints = decodePolyline(step.polyline.encodedPath)
                        allRoutePoints.addAll(polylinePoints)
                    }
                }

                // Add a small delay to avoid hitting rate limits
                kotlinx.coroutines.delay(100)
            }

            Log.d("DirectionsService", "Generated route with ${allRoutePoints.size} points")
            allRoutePoints
        } catch (e: Exception) {
            Log.e("DirectionsService", "Error generating bus route: ${e.message}", e)
            // Fallback to original points if directions fail
            stops
        }
    }

    /**
     * Decode Google polyline algorithm
     * Converts encoded polyline string to list of LatLng points
     */
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(latLng)
        }

        return poly
    }
}
