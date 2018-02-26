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
import com.clubz.model.User
import com.clubz.util.Constants
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
        val activity = activity as Sign_up_Activity
     when(p0!!.id){
         R.id.confirm -> {
             activity .hideKeyBoard();
             if(verfiy())
             if(activity._first_name.isBlank())activity.replaceFragment(Frag_Sign_Up_Two().setData(_contact,_code))
             else {
                 register(activity);
             }

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

    fun register(activity: Sign_up_Activity){

        val dialog = CusDialogProg(context);
        dialog.show();
        object  : VolleyGetPost(activity,activity, WebService.Registraion,false) {
            override fun onVolleyResponse(response: String?) {
                Util.e("Response",response.toString())
                try {
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                        //{"status":"success","message":"User registration successfully done","userDetail":{"userId":"16","full_name":"ratnesh","social_id":"","social_type":"","email":"ratnesh.mindiii@gmail.com","country_code":"91","contact_no":"9770495603","profile_image":"http:\/\/clubz.co\/dev\/uploads\/profile\/62db25443654d90353e25317bf5aa73b.jpg","auth_token":"f2c6e239029dfa5f34d474ad5ca2efeef2b1640d","device_type":"1","device_token":"1234"},"messageCode":"normal_reg","step":4}
                        (activity as Sign_up_Activity).replaceFragment(Frag_Sign_UP_Three().setData(_contact ,_code ,obj.getJSONObject("userDetail").getString("auth_token"))) ////Its Temp
                    }else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch ( e : Exception){
                    e.printStackTrace()
                    Toast.makeText(context,R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }

            override fun onVolleyError(error: VolleyError?) {
                Util.e("Error",error.toString())
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("full_name",activity._first_name)
                //params.put("last_name",lastname.text.toString())
                params.put("email",activity._email)
                params.put("contact_no",_contact)
                params.put("device_token","1234")
                params.put("device_type",Constants.DEVICE_TYPE)
                params.put("country_code","+"+_code)
                params.put("social_id",activity._social_id)
                params.put("social_type",activity._social_type)
                params.put("profile_image",activity._profile_image)
                return params

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put( "language", SessionManager.getObj().getLanguage());
                Util.e("headers" , params.toString())
                return params
            }
        }.execute()

    }
}