package com.ayaan.incompletion.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.ayaan.incompletion.data.local.dao.FavoriteRouteDao
import com.ayaan.incompletion.data.local.entity.FavoriteRoute

@Database(
    entities = [FavoriteRoute::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteRouteDao(): FavoriteRouteDao
}
