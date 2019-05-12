package com.ltei.lauviews

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout


class DialogBuilder(context: Context, padding: Int? = null, orientation: Int = LinearLayout.VERTICAL) : LinearLayoutBuilder(context, padding, orientation) {

    open class BuiltDialog(context: Context) : Dialog(context) {
        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    val dialog = BuiltDialog(context)

    fun buildInDialog(): Dialog {
        assertIsNotBuild()
        dialog.setContentView(root)
        isBuilt = true
        return dialog
    }

}