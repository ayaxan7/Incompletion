package com.ayaan.incompletion.data.repository

import com.ayaan.incompletion.data.local.dao.FavoriteRouteDao
import com.ayaan.incompletion.data.local.entity.FavoriteRoute
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRouteRepository @Inject constructor(
    private val favoriteRouteDao: FavoriteRouteDao
) {

    fun getAllFavoriteRoutes(): Flow<List<FavoriteRoute>> {
        return favoriteRouteDao.getAllFavoriteRoutes()
    }

    suspend fun addFavoriteRoute(
        sourceName: String,
        sourceId: String,
        destinationName: String,
        destinationId: String
    ) {
        val favoriteRoute = FavoriteRoute(
            sourceName = sourceName,
            sourceId = sourceId,
            destinationName = destinationName,
            destinationId = destinationId
        )
        favoriteRouteDao.insertFavoriteRoute(favoriteRoute)
    }

    suspend fun removeFavoriteRoute(favoriteRoute: FavoriteRoute) {
        favoriteRouteDao.deleteFavoriteRoute(favoriteRoute)
    }

    suspend fun removeFavoriteRouteById(id: Long) {
        favoriteRouteDao.deleteFavoriteRouteById(id)
    }

    suspend fun isFavoriteRoute(sourceId: String, destinationId: String): Boolean {
        return favoriteRouteDao.isFavoriteRoute(sourceId, destinationId)
    }
}
