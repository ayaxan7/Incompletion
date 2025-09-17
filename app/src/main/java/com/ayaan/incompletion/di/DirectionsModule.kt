package com.ayaan.incompletion.di

import com.ayaan.incompletion.BuildConfig
import com.ayaan.incompletion.data.directions.DirectionsService
import com.google.maps.GeoApiContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DirectionsModule {

    @Provides
    @Singleton
    fun provideDirectionsService(): DirectionsService {
        return DirectionsService()
    }

//    @Provides
//    @Singleton
//    fun provideGeoApiContext(): GeoApiContext {
//        return GeoApiContext.Builder()
//            .apiKey(BuildConfig.GMAPS_API_KEY)
//            .build()
//    }

}
