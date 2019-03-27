package com.ltei.lauutils

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.ltei.ljubase.Result
import java8.util.concurrent.CompletableFuture

class PermissionsManager(
        private val activity: Activity,
        initialRequestCode: Int
) {

    private class AwaitingPermissions(
            val permissions: Array<String>,
            val future: CompletableFuture<Result<Array<String>, IntArray>>
    )

    private val mAwaitingPermissions = mutableMapOf<Int, AwaitingPermissions>()
    private var mNextRequestCode = initialRequestCode

    fun assertPermission(permission: String): CompletableFuture<Result<Array<String>, IntArray>> {
        return assertPermissions(arrayOf(permission))
    }

    fun assertPermissions(permissions: Array<String>): CompletableFuture<Result<Array<String>, IntArray>> {
        if (areAllGranted(permissions)) {
            return CompletableFuture.completedFuture(Result.ok(permissions))
        }
        val future = CompletableFuture<Result<Array<String>, IntArray>>()
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
                awaiting.future.complete(Result.ok(awaiting.permissions))
            } else {
                awaiting.future.complete(Result.err(grantResults))
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