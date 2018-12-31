package com.clubz.data.remote

import android.os.AsyncTask
import android.util.Log
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

class AdsAllowtask {

    var listener: Listener? = null

    var clubid="" ;
    interface Listener {
        fun onProgressDone()
        fun onProgressCancel(status: String?)
    }



    fun syncAppData(clubd:String ) {
        this.clubid = clubd
        val update = SessionManager.getObj().update

        //put your logic heare
        //if (update.needToUpdateMyClubs)
            FatchMyAllowClubs().execute()
    }


    private inner class FatchMyAllowClubs : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {

            val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.getUserAllowAds, Response.Listener<NetworkResponse> { response ->
                val data = String(response.data)
                Log.e("allow Club:", data)
                try {
                    val obj = JSONObject(data)
                    if (obj.getString("status") == "success") {

                        listener?.onProgressDone()
                    } else {
                        listener?.onProgressCancel(obj.getString("message"))
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    listener?.onProgressCancel("")
                }
            }, Response.ErrorListener {
                listener?.onProgressCancel("")
            })


            {
                    override fun getParams(): MutableMap<String, String> {
                        val params = java.util.HashMap<String, String>()
                        params["clubId"] = clubid
                        return params
                    }
                override fun getHeaders(): MutableMap<String, String> {
                    val param = java.util.HashMap<String, String>()
                    param["authToken"] = ClubZ.currentUser!!.auth_token
                    param["language"] = SessionManager.getObj().language
                    return param
                }


            }
            request.retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            ClubZ.instance.addToRequestQueue(request)
            return true
        }
    }
}