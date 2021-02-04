package com.malibin.morse.data.repository

import com.malibin.morse.data.entity.Account
import com.malibin.morse.data.service.MorseService
import com.malibin.morse.data.service.params.CheckEmailParams
import com.malibin.morse.data.service.params.CheckNicknameParams
import com.malibin.morse.data.service.params.LoginParams
import com.malibin.morse.data.service.params.SignUpParams
import com.malibin.morse.data.service.params.VerifyEmailParams
import com.malibin.morse.data.service.response.LoginResponse
import com.malibin.morse.data.source.AuthLocalDataSource
import com.malibin.morse.presentation.utils.printLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created By Malibin
 * on 1월 22, 2021
 */

class AuthRepository @Inject constructor(
    private val morseService: MorseService,
    private val localDataSource: AuthLocalDataSource,
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
        localDataSource.saveTokens(response.data.accessToken, response.data.refreshToken)
        return@withContext true
    }

    suspend fun isSavedTokenValid(): Boolean = withContext(Dispatchers.IO) {
        val savedToken = localDataSource.getAccessToken() ?: return@withContext false
        return@withContext morseService.checkValidToken(savedToken).isSuccessful
    }

    suspend fun refreshTokens(): LoginResponse? = withContext(Dispatchers.IO) {
        val refreshToken = getRefreshToken() ?: return@withContext null
        val response = morseService.refreshToken(refreshToken)
        localDataSource.saveTokens(response.data.accessToken, response.data.refreshToken)
        return@withContext response.data
    }

    suspend fun getAccessToken(): String? {
        return localDataSource.getAccessToken()
    }

    suspend fun getRefreshToken(): String? {
        return localDataSource.getRefreshToken().also { printLog("refresh : $it") }
    }

    suspend fun deleteTokens() {
        localDataSource.deleteTokens()
    }

    suspend fun getAccount(): Account = withContext(Dispatchers.IO) {
        return@withContext morseService.getAccount().data.toAccount()
    }

    suspend fun saveEmail(nickname: String) {
        localDataSource.saveEmail(nickname)
    }

    suspend fun getEmail(): String? {
        return localDataSource.getEmail()
    }

    suspend fun deleteEmail() {
        localDataSource.deleteEmail()
    }
}
