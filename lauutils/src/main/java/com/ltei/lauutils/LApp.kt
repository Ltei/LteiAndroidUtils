package com.ltei.lauutils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object LApp {

    fun getVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            "unknown"
        }

    }

    fun getName(context: Context): String {
        val packageManager = context.packageManager
        val applicationInfo = try {
            packageManager.getApplicationInfo(context.applicationInfo.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        } ?: return "Unknown"
        return packageManager.getApplicationLabel(applicationInfo) as String
    }

    fun getPackageName(context: Context): String {
        return context.packageName
    }

    fun getGooglePlaystoreUrl(context: Context): String {
        return "https://play.google.com/store/apps/details?id=${getPackageName(context)}"
    }

    fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

}
