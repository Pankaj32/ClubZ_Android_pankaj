package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.clubz.R
import com.clubz.data.model.ClubMember
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.baseclub_list.*
import kotlinx.android.synthetic.main.z_user_profile_dialog.*

abstract class UserProfileDialog (internal val context: Context, member: ClubMember) : Dialog(context), View.OnClickListener{

    var user: ClubMember? = null

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.ic_call->{      onCallClicked()}
            R.id.ic_chat->{      onChatClicked()}
            R.id.ic_favorite->{      onLikeClicked()}
            R.id.ic_flag->{      onFlagClicked()}
            R.id.ivEdit->{ //tv_FullName.isEnabled = !tv_FullName.isEnabled

                val bool = !tv_FullName.isEnabled
                if(bool){
                    tv_FullName.isEnabled = bool
                    ivEdit.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_white_24dp))
                }else {

                    val name = tv_FullName.text.toString()

                    if(name.isEmpty())
                        showError(context.getString(R.string.comments))
                    else if(name.equals(user?.getFirstTagName())){
                        //showToast(context.getString(R.string.comments))
                        tv_FullName.isEnabled = bool
                        ivEdit.setImageDrawable(context.resources.getDrawable(R.drawable.ic_create))
                    }else{
                        tv_FullName.isEnabled = bool
                        ivEdit.setImageDrawable(context.resources.getDrawable(R.drawable.ic_create))
                    }
                }
            }
        }
    }


    init {
        this.user = member
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view : View = LayoutInflater.from(context).inflate(R.layout.z_user_profile_dialog,null);
        this.setContentView(view)

        if(member.profile_image.isNotEmpty()){
            iv_profileImage.clearColorFilter()
            Picasso.with(context).load(member.profile_image).into(iv_profileImage)
        } else iv_profileImage.setColorFilter(R.color.white)

        tv_FullName.setText(member.getFirstTagName())

        ic_call.setOnClickListener(this)
        ic_chat.setOnClickListener(this)
        ic_favorite.setOnClickListener(this)
        ic_flag.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
    }

    abstract fun onCallClicked()
    abstract fun onChatClicked()
    abstract fun onLikeClicked()
    abstract fun onFlagClicked()
    abstract fun showError(msg : String)
}