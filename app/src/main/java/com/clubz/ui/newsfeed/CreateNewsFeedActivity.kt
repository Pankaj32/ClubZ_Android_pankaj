package com.clubz.ui.newsfeed

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.android.volley.*
import com.bumptech.glide.Glide
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.FeedBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Internet_Connection_dialog
import com.clubz.ui.newsfeed.model.NewsFeedDetails
import com.clubz.utils.Constants
import com.clubz.utils.Util
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.clubz.utils.picker.ImageRotator
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_create_news_feed.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class CreateNewsFeedActivity : AppCompatActivity(),
        View.OnClickListener
/*AdapterView.OnItemClickListener*/ {

    private var userRole: String? = ""
    private var feedTitle: String? = null
    private var clubId: String? = null
    private var clubName: String? = ""
    private var description: String? = null
    private var isCameraSelected: Boolean = false
    private var imageUri: Uri? = null
    private var feedImage: Bitmap? = null
    //private val tagFilter: ArrayList<String>? = arrayListOf()
    //private var adapter: AdapterAutoTextView? = null
    private var feed: Feed? = null
    private var tagList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_news_feed)

        run {
            if (intent.hasExtra("clubId")) clubId = intent.extras.getString("clubId")
            if (intent.hasExtra("clubName")) clubName = intent.extras.getString("clubName")
            if (intent.hasExtra("feed")) {
                feed = intent.extras.getSerializable("feed") as Feed
                clubName = feed?.club_name
            }
        }

        //tvLeadby.text = String.format("%s " + ClubZ.currentUser?.full_name, getString(R.string.lead_by))
        tvLeadby.text = String.format("" + ClubZ.currentUser?.full_name)
        if (clubName!!.isNotEmpty()) {
            tv_clubName.visibility = View.VISIBLE
            tv_clubName.text = clubName
        }

        let {
            if (ClubZ.currentUser!!.profile_image.isNotEmpty()) {
                Glide.with(image_member2.context).load(ClubZ.currentUser!!.profile_image).into(image_member2)
            } else {
                val padding = resources.getDimension(R.dimen._8sdp).toInt()
                image_member2.setPadding(padding, padding, padding, padding)
                image_member2.background = ContextCompat.getDrawable(this, R.drawable.bg_circle_blue)
                image_member2.setImageResource(R.drawable.ic_user_shape)
            }
        }

        for (views in arrayOf(img_newsFeed, backBtn, ivDone, tvMarkView, markView)) views.setOnClickListener(this)

        /* edFilterTag.setOnEditorActionListener { _, actionId, event ->
             if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                 val tag = edFilterTag.text.toString().trim { it <= ' ' }
                 addTag(tag)
                 if (tagList.size > 0) chip_grid.visibility = View.VISIBLE
             }
             false
         }*/

        spn_commentStatus!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (spn_commentStatus.selectedItem.toString() == getString(R.string.prompt_disabled)) {
                    ivComment.setImageResource(R.drawable.ic_comment_disable)
                } else ivComment.setImageResource(R.drawable.ic_comment_enable)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        ed_description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                countTxt.text = "" + p0?.length
            }
        })
        if (feed != null) {
            updateViewIntoEditableMode()
        }
    }

    private fun updateViewIntoEditableMode() {
        tv_header.text = getString(R.string.update_article)
        tvLeadby.text = ClubZ.currentUser?.full_name
        titile_name.setText(feed!!.news_feed_title)
        usrerole.setText(getString(R.string.manager))
        ed_description.setText(feed!!.news_feed_description)
        spn_commentStatus.setSelection(feed!!.is_comment_allow)
        clubId = feed?.clubId
        if (!feed?.news_feed_attachment.isNullOrEmpty())
            Glide.with(img_newsFeed.context).load(feed!!.news_feed_attachment)
                    /* .placeholder(R.drawable.ic_new_img).fitCenter()*/.into(img_newsFeed)

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.backBtn -> {
                showBackConfirmationDialog()
            }

            R.id.img_newsFeed -> {
                permissionPopUp()
            }

            R.id.ivDone -> {
                if (isValidData()) {
                    if (feed == null) {
                        if (Util.isConnectingToInternet(this@CreateNewsFeedActivity)) {
                            publishNewsFeed()
                        } else {
                            object : Internet_Connection_dialog(this@CreateNewsFeedActivity) {
                                override fun tryaginlistner() {
                                    this.dismiss()
                                    publishNewsFeed()
                                }
                            }.show()
                        }
                    } else {
                        if (Util.isConnectingToInternet(this@CreateNewsFeedActivity)) {
                            updateNewsFeed()
                        } else {
                            object : Internet_Connection_dialog(this@CreateNewsFeedActivity) {
                                override fun tryaginlistner() {
                                    this.dismiss()
                                    updateNewsFeed()
                                }
                            }.show()
                        }
                    }
                }
            }

            R.id.tvMarkView, R.id.markView -> {
                showSneckBar(getString(R.string.under_development))
            }
        }
    }


    private fun isValidData(): Boolean {
        feedTitle = titile_name.editableText.toString()
        userRole = usrerole.editableText.toString()
        description = ed_description.editableText.toString()

        when {
            feedTitle!!.isEmpty() -> {
                titile_name.requestFocus()
                showSneckBar(getString(R.string.error_article_name))
                return false
            }
            clubId.isNullOrBlank() -> {
                showSneckBar(getString(R.string.error_article_club_id))
                return false
            }

            clubId == "-1" -> {
                showSneckBar(getString(R.string.error_article_club_id))
                return false
            }

            description!!.isEmpty() -> {
                ed_description.requestFocus()
                showSneckBar(getString(R.string.error_article_desc))
                return false
            }
            else -> return true
        }
    }

    private fun showSneckBar(text: String) {
        Snackbar.make(findViewById(R.id.clRootView), text, Snackbar.LENGTH_SHORT).show()
    }


    private fun publishNewsFeed() {
        val dialog = CusDialogProg(this@CreateNewsFeedActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST,
                WebService.create_newsFeed,
                Response.Listener<NetworkResponse> { response ->
                    val data = String(response.data)
                    //{"status":"success","message":"Club added successfully"}
                    try {
                        val obj = JSONObject(data)
                        if (obj.getString("status") == "success") {
                            Toast.makeText(this@CreateNewsFeedActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                            val feeDetails = Gson().fromJson(data, NewsFeedDetails::class.java)
                            createFeedInFireBase(feeDetails, "new")

                        } else {
                            Toast.makeText(this@CreateNewsFeedActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        dialog.dismiss()
                        Toast.makeText(this@CreateNewsFeedActivity, R.string.swr, Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@CreateNewsFeedActivity, "Something went wrong", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["city"] = ClubZ.city
                params["clubId"] = clubId!!
                params["newsFeedTitle"] = feedTitle!!
                params["newsFeedDescription"] = description!!
                params["tagName"] = tagList.joinToString()
                params["isCommentAllow"] = if (spn_commentStatus.selectedItem.toString().toLowerCase() == "disabled") "0" else "1"
                params["userRole"] = if (userRole.isNullOrBlank()) getString(R.string.manager) else userRole!!
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (feedImage != null) {
                    params["newsFeedAttachment"] = DataPart("newsFeed_image.jpg",
                            AppHelper.getFileDataFromDrawable(feedImage),
                            "image*//*")
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

    private fun createFeedInFireBase(feeDetails: NewsFeedDetails?, status: String) {
        val feedBean = FeedBean()
        feedBean.feedId = feeDetails?.getFeedDetail()?.newsFeedId
        feedBean.feedTitle = feeDetails?.getFeedDetail()?.news_feed_title
        feedBean.feedImage = feeDetails?.getFeedDetail()?.news_feed_attachment
        feedBean.clubId = feeDetails?.getFeedDetail()?.clubId
        feedBean.isCommentAllow = feeDetails?.getFeedDetail()?.is_comment_allow
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_NEWS_FEED)
                .child(feeDetails?.getFeedDetail()?.clubId!!)
                .child(feeDetails?.getFeedDetail()?.newsFeedId!!)
                .setValue(feedBean).addOnCompleteListener {
                    if (status == "update") {
                        setResult(Activity.RESULT_OK, intent)
                    }
                    finish()
                }
    }

    private fun updateNewsFeed() {
        val dialog = CusDialogProg(this@CreateNewsFeedActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST,
                WebService.update_newsFeed,
                Response.Listener<NetworkResponse> { response ->
                    val data = String(response.data)
                    Util.e("data", data)
                    dialog.dismiss()
                    //{"status":"success","message":"Club added successfully"}
                    try {
                        val obj = JSONObject(data)
                        if (obj.getString("status") == "success") {
                            Toast.makeText(this@CreateNewsFeedActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                            val feeDetails = Gson().fromJson(data, NewsFeedDetails::class.java)
                            createFeedInFireBase(feeDetails, "update")
                        } else {
                            Toast.makeText(this@CreateNewsFeedActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@CreateNewsFeedActivity, R.string.swr, Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@CreateNewsFeedActivity, "Something went wrong", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["city"] = ClubZ.city
                params["clubId"] = feed!!.clubId
                params["newsFeedId"] = feed!!.newsFeedId.toString()
                params["newsFeedTitle"] = feedTitle!!
                params["newsFeedDescription"] = description!!
                params["tagName"] = tagList.joinToString()
                params["isCommentAllow"] = if (spn_commentStatus.selectedItem.toString().toLowerCase() == "comment disabled") "0" else "1"
                params["userRole"] = if (userRole.isNullOrBlank()) getString(R.string.manager) else userRole!!
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (feedImage != null) {
                    params["newsFeedAttachment"] = DataPart("newsFeed_image.jpg",
                            AppHelper.getFileDataFromDrawable(feedImage),
                            "image*//*")
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

    private fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this@CreateNewsFeedActivity, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, img_newsFeed, Gravity.CENTER)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            isCameraSelected = true
            when (item.itemId) {
                R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                    when {
                        this@CreateNewsFeedActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTCAMERA)
                        this@CreateNewsFeedActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTREAD)
                        else -> callIntent(Constants.INTENTCAMERA)
                    }
                } else {
                    callIntent(Constants.INTENTCAMERA)
                }
                R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                    isCameraSelected = false
                    if (this@CreateNewsFeedActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                            FileProvider.getUriForFile(this@CreateNewsFeedActivity, BuildConfig.APPLICATION_ID + ".provider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri) //USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                // ImagePicker.pickImage(this)
                val intentgallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentgallery, Constants.SELECT_FILE)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@CreateNewsFeedActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this@CreateNewsFeedActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this@CreateNewsFeedActivity, arrayOf(Manifest.permission.INTERNET),
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
                    Toast.makeText(this@CreateNewsFeedActivity, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@CreateNewsFeedActivity, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@CreateNewsFeedActivity, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == -1) {

            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@CreateNewsFeedActivity, requestCode, resultCode, data)
                try {
                    if (imageUri != null)
                        feedImage = MediaStore.Images.Media.getBitmap(this@CreateNewsFeedActivity.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    feedImage = ImageRotator.rotate(feedImage, rotation)
                    if (feedImage != null) {
                        val padding = 0
                        img_newsFeed.setPadding(padding, padding, padding, padding)
                        img_newsFeed.setImageBitmap(feedImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (requestCode == Constants.REQUEST_CAMERA) {
                try {
                    if (imageUri != null)
                        feedImage = MediaStore.Images.Media.getBitmap(this@CreateNewsFeedActivity.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    feedImage = ImageRotator.rotate(feedImage, rotation)
                    if (feedImage != null) {
                        val padding = 0
                        img_newsFeed.setPadding(padding, padding, padding, padding)
                        img_newsFeed.setImageBitmap(feedImage)
                        // img_newsFeed.setImageURI(imageUri)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    feedImage = MediaStore.Images.Media.getBitmap(this@CreateNewsFeedActivity.contentResolver, result.uri)
                    if (feedImage != null) {
                        img_newsFeed.setImageBitmap(feedImage)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    fun showBackConfirmationDialog() {
        val builder1 = android.app.AlertDialog.Builder(this@CreateNewsFeedActivity)
        builder1.setTitle(getString(R.string.be_careful))
        builder1.setMessage(getString(R.string.are_you_sure_you_want_to_discard_this_new_news))
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

    override fun onBackPressed() {
        showBackConfirmationDialog()
    }
}
