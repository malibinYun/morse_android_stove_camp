package com.malibin.morse.presentation.utils

import com.malibin.morse.data.service.response.ErrorResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import org.json.JSONObject
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Created By Malibin
 * on 1월 25, 2021
 */

inline fun HttpExceptionHandler(
    crossinline handler: (ErrorResponse) -> Unit
) = CoroutineExceptionHandler { _, t ->
    if (t is UnknownHostException) {
        handler(ErrorResponse(",", -1, t.message.orEmpty(), ""))
    }
    if (t !is HttpException) return@CoroutineExceptionHandler
    val errorBody = t.response()?.errorBody()?.string().orEmpty()
    val errorJson = JSONObject(errorBody)
    val errorResponse = ErrorResponse(
        timestamp = errorJson["timestamp"].toString(),
        status = errorJson["status"].toString().toInt(),
        message = errorJson["message"].toString(),
        path = errorJson["path"].toString(),
    )
    handler(errorResponse)
}
