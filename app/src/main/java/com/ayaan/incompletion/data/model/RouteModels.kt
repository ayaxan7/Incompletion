package com.ayaan.incompletion.data.model

import com.google.gson.annotations.SerializedName

data class RouteRequest(
    @SerializedName("startStopId")
    val startStopId: String,
    @SerializedName("destinationStopId")
    val destinationStopId: String
)

data class RouteResponse(
    @SerializedName("startStop")
    val startStop: StopInfo,
    @SerializedName("destinationStop")
    val destinationStop: StopInfo,
    @SerializedName("commonRoutes")
    val commonRoutes: List<RouteInfo>,
    @SerializedName("totalRoutes")
    val totalRoutes: Int
)

data class StopInfo(
    @SerializedName("stopId")
    val stopId: String,
    @SerializedName("name")
    val name: String
)

data class RouteInfo(
    @SerializedName("routeNumber")
    val routeNumber: String,
    @SerializedName("startStopIndex")
    val startStopIndex: Int,
    @SerializedName("destinationStopIndex")
    val destinationStopIndex: Int,
    @SerializedName("direction")
    val direction: String // "forward" or "backward"
)

data class ApiError(
    @SerializedName("error")
    val error: String
)
