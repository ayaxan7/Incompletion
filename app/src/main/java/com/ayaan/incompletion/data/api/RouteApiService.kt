package com.ayaan.incompletion.data.api

import com.ayaan.incompletion.data.model.RouteRequest
import com.ayaan.incompletion.data.model.RouteResponse
import com.ayaan.incompletion.data.model.NearestBusStopRequest
import com.ayaan.incompletion.data.model.NearestBusStopResponse
import com.ayaan.incompletion.data.model.TrackBusRequest
import com.ayaan.incompletion.data.model.TrackBusResponse
import com.ayaan.incompletion.data.model.BusesForStopRequest
import com.ayaan.incompletion.data.model.BusesForStopResponse
import com.ayaan.incompletion.data.model.getRoute.Routes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RouteApiService {
    @POST("getCommonRoutes")
    suspend fun getCommonRoutes(
        @Body request: RouteRequest
    ): Response<RouteResponse>
    @GET("getRoute")
    suspend fun getRoutes(
        @Query("RouteId") routeNumber: String,
    ): Response<Routes>
    @GET("test")
    suspend fun testEndpoint(): Response<String>
    @POST("getNearestBustops")
    suspend fun getNearestBusStops(
        @Body request: NearestBusStopRequest
    ): Response<NearestBusStopResponse>
    @POST("trackBus")
    suspend fun trackBus(
        @Body request: TrackBusRequest
    ): Response<TrackBusResponse>

    @POST("getBusesForStop")
    suspend fun getBusesForStop(
        @Body request: BusesForStopRequest
    ): Response<BusesForStopResponse>
}