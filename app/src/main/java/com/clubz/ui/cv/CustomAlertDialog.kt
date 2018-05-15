package com.clubz.ui.cv

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.clubz.R

/**
 * Created by mindiii on २३/३/१८.
 */
abstract class CustomAlertDialog(internal val context: Context) :Dialog(context), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.cancel->{ onCancelListner()}
            R.id.ok->{      onOkListner()}
        }
    }

    lateinit var message :TextView ;
    lateinit var title :TextView ;
    lateinit var ok :Button ;
    lateinit var cancel :Button ;
    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view : View = LayoutInflater.from(context).inflate(R.layout.z_cus_alert,null);
        this.setContentView(view)
        setViews(view)
    }


    fun setViews(view :View){
        message = view.findViewById<TextView>(R.id.message)
        title = view.findViewById<TextView>(R.id.title_t)
        ok = view.findViewById<Button>(R.id.ok)
        cancel = view.findViewById<Button>(R.id.cancel)
        for(views in arrayOf(view.findViewById<Button>(R.id.ok),
                view.findViewById(R.id.cancel)))views.setOnClickListener(this)
    }

    abstract fun onCancelListner()
    abstract fun onOkListner()

    fun setMessage(resource : Int){
        message.setText(resource)
    }
    fun setTitile(resource : Int){
        title.setText(resource)
    }
    fun setOktext(resource : Int){
        ok.setText(resource)
    }
    fun setCanceltext(resource : Int){
        cancel.setText(resource)
    }
}