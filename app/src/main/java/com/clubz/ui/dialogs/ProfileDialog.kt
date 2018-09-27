package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import com.bumptech.glide.Glide
import com.clubz.R
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil
import com.clubz.data.model.UserInfo
import kotlinx.android.synthetic.main.z_profile_dialog.*

abstract class ProfileDialog(internal val context: Context, userInfo: UserInfo)
    : Dialog(context),
        View.OnClickListener {
    var user: UserInfo? = null
    private val ARG_CHATFOR = "chatFor"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"

    init {
        this.user = userInfo
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = LayoutInflater.from(context).inflate(R.layout.z_profile_dialog, null)
        this.setContentView(view)
        tv_FullName.text=user!!.full_name
        if (user!!.profile_image.isNotEmpty()) {
            if(!user!!.profile_image.contains("defaultUser")){
                iv_profileImage.clearColorFilter()
                Glide.with(context).load(user!!.profile_image)/*.placeholder(R.drawable.ic_person_512)*/.into(iv_profileImage)
            }

        } else iv_profileImage.setColorFilter(R.color.white)
        ic_favorite.setOnClickListener(this)
        ic_chat.setOnClickListener(this)
        ic_call.setOnClickListener(this)
        ic_flag.setOnClickListener(this)
    }
    //http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ic_favorite -> {
                OnFabClick(user!!)
            }
            R.id.ic_chat -> {
                if (!user!!.userId.equals("")) {
                    context.startActivity(Intent(context, AllChatActivity::class.java)
                            .putExtra(ARG_CHATFOR, ChatUtil.ARG_IDIVIDUAL)
                            .putExtra(ARG_HISTORY_ID, user!!.userId)
                            .putExtra(ARG_HISTORY_NAME, user!!.full_name)
                    )
                } else {
                    Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.ic_call -> {
                if (!user!!.userId.equals("")) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "${user?.country_code} ${user?.contact_no}"))
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.ic_flag -> {
                OnProfileClick(user!!)
            }
        }
    }

    abstract fun OnFabClick(user: UserInfo)
    abstract fun OnProfileClick(user: UserInfo)
}