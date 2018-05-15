package com.clubz.ui.cv

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import com.clubz.R

abstract class TermsConditionDialog (internal val context: Context, val title : String, val desc : String) : Dialog(context), View.OnClickListener {

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.mClose->{      onCloseClicked()}
        }
    }

    lateinit var mTitle : TextView;
    lateinit var mDesc : TextView;
    lateinit var mClose : TextView;
    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view : View = LayoutInflater.from(context).inflate(R.layout.dialog_infoview,null);
        this.setContentView(view)
        setViews(view)

        mTitle.setText(title)
        mDesc.setText(desc)
    }


    fun setViews(view :View){
        mTitle = view.findViewById<TextView>(R.id.mTitle)
        mDesc = view.findViewById<TextView>(R.id.mDesc)
        mClose = view.findViewById<TextView>(R.id.mClose)
        mClose.setOnClickListener(this)
    }

    abstract fun onCloseClicked()
}