package com.clubz.ui.club.fragment

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.android.volley.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import com.clubz.*
import com.clubz.BuildConfig
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.ui.club.adapter.CreateClub_Spinner
import com.clubz.data.local.pref.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.data.model.Club_Category
import com.clubz.ui.core.BaseFragment
import com.clubz.ui.newsfeed.fragment.FragNewsList
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.*
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.frag_create_club.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.NullPointerException
import java.util.*

/**
 * Created by mindiii on 3/10/18.
 */

class Frag_Create_club : BaseFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        (activity as HomeActivity).hideKeyBoard()
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
            year = p1 ; month = p2+1 ;day = p3
        tv_fondationdate.setText(Util.convertDate("$year-$month-$day"))
    }


    var clubImage : Bitmap? = null
    var clubIcon : Bitmap? = null
    var isCameraSelected : Boolean = false
    var isClubIcon : Boolean = false
    var imageUri : Uri? = null
    lateinit var  autocompleteFragment1 :PlaceAutocompleteFragment
    var lat = 0.0
    var lng = 0.0
    var isvalidate: Boolean = false


    var day = -1
    var month = -1
    var year = -1

    lateinit var titile_name :EditText //Because emoji not supported
    lateinit var etv_description:EditText  //Because emoji not supported

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_create_club, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener(this)
        etv_description = view.findViewById<EditText>(R.id.etv_description)
        titile_name = view.findViewById<EditText>(R.id.titile_name)
        for(views in arrayOf(img_club ,tv_fondationdate , iv_like ,done ,back_f, all , arow ,image_icon))views.setOnClickListener(this)
         try{
             autocompleteFragment1 = activity?.fragmentManager?.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment
            // var autocompleteFragment  =( activity as HomeActivity).supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment;
             autocompleteFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place?) {
                club_location.text = p0!!.name
                club_location.isSelected = true
                lat = p0.latLng.latitude
                lng = p0.latLng.longitude
            }


            override fun onError(p0: Status?) {
                Util.e("User", "An error occurred: " + p0)
            }
        })}catch (ex :Exception){
             ex.printStackTrace()
         }

       val list = Arrays.asList(*context!!.resources.getStringArray(R.array.privacy_type))
       spn_privacy.adapter = CreateClub_Spinner(context!!, list, Constants.CreateClub_Spinner_Type_privacy_type)
       getCategory()
        username.text = SessionManager.getObj().user.full_name
        try{
            Glide.with(image_member2.context)
                    .load(SessionManager.getObj().user.profile_image)
                    /*.placeholder(R.drawable.ic_user_shape)
                    .fitCenter()*/
                    .into(image_member2)


        }catch (ex :Exception){
            ex.printStackTrace()
        }catch (ex :NullPointerException){
            ex.printStackTrace()
        }
        club_phone.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_NEXT) {
                    club_adres.requestFocus()               }
                return false
            }
        })
        for(spinner in arrayOf(spn_privacy,spn_club_category))spinner.setOnTouchListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            (activity as HomeActivity).showStatusBar()
            if(activity!=null)activity?.fragmentManager?.beginTransaction()?.remove(autocompleteFragment1)?.commit()
        }catch (ex :Exception){
        }

    }




    override fun onClick(p0: View?) {
        (activity as HomeActivity).hideKeyBoard()
        when(p0!!.id){
            R.id.img_club-> { isClubIcon = false;   permissionPopUp();  }
            R.id.tv_fondationdate , R.id.iv_like , R.id.arow->{
                datePicker(day,month,year)
            }
            R.id.done-> if(validator())crateClub()
            R.id.back_f->{
                activity?.onBackPressed()
            }
            R.id.image_icon->{ isClubIcon = true;   permissionPopUp();            }
        }

    }

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
        val datepickerdialog = DatePickerDialog(context, R.style.DialogTheme2, this, year, month, day)
       // datepickerdialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datepickerdialog.window!!.setBackgroundDrawableResource(R.color.white)

        datepickerdialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(context, requestCode, resultCode, data)
                if (imageUri != null) {
                   if(!isClubIcon) CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300,200).setMaxCropResultSize(4000,4000).setAspectRatio(300, 200).start(context!!,this)
                    else CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(200,200).setMaxCropResultSize(4000,4000).setAspectRatio(300, 300).start(context!!,this)
                } else {
                    Toast.makeText(context ,R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    if(!isClubIcon) CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300,200).setMaxCropResultSize(4000,4000).setAspectRatio(300, 200).start(context!!,this)
                    else CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(200,200).setMaxCropResultSize(4000,4000).setAspectRatio(300, 300).start(context!!,this)
                } else {
                    Toast.makeText(context ,R.string.swr , Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result : CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    if (result != null)
                    if(isClubIcon){
                        clubIcon = MediaStore.Images.Media.getBitmap(context?.contentResolver, result.uri)

                        if (clubIcon != null) {
                            image_icon.setPadding(0,0,0,0)
                            image_icon.setImageBitmap(CircleTransform_NoRecycle().transform(clubIcon))
                        }
                    }
                    else
                    {    clubImage = MediaStore.Images.Media.getBitmap(context?.contentResolver, result.uri)


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
        val wrapper = ContextThemeWrapper(activity, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, if(isClubIcon) image_icon else img_club, Gravity.CENTER)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            isCameraSelected = true
            when (item.itemId) {
                R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                    if (activity?.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constants.INTENTREQUESTCAMERA)
                    } else if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constants.INTENTREQUESTREAD)
                    } else {
                        callIntent(Constants.INTENTCAMERA)
                    }
                } else {
                    callIntent(Constants.INTENTCAMERA)
                }
                R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                    isCameraSelected = false
                    if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constants.INTENTREQUESTREAD)
                    } else {
                        callIntent(Constants.INTENTGALLERY)
                    }
                } else {
                    callIntent(Constants.INTENTGALLERY)
                }
            }
            false
        }
        popupMenu.show()
    }
    fun callIntent(caseid: Int) {

        when (caseid) {
            Constants.INTENTCAMERA -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val file  = File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg")
                imageUri =
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                            FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider",file)
                        }else {
                            Uri.fromFile(file)}
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                val intentgallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentgallery, Constants.SELECT_FILE)
                //ImagePicker.pickImage(this)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.INTERNET),
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

    fun crateClub() {
        val activity = activity as HomeActivity
        val dialog = CusDialogProg(context)
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
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                        activity.replaceFragment(FragNewsList())
                    }else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch ( e : java.lang.Exception){
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

                params.put("clubDescription",etv_description.text.toString()+"") //*\\StringEscapeUtils.escapeJava(etv_description.getText().toString()).replace("\\uD83D"," \\uD83D")+"")*//*
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
                params.put("language", SessionManager.getObj().language)
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        ClubZ.instance.addToRequestQueue(request)
    }



    fun getCategory(){
        val activity = activity as HomeActivity
        val dialog = CusDialogProg(context)
        dialog.show()
         object : VolleyGetPost(activity , activity, WebService.club_category,true,
                 true){
             override fun onVolleyResponse(response: String?) {
                 dialog.dismiss()
                 try {
                       val json = JSONObject(response)
                     if(json.getString("status").equals("success")){
                         val list  = Gson().fromJson<ArrayList<Club_Category>>(json.getJSONArray("data").toString() , Type_Token.club_category)
                         spn_club_category.adapter = CreateClub_Spinner(context!!, list, Constants.CreateClub_Spinner_Type_ClubCategory)
                     }else{
//TODO check all failure conditions //
                        Util.showToast(json.getString("message"),context)
                     }
                 }catch (ex :Exception){
                     Util.e("Error", ex.toString())
                     Util.showToast(R.string.swr,context!!)
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
        //(activity as HomeActivity).hideKeyBoard()
        listner?.hideKeyBoard()
        checkPhoneNumber(SessionManager.getObj().user.country_code.replace("+",""))
        if(titile_name.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_clubnme)
            return false
        }
        if(clubImage==null ){
            Util.showSnake(context,view!!,R.string.a_image)
        }
        if(tv_fondationdate.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_foundation)
            return false
        }
        if(club_email.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_email)
            return false
        }
        if(!club_email.text.toString().isBlank() && !PatternCheck.instance.check(PatternCheck._email, club_email.text.toString())){
            Util.showSnake(context, view!! ,R.string.a_email_valid)
            return false
        }
        if(club_phone.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_phone_club)
            return false
        }
        /*if(!isvalidate){
            Util.showSnake(context, view!!,R.string.a_phone_no_valid);
            return false
        }*/
        if(club_adres.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_address)
            return false
        }
        if(club_location.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_location)
            return false
        }
        if(club_location.text.toString().isBlank() || (lat==0.0 && lng == 0.0)){
            Util.showSnake(context,view!!,R.string.a_location)
            return false
        }
        if(club_web.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_web)
            return false
        }
        if(!android.util.Patterns.WEB_URL.matcher(club_web.text.toString()).matches()){
            Util.showSnake(context,view!!,R.string.a_web_valid)
            return false
        }
        if(etv_description.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_description)
            return false
        }
        if(terms_n_condition.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_terms_n_con)
            return false
        }
        if(usrerole.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_userRole)
            return false
        }
        return true
    }


    private fun checkPhoneNumber( countryCode : String) {
        val contactNo = club_phone.text.toString()
        try {
                val phoneUtil = PhoneNumberUtil.createInstance(context)
                val code = countryCode.toUpperCase()
                val swissNumberProto = phoneUtil.parse(contactNo, code)
                isvalidate = phoneUtil.isValidNumber(swissNumberProto)
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: " + e.toString())
        }
    }

}
