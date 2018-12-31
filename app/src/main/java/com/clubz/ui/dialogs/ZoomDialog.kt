package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import com.clubz.R

import kotlinx.android.synthetic.main.zoom_image.*

class ZoomDialog(internal val context: Context, imgUrl: String) : Dialog(context), View.OnClickListener {

    private var imgUrl = ""

    init {
        this.imgUrl = imgUrl
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = LayoutInflater.from(context).inflate(R.layout.zoom_image, null)
       // this.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setContentView(view)
        this.setCancelable(true)
        this.setCanceledOnTouchOutside(true)
        if (!imgUrl.isNullOrEmpty()) {
            Glide.with(zoomImageView.context).load(imgUrl)
                    .into(zoomImageView)
        }
        cancelBtn.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.cancelBtn -> {
                dismiss()
            }
        }
    }
}