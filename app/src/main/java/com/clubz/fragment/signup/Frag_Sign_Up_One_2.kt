package com.clubz.fragment.signup

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.R
import com.clubz.Sign_up_Activity
import com.clubz.helper.WebService
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.clubz.HomeActivity
import com.clubz.SMSreciver.OnSmsCatchListener
import com.clubz.SMSreciver.SmsVerifyCatcher
import com.clubz.helper.SessionManager
import com.clubz.model.User
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Exception


/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_One_2 : Fragment()  , View.OnClickListener {


    lateinit var _otp : String
    lateinit var _contact : String
    lateinit var _code : String
     var _isnewuser : Boolean = false;
    //ClubZ- Your PIN for registration is: 4567

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one_2, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for( view in arrayOf(confirm)) view.setOnClickListener(this)
        var smsverify :SmsVerifyCatcher= SmsVerifyCatcher(activity as Sign_up_Activity, this,object :OnSmsCatchListener<String> {
            override fun onSmsCatch(message: String?) {
                if(message!!.contains("ClubZ")){
                    confirmation_code.setText(message.replace("The verification code for your Club Z account is:","").trim())
                }//Util.showToast(message!!,context);
            }
        })
        smsverify.onStart();
        Util.showToast(_otp+" : This message is Temporary ", context)
       // confirmation_code.setText(_otp)
        confirmation_code.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.length==4){

                    confirm.callOnClick();
                }
            }
        })

        confirmation_code.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                return if (p1 == EditorInfo.IME_ACTION_DONE) {
                    confirm.callOnClick();
                    true
                } else true
            }
        })
    }


    override fun onClick(p0: View?) {
        val activity = activity as Sign_up_Activity
     when(p0!!.id){
         R.id.confirm -> {
             activity .hideKeyBoard();
             if(verfiy())
                 if(_isnewuser)activity.replaceFragment(Frag_Sign_Up_Two().setData(_contact,_code))
             else{ verify_otp();   }
             /*if(activity._first_name.isBlank())activity.replaceFragment(Frag_Sign_Up_Two().setData(_contact,_code))
             else {
                 register(activity);
             }*/

         }
     }
    }

    fun setData(otp : String , contact : String , code : String ,isnewuser :String) :Frag_Sign_Up_One_2{
        _otp = otp;
        _contact = contact;
        _code = code;
        _isnewuser = if(isnewuser.equals("1")) true else false;
        return this;
    }

    fun verfiy() :Boolean{

        if(confirmation_code.text.isBlank()){
            Util.showSnake(context,view!! ,R.string.a_confirmation_code )
            return false;
        }
        if(!confirmation_code.text.toString().trim().equals(_otp)){
            Util.showSnake(context,view!! ,R.string.a_confirmation_code2 )
            return false;
        }
        return true;
    }

    fun verify_otp(){
        val dialog = CusDialogProg(context);
        dialog.show();
        object  : VolleyGetPost(activity,activity, WebService.Login,false) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Contact verified successfully","step":2}
                // http://clubz.co/dev/service/login{"status":"success","message":"User authentication successfully done!","messageCode":"normal_login","userDetail":{"userId":"25","full_name":"ratnesh","social_id":"","social_type":"","email":"ratnesh.mindiii@gmail.com","country_code":"+91","contact_no":"9770495603","profile_image":"http:\/\/clubz.co\/dev\/uploads\/profile\/24a9315b55d30ed6bb7d351a469aea09.jpg","auth_token":"3a05500a49e8d5eb03af32f21aa8a7f7c156dc0f","device_type":"3","device_token":""}}
                try {
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){

                        SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                        startActivity(Intent(activity, HomeActivity::class.java))
                        activity.finish()
                    }
                    else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch (ex: Exception){
                    Toast.makeText(activity,R.string.swr, Toast.LENGTH_LONG).show()
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
                params.put("country_code" , "+"+_code);
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