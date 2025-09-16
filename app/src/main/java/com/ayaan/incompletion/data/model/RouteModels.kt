package com.ayaan.incompletion.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for getting common routes between two bus stops
 * Used with POST /getCommonRoutes endpoint
 */
data class RouteRequest(
    @SerializedName("sourceId")
    val sourceId: String,
    @SerializedName("destinationId")
    val destinationId: String
)

/**
 * Response model for common routes between two bus stops
 * Contains list of route numbers (e.g., ["210A", "15G"])
 */
data class RouteResponse(
    @SerializedName("commonRoutes")
    val commonRoutes: List<String>
)

/**
 * Generic API error response model
 */
data class ApiError(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String? = null
)

/**
 * Data class for detailed route information used in RouteCard component
 * Contains route details including direction and stop indices
 */
data class RouteInfo(
    val routeNumber: String,
    val direction: String = "forward", // "forward" or "backward"
    val startStopIndex: Int,
    val destinationStopIndex: Int,
    val estimatedTime: String? = null, // Optional estimated travel time
    val distance: String? = null // Optional distance
)

/**
 * Enhanced route data with additional metadata
 * Used for more detailed route information display
 */
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

/**
 * Time preference for route search
 */
enum class TimePreference {
    FASTEST,
    SHORTEST_DISTANCE,
    FEWEST_TRANSFERS,
    MOST_FREQUENT
}

/**
 * Route planning result with multiple options
 */
data class RoutePlanningResult(
    val sourceStopId: String,
    val destinationStopId: String,
    val routes: List<RouteOption>,
    val searchTimestamp: Long = System.currentTimeMillis()
)

/**
 * Individual route option in route planning
 */
data class RouteOption(
    val routes: List<RouteSegment>,
    val totalTime: String,
    val totalDistance: String,
    val transfers: Int,
    val totalFare: Double? = null
)

/**
 * Route segment for multi-route journeys
 */
data class RouteSegment(
    val routeNumber: String,
    val direction: String,
    val startStop: BusStopInfo,
    val endStop: BusStopInfo,
    val estimatedTime: String,
    val distance: String
)
