package com.ayaan.incompletion.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ayaan.incompletion.data.preferences.LanguagePreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing DataStore dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private const val PREFERENCES_NAME = "incompletion_preferences"

    // Extension property for DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME
    )

    /**
     * Provides DataStore<Preferences> instance
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * Provides LanguagePreferencesRepository instance
     */
    @Provides
    @Singleton
    fun provideLanguagePreferencesRepository(
        dataStore: DataStore<Preferences>
    ): LanguagePreferencesRepository {
        return LanguagePreferencesRepository(dataStore)
    }
}
