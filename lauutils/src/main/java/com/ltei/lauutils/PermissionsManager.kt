package com.ltei.lauutils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionsManager(
    private val activity: Activity,
    initialRequestCode: Int
) {

    private val mAwaitingPermissions = mutableMapOf<Int, AwaitingPermissions>()
    private var mNextRequestCode = initialRequestCode

    fun assertPermission(permission: String, callback: (Boolean) -> Unit) {
        return assertPermissions(arrayOf(permission), callback)
    }

    fun assertPermissions(permissions: Array<String>, callback: (Boolean) -> Unit) {
        if (areGranted(*permissions)) callback.invoke(true)

        val awaiting = AwaitingPermissions(permissions, callback)
        mAwaitingPermissions[mNextRequestCode] = awaiting
        ActivityCompat.requestPermissions(activity, permissions, mNextRequestCode)
        mNextRequestCode += 1
    }

    fun onRequestPermissionsResult(requestCode: Int): Boolean {
        val awaiting = mAwaitingPermissions.remove(requestCode)
        return if (awaiting != null) {
            awaiting.callback.invoke(areGranted(*awaiting.permissions))
            true
        } else {
            false
        }
    }

    fun areGranted(vararg permissions: String) = areGranted(activity, *permissions)

    companion object {
        fun areGranted(context: Context, vararg permissions: String): Boolean = permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        fun areGranted(context: Context, permissions: List<String>): Boolean = permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private class AwaitingPermissions(val permissions: Array<String>, val callback: (Boolean) -> Unit)

}