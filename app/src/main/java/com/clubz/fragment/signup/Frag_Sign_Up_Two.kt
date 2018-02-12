package com.clubz.fragment.signup

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
import android.view.*
import android.widget.Toast
import com.android.volley.*
import com.clubz.BuildConfig
import com.clubz.Cropper.CropImage
import com.clubz.Cropper.CropImageView
import com.clubz.R
import com.clubz.Sign_up_Activity
import com.clubz.helper.WebService
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.util.Constants
import com.clubz.util.PatternCheck
import com.clubz.util.Util
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.frag_sign_up_two.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception

/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_Two : Fragment()  , View.OnClickListener {


    var isCameraSelected :Boolean=false ;
    var imageUri : Uri? = null;
    var profilieImage :Bitmap? = null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_two, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(view in arrayOf(iv_capture ,next ))view.setOnClickListener(this)


    }


    override fun onClick(p0: View?) {
     when(p0!!.id){
         R.id.iv_capture-> permissionPopUp();
         R.id.next -> if(verify())(activity as Sign_up_Activity).replaceFragment(Frag_Sign_UP_Three());
     }
    }

    fun verify():Boolean{
        (activity as Sign_up_Activity).hideKeyBoard();
        if(username.text.toString().isBlank()){
            Util.showSnake(context, view!! ,R.string.a_firstname)
            return false
        }
        if(lastname.text.toString().isBlank()){
            Util.showSnake(context, view!! ,R.string.a_last_name)
            return false
        }
        if(!email.text.toString().isBlank() && !PatternCheck.instance.check(PatternCheck._email, email.text.toString())){
            Util.showSnake(context, view!! ,R.string.a_email_valid)
            return false
        }

        return true
    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(activity, R.style.popstyle);
        val popupMenu = PopupMenu(wrapper, image_picker, Gravity.BOTTOM)
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.getItemId()) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTCAMERA)
                        }
                        else if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTREAD)
                        }
                        else {
                            callIntent(Constants.INTENTCAMERA)
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
                var file : File = File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg");
                imageUri =
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file)
                        }else {
                            Uri.fromFile(file)}
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//USE file code in this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this);
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

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {

                imageUri = com.clubz.Picker.ImagePicker.getImageURIFromResult(context, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(context,this);
                } else {
                    Toast.makeText(context ,R.string.swr, Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(context,this);
                } else {
                    Toast.makeText(context ,R.string.swr , Toast.LENGTH_SHORT).show();
                }


            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                var result : CropImage.ActivityResult = CropImage.getActivityResult(data);
                try {
                    if (result != null)
                        profilieImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), result.getUri());


                    if (profilieImage != null) {
                        image_picker.setImageBitmap(profilieImage)
                    }
                } catch ( e : IOException) {
                    e.printStackTrace();
                }

            }

        }

        isCameraSelected = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
//TODO in string resources
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(context, "Permission denied can't select image", Toast.LENGTH_LONG).show()
                }
            }

            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(context, "Camera  permission denied ", Toast.LENGTH_LONG).show()
            }

            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(context, "Permission not granted for Read", Toast.LENGTH_LONG).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun signup( ){

        /*if (Util.isConnectingToInternet(context)) {

            val multipartRequest = object : VolleyMultipartRequest(Request.Method.POST, WebService.Registraion, object : Response.Listener<NetworkResponse> {
                override fun onResponse(response: NetworkResponse) {
                    val data = String(response.data)

                    progressDialog.dismiss()
                    //{"status":"success","message":"User registration successfully done","userDetail":{"id":"26","userName":"","fullName":"Ratnesh","email":"ratnesh.mindiii@gmail.com","userType":"user","countryCode":"","contactNumber":"1234567890","authToken":"a56f75eef07d22577eada7b75ef4d7c139bc5f10","status":"1","createdOn":"2017-11-14 05:53:22","image":""}}
                    //{"status":"fail","message":"<p>The image you are attempting to upload doesn't fit into the allowed dimensions.<\/p>"}
                    try {
                        val obj = JSONObject(data)
                        if(obj.getString("status").equals("success")){

                            if(type.equals(Constants.TYPE_USER)){
                                val user2 :com.tulia.Models.User = Gson().fromJson(obj.getJSONObject("userDetail").toString(), com.tulia.Models.User::class.java)
                                user2.isSocial = false
                                SessionManager(this@SignUP_Activity).createSession(user2,false,"","");
                                //  Toast.makeText(this@SignUP_Activity,obj.getString("message").trim(),Toast.LENGTH_LONG).show();
                                val intent =Intent(this@SignUP_Activity,Home_Activity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                                return
                            }
                            if(type.equals(Constants.TYPE_VENDOR)){
                                //{"status":"success","message":"User registration successfully done","userDetail":{"id":"3","userName":"","fullName":"Thomas Lewis","email":"ratnesh.mindiii@gmail.com","userType":"vendor","countryCode":"+91","contactNumber":"","address":"","authToken":"e35bce1486a2dba047f71aa2bd9fc598e8cac98f","status":"1","createdOn":"2017-11-22 09:27:59","image":"https:\/\/graph.facebook.com\/302008740295822\/picture?type=large","thumbImage":"https:\/\/graph.facebook.com\/302008740295822\/picture?type=large"}}
                                if((if(obj.has("messageCode"))obj.getString("messageCode").equals(Constants.VENDOR_social_reg)else{false}) || obj.getJSONObject("userDetail").getString("category").isBlank()){
                                    progressDialog.dismiss()
                                    Toast.makeText(this@SignUP_Activity,R.string.categorye,Toast.LENGTH_SHORT).show()
                                    categoryPop(user)
                                }
                                else{

                                    var vendor:Vendor  = Gson().fromJson(obj.getJSONObject("userDetail").toString(), Vendor::class.java)
                                    vendor.isSocial = false
                                    SessionManager(this@SignUP_Activity).createSession(vendor,false,"","");
                                    //Toast.makeText(this@SignUP_Activity,obj.getString("message").trim(),Toast.LENGTH_LONG).show();
                                    var intent =Intent(this@SignUP_Activity,Home_Activity::class.java)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    finish()

                                }
                            }
                        }else{
                            Toast.makeText(this@SignUP_Activity,obj.getString("message").trim(),Toast.LENGTH_LONG).show();
                        }

                    }catch ( e : Exception){
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                    progressDialog.dismiss()

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params.put("userName",user.userName)
                    params.put("fullName",user.fullName)
                    params.put("email",user.email)
                    params.put("password",user.password)
                    params.put("userType",type)
                    params.put("contactNumber",user.contactNumber)
                    params.put("deviceType","1")
                    params.put("deviceToken", FirebaseInstanceId.getInstance().getToken()!!)
                    params.put("countryCode","+254")
                    params.put("contact","")
                    params.put("socialId",user.socialId)
                    params.put("socialType",user.socialType)
                    if(user.category!=null) params.put("category",user.category+"")
                    if(!user.userimage.isBlank())params.put("image",user.userimage)
                    Util.printBigLogcat(this@SignUP_Activity.javaClass.name,params.toString())
                    return params
                }


                override fun getHeaders(): MutableMap<String, String> {
                    return super.getHeaders()
                }


                override fun getByteData(): MutableMap<String, DataPart>? {
                    val params = java.util.HashMap<String, DataPart>()


                    if (profilieImage != null) {
                        params.put("image", DataPart("profileImage.jpg", AppHelper.getFileDataFromDrawable(profilieImage), "image/jpeg"))
                        Util.printBigLogcat("image ",params.toString())
                    }
                    return params
                }
            }
            val socketTimeout = 30000;
            multipartRequest.setRetryPolicy(DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest)

        } else {
            if (progressDialog != null) progressDialog.dismiss()
            Toast.makeText(applicationContext, "Please Check internet connection.!", Toast.LENGTH_LONG).show()
        }*/
    }
}