package com.ayaan.incompletion.data.model

import com.google.gson.annotations.SerializedName

data class NearestBusStopRequest(
    @SerializedName("userLat")
    val userLat: Double,
    @SerializedName("userLon")
    val userLon: Double
)

data class NearestBusStopResponse(
    @SerializedName("stops")
    val stops: List<BusStop>
)

data class BusStop(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("stopId")
    val stopId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: BusStopLocation,
    @SerializedName("routes")
    val routes: List<BusRoute>? = null
)

data class BusStopLocation(
    @SerializedName("type")
    val type: String,
    @SerializedName("coordinates")
    val coordinates: List<Double>
)

data class BusRoute(
    @SerializedName("_id")
    val id: String,
    @SerializedName("routeNumber")
    val routeNumber: String,
    @SerializedName("index")
    val index: Int
)
