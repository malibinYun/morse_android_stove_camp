package com.malibin.morse.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * Created By Malibin
 * on 1월 22, 2021
 */

@Module
@InstallIn(ApplicationComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.createDataStore("morseDataStore")
    }
}