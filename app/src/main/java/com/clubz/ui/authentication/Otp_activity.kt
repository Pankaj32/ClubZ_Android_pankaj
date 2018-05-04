package com.clubz.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.data.model.User
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.Constants
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*
import org.json.JSONObject

/**
 * Created by mindiii on 2/13/18.
 */

class Otp_activity : AppCompatActivity(), View.OnClickListener {

    lateinit var _otp :String;
    lateinit var _contact :String;
    lateinit var _code :String;
    lateinit var _step :String;

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
            R.id.confirm ->if(verfiy())verify_otp();
        }

    }

    fun verfiy() :Boolean{

        if(confirmation_code.text.isBlank()){
            Util.showSnake(this,getWindow().getDecorView().getRootView() , R.string.a_confirmation_code)
            return false;
        }
        if(!confirmation_code.text.toString().trim().equals(_otp)){
            Util.showSnake(this,getWindow().getDecorView().getRootView() , R.string.a_confirmation_code2)
            return false;
        }
        return true;
    }
    fun verify_otp(){
        val dialog = CusDialogProg(this);
        dialog.show();
        object  : VolleyGetPost(this,this, WebService.Login,false) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Contact verified successfully","step":2}
                // http://clubz.co/dev/service/login{"status":"success","message":"User authentication successfully done!","messageCode":"normal_login","userDetail":{"userId":"25","full_name":"ratnesh","social_id":"","social_type":"","email":"ratnesh.mindiii@gmail.com","country_code":"+91","contact_no":"9770495603","profile_image":"http:\/\/clubz.co\/dev\/uploads\/profile\/24a9315b55d30ed6bb7d351a469aea09.jpg","auth_token":"3a05500a49e8d5eb03af32f21aa8a7f7c156dc0f","device_type":"3","device_token":""}}
                try {
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){

                        SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                        startActivity(Intent(this@Otp_activity, HomeActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this@Otp_activity,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch (ex: Exception){
                    Toast.makeText(this@Otp_activity, R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss();
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()

            }

            override fun onNetError() {
                dialog.dismiss()

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("country_code" , _code);
                params.put("contact_no" ,_contact);
                Util.e("params" , params.toString())
                return params;

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                return params

            }
        }.execute()
    }


}
