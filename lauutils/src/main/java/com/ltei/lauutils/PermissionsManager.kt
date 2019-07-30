package com.ltei.lauutils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java8.util.concurrent.CompletableFuture

class PermissionsManager(
    private val activity: Activity,
    initialRequestCode: Int
) {

    private class AwaitingPermissions(
        val permissions: Array<String>,
        val future: CompletableFuture<Array<String>>
    )

    private val mAwaitingPermissions = mutableMapOf<Int, AwaitingPermissions>()
    private var mNextRequestCode = initialRequestCode

    fun assertPermission(permission: String): CompletableFuture<Array<String>> {
        return assertPermissions(arrayOf(permission))
    }

    fun assertPermissions(permissions: Array<String>): CompletableFuture<Array<String>> {
        if (areAllGranted(permissions)) {
            return CompletableFuture.completedFuture(permissions)
        }
        val future = CompletableFuture<Array<String>>()
        val awaiting = AwaitingPermissions(permissions, future)
        mAwaitingPermissions[mNextRequestCode] = awaiting
        ActivityCompat.requestPermissions(activity, permissions, mNextRequestCode)
        mNextRequestCode += 1
        return future
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray): Boolean {
        val awaiting = mAwaitingPermissions[requestCode]
        return if (awaiting != null) {
            if (areAllGranted(awaiting.permissions)) {
                awaiting.future.complete(awaiting.permissions)
            } else {
                awaiting.future.completeExceptionally(Exception())
            }
            true
        } else {
            false
        }
    }

    private fun areAllGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}