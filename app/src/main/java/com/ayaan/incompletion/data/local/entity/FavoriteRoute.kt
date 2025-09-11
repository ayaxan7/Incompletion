package com.ayaan.incompletion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_routes")
data class FavoriteRoute(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceName: String,
    val sourceId: String,
    val destinationName: String,
    val destinationId: String,
    val createdAt: Long = System.currentTimeMillis()
)