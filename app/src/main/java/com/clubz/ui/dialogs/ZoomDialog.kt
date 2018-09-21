package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.clubz.R
import com.squareup.picasso.Callback

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.zoom_image.*

class ZoomDialog(internal val context: Context, imgUrl: String) : Dialog(context), View.OnClickListener {

    private var imgUrl = ""

    init {
        this.imgUrl = imgUrl
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = LayoutInflater.from(context).inflate(R.layout.zoom_image, null)
       // this.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.setContentView(view)

        if (!imgUrl.isNullOrEmpty()) {
            Picasso.with(zoomImageView.context).load(imgUrl)
                    .into(zoomImageView, object : Callback {
                override fun onSuccess() {
                    smlProgress.visibility = View.GONE
                }

                override fun onError() {
                    smlProgress.visibility = View.GONE
                }
            })
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