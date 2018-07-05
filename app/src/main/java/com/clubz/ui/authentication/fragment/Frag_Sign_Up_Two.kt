package com.clubz.ui.authentication.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Toast
import com.android.volley.*
import com.clubz.*
import com.clubz.BuildConfig
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.helper.vollyemultipart.VolleySingleton
import com.clubz.data.model.User
import com.clubz.ui.authentication.Sign_up_Activity
import com.clubz.utils.*
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.frag_sign_up_two.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_Two : Fragment()  , View.OnClickListener, LinearLayoutThatDetectsSoftKeyboard.Listener {


    var isCameraSelected :Boolean=false
    var faebookLogin :Boolean=false
    var imageUri : Uri? = null
    var profilieImage :Bitmap? = null
    lateinit var _contact : String
    lateinit var _code : String
    lateinit var actvity : Sign_up_Activity
    var callbackManager: CallbackManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_two, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FacebookSdk.sdkInitialize(activity)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        FacebookSdk.setApplicationId(resources.getString(R.string.facebook_app_id))
        actvity = activity as Sign_up_Activity
        username.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        for(view in arrayOf(iv_capture ,next , google_lnr ,facebook_lnr ))view.setOnClickListener(this)
       /* try {
            val user : User = SessionManager.getObj().getUser()
            *//*username.setText(user.first_name)
            email.setText(user.email)*//*
        }catch (ex :Exception){

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
                    var delta = bottom - (sy + sh)
                    delta = delta - (rrScrollEnd.height+20)
                    scrollView.smoothScrollBy(0, delta)
                }
            }, 200)
        }
    }

    override fun onClick(p0: View?) {
        faebookLogin = false
        when(p0!!.id){
            R.id.iv_capture-> permissionPopUp()
            R.id.next -> if(verify()) signup()
            R.id.facebook_lnr->{
                faebookLogin = true
                facebooklogin()
            }

            R.id.google_lnr->{
                googleSignin()
            }
        }
    }


    /**
     * Google sign in_
     */
    private fun googleSignin() {
        //val account = GoogleSignIn.getLastSignedInAccount(this)
        actvity.dialog.show()
        val signInIntent = actvity.mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Util.e("email" ,account.email.toString())
            Util.e("dpname" ,account.displayName.toString())
            Util.e("id" ,account.id.toString())
            Util.e("photo" ,account.photoUrl.toString())
            actvity.dialog.dismiss()
            registrion(account)
        } catch (e: ApiException) {
            actvity.dialog.dismiss()
        }
    }

    fun setData( contact : String , code : String ) : Frag_Sign_Up_Two {
        _contact = contact
        _code = code
        return this
    }


    fun verify():Boolean{
        (activity as Sign_up_Activity).hideKeyBoard()
        if(username.text.toString().isBlank()){
            Util.showSnake(context, view!! ,R.string.a_full_name_new)
            return false
        }
        /*if(lastname.text.toString().isBlank()){
            Util.showSnake(context, view!! ,R.string.a_last_name)
            return false
        }*/
        if(email.text.toString().isBlank()){
            Util.showSnake(context, view!! ,R.string.a_email)
            return false
        }
        if(!email.text.toString().isBlank() && !PatternCheck.instance.check(PatternCheck._email, email.text.toString())){
            Util.showSnake(context, view!! ,R.string.a_email_new)
            return false
        }

        return true
    }

    private fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(activity, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, image_picker, Gravity.BOTTOM)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.itemId) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        when {
                            activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTCAMERA)
                            activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTREAD)
                            else -> callIntent(Constants.INTENTCAMERA)
                        }
                    } else {
                        callIntent(Constants.INTENTCAMERA)
                    }
                    R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                        isCameraSelected = false
                        if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTREAD)
                        } else {
                            callIntent(Constants.INTENTGALLERY)
                        }
                    } else {
                        callIntent(Constants.INTENTGALLERY)
                    }
                }
                return false
            }
        })
        popupMenu.show()
    }

    fun callIntent(caseid: Int) {

        when (caseid) {
            Constants.INTENTCAMERA -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val file = File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg")
                imageUri =
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file)
                        }else {
                            Uri.fromFile(file)}
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.INTERNET),
                        Constants.MY_PERMISSIONS_REQUEST_INTERNET)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(faebookLogin){
            actvity.dialog.dismiss()
            if(callbackManager==null) callbackManager = CallbackManager.Factory.create()
            callbackManager!!.onActivityResult(requestCode, resultCode, data)

        }else if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

        }else if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {

                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(context, requestCode, resultCode, data)
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(context,this)
                } else {
                    Toast.makeText(context ,R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(context,this)
                } else Toast.makeText(context ,R.string.swr , Toast.LENGTH_SHORT).show()
            }
             if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result : CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    if (result != null)
                        profilieImage = MediaStore.Images.Media.getBitmap(context.contentResolver, result.uri)
                    if (profilieImage != null) {
                        image_picker.setImageBitmap(profilieImage)
                    }
                } catch ( e : IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(context, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }

            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(context, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }

            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(context, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun signup( ){
        val dialog = CusDialogProg(context)
        dialog.show()
        if (Util.isConnectingToInternet(context)) {

            val multipartRequest = object : VolleyMultipartRequest(Request.Method.POST, WebService.Registraion, object : Response.Listener<NetworkResponse> {
                override fun onResponse(response: NetworkResponse) {
                    val data = String(response.data)
                    Util.e("data",data)
                    dialog.dismiss()
                    try {
                        val obj = JSONObject(data)
                        if(obj.getString("status") == "success"){
                            SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
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
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    dialog.dismiss()
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params["full_name"] = username.text.toString()
                    //params.put("last_name",lastname.text.toString())
                    params["email"] = email.text.toString()
                    params["contact_no"] = _contact
                    params["device_token"] = FirebaseInstanceId.getInstance().token!!
                    params["device_type"] = Constants.DEVICE_TYPE
                    params["country_code"] = "+"+_code
                    params["social_id"] = ""
                    params["social_type"] = ""
                    return params
                }

                override fun getHeaders(): MutableMap<String, String> {
                    params["language"] = SessionManager.getObj().language
                    return super.getHeaders()
                }

                override fun getByteData(): MutableMap<String, DataPart>? {
                    val params = java.util.HashMap<String, DataPart>()
                    if (profilieImage != null) {
                        params["profile_image"] = DataPart("profileImage.jpg", AppHelper.getFileDataFromDrawable(profilieImage), "image/jpeg")
                    }
                    return params
                }
            }
            val socketTimeout = 30000
            multipartRequest.retryPolicy = DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest)

        } else {
            dialog.dismiss()
            Toast.makeText(context, "Please Check internet connection.!", Toast.LENGTH_LONG).show()
        }
    }


    fun facebooklogin() {

        if (Util.isConnectingToInternet(context)) {
            LoginManager.getInstance().logOut()
            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"))
            callbackManager = CallbackManager.Factory.create()
//            CALLBACK = Constants.CALL_BACK_FB
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val progressDialog  = CusDialogProg(context, R.layout.custom_progress_dialog_layout)
                    progressDialog.setCancelable(false)
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()
                    Util.e(this.javaClass.name, "FACEBOOK" + loginResult.toString())
                    val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                    ) { `object`, response ->
                        Util.e(this.javaClass.name, "login result" + `object`.toString() + response.toString())
                        var fbEmail = ""
                        try {

                            fbEmail = try {
                                `object`.getString("email")
                            } catch (e: java.lang.Exception) {
                                `object`.getString("id") + ClubZ::class.java.simpleName + "@fb.com"
                            }

                            val FBid = `object`.getString("id")
                            val FBname = `object`.getString("name")
                            val FBgender = `object`.getString("gender")
                            val age = `object`.getJSONObject("age_range")
                            val fbImageurl = "https://graph.facebook.com/$FBid/picture?type=large"

                            val token = AccessToken.getCurrentAccessToken()
                            Util.e("access only Token is", (token.token).toString())
                            Util.e("image", fbImageurl)
                            Util.e("response", response.toString())
                            Util.e("Email", fbEmail)
                            Util.e("ID", FBid)
                            Util.e("Name", FBname)
                            Util.e("Fb Image", fbImageurl)
                            try {
                                Util.e("Fb BirthDay", `object`.getString("birthday"))
                            } catch (e: java.lang.Exception) {

                            }
                            progressDialog.dismiss()
                            registrion(null, arrayOf(FBid, FBname , fbImageurl , fbEmail))

                        } catch (e: JSONException) {
                            Toast.makeText(context, "Facebooklogin :something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,age_range,location")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Toast.makeText(context, "Cancelled by User", Toast.LENGTH_LONG).show()

                }

                override fun onError(error: FacebookException) {
                    //if(dialogProg != null) dialogProg.dismiss()
                    Log.e("Facebook", "Error" + error)
                    Toast.makeText(context, "Facebooklogin :something went wrong", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Plese connect to inernet", Toast.LENGTH_SHORT).show()
        }
    }

    fun registrion(account: GoogleSignInAccount?, arrayOf: Array<String> = arrayOf("")) {

        actvity.dialog.show()
        object : VolleyGetPost(activity,activity, WebService.Registraion, false){
            override fun onVolleyResponse(response: String?) {
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success") ) {
                        SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                        (activity as Sign_up_Activity).replaceFragment(Frag_Sign_UP_Three().setData(_contact ,_code ,obj.getJSONObject("userDetail").getString("auth_token"))) ////Its Temp
                        /*if(obj.has("step") && obj.getInt("step")==1){
                            var intent = Intent(actvity, Sign_up_Activity::class.java)
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
                            actvity.finish()
                        }else {
                            SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                            startActivity(Intent(context, HomeActivity::class.java))
                            actvity.finish()
                        }*/
                    } else {
                        Toast.makeText(context,obj.getString("message"),Toast.LENGTH_LONG).show()
                    }
                }catch (ex : Exception){
                    Toast.makeText(context,R.string.swr, Toast.LENGTH_LONG).show()
                }
                actvity.dialog.dismiss()
            }

            override fun onVolleyError(error: VolleyError?) {
                Util.e("response" , error.toString())
                actvity.dialog.dismiss()
            }

            override fun onNetError() {
                actvity.dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                if(account!=null){
                    params["social_id"] = account.id+""
                    params["social_type"] = "google"
                    params["full_name"] = account.displayName.toString()
                    params["email"] = account.email.toString()
                    params["profile_image"] = account.photoUrl.toString()
                }
                else{
                    params["social_id"] = arrayOf[0] +""
                    params["social_type"] = "facebook"
                    params["full_name"] = arrayOf[1]
                    params["profile_image"] = arrayOf[2]
                    params["email"] = arrayOf[3]
                }
                params["contact_no"] = _contact
                params["country_code"] = "+"+_code
                params["device_type"] = Constants.DEVICE_TYPE
                params["device_token"] = FirebaseInstanceId.getInstance().token!!
                Util.e("Param" ,params.toString())
                return params

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }
        }.execute()


    }
}

