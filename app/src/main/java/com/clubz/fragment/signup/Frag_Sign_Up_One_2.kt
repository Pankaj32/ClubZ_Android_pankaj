package com.clubz.fragment.signup

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
import com.clubz.helper.SessionManager
import org.json.JSONObject


/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_One_2 : Fragment()  , View.OnClickListener {


    lateinit var _otp : String
    lateinit var _contact : String
    lateinit var _code : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one_2, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for( view in arrayOf(confirm)) view.setOnClickListener(this)
        confirmation_code.setText(_otp)
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
     when(p0!!.id){
         R.id.confirm -> {
             (activity as Sign_up_Activity).hideKeyBoard(); if(verfiy())
             (activity as Sign_up_Activity).replaceFragment(Frag_Sign_Up_Two().setData(_contact,_code))
             //verify_otp()
         }
     }
    }

    fun setData(otp : String , contact : String , code : String) :Frag_Sign_Up_One_2{
        _otp = otp;
        _contact = contact;
        _code = code;
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
       /* val dialog = CusDialogProg(context);
        dialog.show();
        object  : VolleyGetPost(activity,context, WebService.Verify_Otp,true) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Contact verified successfully","step":2}
                try {
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                       if((activity as Sign_up_Activity)._authToken.isBlank()) (activity as Sign_up_Activity).replaceFragment(Frag_Sign_Up_Two().setData(_contact,_code))
                        else (activity as Sign_up_Activity).replaceFragment(Frag_Sign_UP_Three())
                    }
                    else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
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
                params.put("contact_no" , _contact)
                params.put("country_code" , _code)
                Util.e("params" , params.toString())
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put( "language", SessionManager.obj.getLanguage());
                Util.e("headers" , params.toString())
                return params

            }
        }.execute()*/
    }
}