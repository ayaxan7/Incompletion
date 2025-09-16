package com.ayaan.incompletion.data.api

import com.ayaan.incompletion.data.model.RouteRequest
import com.ayaan.incompletion.data.model.RouteResponse
import com.ayaan.incompletion.data.model.NearestBusStopRequest
import com.ayaan.incompletion.data.model.NearestBusStopResponse
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
    ): Response<Routes> // Changed from List<Routes> to Routes since API returns single object
    @GET("test")
    suspend fun testEndpoint(): Response<String>
    @POST("getNearestBustops")
    suspend fun getNearestBusStops(
        @Body request: NearestBusStopRequest
    ): Response<NearestBusStopResponse>
}