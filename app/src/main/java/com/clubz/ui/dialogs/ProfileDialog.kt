package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.repo.AllFabContactRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.AllFavContact
import com.clubz.data.model.UserInfo
import com.clubz.data.remote.WebService
import com.clubz.ui.main.HomeActivity
import com.clubz.ui.profile.model.FabContactList
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.z_profile_dialog.*
import org.json.JSONObject

abstract class ProfileDialog(internal val context: Context, userInfo: UserInfo)
    : Dialog(context),
        View.OnClickListener {
    var user: UserInfo? = null
    private val ARG_CHATFOR = "chatFor"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"
    private val ARG_HISTORY_PIC = "historyPic"

    init {
        this.user = userInfo
        user!!.isLiked = "0"
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = LayoutInflater.from(context).inflate(R.layout.z_profile_dialog, null)
        this.setContentView(view)
        tv_FullName.text = user!!.full_name
        val favContactList = AllFabContactRepo().getAllFavContats()
        for (contact in favContactList) {
            if (contact.userId.equals(user!!.userId) && contact.clubId.equals(user!!.clubId)) {
                user!!.isLiked = "1"
                break
            }
        }
        if (user!!.isLiked.equals("0")) {
            ic_favorite.setImageResource(R.drawable.ic_favorite_border)
            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            ic_favorite.setImageResource(R.drawable.ic_cards_heart_active)
            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.red_favroit), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        if (user!!.profile_image.isNotEmpty()) {
            if (!user!!.profile_image.contains("defaultUser")) {
                iv_profileImage.clearColorFilter()
                Glide.with(context).load(user!!.profile_image)/*.placeholder(R.drawable.ic_person_512)*/.into(iv_profileImage)
            }
        }
        if (!userInfo.contact_no_visibility.equals("1")) ic_call.visibility = View.GONE
        ic_favorite.setOnClickListener(this)
        ic_chat.setOnClickListener(this)
        ic_call.setOnClickListener(this)
        ic_flag.setOnClickListener(this)
    }

    //http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ic_favorite -> {
                /// updateContactList()
                addFavoriteUser()
                // OnFabClick(user!!)
            }
            R.id.ic_chat -> {
                if (!user!!.userId.equals("")) {
                    context.startActivity(Intent(context, AllChatActivity::class.java)
                            .putExtra(ARG_CHATFOR, ChatUtil.ARG_IDIVIDUAL)
                            .putExtra(ARG_HISTORY_ID, user!!.userId)
                            .putExtra(ARG_HISTORY_NAME, user!!.full_name)
                            .putExtra(ARG_HISTORY_PIC, user!!.profile_image)
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

    //  abstract fun OnFabClick(user: UserInfo)
    abstract fun OnProfileClick(user: UserInfo)

    private fun updateContactList() {
        /* val dialog = CusDialogProg(context)
         dialog.show()*/

        object : VolleyGetPost(context, WebService.updateContact, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    // dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        if (user!!.isLiked.equals("0")) user!!.isLiked = "1" else user!!.isLiked = "0"
                        if (user!!.isLiked.equals("0")) {
                            ic_favorite.setImageResource(R.drawable.ic_favorite_border)
                        } else {
                            ic_favorite.setImageResource(R.drawable.ic_cards_heart_active)
                        }
                    }
                } catch (ex: Exception) {
                }
            }

            override fun onVolleyError(error: VolleyError?) { /*dialog.dismiss()*/
            }

            override fun onNetError() { /*dialog.dismiss()*/
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["clubUserId"] = user!!.clubUserId
                params["isFavorite"] = if (user!!.isLiked.equals("0")) "1" else "0"
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    private fun addFavoriteUser() {
        /* val dialog = CusDialogProg(context)
         dialog.show()*/

        object : VolleyGetPost(context, WebService.addFavoriteUser, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    // dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        if (user!!.isLiked.equals("0")) user!!.isLiked = "1" else user!!.isLiked = "0"
                        if (user!!.isLiked.equals("0")) {
                            ic_favorite.setImageResource(R.drawable.ic_favorite_border)
                            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                        } else {
                            ic_favorite.setImageResource(R.drawable.ic_cards_heart_active)
                            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.red_favroit), android.graphics.PorterDuff.Mode.MULTIPLY);
                        }
                        if (context is HomeActivity) {
                            val homeActivity = context as HomeActivity
                            homeActivity.getfavContactList()
                        } else {
                            getfavContactList()
                        }
                    }
                } catch (ex: Exception) {
                }
            }

            override fun onVolleyError(error: VolleyError?) { /*dialog.dismiss()*/
            }

            override fun onNetError() { /*dialog.dismiss()*/
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["favoriteUserId"] = user!!.userId
                params["clubId"] = user!!.clubId
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                //   params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    fun getfavContactList() {

        object : VolleyGetPost(context,
                "${WebService.favoriteUserList}", true, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        val favContactBen: FabContactList = Gson().fromJson(response, FabContactList::class.java)
                        updateContactInDb(favContactBen.getUserList())
                    } else {

                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute(context::class.java.name)
    }

    private fun updateContactInDb(userList: List<FabContactList.UserListBean>?) {
        AllFabContactRepo().deleteTable()
        for (user in userList!!) {
            val allFavContact = AllFavContact()
            allFavContact.userId = user.userId
            allFavContact.device_token = user.device_token
            allFavContact.clubId = user.clubId
            allFavContact.club_name = user.club_name
            allFavContact.name = user.name
            allFavContact.profile_image = user.profile_image
            AllFabContactRepo().insert(allFavContact)
        }
    }
}