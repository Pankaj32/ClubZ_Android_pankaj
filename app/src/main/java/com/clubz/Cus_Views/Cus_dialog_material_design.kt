package com.clubz.Cus_Views

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import com.clubz.ClubZ
import com.clubz.R

/**
 * Created by mindiii on ३०/३/१८.
 */
abstract class Cus_dialog_material_design(internal val context: Context ) : Dialog(context) {

     var alert_title : TextView;
     var alert_msg   : TextView;
     var disagree    : TextView;
     var agree       : TextView;


    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view : View = LayoutInflater.from(context).inflate(R.layout.z_alert_material_design, null)
        this.setContentView(view)
        this.setCanceledOnTouchOutside(false)
        alert_title = view.findViewById<TextView>(R.id.alert_title)
        alert_msg   = view.findViewById<TextView>(R.id.alert_msg)
        disagree    = view.findViewById<TextView>(R.id.disagree)
        agree       = view.findViewById<TextView>(R.id.agree)
        agree.setOnClickListener({
            onAgree()
        })

        disagree.setOnClickListener({
            onDisagree()
        })
    }

    fun setTextAlert_title(resourse :Int){ alert_title.setText(resourse)    }
    fun setTextAlert_msg  (resourse :Int){ alert_msg  .setText(resourse)    }
    fun setTextAgree      (resourse :Int){ agree      .setText(resourse)    }
    fun setTextDisagree   (resourse :Int){ disagree   .setText(resourse)    }

    abstract fun onDisagree()
    abstract fun onAgree()
}