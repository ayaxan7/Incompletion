package com.ayaan.incompletion.di

import android.content.Context
import androidx.room.Room
import com.ayaan.incompletion.data.local.dao.FavoriteRouteDao
import com.ayaan.incompletion.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "incompletion_database"
        ).build()
    }

    @Provides
    fun provideFavoriteRouteDao(database: AppDatabase): FavoriteRouteDao {
        return database.favoriteRouteDao()
    }
}
