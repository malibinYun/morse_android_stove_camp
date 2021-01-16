package com.malibin.morse.data.service

import com.malibin.morse.data.service.params.ChangeNicknameParams
import com.malibin.morse.data.service.params.ChangePasswordParams
import com.malibin.morse.data.service.params.CheckEmailParams
import com.malibin.morse.data.service.params.VerifyEmailParams
import com.malibin.morse.data.service.params.CheckNicknameParams
import com.malibin.morse.data.service.params.SignUpParams
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

interface MorseService {

    @POST("auth/check/nickname")
    suspend fun checkNickname(
        @Body params: CheckNicknameParams,
    )

    @POST("auth/check/email")
    suspend fun checkEmail(
        @Body params: CheckEmailParams,
    )

    @POST("auth/check/email-code")
    suspend fun verifyEmail(
        @Body params: VerifyEmailParams,
    )

    @POST("auth/signup")
    suspend fun signUp(
        @Body params: SignUpParams,
    )

    @POST("auth/login")
    suspend fun login(
        @Body params: SignUpParams,
    )

    @POST("auth/refresh")
    suspend fun login(
        @Header("RefreshToken") token: String,
    )

    @GET("auth/find-pw")
    suspend fun getTempPassword(
        @Header("RefreshToken") token: String,
    )

    @POST("auth/change/pw")
    suspend fun changePassword(
        @Header("RefreshToken") token: String,
        @Body params: ChangePasswordParams,
    )

    @POST("auth/change/nickname")
    suspend fun changeNickname(
        @Header("token") token: String,
        @Body params: ChangeNicknameParams,
    )

    @GET("auth/withdraw")
    suspend fun deleteAccount(
        @Header("token") token: String,
    )

    companion object {
        const val BASE_URL = "http://downsups.onstove.com:8005"
    }
}
