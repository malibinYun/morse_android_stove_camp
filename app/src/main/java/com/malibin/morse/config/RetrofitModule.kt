package com.malibin.morse.config

import com.malibin.morse.data.service.MorseService
import com.malibin.morse.data.source.AuthLocalDataSource
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

@Module
@InstallIn(ApplicationComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideMorseService(authLocalDataSource: AuthLocalDataSource): MorseService {
        return Retrofit.Builder()
            .addConverterFactory(createGsonConverterFactory())
            .client(createOkHttpClient(authLocalDataSource))
            .baseUrl(MorseService.BASE_URL)
            .build()
            .create(MorseService::class.java)
    }

    private fun createGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    private fun createOkHttpClient(authLocalDataSource: AuthLocalDataSource): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createHttpLoggingInterceptor())
            .addNetworkInterceptor(addingTokenInterceptor(authLocalDataSource))
            .build()
    }

    private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { printLog(it) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    private fun addingTokenInterceptor(
        authLocalDataSource: AuthLocalDataSource
    ) = Interceptor {
        val requestWithHeader = it.request().newBuilder()
        runBlocking { authLocalDataSource.getAccessToken() }
            ?.run { requestWithHeader.addHeader("token", this) }
        return@Interceptor it.proceed(requestWithHeader.build())
    }

    private fun printLog(message: String) {
        try {
            JSONObject(message)
            Logger.t("MalibinHTTP").json(message)
        } catch (e: Exception) {
            Logger.t("MalibinHTTP").d(message)
        }
    }
}
