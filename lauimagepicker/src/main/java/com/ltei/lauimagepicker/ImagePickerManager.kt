package com.ltei.lauimagepicker

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.Fragment
import java8.util.concurrent.CompletableFuture


class ImagePickerManager(val fragment: Fragment, val startImagePickerActivity: () -> Unit) {

    private var mCurrentFuture: CompletableFuture<Uri>? = null
    private var mCropImageUri: Uri? = null

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val imageUri = result.uri
                if (CropImage.isReadExternalStoragePermissionsRequired(fragment.requireContext(), imageUri)) {
                    mCropImageUri = imageUri
                    fragment.requestPermissions(
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

    fun pickImage(): CompletableFuture<Uri> {
        return CompletableFuture<Uri>().also {
            mCurrentFuture = it
            startImagePickerActivity.invoke()
        }
    }

}