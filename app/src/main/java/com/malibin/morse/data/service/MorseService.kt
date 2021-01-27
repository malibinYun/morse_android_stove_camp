package com.malibin.morse.data.service

import com.malibin.morse.data.entity.Room
import com.malibin.morse.data.service.params.ChangeNicknameParams
import com.malibin.morse.data.service.params.ChangePasswordParams
import com.malibin.morse.data.service.params.CheckEmailParams
import com.malibin.morse.data.service.params.VerifyEmailParams
import com.malibin.morse.data.service.params.CheckNicknameParams
import com.malibin.morse.data.service.params.LoginParams
import com.malibin.morse.data.service.params.SignUpParams
import com.malibin.morse.data.service.response.BaseResponse
import com.malibin.morse.data.service.response.LoginResponse
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
        @Body params: LoginParams,
    ): BaseResponse<LoginResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Header("RefreshToken") token: String,
    ): BaseResponse<LoginResponse>

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

    @POST("live/search-all")
    suspend fun getAllRooms(): BaseResponse<List<Room>>

    companion object {
        const val BASE_URL = "http://downsups.onstove.com:8005"
    }
}
