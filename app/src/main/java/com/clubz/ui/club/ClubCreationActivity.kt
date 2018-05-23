package com.clubz.ui.club

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Club_Category
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.club.adapter.CreateClub_Spinner
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Purchase_membership_dialog
import com.clubz.utils.*
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_club_creation.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.NullPointerException
import java.util.*

class ClubCreationActivity : BaseActivity(), View.OnClickListener,
        DatePickerDialog.OnDateSetListener, View.OnTouchListener  {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        KeyboardUtil.hideKeyboard(this@ClubCreationActivity)
        return false
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val check = Date()
        check.year = p1-1900 ; check.month ; check.date = p3
        var d = Date(System.currentTimeMillis() - 1000)
        d.hours =0
        d.minutes=0
        d.seconds=0
        Util.e("Tag", "$d : ${p0!!.minDate} : $check")
        year = p1 ; month = p2+1 ;day = p3;
        tv_fondationdate.setText(Util.convertDate("$year-$month-$day"))
    }


    var clubImage : Bitmap? = null
    var clubIcon : Bitmap? = null
    var isCameraSelected : Boolean = false
    var isClubIcon : Boolean = false
    var imageUri : Uri? = null
    lateinit var  autocompleteFragment1 : PlaceAutocompleteFragment
    var lat = 0.0
    var lng = 0.0
    var isvalidate: Boolean = false;


    var day = -1
    var month = -1
    var year = -1

    //lateinit var titile_name : EditText //Because emoji not supported
    //lateinit var etv_description: EditText //Because emoji not supported


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_creation)



        for(views in arrayOf(img_club ,tv_fondationdate , iv_like ,done ,back_f, all , arow ,image_icon))views.setOnClickListener(this)
        try{
            autocompleteFragment1 = fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment
            // var autocompleteFragment  =( activity as HomeActivity).supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment;
            autocompleteFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(p0: Place?) {
                    club_location.setText(p0!!.name)
                    club_location.setSelected(true)
                    lat = p0.latLng.latitude
                    lng = p0.latLng.longitude
                }


                override fun onError(p0: Status?) {
                    Util.e("User", "An error occurred: " + p0)
                }
            })}catch (ex :Exception){
            ex.printStackTrace()
        }

        val list = Arrays.asList(*resources.getStringArray(R.array.privacy_type))
        spn_privacy.adapter = CreateClub_Spinner(this, list, Constants.CreateClub_Spinner_Type_privacy_type)
        getCategory()
        username.setText(SessionManager.getObj().user.full_name)
        try{

            if(!SessionManager.getObj().user.profile_image.isNullOrEmpty()){
                Picasso.with(this).load(SessionManager.getObj().user.profile_image).transform(CircleTransform()).placeholder(R.drawable.ic_user_shape).into(image_member2, object : Callback {
                    override fun onSuccess() {
                        image_member2.setPadding(0,0,0,0)
                    }

                    override fun onError() {

                    }
                })
            }

        }catch (ex :Exception){
            ex.printStackTrace()
        }catch (ex : NullPointerException){
            ex.printStackTrace()
        }
        club_phone.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_NEXT) {
                    club_adres.requestFocus()               }
                return false
            }
        })
        for(spinner in arrayOf(spn_privacy,spn_club_category))spinner.setOnTouchListener(this)

        Handler().postDelayed(Runnable {
            object : Purchase_membership_dialog(this) {
                override fun viewplansListner() {
                    this.dismiss();
                }
            }.show()
        }, 100)
    }



    override fun onClick(p0: View?) {
       hideKeyBoard()
        when(p0!!.id){
            R.id.img_club-> { isClubIcon = false;   permissionPopUp();  }
            R.id.tv_fondationdate , R.id.iv_like , R.id.arow->{
                datePicker(day,month,year)
            }
            R.id.done-> if(validator())crateClub()
            R.id.back_f->{
                onBackPressed()
            }
            R.id.image_icon->{ isClubIcon = true;   permissionPopUp();            }
        }

    }


    /*fun hideKeyBoard() {
        try {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {

        }
    }
*/
    internal fun datePicker(i1: Int, i2: Int, i3: Int) {
        val calendar = GregorianCalendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DATE)
        if (i1 != -1) {
            day = i1
            month = i2 - 1
            year = i3
        }
        val datepickerdialog = DatePickerDialog(this@ClubCreationActivity, R.style.DialogTheme2, this, year, month, day)
        // datepickerdialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datepickerdialog.window!!.setBackgroundDrawableResource(R.color.white)

        datepickerdialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@ClubCreationActivity, requestCode, resultCode, data);
                if (imageUri != null) {
                    if(!isClubIcon)
                        CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setMinCropResultSize(300,200)
                                .setMaxCropResultSize(4000,4000)
                                .setAspectRatio(300, 200).start(this@ClubCreationActivity)
                    else CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200,200)
                            .setMaxCropResultSize(4000,4000)
                            .setAspectRatio(300, 300).start(this@ClubCreationActivity)
                } else {
                    Toast.makeText(this@ClubCreationActivity ,R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    if(!isClubIcon)
                        CropImage.activity(imageUri)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setMinCropResultSize(300,200)
                                .setMaxCropResultSize(4000,4000)
                                .setAspectRatio(300, 200).start(this@ClubCreationActivity)
                    else CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200,200)
                            .setMaxCropResultSize(4000,4000)
                            .setAspectRatio(300, 300).start(this@ClubCreationActivity)
                } else {
                    Toast.makeText(this@ClubCreationActivity ,R.string.swr , Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                var result : CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    if (result != null)
                        if(isClubIcon){
                            clubIcon = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.getContentResolver(), result.getUri())

                            if (clubIcon != null) {
                                image_icon.setPadding(0,0,0,0)
                                image_icon.setImageBitmap(CircleTransform_NoRecycle().transform(clubIcon))
                            }
                        }
                        else
                        {    clubImage = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.getContentResolver(), result.getUri())


                            if (clubImage != null) {
                                img_club.setImageBitmap(clubImage)
                            }}
                } catch ( e : IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this@ClubCreationActivity, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, if(isClubIcon) image_icon else img_club, Gravity.CENTER)
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.getItemId()) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        if (this@ClubCreationActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTCAMERA)
                        }
                        else if (this@ClubCreationActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                        if (this@ClubCreationActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                var file  = File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg");
                imageUri =
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                            FileProvider.getUriForFile(this@ClubCreationActivity, BuildConfig.APPLICATION_ID + ".provider",file)
                        }else {
                            Uri.fromFile(file)}
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this);
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@ClubCreationActivity, arrayOf(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this@ClubCreationActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this@ClubCreationActivity, arrayOf(Manifest.permission.INTERNET),
                        Constants.MY_PERMISSIONS_REQUEST_INTERNET)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(this@ClubCreationActivity, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@ClubCreationActivity, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@ClubCreationActivity, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun crateClub() {
        val activity = this@ClubCreationActivity
        val dialog = CusDialogProg(this@ClubCreationActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.crate_club,object : Response.Listener<NetworkResponse> {
            override fun onResponse(response: NetworkResponse) {
                val data = String(response.data)
                Util.e("data",data)
                dialog.dismiss()
                //{"status":"success","message":"Club added successfully"}
                try {
                    val obj = JSONObject(data)
                    if(obj.getString("status").equals("success")){
                        Toast.makeText(this@ClubCreationActivity,obj.getString("message"), Toast.LENGTH_LONG).show()
                       finish()
                    }else{
                        Toast.makeText(this@ClubCreationActivity,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch ( e : java.lang.Exception){
                    e.printStackTrace()
                    Toast.makeText(this@ClubCreationActivity,R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                dialog.dismiss()
                Toast.makeText(this@ClubCreationActivity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params.put("city", ClubZ.city)
                params.put("clubName",titile_name.text.toString())

                params.put("clubType",if(spn_privacy.selectedItem.toString().toLowerCase().equals("public"))"1" else "2") // 1 public 2 private
                params.put("clubCategoryId",(spn_club_category.selectedItem as Club_Category).clubCategoryId)

                params.put("clubEmail",club_email.text.toString())
                params.put("clubContactNo",club_phone.text.toString())

                params.put("clubCountryCode", SessionManager.getObj().user.country_code)// TODO In Ui

                params.put("clubAddress",club_adres.text.toString())
                params.put("clubLatitude",lat.toString())
                params.put("clubWebsite",club_web.text.toString())
                params.put("clubLongitude",lng.toString())


                params.put("termsConditions",terms_n_condition.text.toString())

                params.put("clubFoundationDate",tv_fondationdate.text.toString())
                params.put("clubLocation",club_location.text.toString())
                params.put("userRole",usrerole.text.toString()+"")

                params.put("clubDescription",etv_description.getText().toString()+"") //*\\StringEscapeUtils.escapeJava(etv_description.getText().toString()).replace("\\uD83D"," \\uD83D")+"")*//*
                Util.e("parms create", params.toString())
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (clubImage != null) {
                    params.put("clubImage",DataPart("club_image.jpg", AppHelper.getFileDataFromDrawable(clubImage), "image*//*"))
                }
                if (clubIcon != null) {
                    params.put("clubIcon",DataPart("club_icon.jpg", AppHelper.getFileDataFromDrawable(clubIcon), "image*//*"))
                }
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }
        request.setRetryPolicy(DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        ClubZ.instance.addToRequestQueue(request)
    }



    fun getCategory(){
        val activity = this@ClubCreationActivity
        val dialog = CusDialogProg(this@ClubCreationActivity)
        dialog.show()
        object : VolleyGetPost(activity , activity, WebService.club_category,true){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {
                    val json = JSONObject(response)
                    if(json.getString("status").equals("success")){
                        val list  = Gson().fromJson<ArrayList<Club_Category>>(json.getJSONArray("data").toString() , Type_Token.club_category)
                        spn_club_category.adapter = CreateClub_Spinner(this@ClubCreationActivity, list, Constants.CreateClub_Spinner_Type_ClubCategory)
                    }else{
//TODO check all failure conditions //
                        Util.showToast(json.getString("message"),this@ClubCreationActivity)
                    }
                }catch (ex :Exception){
                    Util.e("Error", ex.toString())
                    Util.showToast(R.string.swr,this@ClubCreationActivity)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                Util.e("error", error.toString())
                dialog.dismiss()

            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute()
    }

    fun  validator() :Boolean{
        //hideKeyBoard()
        checkPhoneNumber(SessionManager.getObj().user.country_code.replace("+",""))
        if(titile_name.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_clubnme)
            return false
        }
        if(clubImage==null ){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_image)
            return false
        }
        if(tv_fondationdate.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_foundation)
            return false
        }
        if(club_email.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_email)
            return false
        }
        if(!club_email.text.toString().isBlank() && !PatternCheck.instance.check(PatternCheck._email, club_email.text.toString())){
            Util.showSnake(this@ClubCreationActivity, clRootView!! ,R.string.a_email_valid)
            return false
        }
        if(club_phone.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_phone_club)
            return false
        }
        /*if(!isvalidate){
            Util.showSnake(context, view!!,R.string.a_phone_no_valid);
            return false
        }*/
        if(club_adres.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_address)
            return false
        }
        if(club_location.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_location)
            return false
        }
        if(club_location.text.toString().isBlank() || (lat==0.0 && lng == 0.0)){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_location)
            return false
        }
        if(club_web.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_web)
            return false
        }
        if(!android.util.Patterns.WEB_URL.matcher(club_web.text.toString()).matches()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_web_valid)
            return false
        }
        if(etv_description.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_description)
            return false
        }
        if(terms_n_condition.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_terms_n_con)
            return false
        }
        if(usrerole.text.toString().isBlank()){
            Util.showSnake(this@ClubCreationActivity,clRootView!!,R.string.a_userRole)
            return false
        }
        return true
    }


    private fun checkPhoneNumber( countryCode : String) {
        val contactNo = club_phone.getText().toString()
        try {
            val phoneUtil = PhoneNumberUtil.createInstance(this@ClubCreationActivity)
            val code = countryCode.toUpperCase()
            val swissNumberProto = phoneUtil.parse(contactNo, code)
            isvalidate = phoneUtil.isValidNumber(swissNumberProto)
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: " + e.toString())
        }
    }

}
