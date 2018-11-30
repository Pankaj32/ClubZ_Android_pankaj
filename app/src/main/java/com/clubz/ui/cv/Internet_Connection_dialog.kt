package com.clubz.ui.cv

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.clubz.R
import kotlinx.android.synthetic.main.z_cus_internet_er.*

/**
 * Created by mindiii on 2/23/18.
 */
abstract class Internet_Connection_dialog(context: Context) : Dialog(context) {

    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(R.layout.z_cus_internet_er)
        try_again.setOnClickListener { tryaginlistner() }
    }


    abstract fun tryaginlistner()

}