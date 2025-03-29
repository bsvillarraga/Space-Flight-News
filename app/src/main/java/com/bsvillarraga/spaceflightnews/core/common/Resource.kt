package com.bsvillarraga.spaceflightnews.core.common

sealed class Resource<out T> {
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Error<out T>(val code: String?, val msg: String, val error: Throwable? = null) : Resource<T>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()
}

