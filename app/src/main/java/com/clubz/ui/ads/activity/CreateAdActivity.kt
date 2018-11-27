package com.clubz.ui.ads.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.bumptech.glide.Glide
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.AdBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.ads.model.AdDetailsCreated
import com.clubz.ui.ads.model.AdsListBean
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Internet_Connection_dialog
import com.clubz.utils.Constants

import com.clubz.utils.Util
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.clubz.utils.picker.ImageRotator
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker

import kotlinx.android.synthetic.main.activity_create_ad.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*

class CreateAdActivity : AppCompatActivity(), View.OnClickListener {


    private var isCameraSelected: Boolean = false
    private var imageUri: Uri? = null
    private var adImage: Bitmap? = null
    private var userId: String? = ""
    private var userName: String? = ""
    private var userImage: String? = ""
    private var clubId: String? = ""
    private var clubName: String? = ""
    private var adBean: AdsListBean.DataBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_ad)
        initializeView()
    }

    fun initializeView() {
        let {
            if (intent.hasExtra("clubId")) clubId = intent.extras.getString("clubId") else clubId = ""
            if (intent.hasExtra("clubName")) clubName = intent.extras.getString("clubName") else clubName = ""
            if (intent.hasExtra("adBean")) adBean = intent.getParcelableExtra("adBean")
        }
        if (adBean == null) {
            clubNameTxt.text = clubName
            userId = ClubZ.currentUser!!.id
            userName = ClubZ.currentUser!!.full_name
            userImage = ClubZ.currentUser!!.profile_image
        } else {
            clubNameTxt.text = adBean?.club_name
            userId = adBean?.user_id
            userName = adBean?.full_name
            userImage = adBean?.profile_image
            if (!TextUtils.isEmpty(adBean?.image)) Glide.with(imgAd.context).load(adBean?.image)/*.fitCenter().placeholder(R.drawable.ic_new_img)*/.into(imgAd)
            headTitle.text = adBean?.title
            adTitle.setText(adBean?.title)
            adValue.setText(adBean?.fee)
            adValue.setText(adBean?.fee)
            adDescription.setText(adBean?.description)
        }
        if (userImage!!.isNotEmpty()) {
            /*Glide.with(image_member2.context)
                    .load(userImage)
                    .into(image_member2)*/
            Glide.with(image_member2.context).load(userImage)/*.fitCenter().placeholder(R.drawable.user_place_holder)*/.into(image_member2)
        } else {
            image_member2.setImageResource(R.drawable.user_place_holder)
        }
        username.text = userName
        captureImgBtn.setOnClickListener(this@CreateAdActivity)
        back_f.setOnClickListener(this@CreateAdActivity)
        done.setOnClickListener(this@CreateAdActivity)
        adValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val str = adValue.text.toString()
                if (str.isEmpty()) return
                val str2 = perfectDecimal(str, 5, 2)
                if (str2 != str) {
                    adValue.setText(str2)
                    val pos = adValue.text.length
                    adValue.setSelection(pos)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        adDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                countTxt.text = "" + p0?.length
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.captureImgBtn -> {
                permissionPopUp()
            }
            R.id.back_f -> {
                showBackConfirmationDialog()
            }
            R.id.done -> {
                if (validator()) {
                    if (adBean != null) {
                        if (Util.isConnectingToInternet(this@CreateAdActivity)) {
                            updateAd()
                        } else {
                            object : Internet_Connection_dialog(this@CreateAdActivity) {
                                override fun tryaginlistner() {
                                    this.dismiss()
                                    updateAd()
                                }
                            }.show()
                        }
                    } else {
                            if (Util.isConnectingToInternet(this@CreateAdActivity)) {
                                createAd()
                            } else {
                                object : Internet_Connection_dialog(this@CreateAdActivity) {
                                    override fun tryaginlistner() {
                                        this.dismiss()
                                        createAd()
                                    }
                                }.show()
                            }
                    }
                }
            }
        }
    }

    private fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, captureImgBtn, Gravity.CENTER)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            isCameraSelected = true
            when (item.itemId) {
                R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                    when {
                        this@CreateAdActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTCAMERA)
                        this@CreateAdActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTREAD)
                        else -> callIntent(Constants.INTENTCAMERA)
                    }
                } else {
                    callIntent(Constants.INTENTCAMERA)
                }
                R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                    isCameraSelected = false
                    if (this@CreateAdActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                            FileProvider.getUriForFile(this@CreateAdActivity, BuildConfig.APPLICATION_ID + ".provider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                //  ImagePicker.pickImage(this@CreateAdActivity)
                // com.clubz.utils.picker.ImagePicker.pickImage(this@CreateAdActivity)
                val intentgallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentgallery, Constants.SELECT_FILE)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@CreateAdActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this@CreateAdActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this@CreateAdActivity, arrayOf(Manifest.permission.INTERNET),
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
                    Toast.makeText(this@CreateAdActivity, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@CreateAdActivity, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@CreateAdActivity, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@CreateAdActivity, requestCode, resultCode, data)
                /*if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this@CreateAdActivity)
                    *//*CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@CreateAdActivity)*//*

                } else {
                    Toast.makeText(this@CreateAdActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }*/

                try {
                    if (imageUri != null)
                        adImage = MediaStore.Images.Media.getBitmap(this@CreateAdActivity.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    adImage = ImageRotator.rotate(adImage, rotation)
                    if (adImage != null) {
                        val padding = 0
                        imgAd.setPadding(padding, padding, padding, padding)
                        imgAd.setImageBitmap(adImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                /*if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this@CreateAdActivity)
                    *//*CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@CreateAdActivity)*//*
                } else {
                    Toast.makeText(this@CreateAdActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }*/
                try {
                    if (imageUri != null)
                        adImage = MediaStore.Images.Media.getBitmap(this@CreateAdActivity.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    adImage = ImageRotator.rotate(adImage, rotation)
                    if (adImage != null) {
                        val padding = 0
                        imgAd.setPadding(padding, padding, padding, padding)
                        imgAd.setImageBitmap(adImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data) // : CropImage.ActivityResult
                try {
                    if (result != null)
                        adImage = MediaStore.Images.Media.getBitmap(this@CreateAdActivity.contentResolver, result.uri)

                    if (adImage != null) {
                        val padding = 0
                        imgAd.setPadding(padding, padding, padding, padding)
                        imgAd.setImageBitmap(adImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    override fun onBackPressed() {
        showBackConfirmationDialog()
    }

    private fun validator(): Boolean {
        if (adTitle.text.toString().isBlank()) {
            Util.showSnake(this, snackLay!!, R.string.a_addTitle)
            return false
        }
        return true
    }

    fun showBackConfirmationDialog() {
        val builder1 = android.app.AlertDialog.Builder(this@CreateAdActivity)
        builder1.setTitle(getString(R.string.be_careful))
        builder1.setMessage(getString(R.string.you_want_to_discard_this_ad))
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


    //Anil's work
    fun showServerFailResponceDialog(msg: String) {
        val builder1 = android.app.AlertDialog.Builder(this@CreateAdActivity)
        builder1.setMessage(msg)
        builder1.setCancelable(true)
        builder1.setPositiveButton(getString(R.string.ok)
        ) { dialog, id ->
            super.onBackPressed()
        }


        val alert11 = builder1.create()
        alert11.show()
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

    private fun createAd() {
        val dialog = CusDialogProg(this@CreateAdActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.createAd,
                Response.Listener<NetworkResponse> { response ->
                    val data = String(response.data)
                    Util.e("data", data)
                    dialog.dismiss()
                    try {
                        val obj = JSONObject(data)
                        val status = obj.getString("status")
                        // Anil's work
                        val message = obj.getString("message");
                        if (status == "success") {
                            val adDetails = Gson().fromJson(data, AdDetailsCreated::class.java)
                            createAdInFireBase(adDetails)
                        } else {
                            if (Locale.getDefault().language.equals("en")) {
                                showServerFailResponceDialog(message)
                            } else {
                                if (message.equals("You can publish only 3 ads per month")) {
                                    showServerFailResponceDialog("Puedes publicar solo 3 anuncios al mes.")
                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@CreateAdActivity, R.string.swr, Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                },
                Response.ErrorListener {
                    dialog.dismiss()
                    Toast.makeText(this@CreateAdActivity, "Something went wrong", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["title"] = adTitle.text.toString()
                params["clubId"] = clubId!!
                params["fee"] = adValue.text.toString()
                params["isRenew"] = "1"
                params["description"] = adDescription.text.toString()

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
                if (adImage != null) {
                    params["image"] = DataPart("activity_image.jpg", AppHelper.getFileDataFromDrawable(adImage), "image")
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

    private fun updateAd() {

        val dialog = CusDialogProg(this@CreateAdActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.updateAd, Response.Listener<NetworkResponse> { response ->
            val data = String(response.data)
            Util.e("data", data)
            dialog.dismiss()
            try {
                val obj = JSONObject(data)
                val status = obj.getString("status")
                if (status == "success") {
                    val adDetails = Gson().fromJson(data, AdDetailsCreated::class.java)
                    createAdInFireBase(adDetails)
                } else {
                    Toast.makeText(this@CreateAdActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(this@CreateAdActivity, R.string.swr, Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@CreateAdActivity, "Something went wrong", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["adId"] = adBean?.adId.toString()
                params["title"] = adTitle.text.toString()
                params["fee"] = adValue.text.toString()
                params["description"] = adDescription.text.toString()
                if (TextUtils.isEmpty(usrerole.text.toString())) {
                    params["userRole"] = "admin"
                } else {
                    params["userRole"] = usrerole.text.toString()
                }
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (adImage != null) {
                    params["image"] = DataPart("activity_image.jpg", AppHelper.getFileDataFromDrawable(adImage), "image")
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

    private fun createAdInFireBase(adDetails: AdDetailsCreated) {
        val adBean = AdBean()
        adBean.adId = adDetails.adDetail.adId
        adBean.adTitle = adDetails.adDetail.title
        adBean.adImage = adDetails.adDetail.image
        adBean.clubId = adDetails.adDetail.clubId
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_ADS)
                .child(adDetails.adDetail.clubId)
                .child(adDetails.adDetail.adId)
                .setValue(adBean).addOnCompleteListener {
                    Toast.makeText(this@CreateAdActivity, adDetails.message, Toast.LENGTH_LONG).show()
                    finish()
                }
    }


}
