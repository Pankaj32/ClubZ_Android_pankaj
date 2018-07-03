package com.clubz.ui.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.authentication.adapter.Country_spinner_adapter
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.data.remote.WebService
import com.clubz.data.model.Country_Code
import com.clubz.data.model.User
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.Constants
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_signin.*
import org.json.JSONObject
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.clubz.utils.Constants.RC_SIGN_IN
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import java.util.*


/**
 * Created by mindiii on 2/2/18.
 */
class Sign_In_Activity : AppCompatActivity(), View.OnClickListener {



    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var callbackManager: CallbackManager
    var isvalidate: Boolean = false;
    lateinit var dialog :CusDialogProg ;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        setContentView(R.layout.activity_signin);
        dialog = CusDialogProg(this);
        Util.checklaunage(this)
        for(view in arrayOf(next ,google_lnr , facebook_lnr,sign_up ))view.setOnClickListener(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val list = Gson().fromJson<String>(Util.loadJSONFromAsset(this,"country_code.json"), Type_Token.country_list) as ArrayList<Country_Code>
        country_code.adapter = Country_spinner_adapter(this, list, 0, R.layout.spinner_view);
        setCountryCode(list , country_code)


    }

    fun setCountryCode(list : ArrayList<Country_Code> , spinner : Spinner){
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var locale = tm.networkCountryIso
        if(locale.equals("")) locale ="in_";
        Util.e("phone no" , locale);
        for(i  in 0..list.size-1){
            if(list.get(i).code.equals(locale)){spinner.setSelection(i) ; return }
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.sign_up ->startActivity(Intent(this@Sign_In_Activity, Sign_up_Activity::class.java))
            R.id.next -> if(verify()) login()
            R.id.google_lnr ->   googleSignin()
            R.id.facebook_lnr -> try {
                facebooklogin()
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
    }

    fun verify() :Boolean{
        hideKeyBoard()
        checkPhoneNumber((country_code.selectedItem as Country_Code).code)
        if(phone_no.text.isBlank()){
            Util.showSnake(this,getWindow().getDecorView().getRootView(), R.string.a_phone_no);

            return false;
        }
        if(!isvalidate){
            Util.showSnake(this,getWindow().getDecorView().getRootView(), R.string.a_phone_no_valid);
            return false
        }
        return true;
    }
    fun hideKeyBoard() {
        try {
            val inputManager = getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
        }
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


    private fun login(){
        val dialog = CusDialogProg(this);
        dialog.show()
        object  : VolleyGetPost(this,this, WebService.login_Otp,false) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Registered successfully, Generate verify code successfully sent","otp":"3319","step":1}
                //E/response: http://clubz.co/dev/service/login{"status":"success","message":"OTP has been sent to your contact number","otp":"9690"}

                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        var intent = Intent (this@Sign_In_Activity, Otp_activity::class.java)
                        intent.putExtra(Constants.DATA ,arrayOf(obj.getString("otp") ,phone_no.text.toString() , "+"+(country_code.selectedItem as Country_Code).phone_code , if(obj.has("step"))obj.getString("step") else {""}))
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@Sign_In_Activity,obj.getString("message"),Toast.LENGTH_LONG).show()
                    }
                }catch (ex :Exception){
                    Toast.makeText(this@Sign_In_Activity, R.string.swr, Toast.LENGTH_LONG).show()
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
                params.put("device_token" , FirebaseInstanceId.getInstance().getToken()!!);
                params.put("device_type" , Constants.DEVICE_TYPE);
                Util.e("params" , params.toString())
                return params;

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put( "language", SessionManager.getObj().getLanguage());
                Util.e("headers" , params.toString())
                return params

            }
        }.execute()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }


    /**
     * Google sign in_
     */
    private fun googleSignin() {
        //val account = GoogleSignIn.getLastSignedInAccount(this)
        dialog.show()
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
            // Signed in_ successfully, show authenticated UI.

        } catch (e: ApiException) {
            dialog.dismiss()
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("failed google", "signInResult:failed code=" + e.statusCode)

        }

    }

    fun registrion(account: GoogleSignInAccount?, arrayOf: Array<String> = arrayOf("")) {
        dialog.show();
        object : VolleyGetPost(this,this, WebService.Chek_Social, false){
            override fun onVolleyResponse(response: String?) {
               //{"status":"success","message":"User registration successfully done","userDetail":{"id":"3","first_name":"Dharmraj","last_name":"Acharya","social_id":"111490020457098783487","social_type":"google","email":"dharmraj.mindiii@gmail.com","country_code":"+","contact_no":"","profile_image":"","is_verified":"","auth_token":"bbd015a020622ba7023ee0071bbef2dc0a49b2da","device_type":"1","device_token":"1234"},"step":1}
            try {
                val obj = JSONObject(response)
                if (obj.getString("status").equals("success") ) {
                    //{"status":"success","step":1}
                 if(obj.has("step") && obj.getInt("step")==1){
                     var intent = Intent(this@Sign_In_Activity, Sign_up_Activity::class.java)
                        if(account!=null){
                            intent.putExtra(Constants._first_name,account.displayName)
                            intent.putExtra(Constants._email,account.email.toString())
                            intent.putExtra(Constants._social_id,account.id+"")
                            intent.putExtra(Constants._social_type,Constants._google)
                            intent.putExtra(Constants._profile_image,account.photoUrl.toString())

                        }
                        else{
                            intent.putExtra(Constants._first_name,arrayOf.get(1))
                            intent.putExtra(Constants._email,arrayOf.get(3))
                            intent.putExtra(Constants._social_id,arrayOf.get(0)+"")
                            intent.putExtra(Constants._social_type,Constants._facebook)
                            intent.putExtra(Constants._profile_image,arrayOf.get(2))
                        }
                     startActivity(intent)
                     finish()
                 }else {
                     SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                     ClubZ.currentUser = SessionManager.getObj().user
                     startActivity(Intent(this@Sign_In_Activity, HomeActivity::class.java))
                     finish()
                 }
                } else {
                    Toast.makeText(this@Sign_In_Activity,obj.getString("message"),Toast.LENGTH_LONG).show()
                }
            }catch (ex : Exception){
                Toast.makeText(this@Sign_In_Activity, R.string.swr, Toast.LENGTH_LONG).show()
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
                if(account!=null){
                    params.put("social_id",account.id+"")
                    params.put("social_type","google")
                }
                else{
                    params.put("social_id",arrayOf.get(0)+"")
                    params.put("social_type","facebook")

                }
                params.put("device_type",Constants.DEVICE_TYPE)
                params.put("device_token", FirebaseInstanceId.getInstance().getToken()!!)
                Util.e("Param" ,params.toString());
                return params

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {

                return params
            }
        }.execute()


    }

    fun facebooklogin() {

        if (Util.isConnectingToInternet(applicationContext)) {
            LoginManager.getInstance().logOut()
            LoginManager.getInstance().logInWithReadPermissions(this@Sign_In_Activity, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"))
            callbackManager = CallbackManager.Factory.create()
//            CALLBACK = Constants.CALL_BACK_FB
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    var progressDialog  = CusDialogProg(this@Sign_In_Activity, R.layout.custom_progress_dialog_layout)
                    progressDialog.setCancelable(false)
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()
                    Util.e(this.javaClass.name, "FACEBOOK" + loginResult.toString())
                    val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                    ) { `object`, response ->
                        Util.e(this.javaClass.name, "login result" + `object`.toString() + response.toString())
                        //login result{"id":"302008740295822","name":"Thomas Lewis","email":"ratnesh.mindiii@gmail.com","gender":"male","age_range":{"min":21}}{Response:  responseCode: 200, graphObject: {"id":"302008740295822","name":"Thomas Lewis","email":"ratnesh.mindiii@gmail.com","gender":"male","age_range":{"min":21}}, error: null}
                        //{"status":"success","message":"User registration successfully done","userDetail":{"userId":"5","first_name":"Donie","last_name":"Darko","social_id":"345224962638213","social_type":"faceboook","email":"ctom1206@gmail.com","country_code":"+","contact_no":"","profile_image":"","is_verified":"","auth_token":"cf68674b96fcb0bbb722e97514b23941c8b2bd85","device_type":"1","device_token":"1234"},"step":1}
                        var FBemail: String =""
                        try {

                            try {
                                FBemail = `object`.getString("email")
                            } catch (e: java.lang.Exception) {
                               // FBemail = `object`.getString("id") + ".scenekey" + "@fb.com"
                            }

                            val FBid = `object`.getString("id")
                            val FBname = `object`.getString("name")
                            val FBgender = `object`.getString("gender")
                            val age = `object`.getJSONObject("age_range")
                            val FBimageurl = "https://graph.facebook.com/$FBid/picture?type=large"

                            val token = AccessToken.getCurrentAccessToken()
                            Util.e("access only Token is", (token.token).toString())
                            Util.e("image", FBimageurl)
                            Util.e("response", response.toString())
                            Util.e("Email", FBemail)
                            Util.e("ID", FBid)
                            Util.e("Name", FBname)
                            Util.e("Fb Image", FBimageurl)
                            try {
                                Util.e("Fb BirthDay", `object`.getString("birthday"))
                            } catch (e: java.lang.Exception) {

                            }
                            progressDialog.dismiss();
                            registrion(null, arrayOf(FBid, FBname , FBimageurl , FBemail))

                        } catch (e: Exception) {
                            Toast.makeText(this@Sign_In_Activity, "Facebooklogin :something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val parameters = Bundle()
                    //parameters.putString("fields", "id,name,email,gender,user_birthday,age_range,location");
                    parameters.putString("fields", "id,name,email,gender,age_range,location")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    //if(dialogProg != null) dialogProg.dismiss()
                    Toast.makeText(this@Sign_In_Activity, "Cancelled by User", Toast.LENGTH_LONG).show()

                }

                override fun onError(error: FacebookException) {
                    //if(dialogProg != null) dialogProg.dismiss()
                    Log.e("Facebook", "Error" + error)
                    Toast.makeText(this@Sign_In_Activity, "Facebooklogin :something went wrong", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Plese connect to inernet", Toast.LENGTH_SHORT).show()
        }
    }


    /*{contact_no=9752287913, auth_token=, country_code=91} : http://clubz.co/dev/service/generateOtp{"status":"fail","message":"Contact number already exist"}*/
}