package com.clubz.ui.user_activities.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.*
import com.bumptech.glide.Glide
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.ActivityBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.user_activities.model.GetLeaderResponce
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.model.ActivitiesBean
import com.clubz.ui.user_activities.model.ActivityDetailsResponce
import com.clubz.ui.user_activities.model.GetMyClubResponce
import com.clubz.utils.Constants
import com.clubz.utils.Util
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.clubz.utils.picker.ImageRotator
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_new_activities.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class NewActivities : BaseActivity(), View.OnClickListener {

    private var spinnActivityLeaderAdapter: ArrayAdapter<GetLeaderResponce.DataBean>? = null
    //private var spinnActivityClubAdapter: ArrayAdapter<GetMyClubResponce.DataBean>? = null
    private var spinnFeeTypeAdapter: ArrayAdapter<String>? = null
    private var activityLeaderList: ArrayList<GetLeaderResponce.DataBean>? = null
    /*private var activityMyClubList: ArrayList<GetMyClubResponce.DataBean>? = null*/
    private var feestypeList = ArrayList<String>()
    private var isCameraSelected: Boolean = false
    var activityLeader: String = ""
    var feesType: String = ""
    var clubId: String = ""
    var latitute: String = ""
    var longitute: String = ""
    private var imageUri: Uri? = null
    var activityImage: Bitmap? = null

    private var userId = ""
    private var userName = ""
    private var userImage = ""
    private var clubName = ""
    private var activityBean: ActivitiesBean.DataBean? = null
    private lateinit var autocompleteFragment: PlaceAutocompleteFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_activities)

        let {
            if (intent.hasExtra("clubId")) clubId = intent.extras.getString("clubId")
            if (intent.hasExtra("clubName")) clubName = intent.extras.getString("clubName")
            if (intent.hasExtra("activityBean")) activityBean = intent.getParcelableExtra("activityBean")
        }
        initializeView()
        if (activityBean != null) {
            updateUiForEdit()
        }
        //getClub()
        clubNameTxt.text = clubName
        getLeaders(clubId)
    }

    private fun initializeView() {
        val padding = resources.getDimension(R.dimen._8sdp).toInt()
        activityLeaderList = ArrayList()
        feestypeList = ArrayList()
        userId = ClubZ.currentUser!!.id
        userName = ClubZ.currentUser!!.full_name
        userImage = ClubZ.currentUser!!.profile_image
        if (userImage.isNotEmpty()) {
            Glide.with(image_member2.context).load(userImage)/*.fitCenter()*/.into(image_member2)
        } else {
            image_member2.setImageResource(R.drawable.user_place_holder)
        }

        imgActivity.setPadding(padding, padding, padding, padding)
        imgActivity.background = ContextCompat.getDrawable(this, R.drawable.bg_circle_blue)
        imgActivity.setImageResource(R.drawable.ic_camera_white)

        username.text = userName

        feestypeList.add("Fixed")
        feestypeList.add("Voluntary")
        feestypeList.add("Free")
        feestypeList.add("Dynamic")
        spinnActivityLeaderAdapter = ArrayAdapter(this@NewActivities, R.layout.spinner_item, R.id.spinnText, activityLeaderList)
        spinnFeeTypeAdapter = ArrayAdapter(this@NewActivities, R.layout.spinner_item, R.id.spinnText, feestypeList)
        spinnerLeader.adapter = spinnActivityLeaderAdapter
        spinnerFeesType.adapter = this.spinnFeeTypeAdapter

        autocompleteFragment = fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place?) {
                activityLocation.setText(p0!!.address)
                latitute = p0!!.latLng.latitude.toString()
                longitute = p0!!.latLng.longitude.toString()
            }

            override fun onError(p0: Status?) {

            }

        })

        fees.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val str = fees.text.toString()
                if (str.isEmpty()) return
                val str2 = perfectDecimal(str, 5, 2)
                if (str2 != str) {
                    fees.setText(str2)
                    val pos = fees.text.length
                    fees.setSelection(pos)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        spinnerLeader.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                activityLeader = /*if (p2 == 0) "" else */activityLeaderList!![p2].userId!!
            }
        }

        spinnerFeesType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) =
                    when (p2) {
                        2 -> {
                            fees.isEnabled = false
                            feesType = feestypeList!![p2]
                            fees.setText("")
                        }
                        else -> {
                            feesType = feestypeList!![p2]
                            fees.isEnabled = true
                        }
                    }
        }

        imgActivity.setOnClickListener(this@NewActivities)
        back_f.setOnClickListener(this@NewActivities)
        done.setOnClickListener(this@NewActivities)
    }

    fun updateUiForEdit() {
        clubId = activityBean?.clubId!!
        clubName = activityBean?.club_name!!
        headTitle.text = activityBean?.activityName
        val padding = 0
        imgActivity.setPadding(padding, padding, padding, padding)
        if (!TextUtils.isEmpty(activityBean?.image)) Glide.with(imgActivity.context).load(activityBean?.image)
                /*.placeholder(R.drawable.new_img).fitCenter()*/.into(imgActivity)
        activityName.setText(activityBean?.activityName)
        fees.setText(activityBean?.fee)
        for (i in 0..feestypeList.size) {
            val feesType = feestypeList[i]
            if (feesType.equals(activityBean?.fee_type)) {
                spinnerFeesType.setSelection(i)
                break
            }
        }
        activityLocation.setText(activityBean?.location)
        latitute = activityBean?.latitude!!
        longitute = activityBean?.longitude!!
        minUser.setText(activityBean?.min_users)
        maxUser.setText(activityBean?.max_users)
        genDescription.setText(activityBean?.description)
        termNConditionTxt.setText(activityBean?.terms_conditions)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgActivity -> {
                permissionPopUp()
            }
            R.id.back_f -> {
                showBackConfirmationDialog()
            }
            R.id.done -> {
                if (validator()) {
                    if (activityBean != null) {
                        updateActivities()
                    } else {
                        createActivities()
                    }
                }
            }
        }
    }

    private fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, imgActivity, Gravity.CENTER)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            isCameraSelected = true
            when (item.itemId) {
                R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                    when {
                        this@NewActivities.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTCAMERA)
                        this@NewActivities.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTREAD)
                        else -> callIntent(Constants.INTENTCAMERA)
                    }
                } else {
                    callIntent(Constants.INTENTCAMERA)
                }
                R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                    isCameraSelected = false
                    if (this@NewActivities.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                            FileProvider.getUriForFile(this@NewActivities, BuildConfig.APPLICATION_ID + ".provider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this@NewActivities)
                val intentgallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentgallery, Constants.SELECT_FILE)
                //   com.clubz.utils.picker.ImagePicker.pickImage(this@NewActivities)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@NewActivities, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this@NewActivities, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this@NewActivities, arrayOf(Manifest.permission.INTERNET),
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
                    Toast.makeText(this@NewActivities, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@NewActivities, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@NewActivities, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@NewActivities, requestCode, resultCode, data)
                /*if (imageUri != null) {
                    *//*CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this@NewActivities)*//*
                    CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@NewActivities)

                } else {
                    Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_SHORT).show()
                }*/
                try {
                    if (imageUri != null)
                        activityImage = MediaStore.Images.Media.getBitmap(this@NewActivities.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    activityImage = ImageRotator.rotate(activityImage, rotation)
                    if (activityImage != null) {
                        val padding = 0
                        imgActivity.setPadding(padding, padding, padding, padding)
                        imgActivity.setImageBitmap(activityImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
               /* if (imageUri != null) {
                    *//*CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this@NewActivities)*//*
                    CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@NewActivities)
                } else {
                    Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_SHORT).show()
                }*/

                try {
                    if (imageUri != null)
                        activityImage = MediaStore.Images.Media.getBitmap(this@NewActivities.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    activityImage = ImageRotator.rotate(activityImage, rotation)
                    if (activityImage != null) {
                        val padding = 0
                        imgActivity.setPadding(padding, padding, padding, padding)
                        imgActivity.setImageBitmap(activityImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data) // : CropImage.ActivityResult
                try {
                    if (result != null)
                        activityImage = MediaStore.Images.Media.getBitmap(this@NewActivities.contentResolver, result.uri)

                    if (activityImage != null) {
                        val padding = 0
                        imgActivity.setPadding(padding, padding, padding, padding)
                        imgActivity.setImageBitmap(activityImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    fun perfectDecimal(str1: String, MAX_BEFORE_POINT: Int, MAX_DECIMAL: Int): String {
        var str = str1
        if (str[0] == '.') str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
                //  if (up > MAX_BEFORE_POINT) return rFinal;
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }

    private fun validator(): Boolean {

        if (activityName.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actnme)
            return false
        }
        /*if (activityImage == null) {
            Util.showSnake(this, mainLayout!!, R.string.a_actImg)
            return false
        }
        if (TextUtils.isEmpty(clubId)) {
            Util.showSnake(this, mainLayout!!, R.string.a_actClub)
            return false
        }
        *//* if (TextUtils.isEmpty(activityLeader)) {
             Util.showSnake(this, mainLayout!!, R.string.a_actLeader)
             return false
         }*//*
        if (activityLocation.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actLoc)
            return false
        }
        if (TextUtils.isEmpty(feesType)) {
            Util.showSnake(this, mainLayout!!, R.string.a_actFeesType)
            return false
        }
        if (feesType != "Free") {
            if (fees.text.toString().isBlank()) {
                Util.showSnake(this, mainLayout!!, R.string.a_actfee)
                return false
            }
        }
        if (minUser.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actmin)
            return false
        }
        if (maxUser.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actmax)
            return false
        }
        if (minUser.text.toString().toInt() > maxUser.text.toString().toInt()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actamxMin)
            return false
        }
        if (genDescription.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actdes)
            return false
        }
        if (termNConditionTxt.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_foundation)
            return false
        }*/
        return true
    }

    private fun getLeaders(clubId: String) {
        val dialog = CusDialogProg(this@NewActivities)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.GET, WebService.get_leaders + clubId + "&limit=&offset=", Response.Listener<NetworkResponse> { response ->
            val data = String(response.data)
            Util.e("data", data)
            dialog.dismiss()
            try {
                val obj = JSONObject(data)
                if (obj.getString("status") == "success") {
                    /*Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()*/
                    val leaderResponce: GetLeaderResponce = Gson().fromJson(data, GetLeaderResponce::class.java)
                    for (dataBean in leaderResponce.getData()!!) {
                        activityLeaderList!!.add(dataBean)
                    }
                    spinnActivityLeaderAdapter!!.notifyDataSetChanged()
                    if (activityBean != null) {
                        for (i in 0..activityLeaderList!!.size) {
                            val leader = activityLeaderList!![i]
                            if (leader.userId.equals(activityBean?.leader_id)) {
                                spinnerLeader.setSelection(i)
                                break
                            }
                        }
                    }
                } else {
                    /*Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()*/
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@NewActivities, "Something went wrong", Toast.LENGTH_LONG).show()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                //  params.put("language", SessionManager.getObj().getLanguage())
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        ClubZ.instance.addToRequestQueue(request)
    }

    private fun createActivities() {
        val dialog = CusDialogProg(this@NewActivities)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.create_activity, Response.Listener<NetworkResponse> { response ->
            val data = String(response.data)
            Util.e("data", data)
            dialog.dismiss()
            //{"status":"success","message":"Club added successfully"}
            try {
                val obj = JSONObject(data)
                val status = obj.getString("status")
                if (status == "success") {
                    Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()
                    val activityDetails = Gson().fromJson(data, ActivityDetailsResponce::class.java)
                    createActivityInFireBase(activityDetails)
                } else {
                    Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@NewActivities, "Something went wrong", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["name"] = activityName.text.toString()
                params["leaderId"] = activityLeader
                params["location"] = activityLocation.text.toString()
                params["latitude"] = latitute
                params["longitude"] = longitute
                params["feeType"] = feesType
                params["fee"] = fees.text.toString()
                params["minUsers"] = minUser.text.toString()
                params["maxUsers"] = maxUser.text.toString()
                params["description"] = genDescription.text.toString()
                params["termsConditions"] = termNConditionTxt.text.toString()
                params["clubId"] = clubId
                if (TextUtils.isEmpty(usrerole.text.toString())) {
                    params["userRole"] = "admin"
                } else {
                    params["userRole"] = usrerole.text.toString()
                }
                Util.e("parms create", params.toString())
                return params
            }


            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (activityImage != null) {
                    params["image"] = DataPart("activity_image.jpg", AppHelper.getFileDataFromDrawable(activityImage), "image")
                }
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                //  params.put("language", SessionManager.getObj().getLanguage())
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        ClubZ.instance.addToRequestQueue(request)
    }

    private fun updateActivities() {
        val dialog = CusDialogProg(this@NewActivities)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.updateActivity,
                Response.Listener<NetworkResponse> { response ->
                    val data = String(response.data)
                    Util.e("data", data)
                    dialog.dismiss()
                    //{"status":"success","message":"Club added successfully"}
                    try {
                        val obj = JSONObject(data)
                        val status = obj.getString("status")
                        if (status == "success") {
                            Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()
                            val activityDetails = Gson().fromJson(data, ActivityDetailsResponce::class.java)
                            createActivityInFireBase(activityDetails)
                        } else {
                            Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@NewActivities, "Something went wrong", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["activityId"] = activityBean?.activityId.toString()
                params["name"] = activityName.text.toString()
                params["leaderId"] = activityLeader
                params["location"] = activityLocation.text.toString()
                params["latitude"] = latitute
                params["longitude"] = longitute
                params["feeType"] = feesType
                params["fee"] = fees.text.toString()
                params["minUsers"] = minUser.text.toString()
                params["maxUsers"] = maxUser.text.toString()
                params["description"] = genDescription.text.toString()
                params["termsConditions"] = termNConditionTxt.text.toString()

                Util.e("parms create", params.toString())
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (activityImage != null) {
                    params["image"] = DataPart("activity_image.jpg", AppHelper.getFileDataFromDrawable(activityImage), "image")
                }
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                //  params.put("language", SessionManager.getObj().getLanguage())
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        ClubZ.instance.addToRequestQueue(request)
    }

    private fun createActivityInFireBase(activityDetails: ActivityDetailsResponce) {
        val activityBean = ActivityBean()
        activityBean.activityId = activityDetails.getDetails()?.activityId
        activityBean.activityTitle = activityDetails.getDetails()?.name
        activityBean.activityImage = activityDetails.getDetails()?.image
        activityBean.clubId = activityDetails.getDetails()?.clubId
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_ACTIVITIES)
                .child(activityDetails.getDetails()?.clubId!!)
                .child(activityDetails.getDetails()?.activityId!!)
                .setValue(activityBean).addOnCompleteListener {
                    finish()
                }
    }

    override fun onBackPressed() {
        showBackConfirmationDialog()
    }

    fun showBackConfirmationDialog() {
        val builder1 = android.app.AlertDialog.Builder(this@NewActivities)
        builder1.setTitle("Be careful !!")
        builder1.setMessage("Are you sure you want to discard this new activity?")
        builder1.setCancelable(true)
        builder1.setPositiveButton("DISCARD"
        ) { dialog, id ->
            super.onBackPressed()
        }
        builder1.setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, id: Int) {
                dialogInterface.cancel()
            }
        })

        val alert11 = builder1.create()
        alert11.show()
    }
}
