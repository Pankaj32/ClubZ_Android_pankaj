package com.clubz.ui.cv


import android.app.Dialog
import android.content.Context
import android.view.Window

import com.clubz.R


class CusDialogProg(internal var context: Context, Layout: Int = R.layout.custom_progress_dialog_layout) : Dialog(context, R.style.DialogThemes) {
    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(Layout)
        this.setCanceledOnTouchOutside(false)
        setColor()
        instance = this
    }

    fun setColor() {
        this.window!!.setBackgroundDrawableResource(R.color.black50p)
    }

    fun setColor(colorcode: Int) {
        this.window!!.setBackgroundDrawableResource(colorcode)


    }

    fun dismisser() {
        count--
        if (count == 0) {
            this.dismiss()
            instance = null
        }

    }

    companion object {
        internal var instance: CusDialogProg? = null
        internal var count = 0

        fun getInstance(context: Context): CusDialogProg? {
            if (instance == null) CusDialogProg(context, R.layout.custom_progress_dialog_layout)
            count++
            return instance

        }
    }

}
