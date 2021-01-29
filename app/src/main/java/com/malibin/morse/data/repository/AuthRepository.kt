package com.malibin.morse.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import com.malibin.morse.data.get
import com.malibin.morse.data.service.MorseService
import com.malibin.morse.data.service.params.CheckEmailParams
import com.malibin.morse.data.service.params.CheckNicknameParams
import com.malibin.morse.data.service.params.LoginParams
import com.malibin.morse.data.service.params.SignUpParams
import com.malibin.morse.data.service.params.VerifyEmailParams
import com.malibin.morse.data.service.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created By Malibin
 * on 1월 22, 2021
 */

class AuthRepository @Inject constructor(
    private val morseService: MorseService,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun checkNickname(nickname: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext morseService.checkNickname(CheckNicknameParams(nickname)).isSuccessful
    }

    suspend fun checkEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext morseService.checkEmail(CheckEmailParams(email)).isSuccessful
    }

    suspend fun verifyEmail(
        email: String,
        verifyCode: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext morseService
            .verifyEmail(VerifyEmailParams(email, verifyCode))
            .isSuccessful
    }

    suspend fun signUp(
        email: String,
        password: String,
        nickname: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext morseService
            .signUp(SignUpParams(email, password, nickname))
            .isSuccessful
    }

    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val response = morseService.login(LoginParams(email, password))
        saveTokens(response.data)
        return@withContext true
    }

    private suspend fun saveTokens(loginResponse: LoginResponse) = withContext(Dispatchers.IO) {
        dataStore.edit {
            it[KEY_ACCESS_TOKEN] = loginResponse.accessToken
            it[KEY_REFRESH_TOKEN] = loginResponse.refreshToken
        }
    }

    suspend fun isSavedTokenValid(): Boolean = withContext(Dispatchers.IO) {
        val savedToken = getAccessToken() ?: return@withContext false
        morseService.checkValidToken(savedToken)
        return@withContext true
    }

    suspend fun refreshTokens(): LoginResponse? = withContext(Dispatchers.IO) {
        val refreshToken = getRefreshToken() ?: return@withContext null
        val response = morseService.refreshToken(refreshToken)
        saveTokens(response.data)
        return@withContext response.data
    }

    suspend fun getAccessToken(): String? {
        return dataStore[KEY_ACCESS_TOKEN].first()
    }

    private suspend fun getRefreshToken(): String? {
        return dataStore[KEY_REFRESH_TOKEN].first()
    }

    companion object {
        private val KEY_ACCESS_TOKEN = preferencesKey<String>("KEY_ACCESS_TOKEN")
        private val KEY_REFRESH_TOKEN = preferencesKey<String>("KEY_REFRESH_TOKEN")
    }
}
