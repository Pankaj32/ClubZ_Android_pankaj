package com.clubz.data.remote

import android.os.AsyncTask
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.clubz.ClubZ
import com.clubz.data.local.db.repo.ClubNameRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.ClubName
import com.clubz.helper.Type_Token
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.google.gson.Gson
import org.json.JSONObject

class AppAsnycTask {

    var listener : Listener? = null

    interface Listener{
        fun onProgressDone()
        fun onProgressCancel(status : String?)
    }

    fun syncAppData(){
        val update = SessionManager.getObj().update

        //put your logic heare
        if(update.needToUpdateMyClubs)
            FatchMyClubs().execute()
    }


    private inner class FatchMyClubs : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {

            val request = object : VolleyMultipartRequest(Request.Method.GET, WebService.get_my_club, Response.Listener<NetworkResponse> { response ->
                val data = String(response.data)
                try {
                    val obj = JSONObject(data)
                    if (obj.getString("status") == "success") {
                        val list = Gson().fromJson<List<ClubName>>(obj.getJSONArray("data").toString(), Type_Token.clubNameList)
                        for(club in list)
                            ClubNameRepo().insert(club)

                        val update = SessionManager.getObj().update
                        update.needToUpdateMyClubs = false
                        SessionManager.getObj().setUpdateAppData(update)
                        listener?.onProgressDone()
                    }else {
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
}