package com.ltei.lauutils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import androidx.fragment.app.Fragment

object LIntents {

    private const val INTENT_TYPE_PLAIN_TEXT = "plain/text"
    private const val INTENT_TYPE_HTML_TEXT = "html/text"
    private const val INTENT_TYPE_MAIL = "message/rfc822"

    fun openMail(context: Context, dest: String, title: String = "", content: String = "") {
        openMail(context, arrayOf(dest), title, content)
    }

    fun openMail(context: Context, dest: Array<String>, title: String = "", content: String = "") {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
                .setType(INTENT_TYPE_PLAIN_TEXT)
                .putExtra(Intent.EXTRA_SUBJECT, title)
                .putExtra(Intent.EXTRA_TEXT, content)
                .putExtra(android.content.Intent.EXTRA_EMAIL, dest)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_FROM_BACKGROUND)
                .setData(Uri.parse("mailto:${dest[0]}"))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openBrowser(context: Context, uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        context.startActivity(intent)
    }

    fun openGalleryPicker(fragment: Fragment, requestCode: Int) {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        fragment.startActivityForResult(pickPhoto, requestCode)
    }

    /*fun share(text: String? = null) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            text?.let {
                putExtra(Intent.EXTRA_TEXT, it)
                type = "text/plain"
            }
        }
        // todo
    }*/

    fun extractSendString(intent: Intent): String? {
        intent.type?.let { type ->
            if (type.contains("text/plain")) {
                (intent.getStringExtra(Intent.EXTRA_TEXT))?.let { text ->
                    return text
                }
            }
        }
        return null
    }

    fun extractSendImage(intent: Intent): Uri? {
        intent.type?.let { type ->
            if (type.contains("image/")) {
                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { image ->
                    return image
                }
            }
        }
        return null
    }

    fun extractSendAudio(intent: Intent): Uri? {
        intent.type?.let { type ->
            if (type.contains("audio/")) {
                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { image ->
                    return image
                }
            }
        }
        return null
    }

    fun extractSendVideo(intent: Intent): Uri? {
        intent.type?.let { type ->
            if (type.contains("video/")) {
                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { image ->
                    return image
                }
            }
        }
        return null
    }

    fun extractViewLocation(intent: Intent): String? {
        intent.data?.let { location ->
            return location.toString()
        }
        return null
    }

}
