package com.bsvillarraga.spaceflightnews.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

class NetworkHelper @Inject constructor(private val context: Context) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = connectivityManager.activeNetwork ?: return false

        val activeNetworkInfo =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return activeNetworkInfo.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}