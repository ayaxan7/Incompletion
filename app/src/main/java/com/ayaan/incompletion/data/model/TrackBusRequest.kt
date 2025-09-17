package com.ayaan.incompletion.data.model

data class TrackBusRequest(
    val busID: String,
    val busPositionLat: Double,
    val busPositionLon: Double,
    val nextStopID: String,
    val nextStopLat: Double,
    val nextStopLon: Double,
    val routeNo: String
)