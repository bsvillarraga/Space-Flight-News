package com.bsvillarraga.spaceflightnews.data.remote

import com.bsvillarraga.spaceflightnews.core.network.NetworkHelper
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ApiHelper @Inject constructor(
    private val networkHelper: NetworkHelper
) {
    suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResponse<T> {
        return try {
            if (networkHelper.isNetworkAvailable()) {
                val response = call.invoke()
                ApiResponse.create(response)
            } else {
                ApiErrorResponse("5", "Sin conexión a internet")
            }
        } catch (e: SocketTimeoutException) {
            ApiResponse.create(e)
        } catch (e: HttpException) {
            ApiResponse.create(e)
        } catch (e: IOException) {
            ApiResponse.create(e)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
    }
}