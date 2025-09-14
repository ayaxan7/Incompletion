package com.ayaan.incompletion.data.api

import com.ayaan.incompletion.data.model.RouteRequest
import com.ayaan.incompletion.data.model.RouteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RouteApiService {

    @POST("getCommonRoutes")
    suspend fun getCommonRoutes(
        @Body request: RouteRequest
    ): Response<RouteResponse>
}
