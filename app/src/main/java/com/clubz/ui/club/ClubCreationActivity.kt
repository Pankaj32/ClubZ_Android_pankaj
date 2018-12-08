package com.clubz.ui.club

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.Toast
import com.android.volley.*
import com.bumptech.glide.Glide
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.ClubBean
import com.clubz.chat.model.MemberBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.repo.ClubNameRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.ClubName
import com.clubz.data.model.Club_Category
import com.clubz.data.model.Clubs
import com.clubz.data.remote.GioAddressTask
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.club.adapter.CreateClub_Spinner
import com.clubz.ui.club.model.ClubDetails
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Purchase_membership_dialog
import com.clubz.utils.*
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.clubz.utils.picker.ImageRotator
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
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
        DatePickerDialog.OnDateSetListener, View.OnTouchListener {

    private val TAG = ClubCreationActivity::class.java.canonicalName
    var PLACE_PICKER_REQUEST = 1

    var clubImage: Bitmap? = null
    var clubIcon: Bitmap? = null
    private var isCameraSelected: Boolean = false
    private var isClubIcon: Boolean = false
    private var imageUri: Uri? = null
    var lat = 0.0
    var lng = 0.0
    private var isvalidate: Boolean = false


    private var day = -1
    private var month = -1
    private var year = -1

    //lateinit var titile_name : EditText //Because emoji not supported
    //lateinit var etv_description: EditText //Because emoji not supported


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_creation)

        for (views in arrayOf(img_club, ed_foundation_date, done, back_f, image_icon, club_city)) views.setOnClickListener(this)
        /*try{
            autocompleteFragment1 = fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment
            // var autocompleteFragment  =( activity as HomeActivity).supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment;
            autocompleteFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
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
*/
        val list = Arrays.asList(*resources.getStringArray(R.array.privacy_type))
        spn_privacy.adapter = CreateClub_Spinner(this, list, Constants.CreateClub_Spinner_Type_privacy_type)
        getCategory()
        tvLeadby.text = SessionManager.getObj().user.full_name
        try {

            if (ClubZ.currentUser!!.profile_image.isNotBlank()) {
                Picasso.with(image_member2.context)
                        .load(SessionManager.getObj().user.profile_image)
                        .placeholder(R.drawable.user_place_holder)
                        .fit()
                        .into(image_member2)
                /*Picasso.with(this).load(SessionManager.getObj().user.profile_image).transform(CircleTransform()).placeholder(R.drawable.ic_user_shape).fit().into(image_member2, object : Callback {
                    override fun onSuccess() {
                        image_member2.setPadding(0, 0, 0, 0)
                    }

                    override fun onError() {

                    }
                })*/
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
        club_phone.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_NEXT) {
                club_address.requestFocus()
            }
            false
        }
        for (spinner in arrayOf(spn_privacy, spn_club_category)) spinner.setOnTouchListener(this)


        if(SessionManager.getObj().membershipPlan!=null) {
            if (!SessionManager.getObj().membershipPlan.club_create.equals("") &&!SessionManager.getObj().membershipPlan.club_create.equals("1")) {

                Handler().postDelayed({
                    object : Purchase_membership_dialog(this) {
                        override fun cancelplansListner() {
                            finish()
                        }

                        override fun viewplansListner() {
                            this.dismiss()
                        }

                    }.show()
                }, 100)
            }

        }

    }


    override fun onClick(p0: View?) {
        hideKeyBoard()
        when (p0!!.id) {
            R.id.img_club -> {
                isClubIcon = false; permissionPopUp(); }
            R.id.ed_foundation_date -> {
                datePicker(day, month, year)
            }
            R.id.done -> if (validator()) crateClub()
            R.id.back_f -> {
               // onBackPressed()
                showBackConfirmationDialog()
            }
            R.id.image_icon -> {
                isClubIcon = true; permissionPopUp(); }
            R.id.club_city -> {
                showPlacePicker()

            }
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
    private fun datePicker(i1: Int, i2: Int, i3: Int) {
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

    private fun showPlacePicker() {
        val builder = PlacePicker.IntentBuilder()
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTrace.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)

                club_city.isSelected = true
                lat = place.latLng.latitude
                lng = place.latLng.longitude
                val task = @SuppressLint("StaticFieldLeak")
                object : GioAddressTask(this@ClubCreationActivity) {
                    override fun onFail() {
                        club_city.setText("")
                        Toast.makeText(this@ClubCreationActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(address: com.clubz.data.model.Address) {
                        club_city.setText(address.city.toString())
                    }
                }
                task.execute(lat, lng)



            }
        }

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@ClubCreationActivity, requestCode, resultCode, data)
                if (imageUri != null) {
                    if (!isClubIcon) {

                        clubImage = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.contentResolver, imageUri)
                        val rotation = ImageRotator.getRotation(this, imageUri, true)
                        clubImage = ImageRotator.rotate(clubImage, rotation)
                        if (clubImage != null) {
                            val padding = 0
                            img_club.setPadding(padding, padding, padding, padding)
                            img_club.setImageBitmap(clubImage)
                        }
                        /*CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setMinCropResultSize(300, 200)
                                .setMaxCropResultSize(4000, 4000)
                                .setAspectRatio(300, 200).start(this@ClubCreationActivity)*/
                    }else {
                        clubIcon = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.contentResolver, imageUri)
                        val rotation = ImageRotator.getRotation(this, imageUri, true)
                        clubIcon = ImageRotator.rotate(clubIcon, rotation)
                        if (clubIcon != null) {
                            val padding = 0
                            image_icon.setPadding(padding, padding, padding, padding)
                            image_icon.setImageBitmap(clubIcon)
                        }
                        /*CropImage.activity(imageUri)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .setMinCropResultSize(200, 200)
                                .setMaxCropResultSize(4000, 4000)
                                .setAspectRatio(300, 300).start(this@ClubCreationActivity)*/
                    }
                } else {
                    Toast.makeText(this@ClubCreationActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    if (!isClubIcon) {
                        clubImage = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.contentResolver, imageUri)
                        val rotation = ImageRotator.getRotation(this, imageUri, true)
                        clubImage = ImageRotator.rotate(clubImage, rotation)
                        if (clubImage != null) {
                            val padding = 0
                            img_club.setPadding(padding, padding, padding, padding)
                            img_club.setImageBitmap(clubImage)
                        }
                        /*CropImage.activity(imageUri)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setMinCropResultSize(300, 200)
                                .setMaxCropResultSize(4000, 4000)
                                .setAspectRatio(300, 200).start(this@ClubCreationActivity)*/
                    } else {
                        clubIcon = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.contentResolver, imageUri)
                        val rotation = ImageRotator.getRotation(this, imageUri, true)
                        clubIcon = ImageRotator.rotate(clubIcon, rotation)
                        if (clubIcon != null) {
                            val padding = 0
                            image_icon.setPadding(padding, padding, padding, padding)
                            image_icon.setImageBitmap(clubIcon)
                            /*CropImage.activity(imageUri)
                                    .setCropShape(CropImageView.CropShape.OVAL)
                                    .setMinCropResultSize(200, 200)
                                    .setMaxCropResultSize(4000, 4000)
                                    .setAspectRatio(300, 300).start(this@ClubCreationActivity)*/
                        }
                    }
                } else {
                    Toast.makeText(this@ClubCreationActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                try {
                    if (result != null)
                        if (isClubIcon) {
                            clubIcon = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.contentResolver, result.uri)

                            if (clubIcon != null) {
                                image_icon.setPadding(0, 0, 0, 0)
                                image_icon.setImageBitmap(CircleTransform_NoRecycle().transform(clubIcon))
                            }
                        } else {
                            clubImage = MediaStore.Images.Media.getBitmap(this@ClubCreationActivity.contentResolver, result.uri)

                            if (clubImage != null) {
                                img_club.setImageBitmap(clubImage)
                            }
                        }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    private fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this@ClubCreationActivity, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, if (isClubIcon) image_icon else img_club, Gravity.CENTER)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            isCameraSelected = true
            when (item.itemId) {
                R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                    when {
                        this@ClubCreationActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTCAMERA)
                        this@ClubCreationActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTREAD)
                        else -> callIntent(Constants.INTENTCAMERA)
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
            false
        }
        popupMenu.show()
    }

    private fun callIntent(caseid: Int) {

        when (caseid) {
            Constants.INTENTCAMERA -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg")
                imageUri =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(this@ClubCreationActivity, BuildConfig.APPLICATION_ID + ".provider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
               // ImagePicker.pickImage(this)
                val intentgallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentgallery, Constants.SELECT_FILE)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@ClubCreationActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(this@ClubCreationActivity, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@ClubCreationActivity, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@ClubCreationActivity, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun crateClub() {
        val dialog = CusDialogProg(this@ClubCreationActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.crate_club, Response.Listener<NetworkResponse> { response ->
            val data = String(response.data)
            Util.e("data", data)
            dialog.dismiss()
            //{"status":"success","message":"Club added successfully"}
            try {
                val obj = JSONObject(data)
                if (obj.getString("status") == "success") {

                    //set tag for database sync
                    /* val updateAsync = SessionManager.getObj().update
                        updateAsync.needToUpdateMyClubs = true
                        SessionManager.getObj().setUpdateAppData(updateAsync)*/

                    val clubDetails = Gson().fromJson(data, ClubDetails::class.java)

                    //insert club into local db
                    val tmp = ClubName()
                    tmp.clubId = clubDetails?.getClubDetail()?.clubId?.toInt()
                    tmp.club_name = clubDetails?.getClubDetail()?.club_name
                    ClubNameRepo().insert(tmp)
                    createClubInFairBase(clubDetails)
                    //Toast.makeText(this@ClubCreationActivity, obj.getString("message"), Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this@ClubCreationActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(this@ClubCreationActivity, R.string.swr, Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@ClubCreationActivity, R.string.swr, Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["city"] = club_city.text.toString().trim()
                params["clubName"] = titile_name.text.toString()
                params["clubType"] = if (spn_privacy.selectedItem.toString().toLowerCase() == "public") "1" else "2" // 1 public 2 private
                params["clubCategoryId"] = (spn_club_category.selectedItem as Club_Category).clubCategoryId
                params["clubEmail"] = club_email.text.toString()
                params["clubContactNo"] = club_phone.text.toString()
                params["clubCountryCode"] = SessionManager.getObj().user.country_code// TODO In Ui
                params["clubAddress"] = club_address.text.toString()
                params["clubLatitude"] = lat.toString()
                params["clubWebsite"] = club_web.text.toString()
                params["clubLongitude"] = lng.toString()
                params["termsConditions"] = terms_n_condition.text.toString()
                params["clubFoundationDate"] = ed_foundation_date.text.toString()
                params["clubLocation"] = club_address.text.toString()
                params["userRole"] = usrerole.text.toString() + ""
                params["clubDescription"] = etv_description.text.toString() + "" //*\\StringEscapeUtils.escapeJava(etv_description.getText().toString()).replace("\\uD83D"," \\uD83D")+"")*//*
                Util.e("parms create", params.toString())
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (clubImage != null) {
                    params["clubImage"] = DataPart("club_image.jpg", AppHelper.getFileDataFromDrawable(clubImage), "image*//*")
                }
                if (clubIcon != null) {
                    params["clubIcon"] = DataPart("club_icon.jpg", AppHelper.getFileDataFromDrawable(clubIcon), "image*//*")
                }
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["language"] = SessionManager.getObj().language
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        ClubZ.instance.addToRequestQueue(request)
    }

    private fun createClubInFairBase(clubDetails: ClubDetails?) {
        val clubBean = ClubBean()
        clubBean.clubId = clubDetails?.getClubDetail()?.clubId
        clubBean.clubImage = clubDetails?.getClubDetail()?.club_image
        clubBean.clubName = clubDetails?.getClubDetail()?.club_name
        clubBean.ownerId = clubDetails?.getClubDetail()?.user_id
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB)
                .child(clubDetails?.getClubDetail()?.clubId!!)
                .setValue(clubBean).addOnCompleteListener {
                    onUpdateFirebase(clubBean,1)
                }
    }
    fun onUpdateFirebase(club: ClubBean, status:Int) {
        val memberBean = MemberBean()
        memberBean.clubId = club.clubId
        memberBean.userId = ClubZ.currentUser?.id
        memberBean.joind = 1
        memberBean.silent = "1"



        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB_MEMBER)
                .child(club.clubId!!)
                .child(memberBean.userId!!)
                .setValue(memberBean).addOnCompleteListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
    }
    private fun getCategory() {
        val activity = this@ClubCreationActivity
        val dialog = CusDialogProg(this@ClubCreationActivity)
        dialog.show()
        object : VolleyGetPost(activity, activity, WebService.club_category, true,
                true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {
                    val json = JSONObject(response)
                    if (json.getString("status") == "success") {
                        val list = Gson().fromJson<ArrayList<Club_Category>>(json.getJSONArray("data").toString(), Type_Token.club_category)
                        spn_club_category.adapter = CreateClub_Spinner(this@ClubCreationActivity, list, Constants.CreateClub_Spinner_Type_ClubCategory)
                    } else {
                        Util.showToast(json.getString("message"), this@ClubCreationActivity)
                    }
                } catch (ex: Exception) {
                    Util.e("Error", ex.toString())
                    Util.showToast(R.string.swr, this@ClubCreationActivity)
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
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute()
    }

    private fun validator(): Boolean {
        //hideKeyBoard()
     //   checkPhoneNumber(SessionManager.getObj().user.country_code.replace("+", ""))
        if (titile_name.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_clubnme)
            return false
        }
        /*if (clubImage == null) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_image)
            return false
        }
        if (ed_foundation_date.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_foundation)
            return false
        }
        if (club_email.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_email)
            return false
        }
        if (!club_email.text.toString().isBlank() && !PatternCheck.instance.check(PatternCheck._email, club_email.text.toString())) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_email_valid)
            return false
        }
        if (club_phone.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_phone_club)
            return false
        }
        *//*if(!isvalidate){
            Util.showSnake(context, view!!,R.string.a_phone_no_valid);
            return false
        }*//*
        if (club_address.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_address)
            return false
        }
        if (club_address.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_location)
            return false
        }*/
        if (club_city.text.toString().isBlank() || (lat == 0.0 && lng == 0.0)) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_location)
            return false
        }
        /*if (club_web.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_web)
            return false
        }
        if (!android.util.Patterns.WEB_URL.matcher(club_web.text.toString()).matches()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_web_valid)
            return false
        }
        if (etv_description.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_description)
            return false
        }
        if (terms_n_condition.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_terms_n_con)
            return false
        }
        if (usrerole.text.toString().isBlank()) {
            Util.showSnake(this@ClubCreationActivity, clRootView!!, R.string.a_userRole)
            return false
        }*/
        return true
    }

    private fun checkPhoneNumber(countryCode: String) {
        val contactNo = club_phone.text.toString()
        try {
            val phoneUtil = PhoneNumberUtil.createInstance(this@ClubCreationActivity)
            val code = countryCode.toUpperCase()
            val swissNumberProto = phoneUtil.parse(contactNo, code)
            isvalidate = phoneUtil.isValidNumber(swissNumberProto)
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: " + e.toString())
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        KeyboardUtil.hideKeyboard(this@ClubCreationActivity)
        return false
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val check = Date()
        check.year = p1 - 1900; check.month; check.date = p3
        val d = Date(System.currentTimeMillis() - 1000)
        d.hours = 0
        d.minutes = 0
        d.seconds = 0
        Util.e("Tag", "$d : ${p0!!.minDate} : $check")
        year = p1; month = p2 + 1;day = p3
        ed_foundation_date.setText(Util.convertDate("$year-$month-$day"))
    }

    override fun onBackPressed() {
        showBackConfirmationDialog()
    }

    fun showBackConfirmationDialog() {
        val builder1 = android.app.AlertDialog.Builder(this@ClubCreationActivity)
        builder1.setTitle(getString(R.string.be_careful))
        builder1.setMessage(getString(R.string.are_you_sure_you_want_to_discard_this_new_Club))
        builder1.setCancelable(true)
        builder1.setPositiveButton(getString(R.string.discard)
        ) { dialog, id ->
            super.onBackPressed()
        }
        builder1.setNegativeButton(getString(R.string.cancel), object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, id: Int) {
                dialogInterface.cancel()
            }
        })
        val alert11 = builder1.create()
        alert11.show()
    }



}
