@file:Suppress("DEPRECATION")

package com.ltei.lauutils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.Html


object LIncentiveUtils {

    fun launchRate(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + LApp.getPackageName(context)))
        context.startActivity(intent)
    }

    fun launchFeedback(context: Context, mail: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, "Feedback !")
                .putExtra(Intent.EXTRA_TEXT, "")
                .putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(mail))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_FROM_BACKGROUND)
                .setData(Uri.parse("mailto:$mail"))
        context.startActivity(intent)
    }

    fun launchShare(context: Context, title: String, iconResId: Int) {
        var message = "<br><br>Download " + LApp.getName(context) + " on the Google Play Store now :"
        message += ("<br><a href=\"https://market.android.com/details?id=" + LApp.getPackageName(context) + "\">https://market.android.com/details?id=" + LApp.getPackageName(context) + "</a>")
        message = Html.fromHtml(message).toString()

        val bitmap = BitmapFactory.decodeResource(context.resources, iconResId)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, LApp.getName(context), null)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_SUBJECT, LApp.getName(context))
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
        val intent2 = Intent.createChooser(intent, title)
        context.startActivity(intent2)
    }

    /*fun launchWebsite(context: Context) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_confidentiality_policy)))
        context.startActivity(browserIntent)
    }*/

    /*fun shouldShowRateApp(context: Context): Boolean {

        // Do not show when showing version update
        if (isFirstInstall(context)) {
            return false
        }

        // User asked for do not show again, respect his choice
        if (SharedPrefs.getBoolean(context, PREFS_DONT_SHOW_RATE_AGAIN)) {
            return false
        }

        // Increment launch counter
        var launch_count = SharedPrefs.getInt(context, PREFS_LAUNCH_COUNT)
        launch_count = if (launch_count <= 0) 1 else launch_count + 1
        SharedPrefs.editInt(context, PREFS_LAUNCH_COUNT, launch_count)

        // Get date of first launch
        var date_firstLaunch = SharedPrefs.getLong(context, PREFS_DATE_FIRST_LAUNCH)
        if (date_firstLaunch <= 0) {
            date_firstLaunch = System.currentTimeMillis()
            SharedPrefs.editLong(context, PREFS_DATE_FIRST_LAUNCH, date_firstLaunch)
        }

        // Wait at least n app launches before opening
        return if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            // Wait at least n days before opening
            if (System.currentTimeMillis() >= date_firstLaunch!! + HOURS_UNTIL_PROMPT * 60 * 60 * 1000) {
                true
            } else {
                false
            }
        } else {
            false
        }
    }*/

}