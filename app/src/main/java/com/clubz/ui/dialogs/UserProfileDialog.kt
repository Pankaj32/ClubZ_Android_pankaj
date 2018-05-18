package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.clubz.R
import kotlinx.android.synthetic.main.z_user_profile_dialog.*

abstract class UserProfileDialog (internal val context: Context) : Dialog(context), View.OnClickListener{
    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.ic_call->{      onCallClicked()}
            R.id.ic_chat->{      onChatClicked()}
            R.id.ic_favorite->{      onLikeClicked()}
            R.id.ic_flag->{      onFlagClicked()}
        }
    }


    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view : View = LayoutInflater.from(context).inflate(R.layout.z_user_profile_dialog,null);
        this.setContentView(view)
        ic_call.setOnClickListener(this)
        ic_chat.setOnClickListener(this)
        ic_favorite.setOnClickListener(this)
        ic_flag.setOnClickListener(this)
    }

    abstract fun onCallClicked()
    abstract fun onChatClicked()
    abstract fun onLikeClicked()
    abstract fun onFlagClicked()
}