package com.ayaan.incompletion.data.model.getRoute

import com.google.gson.annotations.SerializedName

data class Routes(
    @SerializedName("_id") val id: String,
    val routeNumber: String,
    val routeType: String,
    val stops: List<Stop>
)