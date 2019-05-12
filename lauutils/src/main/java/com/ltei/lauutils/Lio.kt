package com.ltei.lauutils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object Lio {

    fun getInternalFile(context: Context, fileDir: String, fileName: String): File {
        val contextWrapper = ContextWrapper(context)
        val directory = contextWrapper.getDir(fileDir, Context.MODE_PRIVATE)
        return File(directory, fileName)
    }

    fun saveBitmap(file: File, bitmap: Bitmap,
                   compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                   compression: Int = 50) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bitmap.compress(compressFormat, compression, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}