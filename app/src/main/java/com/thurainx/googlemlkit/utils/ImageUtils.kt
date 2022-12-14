package com.thurainx.googlemlkit.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.scale
import com.bumptech.glide.Glide



fun Bitmap.scaleToRatio(ratio: Double) : Bitmap{
    return this.scale((this.width * ratio).toInt(), (this.height * ratio).toInt())
}

fun Uri.loadBitmapFromUri(context: Context) : Bitmap{
    return Glide.with(context)
        .asBitmap()
        .load(this)
        .submit()
        .get()
}