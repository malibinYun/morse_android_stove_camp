package com.malibin.morse.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): Flow<T?> {
    return this.data.map { it[key] }
}
