package com.clubz.ui.cv

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import com.clubz.R
import kotlinx.android.synthetic.main.z_cus_enterpriseplan.*

/**
 * Created by mindiii on 2/23/18.
 */
abstract class Purchase_membership_dialog(context: Context) : Dialog(context) {

    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(R.layout.z_cus_enterpriseplan)
        viewplans.setOnClickListener { viewplansListner() }
        cancel.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                this@Purchase_membership_dialog.dismiss()
            }
        })
    }



    abstract fun viewplansListner()


}