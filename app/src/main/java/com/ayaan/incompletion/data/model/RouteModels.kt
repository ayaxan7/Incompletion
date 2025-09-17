package com.ayaan.incompletion.data.model

import com.google.gson.annotations.SerializedName

data class RouteRequest(
    @SerializedName("sourceId")
    val sourceId: String,
    @SerializedName("destinationId")
    val destinationId: String
)

data class RouteResponse(
    @SerializedName("commonRoutes")
    val commonRoutes: List<String>
)

data class ApiError(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String? = null
)

data class RouteInfo(
    val routeNumber: String,
    val direction: String = "forward", // "forward" or "backward"
    val startStopIndex: Int,
    val destinationStopIndex: Int,
    val estimatedTime: String? = null, // Optional estimated travel time
    val distance: String? = null // Optional distance
)

data class DetailedRoute(
    val routeNumber: String,
    val routeName: String? = null,
    val direction: String = "forward",
    val startStop: BusStopInfo,
    val destinationStop: BusStopInfo,
    val intermediateStops: List<BusStopInfo> = emptyList(),
    val estimatedTime: String? = null,
    val fare: Double? = null,
    val frequency: String? = null // e.g., "Every 15 minutes"
)

/**
 * Bus stop information for detailed route display
 */
data class BusStopInfo(
    val stopId: String,
    val name: String,
    val index: Int,
    val coordinates: Coordinates? = null
)

/**
 * Coordinates for bus stop locations
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

/**
 * Route search filters for advanced route finding
 */
data class RouteSearchFilters(
    val maxTransfers: Int = 2,
    val preferredRoutes: List<String> = emptyList(),
    val avoidRoutes: List<String> = emptyList(),
    val timePreference: TimePreference = TimePreference.FASTEST
)

enum class TimePreference {
    FASTEST,
}

data class RoutePlanningResult(
    val sourceStopId: String,
    val destinationStopId: String,
    val routes: List<RouteOption>,
    val searchTimestamp: Long = System.currentTimeMillis()
)

data class RouteOption(
    val routes: List<RouteSegment>,
    val totalTime: String,
    val totalDistance: String,
    val transfers: Int,
    val totalFare: Double? = null
)

data class RouteSegment(
    val routeNumber: String,
    val direction: String,
    val startStop: BusStopInfo,
    val endStop: BusStopInfo,
    val estimatedTime: String,
    val distance: String
)
