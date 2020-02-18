package com.ltei.lauutils

import android.app.Activity
import android.content.Intent

private typealias Callback = (resultCode: Int, data: Intent?) -> Unit

class ActivityLauncher(private val activity: Activity) {

    private val callbacks = mutableMapOf<Int, Callback>()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val callback = callbacks.remove(requestCode)
        if (callback != null) {
            callback.invoke(resultCode, data)
            return true
        }
        return false
    }

    fun startActivity(clazz: Class<*>) {
        val intent = Intent(activity, clazz)
        activity.startActivity(intent)
    }

    fun startActivityForResult(
        requestCode: Int,
        clazz: Class<*>,
        callback: Callback
    ) {
        val intent = Intent(activity, clazz)
        callbacks[requestCode] = callback
        activity.startActivityForResult(intent, requestCode)
    }

}