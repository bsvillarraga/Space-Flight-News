package com.bsvillarraga.spaceflightnews.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class LiveDataTestObserver<T> : Observer<T> {
    val values = mutableListOf<T>()

    override fun onChanged(value: T) {
        values.add(value)
    }
}

fun <T> LiveData<T>.testObserver(): LiveDataTestObserver<T> {
    val observer = LiveDataTestObserver<T>()
    observeForever(observer)
    return observer
}