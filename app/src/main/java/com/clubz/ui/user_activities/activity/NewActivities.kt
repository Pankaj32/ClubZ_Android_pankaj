package com.clubz.ui.user_activities.activity

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
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.*
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.user_activities.model.GetLeaderResponce
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.model.GetMyClubResponce
import com.clubz.utils.Constants
import com.clubz.utils.Util
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_new_activities.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class NewActivities : BaseActivity(), View.OnClickListener {

    private var spinnActivityLeaderAdapter: ArrayAdapter<GetLeaderResponce.DataBean>? = null
    private var spinnActivityClubAdapter: ArrayAdapter<GetMyClubResponce.DataBean>? = null
    private var spinnFeeTypeAdapter: ArrayAdapter<String>? = null
    private var activityLeaderList: ArrayList<GetLeaderResponce.DataBean>? = null
    private var activityMyClubList: ArrayList<GetMyClubResponce.DataBean>? = null
    private var feestypeList: ArrayList<String>? = null
    var isCameraSelected: Boolean = false
    var activityLeader: String = ""
    var feesType: String = ""
    var clubId: String = ""
    var latitute: String = ""
    var longitute: String = ""
    var imageUri: Uri? = null
    var activityImage: Bitmap? = null
    lateinit var autocompleteFragment: PlaceAutocompleteFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_activities)
        initializeView()
        getClub()
    }

    private fun initializeView() {
        activityLeaderList = ArrayList()
        activityMyClubList = ArrayList()
        feestypeList = ArrayList()
        addLeader()

        val clubListBean = GetMyClubResponce.DataBean()
        clubListBean.club_name = "Activity Club"
        activityMyClubList!!.add(clubListBean)

        feestypeList!!.add("Fees type")
        feestypeList!!.add("Fixed")
        feestypeList!!.add("Voluntary")
        feestypeList!!.add("Free")
        feestypeList!!.add("Dynamic")

        spinnActivityLeaderAdapter = ArrayAdapter<GetLeaderResponce.DataBean>(this@NewActivities, R.layout.spinner_item, R.id.spinnText, activityLeaderList)
        spinnFeeTypeAdapter = ArrayAdapter<String>(this@NewActivities, R.layout.spinner_item, R.id.spinnText, feestypeList)
        spinnActivityClubAdapter = ArrayAdapter<GetMyClubResponce.DataBean>(this@NewActivities, R.layout.spinner_item, R.id.spinnText, activityMyClubList)

        spinnerClub.adapter = spinnActivityClubAdapter
        spinnerLeader.adapter = spinnActivityLeaderAdapter
        spinnerFeesType.adapter = spinnFeeTypeAdapter


        autocompleteFragment = fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place?) {
                activityLocation.text = p0!!.address
                latitute = p0.latLng.latitude.toString()
                longitute = p0.latLng.longitude.toString()
            }

            override fun onError(p0: Status?) {

            }

        })

        fees.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val str = fees.getText().toString()
                if (str.isEmpty()) return
                val str2 = PerfectDecimal(str, 5, 2)
                if (str2 != str) {
                    fees.setText(str2)
                    val pos = fees.getText().length
                    fees.setSelection(pos)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        spinnerLeader.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    activityLeader = ""
                } else {
                    activityLeader = activityLeaderList!![p2].userTagId!!
                }
            }
        })

        spinnerFeesType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    feesType = ""
                } else {
                    feesType = feestypeList!![p2]
                }
            }
        })
        spinnerClub.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    clubId = ""
                    addLeader()
                    spinnActivityLeaderAdapter!!.notifyDataSetChanged()
                } else {
                    clubId = activityMyClubList!![p2].clubId!!
                    getLeaders(clubId);
                }
            }
        })
        imageLay.setOnClickListener(this@NewActivities)
        back_f.setOnClickListener(this@NewActivities)
        done.setOnClickListener(this@NewActivities)
    }

    private fun addLeader() {
        activityLeaderList?.clear()
        val leaderListBean = GetLeaderResponce.DataBean()
        leaderListBean.tag_name = "Activity Leader"
        activityLeaderList!!.add(leaderListBean)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imageLay -> {
                permissionPopUp()
            }
            R.id.back_f -> {
                finish()
            }
            R.id.done -> {
                if (validator()) createActivities()
            }
        }

    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, imgActivity, Gravity.CENTER)
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.getItemId()) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        if (this@NewActivities.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTCAMERA)
                        } else if (this@NewActivities.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTREAD)
                        } else {
                            callIntent(Constants.INTENTCAMERA)
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
                return false
            }
        })
        popupMenu.show()
    }


    fun callIntent(caseid: Int) {

        when (caseid) {
            Constants.INTENTCAMERA -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg");
                imageUri =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(this@NewActivities, BuildConfig.APPLICATION_ID + ".provider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this@NewActivities);
                // com.clubz.utils.picker.ImagePicker.pickImage(this@NewActivities)
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
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(this@NewActivities, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@NewActivities, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@NewActivities, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this@NewActivities)
                } else {
                    Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this@NewActivities)
                } else {
                    Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    if (result != null)
                        activityImage = MediaStore.Images.Media.getBitmap(this@NewActivities.getContentResolver(), result.getUri())

                    if (activityImage != null) {
                        imgActivity.setImageBitmap(activityImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    fun PerfectDecimal(str: String, MAX_BEFORE_POINT: Int, MAX_DECIMAL: Int): String {
        var str = str
        if (str[0] == '.') str = "0" + str
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < max) {
            t = str[i]
            if (t != '.' && after == false) {
                up++
                //  if (up > MAX_BEFORE_POINT) return rFinal;
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal = rFinal + t
            i++
        }
        return rFinal
    }

    fun validator(): Boolean {

        if (activityName.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actnme)
            return false
        }
        if (activityImage == null) {
            Util.showSnake(this, mainLayout!!, R.string.a_actImg)
            return false
        }
        if (TextUtils.isEmpty(clubId)) {
            Util.showSnake(this, mainLayout!!, R.string.a_actClub)
            return false
        }
        if (TextUtils.isEmpty(activityLeader)) {
            Util.showSnake(this, mainLayout!!, R.string.a_actLeader)
            return false
        }
        if (activityLocation.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actLoc)
            return false
        }
        if (TextUtils.isEmpty(feesType)) {
            Util.showSnake(this, mainLayout!!, R.string.a_actFeesType)
            return false
        }
        if (fees.text.toString().isBlank()) {
            Util.showSnake(this, mainLayout!!, R.string.a_actfee)
            return false
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
        }
        return true
    }

    fun getClub() {
        val dialog = CusDialogProg(this@NewActivities)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.GET, WebService.get_my_club, object : Response.Listener<NetworkResponse> {
            override fun onResponse(response: NetworkResponse) {
                val data = String(response.data)
                Util.e("data", data)
                dialog.dismiss()
                //{"status":"success","message":"Club added successfully"}
                try {
                    val obj = JSONObject(data)
                    if (obj.getString("status").equals("success")) {
                       /* Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()*/
                        var clubResponce: GetMyClubResponce = Gson().fromJson(data, GetMyClubResponce::class.java)
                        for (dataBean in clubResponce.getData()!!) {
                            activityMyClubList!!.add(dataBean)
                        }
                        spinnActivityLeaderAdapter!!.notifyDataSetChanged()
                    } else {
                        /*Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()*/
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                dialog.dismiss()
                Toast.makeText(this@NewActivities, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                //  params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }
        request.setRetryPolicy(DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        ClubZ.instance.addToRequestQueue(request)
    }

    fun getLeaders(clubId: String) {
        val dialog = CusDialogProg(this@NewActivities)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.GET, WebService.get_leaders + clubId + "&limit=&offset=", object : Response.Listener<NetworkResponse> {
            override fun onResponse(response: NetworkResponse) {
                val data = String(response.data)
                Util.e("data", data)
                dialog.dismiss()
                try {
                    val obj = JSONObject(data)
                    if (obj.getString("status").equals("success")) {
                        /*Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()*/
                        var leaderResponce: GetLeaderResponce = Gson().fromJson(data, GetLeaderResponce::class.java)
                        for (dataBean in leaderResponce.getData()!!) {
                            activityLeaderList!!.add(dataBean)
                        }
                        spinnActivityLeaderAdapter!!.notifyDataSetChanged()
                    } else {
                        /*Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()*/
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                dialog.dismiss()
                Toast.makeText(this@NewActivities, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                //  params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }
        request.setRetryPolicy(DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        ClubZ.instance.addToRequestQueue(request)
    }

    fun createActivities() {
        val dialog = CusDialogProg(this@NewActivities)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.create_activity, object : Response.Listener<NetworkResponse> {
            override fun onResponse(response: NetworkResponse) {
                val data = String(response.data)
                Util.e("data", data)
                dialog.dismiss()
                //{"status":"success","message":"Club added successfully"}
                try {
                    val obj = JSONObject(data)
                    if (obj.getString("status").equals("success")) {
                        Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@NewActivities, obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                    finish()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@NewActivities, R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                dialog.dismiss()
                Toast.makeText(this@NewActivities, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params.put("name", activityName.text.toString())
                params.put("leaderId", activityLeader)
                params.put("location", activityLocation.text.toString())
                params.put("latitude", latitute)
                params.put("longitude", longitute)
                params.put("feeType", feesType)
                params.put("fee", fees.text.toString())
                params.put("minUsers", minUser.text.toString())
                params.put("maxUsers", maxUser.text.toString())
                params.put("description", genDescription.text.toString())
                params.put("termsConditions", termNConditionTxt.text.toString())
                params.put("clubId", clubId)
                if (TextUtils.isEmpty(usrerole.text.toString())) {
                    params.put("userRole", "admin")
                } else {
                    params.put("userRole", usrerole.text.toString())
                }
                Util.e("parms create", params.toString())
                return params
            }


            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (activityImage != null) {
                    params.put("image", DataPart("activity_image.jpg", AppHelper.getFileDataFromDrawable(activityImage), "image"))
                }
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                //  params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }
        request.setRetryPolicy(DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        ClubZ.instance.addToRequestQueue(request)
    }

}
