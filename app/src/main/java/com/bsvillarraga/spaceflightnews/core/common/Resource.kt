package com.bsvillarraga.spaceflightnews.core.common

/**
 * Clase sellada para representar el estado de una operación.
 * @param T El tipo de datos que contiene el recurso.
 * */
sealed class Resource<out T> {
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Error<out T>(val code: String?, val msg: String, val error: Throwable? = null) : Resource<T>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()
}

