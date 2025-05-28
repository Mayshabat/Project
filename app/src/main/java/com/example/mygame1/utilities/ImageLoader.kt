package com.example.mygame1.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mygame1.R

object ImageLoader {

    fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        placeholder: Int = R.drawable.unavailable_photo
    ) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }

    fun loadDrawable(
        context: Context,
        drawable: Drawable,
        imageView: ImageView,
        placeholder: Int = R.drawable.unavailable_photo
    ) {
        Glide.with(context)
            .load(drawable)
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }

    fun loadResource(
        context: Context,
        resId: Int,
        imageView: ImageView,
        placeholder: Int = R.drawable.unavailable_photo
    ) {
        Glide.with(context)
            .load(resId)
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }
}
