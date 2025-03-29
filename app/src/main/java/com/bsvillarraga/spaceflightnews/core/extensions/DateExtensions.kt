package com.bsvillarraga.spaceflightnews.core.extensions

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

        val date = inputFormat.parse(this)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}