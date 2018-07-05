package com.clubz.ui.authentication.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.ui.authentication.adapter.Country_spinner_adapter
import com.clubz.R
import com.clubz.ui.authentication.Sign_up_Activity
import com.clubz.helper.Type_Token
import com.clubz.data.model.Country_Code
import com.clubz.utils.Util
import com.google.gson.Gson
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.frag_sign_up_one.*
import java.util.ArrayList
import android.widget.Spinner
import android.telephony.TelephonyManager
import android.view.MotionEvent
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ui.cv.CusDialogProg
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.utils.VolleyGetPost
import org.json.JSONObject
import com.clubz.helper.sms_reciver.OnSmsCatchListener
import com.clubz.helper.sms_reciver.SmsVerifyCatcher
import com.clubz.utils.LinearLayoutThatDetectsSoftKeyboard
import com.clubz.utils.PhoneNumberTextWatcher


/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_one : Fragment(), View.OnClickListener, LinearLayoutThatDetectsSoftKeyboard.Listener {

    var isvalidate : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val smsverify = SmsVerifyCatcher(activity as Sign_up_Activity, this,object : OnSmsCatchListener<String> {
            override fun onSmsCatch(message: String?) {
                //Util.showToast(message!!,context);
            }
        })

        smsverify.onStart()
        for( view in arrayOf(next)) view.setOnClickListener(this)
        val list = Gson().fromJson<String>(Util.loadJSONFromAsset(context,"country_code.json"), Type_Token.country_list) as ArrayList<Country_Code>
        country_code.adapter = Country_spinner_adapter(context, list, 0, R.layout.spinner_view)
        setCountryCode(list , country_code)
        phone_no.addTextChangedListener(PhoneNumberTextWatcher(phone_no))
        country_code.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                (activity as Sign_up_Activity).hideKeyBoard()
                return false
            }
        })

       /* phone_no.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if(b){
                scrollView.postDelayed(Runnable {
                    run {

                    }
                }, 200)
            }
        }*/

        mainLayout.setListener(this)

    }

    override fun onSoftKeyboardShown(isShowing: Boolean) {
        if(isShowing){
            scrollView.postDelayed(Runnable {
                run {
                    val lastChild = scrollView?.getChildAt(scrollView.childCount - 1) as View
                    val bottom = lastChild.bottom + scrollView.paddingBottom
                    val sy = scrollView.scrollY
                    val sh = scrollView.height
                    val delta = bottom - (sy + sh)
                    scrollView.smoothScrollBy(0, delta)
                }
            }, 200)
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.next-> if(verfiy())generateOtp()
        }
    }


    /***** Verfication ****/
    private fun verfiy() :Boolean{
        (activity as Sign_up_Activity).hideKeyBoard()
        Util.e("phone",phone_no.text.toString())
        checkPhoneNumber((country_code.selectedItem as Country_Code).code)
        if(phone_no.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_phone_no)
            return false
        }
        if(!isvalidate){
            Util.showSnake(context,view!!,R.string.a_phone_no_valid)
            return false
        }
        return true
    }

    private fun checkPhoneNumber( countryCode : String) {
        val contactNo = phone_no.text.toString().replace("-","")
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
        if(locale.equals("")) locale ="in_"
        Util.e("phone no" , locale)
        for(i  in 0..list.size-1){
            if(list.get(i).code.equals(locale)){spinner.setSelection(i) ; return }
        }
    }

    private fun generateOtp(){
        val activity = activity as Sign_up_Activity
        val dialog = CusDialogProg(context)
        dialog.show()
        object  : VolleyGetPost(activity,context, WebService.Generate_Otp,false) {
            override fun onVolleyResponse(response: String?) {
                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status") == "success"){
                        activity.replaceFragment(Frag_Sign_Up_One_2().setData(obj.getString("otp") ,  phone_no.text.toString().replace(PhoneNumberTextWatcher.replacer,"") , (country_code.selectedItem as Country_Code).phone_code ,obj.getString("isNewUser") ))
                    }else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch (ex :Exception){
                    Toast.makeText(context,R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("country_code" , "+"+(country_code.selectedItem as Country_Code).phone_code)//country_code
                params.put("contact_no" , phone_no.text.toString().replace(PhoneNumberTextWatcher.replacer,""))
                Util.e("params" , params.toString())
                return params

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put( "language", SessionManager.getObj().language)
                return params
            }
        }.execute()
    }

}