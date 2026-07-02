package com.wan.android.library.mvvm

import com.wan.android.library.network.HttpResult
import com.wan.android.library.network.exception.ApiException
import kotlinx.coroutines.coroutineScope

/**
 * Created by loyal
 * on 2019/4/10 9:41
 */
abstract class BaseRepository {

    suspend fun <T : Any> apiCall(call: suspend () -> HttpResult<T>): HttpResult<T> {
        return call.invoke()
    }

    suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            // An exception was thrown when calling the API so we're converting this to an IOException
            Result.failure(ApiException(e.message))
        }
    }

    suspend fun <T : Any> executeResponse(response: HttpResult<T>): Result<T> {
        return coroutineScope {
            if (response.success()) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException(response.errorCode, response.errorMsg))
            }
        }
    }

}