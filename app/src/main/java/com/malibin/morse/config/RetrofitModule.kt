package com.malibin.morse.config

import com.malibin.morse.data.service.MorseService
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

@Module
@InstallIn(ApplicationComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideMorseService(): MorseService {
        return Retrofit.Builder()
            .addConverterFactory(createGsonConverterFactory())
            .client(createOkHttpClient())
            .baseUrl(MorseService.BASE_URL)
            .build()
            .create(MorseService::class.java)
    }

    private fun createGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createHttpLoggingInterceptor())
            .build()
    }

    private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { printLog(it) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }
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
