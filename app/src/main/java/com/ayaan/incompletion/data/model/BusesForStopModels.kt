package com.ayaan.incompletion.data.model

import com.google.gson.annotations.SerializedName

data class BusesForStopRequest(
    @SerializedName("stopId")
    val stopId: String
)

data class BusForStop(
    @SerializedName("busId")
    val busId: String,
    @SerializedName("routeNo")
    val routeNo: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("crowdDensity")
    val crowdDensity: String
)

typealias BusesForStopResponse = List<BusForStop>
