package com.ltei.lauimagepicker

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import java8.util.concurrent.CompletableFuture


sealed class ImagePickerManager(val startImagePickerActivity: () -> Unit) {

    private var mCurrentFuture: CompletableFuture<Uri>? = null
    private var mCropImageUri: Uri? = null

    protected abstract fun shouldRequirePermissions(imageUri: Uri): Boolean
    protected abstract fun requestPermissions(permissions: Array<String>, requestCode: Int)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val imageUri = result.uri
                if (shouldRequirePermissions(imageUri)) {
                    mCropImageUri = imageUri
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                    )
                } else {
                    if (mCurrentFuture == null) {
                        IllegalStateException().printStackTrace()
                    } else {
                        mCurrentFuture!!.complete(imageUri)
                        mCurrentFuture = null
                        mCropImageUri = null
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                if (mCurrentFuture == null) {
                    IllegalStateException().printStackTrace()
                } else {
                    mCurrentFuture!!.completeExceptionally(IllegalStateException(error.localizedMessage))
                    mCurrentFuture = null
                    mCropImageUri = null
                }
            }
            true
        } else {
            false
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray): Boolean {
        return when (requestCode) {
            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startImagePickerActivity.invoke()
                } else {
                    if (mCurrentFuture == null) {
                        IllegalStateException().printStackTrace()
                    } else {
                        mCurrentFuture!!.completeExceptionally(IllegalStateException("Cancelling, required permissions are not granted 1"))
                        mCurrentFuture = null
                        mCropImageUri = null
                    }
                }
                true
            }
            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE -> {
                if (mCurrentFuture == null) {
                    IllegalStateException().printStackTrace()
                } else {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mCurrentFuture!!.complete(mCropImageUri!!)
                        mCurrentFuture = null
                        mCropImageUri = null
                    } else {
                        mCurrentFuture!!.completeExceptionally(IllegalStateException("Cancelling, required permissions are not granted 2"))
                        mCurrentFuture = null
                        mCropImageUri = null
                    }
                }
                true
            }
            else -> false
        }
    }

    fun pickImageUri(): CompletableFuture<Uri> = CompletableFuture<Uri>().also {
        mCurrentFuture = it
        startImagePickerActivity.invoke()
    }

    fun pickImageBitmap(): CompletableFuture<Bitmap> = pickImageUri().thenApply {
        BitmapFactory.decodeFile(it.path!!)
    }

    companion object {
        fun forActivity(activity: Activity, startImagePickerActivity: () -> Unit): ImagePickerManager =
            ForActivity(activity, startImagePickerActivity)

        fun forFragment(fragment: Fragment, startImagePickerActivity: () -> Unit): ImagePickerManager =
            ForFragment(fragment, startImagePickerActivity)
    }

    private class ForActivity(val activity: Activity, startImagePickerActivity: () -> Unit) :
        ImagePickerManager(startImagePickerActivity) {
        override fun shouldRequirePermissions(imageUri: Uri): Boolean {
            return CropImage.isReadExternalStoragePermissionsRequired(activity, imageUri)
        }

        override fun requestPermissions(permissions: Array<String>, requestCode: Int) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    private class ForFragment(val fragment: Fragment, startImagePickerActivity: () -> Unit) :
        ImagePickerManager(startImagePickerActivity) {
        override fun shouldRequirePermissions(imageUri: Uri): Boolean {
            return CropImage.isReadExternalStoragePermissionsRequired(fragment.requireContext(), imageUri)
        }

        override fun requestPermissions(permissions: Array<String>, requestCode: Int) {
            fragment.requestPermissions(permissions, requestCode)
        }
    }

}