package com.ayaan.incompletion.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleMapsModule {

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        // Initialize Places if not already initialized
        if (!Places.isInitialized()) {
            Places.initialize(context, com.ayaan.incompletion.BuildConfig.GMAPS_API_KEY)
        }
        return Places.createClient(context)
    }
}
