package com.ltei.lauutils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object LAssets {

    fun loadBitmap(context: Context, fileName: String): Bitmap? {
        return BitmapFactory.decodeStream(context.assets.open(fileName))
    }

    fun loadResizedBitmap(context: Context, fileName: String, width: Int, height: Int): Bitmap? {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(context.assets.open(fileName), null, this)
            inSampleSize = calculateInSampleSize(this, width, height)
            inJustDecodeBounds = false
            BitmapFactory.decodeStream(context.assets.open(fileName), null, this)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}