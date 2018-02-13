package com.clubz

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.android.volley.VolleyError
import com.clubz.Adapter.MyViewPagerAdapter
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.helper.WebService
import com.clubz.util.Constants
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_signin.*
import org.json.JSONObject
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.clubz.util.Constants.RC_SIGN_IN
import com.google.android.gms.common.api.ApiException


/**
 * Created by mindiii on 2/2/18.
 */
class Sign_In_Activity : AppCompatActivity(), ViewPager.OnPageChangeListener, View.OnClickListener {



    lateinit var  viewPager : ViewPager ;
    lateinit var layouts: IntArray;
    lateinit var lnr_indicator: LinearLayout;
    lateinit var mGoogleSignInClient : GoogleSignInClient
    var isvalidate: Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin);
        Util.checklaunage(this)
        viewPager = findViewById(R.id.view_pager);
        lnr_indicator = findViewById(R.id.lnr_indicator);
        layouts = intArrayOf(R.layout.welcome_slide1, R.layout.welcome_slide2, R.layout.welcome_slide3, R.layout.welcome_slide4)
        viewPager.setAdapter(MyViewPagerAdapter(this@Sign_In_Activity , layouts , viewPager))
        viewPager.addOnPageChangeListener(this)
        lnr_indicator.getChildAt(0).setBackgroundResource(R.drawable.indicator_active)


        for(view in arrayOf(sign_up ,next ,google_lnr))view.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.sign_up->startActivity(Intent(this@Sign_In_Activity,Sign_up_Activity::class.java))
            R.id.next-> login()
            R.id.google_lnr-> googleSignin()
        }
    }




    /**
     *
     */
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        for(i in 0..lnr_indicator.childCount-1){
            lnr_indicator.getChildAt(i).setBackgroundResource(R.drawable.indicator_inactive)
        }
        lnr_indicator.getChildAt(position).setBackgroundResource(R.drawable.indicator_active)

    }

    fun verfiy() :Boolean{

        return true;
    }
    private fun checkPhoneNumber( countryCode : String) {
        val contactNo = phone_no.getText().toString()
        try {
            val phoneUtil = PhoneNumberUtil.createInstance(this)
            if (countryCode != null) {
                val code = countryCode.toUpperCase()
                val swissNumberProto = phoneUtil.parse(contactNo, code)
                isvalidate = phoneUtil.isValidNumber(swissNumberProto)
            }
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: " + e.toString())
        }

    }


    fun login(){
        val dialog = CusDialogProg(this);
        dialog.show();
        object  : VolleyGetPost(this,this, WebService.Login,false) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Registered successfully, Generate verify code successfully sent","otp":"3319","step":1}
                //E/response: http://clubz.co/dev/service/login{"status":"success","message":"OTP has been sent to your contact number","otp":"9690"}

                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        var intent = Intent (this@Sign_In_Activity,Otp_activity::class.java)
                        intent.putExtra(Constants.DATA ,arrayOf(obj.getString("otp") ,phone_no.text.toString() , "91"))
                        startActivity(intent)
                    }else{

                    }
                }catch (ex :Exception){

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
                params.put("country_code" , "91");
                params.put("contact_no" , phone_no.text.toString());
                params.put("device_token" , "1234");
                params.put("device_type" , "1");
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    /**
     * Google sign in
     */
    private fun googleSignin() {
        //val account = GoogleSignIn.getLastSignedInAccount(this)
        val signInIntent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Util.e("email" ,account.email.toString())
            Util.e("dpname" ,account.displayName.toString())
            Util.e("id" ,account.id.toString())
            Util.e("photo" ,account.photoUrl.toString())

            registrion(account)
            // Signed in successfully, show authenticated UI.

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("failed google", "signInResult:failed code=" + e.statusCode)

        }

    }

    fun registrion(account: GoogleSignInAccount) {
        val dialog = CusDialogProg(this);
        dialog.show();
        object : VolleyGetPost(this,this,WebService.Registraion, false){
            override fun onVolleyResponse(response: String?) {
               //{"status":"success","message":"User registration successfully done","userDetail":{"id":"3","first_name":"Dharmraj","last_name":"Acharya","social_id":"111490020457098783487","social_type":"google","email":"dharmraj.mindiii@gmail.com","country_code":"+","contact_no":"","profile_image":"","is_verified":"","auth_token":"bbd015a020622ba7023ee0071bbef2dc0a49b2da","device_type":"1","device_token":"1234"},"step":1}
            try {
                val obj = JSONObject(response)
                if (obj.getString("status").equals("success")) {
                    val auth_token = obj.getJSONObject("userDetail").getString("auth_token");
                    var intent = Intent(this@Sign_In_Activity, Sign_up_Activity::class.java)
                    intent.putExtra(Constants.DATA, arrayOf(obj.getString("step"), auth_token))
                    startActivity(intent)
                } else {

                }
            }catch (ex : Exception){

            }
                dialog.dismiss();
            }

            override fun onVolleyError(error: VolleyError?) {
                Util.e("response" , error.toString()!!)
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                val s = account.displayName.toString().split(" ")
                params.put("first_name",s[0])
                params.put("last_name",if(s.size>=2)s[1] else "")
                params.put("email",account.email.toString())
                params.put("contact_no","")
                params.put("device_token","1234")
                params.put("device_type","1")
                params.put("country_code","")
                params.put("social_id",account.id+"")
                params.put("social_type","google")
                params.put("profile_image",account.photoUrl.toString())
                return params

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }
        }.execute()


    }

}