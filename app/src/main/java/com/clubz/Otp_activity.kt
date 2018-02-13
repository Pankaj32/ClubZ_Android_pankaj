package com.clubz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.VolleyError
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.R.id.confirm
import com.clubz.helper.WebService
import com.clubz.util.Constants
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*
import org.json.JSONObject

/**
 * Created by mindiii on 2/13/18.
 */

class Otp_activity : AppCompatActivity(), View.OnClickListener {

    lateinit var _otp :String;
    lateinit var _contact :String;
    lateinit var _code :String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_sign_up_one_2)
        val s = intent.getStringArrayExtra(Constants.DATA)
        _otp = s[0]
        _contact = s[1]
        _code = s[2]
        for(views in arrayOf(confirm))views.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.confirm->if(verfiy())verify_otp();
        }

    }

    fun verfiy() :Boolean{

        if(confirmation_code.text.isBlank()){
            Util.showSnake(this,getWindow().getDecorView().getRootView() ,R.string.a_confirmation_code )
            return false;
        }
        if(!confirmation_code.text.toString().trim().equals(_otp)){
            Util.showSnake(this,getWindow().getDecorView().getRootView() ,R.string.a_confirmation_code2 )
            return false;
        }
        return true;
    }
    fun verify_otp(){
        val dialog = CusDialogProg(this);
        dialog.show();
        object  : VolleyGetPost(this,this, WebService.Verify_OtpLogin,true) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Contact verified successfully","step":2}
                try {
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){

                    }
                    else{

                    }
                }catch (ex: Exception){

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
                //params.put("OTP" , confirmation_code.text.toString());
                params.put("country_code" , _code);
                params.put("contact_no" , _contact);
                Util.e("params" , params.toString())
                return params;

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put( "language","en");
                Util.e("headers" , params.toString())
                return params

            }
        }.execute()
    }


}
