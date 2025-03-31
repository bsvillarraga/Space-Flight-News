package com.bsvillarraga.spaceflightnews.core.extensions

import android.content.Context
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable

/**
 * Función de extensión para cargar una imagen desde un recurso local.
 * */
fun ImageView.toResourceGlide(context: Context, image: Int) {
    val drawable = ResourcesCompat.getDrawable(context.resources, image, null)

    val requestBuilder = if (drawable is GifDrawable) {
        Glide.with(context).asGif()
    } else {
        Glide.with(context).asDrawable()
    }

    requestBuilder
        .load(image)
        .centerCrop()
        .override(this.width, this.height)
        .into(this)
}

/**
 * Función de extensión para cargar una imagen desde una URL.
 * */
fun ImageView.toNetworkGlide(context: Context, image: String) {
    Glide.with(context)
        .load(image)
        .centerCrop()
        .into(this)
}

