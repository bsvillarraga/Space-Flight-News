package com.bsvillarraga.spaceflightnews.data.remote

import android.accounts.NetworkErrorException
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return when (error) {
                is SocketTimeoutException -> {
                    ApiErrorResponse("408", "Timeout", error)
                }

                is HttpException -> {
                    ApiErrorResponse(error.code().toString(), "Error de http", error)
                }

                is NetworkErrorException -> {
                    ApiErrorResponse("2", "Error de conexión", error)
                }

                else -> {
                    ApiErrorResponse("4", "error desconocido", error)
                }
            }
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    ApiSuccessResponse(body)
                } else {
                    ApiErrorResponse(code = "1", msg = "La respuesta del body es null")
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                ApiErrorResponse(code = response.code().toString(), msg = errorBody)
            }
        }
    }
}

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()

data class ApiErrorResponse<T>(
    val code: String? = null,
    val msg: String,
    val error: Throwable? = null
) : ApiResponse<T>()

