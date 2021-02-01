package com.malibin.morse.data.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import com.malibin.morse.data.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created By Malibin
 * on 2월 01, 2021
 */

class AuthLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        return@withContext "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb21lQG5hdmVyLmNvbSIsIm5pY2tuYW1lIjoi66qo66mUIiwiZXhwIjoxNjExOTIyMzE3LCJpYXQiOjE2MTE5MTg3MTd9.fjQ3qWuBff6dm-oJA-jwQ1CSC8-8Bv2DU9JJFzLT8Lg"
//        return@withContext dataStore[KEY_ACCESS_TOKEN].first()
    }

    suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        return@withContext dataStore[KEY_REFRESH_TOKEN].first()
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) = withContext(Dispatchers.IO) {
        dataStore.edit {
            it[KEY_ACCESS_TOKEN] = accessToken
            it[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    companion object {
        private val KEY_ACCESS_TOKEN = preferencesKey<String>("KEY_ACCESS_TOKEN")
        private val KEY_REFRESH_TOKEN = preferencesKey<String>("KEY_REFRESH_TOKEN")
    }
}