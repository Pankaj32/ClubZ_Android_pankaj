package com.clubz.ui.core

import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.model.ClubName
import com.clubz.data.model.Clubs
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.model.GetMyClubResponce
import com.clubz.utils.Util
import com.google.gson.Gson
import org.json.JSONObject

abstract class ClubNameListActivity : AppCompatActivity() {

    protected var clubList: ArrayList<ClubName> = ArrayList()

    protected fun getAllMyClubList() {
        val dialog = CusDialogProg(this)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.GET, WebService.get_my_club, Response.Listener<NetworkResponse> { response ->
            val data = String(response.data)
            dialog.dismiss()
            try {
                val obj = JSONObject(data)
                if (obj.getString("status") == "success") {
                    clubList.clear()
                    val firstItem = ClubName()
                    firstItem.club_name = getString(R.string.prompt_select_club)
                    firstItem.clubId = -1
                    clubList.add(firstItem)
                    clubList.addAll(Gson().fromJson<ArrayList<ClubName>>(obj.getJSONArray("data").toString() ,
                            Type_Token.clubNameList))
                    refreshSpinner()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            dialog.dismiss()
        }, Response.ErrorListener {
            dialog.dismiss()
        }) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["authToken"] = ClubZ.currentUser!!.auth_token
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        ClubZ.instance.addToRequestQueue(request)
    }

    abstract fun refreshSpinner()
}