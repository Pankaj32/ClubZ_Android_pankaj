package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.repo.AllFabContactRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.AllFavContact
import com.clubz.data.model.ClubMember
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.club.ClubsActivity
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.main.HomeActivity
import com.clubz.ui.profile.model.FabContactList
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.z_user_profile_dialog.*
import org.json.JSONObject

abstract class UserProfileDialog(internal val context: Context, val member: ClubMember, isEditable : Boolean=true)
    : Dialog(context), View.OnClickListener{

    var user: ClubMember? = null
    private val ARG_CHATFOR = "chatFor"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"
    private val ARG_HISTORY_PIC = "historyPic"

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.ic_call-> {
                if (!user!!.userId.equals("")) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "${user?.country_code} ${user?.contact_no}"))
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show()
                }
            } R.id.ic_chat->{
                context.startActivity(Intent(context, AllChatActivity::class.java)
                        .putExtra(ARG_CHATFOR, ChatUtil.ARG_IDIVIDUAL)
                        .putExtra(ARG_HISTORY_ID, user!!.userId)
                        .putExtra(ARG_HISTORY_NAME, user!!.full_name)
                        .putExtra(ARG_HISTORY_PIC, user!!.profile_image)
                )
            }
            R.id.ic_flag->{      onFlagClicked()}
            R.id.ivEdit->{ //tv_FullName.isEnabled = !tv_FullName.isEnabled

                val bool = !tv_FullName.isEnabled
                if(bool){
                    tv_FullName.isEnabled = bool
                    ivEdit.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_white_24dp))
                }else {

                    val name = tv_FullName.text.toString()

                    when {
                        name.isEmpty() -> showError(context.getString(R.string.comments))
                        name == user?.getNickname() -> {
                            //showToast(context.getString(R.string.comments))
                            tv_FullName.isEnabled = bool
                            ivEdit.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_create))
                        }
                        else -> {
                            updateNickName(name)
                        }
                    }
                }
            }

            R.id.ic_favorite->{

                addFavoriteUser()

               /* member.isLiked = if(member.isLiked==0) 1 else 0
                if(member.isLiked==0)
                    ic_favorite.setImageResource(R.drawable.ic_favorite_border)
                else
                    ic_favorite.setImageResource(R.drawable.ic_favorite_fill)
                updateContactList()*/
                //onLikeClicked()
            }
        }
    }


    init {
        this.user = member
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view : View = LayoutInflater.from(context).inflate(R.layout.z_user_profile_dialog,null)
        this.setContentView(view)

        if(member.profile_image.isNotEmpty()){
            iv_profileImage.clearColorFilter()
            Glide.with(context).load(member.profile_image).into(iv_profileImage)
        } else iv_profileImage.setColorFilter(R.color.white)

        tv_FullName.setText(member.getNickname())
        tv_FullName.isEnabled = false
        //ivEdit.visibility = if(isEditable) View.VISIBLE else View.GONE

        if(isEditable){
            ivEdit.visibility =  View.VISIBLE
        }else{
            ivEdit.visibility =  View.GONE
        }
        val favContactList = AllFabContactRepo().getAllFavContats()
        for (contact in favContactList) {
            if (contact.userId.equals(user!!.userId) && contact.clubId.equals(user!!.clubId)) {
                user!!.isLiked = 1
                break
            }
        }

        if (user!!.isLiked==0) {
            ic_favorite.setImageResource(R.drawable.ic_favorite_border)
            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            ic_favorite.setImageResource(R.drawable.ic_cards_heart_active)
            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.red_favroit), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        if (member!!.contact_no_visibility.equals("0")) ic_call.visibility = View.GONE
        ic_call.setOnClickListener(this)
        ic_chat.setOnClickListener(this)
        ic_favorite.setOnClickListener(this)
        ic_flag.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
    }

    abstract fun onCallClicked()
    /*abstract fun onChatClicked()*/
    abstract fun onLikeClicked(isLIked: Int)
    abstract fun onFlagClicked()
    abstract fun onProfileUpdate(name : String)
    abstract fun showError(msg : String)

    private fun updateNickName(nickName : String = ""){
        ClubZ.instance.cancelPendingRequests(UserProfileDialog::class.java.name)
        progress_bar.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        val request = object : VolleyMultipartRequest(Request.Method.POST,
                WebService.update_nickName,
                Response.Listener<NetworkResponse> { response ->
                    val data = String(response.data)
                    try {
                        progress_bar.visibility = View.GONE
                        ivEdit.visibility = View.VISIBLE
                        val obj = JSONObject(data)
                        if(obj.getString("status")=="success"){
                           // val dataArray = obj.getJSONArray("data")
                            tv_FullName.isEnabled = false
                            ivEdit.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_create))
                            onProfileUpdate(nickName)

                        }
                    }catch ( e : java.lang.Exception){
                        e.printStackTrace()
                        progress_bar.visibility = View.GONE
                    }
                }, Response.ErrorListener {
            progress_bar.visibility = View.GONE
            ivEdit.visibility = View.VISIBLE
            //pb_loading_indicator.visibility = View.GONE
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["clubUserId"] = member.clubUserId
                params["userNickName"] = nickName
                params["userId"] = member.userId
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["language"] = SessionManager.getObj().language
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }

        ClubZ.instance.addToRequestQueue(request, UserProfileDialog::class.java.name)
    }

   /* private fun updateContactList(){
       *//* val dialog = CusDialogProg(context)
        dialog.show()*//*

        object : VolleyGetPost(context, WebService.updateContact, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                   // dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status")=="success"){
                        onLikeClicked(member.isLiked)
                    }
                } catch (ex: Exception) { }
            }

            override fun onVolleyError(error: VolleyError?) { *//*dialog.dismiss()*//* }

            override fun onNetError() { *//*dialog.dismiss()*//* }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["clubUserId"] = member.userId
                params["isFavorite"] = if(member.isLiked==0) "1" else "0"
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }*/

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
                        if (user!!.isLiked==0) user!!.isLiked = 1 else user!!.isLiked = 0
                        if (user!!.isLiked==0) {
                            ic_favorite.setImageResource(R.drawable.ic_favorite_border)
                            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                        } else {
                            ic_favorite.setImageResource(R.drawable.ic_cards_heart_active)
                            ic_favorite.setColorFilter(ContextCompat.getColor(context, R.color.red_favroit), android.graphics.PorterDuff.Mode.MULTIPLY);
                        }

                            getfavContactList()

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