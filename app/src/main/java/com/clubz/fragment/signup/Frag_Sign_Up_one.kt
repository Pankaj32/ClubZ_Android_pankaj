package com.clubz.fragment.signup

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.clubz.Adapter.Country_spinner_adapter
import com.clubz.Adapter.MyViewPagerAdapter
import com.clubz.R
import com.clubz.Sign_up_Activity
import com.clubz.helper.Type_Token
import com.clubz.model.Country_Code
import com.clubz.util.Util
import com.google.gson.Gson
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.frag_sign_up_one.*
import java.util.ArrayList
import android.widget.Spinner
import android.telephony.TelephonyManager
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.helper.SessionManager
import com.clubz.helper.WebService
import com.clubz.util.VolleyGetPost
import org.json.JSONObject


/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_one : Fragment(), View.OnClickListener {



    var isvalidate : Boolean = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for( view in arrayOf(next)) view.setOnClickListener(this)
        val list = Gson().fromJson<String>(Util.loadJSONFromAsset(context,"country_code.json"), Type_Token.country_list) as ArrayList<Country_Code>
        country_code.adapter = Country_spinner_adapter(context,list,0,R.layout.spinner_view);
        setCountryCode(list , country_code)
    }


    override fun onClick(p0: View?) {
     when(p0!!.id){
         R.id.next-> if(verfiy())
             //(activity as Sign_up_Activity).replaceFragment(Frag_Sign_Up_One_2().setData("1234" ,  phone_no.text.toString() , (country_code.selectedItem as Country_Code).phone_code))
          generateOtp()
     }
    }


    /***** Verfication ****/


    fun verfiy() :Boolean{
        checkPhoneNumber((country_code.selectedItem as Country_Code).code)
        if(phone_no.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_phone_no);
            //Toast.makeText(context,R.string.a_phone_no,Toast.LENGTH_SHORT).show()
            return false;
        }
        if(!isvalidate){
            Util.showSnake(context,view!!,R.string.a_phone_no_valid);
            //Toast.makeText(context,R.string.a_phone_no_valid,Toast.LENGTH_SHORT).show()
            return false
        }
        return true;
    }
    private fun checkPhoneNumber( countryCode : String) {
        val contactNo = phone_no.getText().toString()
        try {
            val phoneUtil = PhoneNumberUtil.createInstance(context)
            if (countryCode != null) {
                val code = countryCode.toUpperCase()
                val swissNumberProto = phoneUtil.parse(contactNo, code)
                isvalidate = phoneUtil.isValidNumber(swissNumberProto)
            }
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: " + e.toString())
        }

    }

    fun setCountryCode(list : ArrayList<Country_Code> , spinner : Spinner){
        val tm = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var locale = tm.networkCountryIso
        if(locale.equals("")) locale ="in_";
        Util.e("phone no" , locale);
        for(i  in 0..list.size-1){
            if(list.get(i).code.equals(locale)){spinner.setSelection(i) ; return }
        }
    }

    fun generateOtp(){
        val activity = activity as Sign_up_Activity;
        val dialog = CusDialogProg(context);
        dialog.show();
        object  : VolleyGetPost(activity,context,WebService.Generate_Otp,false) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Registered successfully, Generate verify code successfully sent","otp":"3319","step":1}

                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        (activity as Sign_up_Activity).replaceFragment(Frag_Sign_Up_One_2().setData(obj.getString("otp") ,  phone_no.text.toString() , (country_code.selectedItem as Country_Code).phone_code))
                    }else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch (ex :Exception){
                    Toast.makeText(context,R.string.swr, Toast.LENGTH_LONG).show()
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
                params.put("country_code" , "+"+(country_code.selectedItem as Country_Code).phone_code);
                params.put("contact_no" , phone_no.text.toString());
                Util.e("params" , params.toString())
                return params;

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put( "language",SessionManager.getObj().getLanguage());
                Util.e("headers" , params.toString())
                return params

            }
        }.execute()
    }

}