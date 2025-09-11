package com.ayaan.incompletion.data.local.dao

import androidx.room.*
import com.ayaan.incompletion.data.local.entity.FavoriteRoute
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRouteDao {

    @Query("SELECT * FROM favorite_routes ORDER BY createdAt DESC")
    fun getAllFavoriteRoutes(): Flow<List<FavoriteRoute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRoute(favoriteRoute: FavoriteRoute)

    @Delete
    suspend fun deleteFavoriteRoute(favoriteRoute: FavoriteRoute)

    @Query("DELETE FROM favorite_routes WHERE id = :id")
    suspend fun deleteFavoriteRouteById(id: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_routes WHERE sourceId = :sourceId AND destinationId = :destinationId)")
    suspend fun isFavoriteRoute(sourceId: String, destinationId: String): Boolean
}
