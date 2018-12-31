package com.clubz.data.remote

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.clubz.ClubZ
import com.clubz.chat.model.ClubBean
import com.clubz.chat.model.MemberBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.repo.ClubNameRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.ClubName
import com.clubz.helper.Type_Token
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.club.model.ClubDetails
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import org.json.JSONObject

class AppAsnycTask {

    var listener: Listener? = null

    interface Listener {
        fun onProgressDone()
        fun onProgressCancel(status: String?)
    }

    fun syncAppData() {
        val update = SessionManager.getObj().update

        //put your logic heare
        if (update.needToUpdateMyClubs)
            FatchMyClubs().execute()
    }


    private inner class FatchMyClubs : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {

            val request = object : VolleyMultipartRequest(Request.Method.GET, WebService.get_my_club, Response.Listener<NetworkResponse> { response ->
                val data = String(response.data)
                Log.e("my Club:", data)
                try {
                    val obj = JSONObject(data)
                    if (obj.getString("status") == "success") {
                        val list = Gson().fromJson<List<ClubName>>(obj.getJSONArray("data").toString(), Type_Token.clubNameList)
                        for (club in list){

                            ClubNameRepo().insert(club)
                            if(SessionManager.getObj().user.clubz_owner_id.equals(SessionManager.getObj().user.id)){
                                if(club.clubId==1){
                                    createClubInFairBase(club)
                                }
                            }
                            else{
                                if(club.clubId==1){
                                    val memberBean = ClubBean()
                                    memberBean.clubId = ""+club.clubId
                                    onUpdateFirebase(memberBean,1)
                                }
                            }
                        }


                        val update = SessionManager.getObj().update
                        update.needToUpdateMyClubs = false
                        SessionManager.getObj().setUpdateAppData(update)
                        listener?.onProgressDone()
                    } else {
                        val memberBean = ClubBean()
                        memberBean.clubId = "1"
                        onUpdateFirebase(memberBean,1)

                        listener?.onProgressCancel(obj.getString("status"))
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    listener?.onProgressCancel("")
                }
            }, Response.ErrorListener {
                listener?.onProgressCancel("")
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val param = java.util.HashMap<String, String>()
                    param["authToken"] = ClubZ.currentUser!!.auth_token
                    return param
                }
            }
            request.retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            ClubZ.instance.addToRequestQueue(request)
            return true
        }
    }



    private fun createClubInFairBase(clubDetails: ClubName?) {
        val clubBean = ClubBean()
        clubBean.clubId = ""+clubDetails?.clubId
        clubBean.clubImage = clubDetails?.club_image
        clubBean.clubName = clubDetails?.club_name
        clubBean.ownerId = SessionManager.getObj().user.id
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB)
                .child(""+clubDetails?.clubId)
                .setValue(clubBean).addOnCompleteListener {
                    onUpdateFirebase(clubBean,1)
                }
    }
    fun onUpdateFirebase(club: ClubBean, status:Int) {
        val memberBean = MemberBean()
        memberBean.clubId = club.clubId
        memberBean.userId = ClubZ.currentUser?.id
        memberBean.joind = 1
        memberBean.silent = "1"



        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB_MEMBER)
                .child(club.clubId!!)
                .child(memberBean.userId!!)
                .setValue(memberBean).addOnCompleteListener {
                    Log.e("onUpdateFirebase", "onUpdateFirebase")
                }
    }
}