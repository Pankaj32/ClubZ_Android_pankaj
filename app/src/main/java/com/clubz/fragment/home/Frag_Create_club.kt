package com.clubz.fragment.home

import android.Manifest
import android.app.DatePickerDialog
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
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.android.volley.NetworkResponse

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.clubz.*
import com.clubz.Cropper.CropImage
import com.clubz.Cropper.CropImageView
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.Spinner_adpter.CreateClub_Spinner
import com.clubz.helper.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.helper.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.model.Club_Category
import com.clubz.model.Country_Code
import com.clubz.model.User
import com.clubz.util.*
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
import kotlinx.android.synthetic.main.frag_create_club.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by mindiii on 3/10/18.
 */

class Frag_Create_club : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val check = Date(p1 - 1900, p2, p3)
        var d = Date(System.currentTimeMillis() - 1000)
        d.hours =0;
        d.minutes=0;
        d.seconds=0;
        Util.e("Tag", "$d : ${p0!!.minDate} : ${check}")
            year = p1 ; month = p2+1 ;day = p3;
            tv_fondationdate.setText(Util.convertDate("$year-$month-$day"))
    }


    var clubImage : Bitmap? = null;
    var isCameraSelected : Boolean = false;
    var imageUri : Uri? = null;
    lateinit var  autocompleteFragment1 :PlaceAutocompleteFragment
    var lat = 0.0
    var lng = 0.0
    var isvalidate: Boolean = false;


    var day = -1
    var month = -1
    var year = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_create_club, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(view in arrayOf(img_club ,tv_fondationdate , iv_like ,done ,back_f))view.setOnClickListener(this)
         try{
             autocompleteFragment1 = activity.fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment
            // var autocompleteFragment  =( activity as Home_Activity).supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment;
             autocompleteFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place?) {
                club_location.setText(p0!!.name)
                club_location.setSelected(true)
                lat = p0.latLng.latitude
                lng = p0.latLng.longitude
            }


            override fun onError(p0: Status?) {
                Util.e("User", "An error occurred: " + p0);
            }
        })}catch (ex :Exception){
             ex.printStackTrace()
         }

       val list = Arrays.asList(*context.resources.getStringArray(R.array.privacy_type))
       spn_privacy.adapter = CreateClub_Spinner(context, list,Constants.CreateClub_Spinner_Type_privacy_type);
       getCategory();
       username.setText(SessionManager.getObj().user.full_name)
        try{
            Picasso.with(context).load(SessionManager.getObj().user.profile_image).transform(CircleTransform()).placeholder(R.drawable.ic_user_shape).into(image_member2, object :Callback{
                override fun onSuccess() {
                    image_member2.setPadding(0,0,0,0)
                }

                override fun onError() {

                }
            })
        }catch (ex :Exception){

        }
        club_phone.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_DONE) {
                    (activity as Home_Activity).hideKeyBoard()                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            (activity as Home_Activity).showStatusBar()
            if(activity!=null)activity.fragmentManager.beginTransaction().remove(autocompleteFragment1).commit();
        }catch (ex :Exception){
        }

    }




    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.img_club-> permissionPopUp();
            R.id.tv_fondationdate , R.id.iv_like , R.id.arow->{
                (activity as Home_Activity).hideKeyBoard()
                datePicker(day,month,year)
            }
            R.id.done-> if(validator())crateClub()
            R.id.back_f->{
                (activity as Home_Activity).hideKeyBoard()
                activity.onBackPressed();
            }
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
                imageUri = com.clubz.Picker.ImagePicker.getImageURIFromResult(context, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(300, 200).start(context,this);
                } else {
                    Toast.makeText(context ,R.string.swr, Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(300, 200).start(context,this);
                } else {
                    Toast.makeText(context ,R.string.swr , Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                var result : CropImage.ActivityResult = CropImage.getActivityResult(data);
                try {
                    if (result != null)
                        clubImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), result.getUri());


                    if (clubImage != null) {
                        img_club.setImageBitmap(clubImage)
                    }
                } catch ( e : IOException) {
                    e.printStackTrace();
                }

            }



        }

        isCameraSelected = false
    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(activity, R.style.popstyle);
        val popupMenu = PopupMenu(wrapper, img_club, Gravity.CENTER)
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this);
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.INTERNET),
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

    public fun crateClub() {
        val activity = activity as Home_Activity;
        val dialog = CusDialogProg(context);
        dialog.show();
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.crate_club,object : Response.Listener<NetworkResponse> {
            override fun onResponse(response: NetworkResponse) {
                val data = String(response.data)
                Util.e("data",data);
                dialog.dismiss()
                //{"status":"success","message":"Club added successfully"}
                try {
                    val obj = JSONObject(data)
                    if(obj.getString("status").equals("success")){
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                        activity.replaceFragment(Frag_News_List())
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

                params.put("clubFoundationDate",tv_fondationdate.text.toString());
                params.put("clubLocation",club_location.text.toString())

                params.put("clubDescription",etv_description.text.toString())
                Util.e("parms create", params.toString());
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (clubImage != null) {
                    params.put("clubImage",DataPart("club_image.jpg", AppHelper.getFileDataFromDrawable(clubImage), "image/jpeg"))
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
        ClubZ.instance.addToRequestQueue(request)
    }

    fun getCategory(){
        val activity = activity as Home_Activity;
        val dialog = CusDialogProg(context);
        dialog.show();
         object : VolleyGetPost(activity , activity, WebService.club_category,true){
             override fun onVolleyResponse(response: String?) {
                 dialog.dismiss()
                 try {
                       val json = JSONObject(response);
                     if(json.getString("status").equals("success")){
                         val list  = Gson().fromJson<ArrayList<Club_Category>>(json.getJSONArray("data").toString() , Type_Token.club_category);
                         spn_club_category.adapter = CreateClub_Spinner(context, list,Constants.CreateClub_Spinner_Type_ClubCategory);
                     }else{

                     }
                 }catch (ex :Exception){
                     Util.e("Error", ex.toString())
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
                 return params;
             }

             override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                 params.put("authToken", SessionManager.getObj().user.auth_token)
                 return params;
             }
         }.execute()
    }

    fun  validator() :Boolean{
        (activity as Home_Activity).hideKeyBoard()
        checkPhoneNumber(SessionManager.getObj().user.country_code.replace("+",""))
        if(titile_name.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_clubnme);
            return false
        }
        if(clubImage==null ){
            Util.showSnake(context,view!!,R.string.a_clubnme);
        }
        if(tv_fondationdate.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_foundation);
            return false
        }
        if(club_email.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_email);
            return false
        }
        if(!club_email.text.toString().isBlank() && !PatternCheck.instance.check(PatternCheck._email, club_email.text.toString())){
            Util.showSnake(context, view!! ,R.string.a_email_valid)
            return false
        }
        if(club_phone.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_phone_club);
            return false
        }
        /*if(!isvalidate){
            Util.showSnake(context, view!!,R.string.a_phone_no_valid);
            return false
        }*/
        if(club_adres.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_address);
            return false
        }
        if(club_location.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_location);
            return false
        }
        if(club_location.text.toString().isBlank() || (lat==0.0 && lng == 0.0)){
            Util.showSnake(context,view!!,R.string.a_location);
            return false
        }
        if(club_web.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_web);
            return false
        }
        if(!android.util.Patterns.WEB_URL.matcher(club_web.text.toString()).matches()){
            Util.showSnake(context,view!!,R.string.a_web_valid);
            return false
        }
        if(etv_description.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_description);
            return false
        }
        if(terms_n_condition.text.toString().isBlank()){
            Util.showSnake(context,view!!,R.string.a_terms_n_con);
            return false
        }
        return true
    }


    private fun checkPhoneNumber( countryCode : String) {
        val contactNo = club_phone.getText().toString()
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





}
