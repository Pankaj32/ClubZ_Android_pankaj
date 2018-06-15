package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import com.clubz.R

abstract class DeleteNewsFeedDialog (internal val context: Context) : Dialog(context), View.OnClickListener {

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.mCancel->{      onCloseClicked(this)}
            R.id.mLeaveClub->{      onDeleteNewsFeed(this)}
        }
    }

    lateinit var mTitle : TextView;
    lateinit var mDesc : TextView;
    lateinit var mCancel : TextView;
    lateinit var mLeaveClub : TextView;

    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view : View = LayoutInflater.from(context).inflate(R.layout.dialog_delete_newsfeed,null);
        this.setContentView(view)
        setViews(view)
    }


    fun setViews(view :View){
        mTitle      = view.findViewById(R.id.mTitle)
        mDesc       = view.findViewById(R.id.mDesc)
        mCancel     = view.findViewById(R.id.mCancel)
        mLeaveClub  = view.findViewById(R.id.mLeaveClub)
        mCancel.setOnClickListener(this)
        mLeaveClub.setOnClickListener(this)
    }

    abstract fun onCloseClicked(dialog : DeleteNewsFeedDialog)
    abstract fun onDeleteNewsFeed(dialog : DeleteNewsFeedDialog)
}