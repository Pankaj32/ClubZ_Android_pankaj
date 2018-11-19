package com.clubz.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.data.model.User
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.Constants
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*
import org.json.JSONObject

/**
 * Created by mindiii on 2/13/18.
 */

class Otp_activity : BaseActivity(), View.OnClickListener {

    lateinit var _otp :String
    lateinit var _contact :String
    lateinit var _code :String
    lateinit var _step :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_sign_up_one_2)
        val s = intent.getStringArrayExtra(Constants.DATA)
        _otp = s[0]
        _contact = s[1]
        _code = s[2]
        _step = s[3]
        for(views in arrayOf(confirm))views.setOnClickListener(this)
        confirmation_code.setText(_otp)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.confirm ->if(verfiy())verify_otp()
        }

    }

    private fun verfiy() :Boolean{
        if(confirmation_code.text.isBlank()){
            Util.showSnake(this, window.decorView.rootView, R.string.a_confirmation_code)
            return false
        }
        if(!confirmation_code.text.toString().trim().equals(_otp)){
            Util.showSnake(this, window.decorView.rootView, R.string.a_confirmation_code2)
            return false
        }
        return true
    }

    private fun verify_otp(){
        val dialog = CusDialogProg(this)
        dialog.show()
        object  : VolleyGetPost(this,this, WebService.Login,false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    val obj = JSONObject(response)
                    if(obj.getString("status") == "success"){
                        SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                        ClubZ.currentUser = SessionManager.getObj().user
                        startActivity(Intent(this@Otp_activity, HomeActivity::class.java))
                        finish()
                    }
                    else{ showToast(obj.getString("message")) }
                }catch (ex: Exception){ showToast(getString(R.string.swr)) }
                dialog.dismiss()
            }

            override fun onVolleyError(error: VolleyError?) { dialog.dismiss() }
            override fun onNetError() { dialog.dismiss() }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("country_code" , _code)
                params["contact_no"] = _contact
                params.put("device_token" , FirebaseInstanceId.getInstance().getToken()!!);
                params.put("device_type" , Constants.DEVICE_TYPE);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }
        }.execute()
    }


}
