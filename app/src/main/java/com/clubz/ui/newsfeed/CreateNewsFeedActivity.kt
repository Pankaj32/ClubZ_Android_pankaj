package com.clubz.ui.newsfeed

import android.Manifest
import android.app.Activity
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
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.Toast
import com.android.volley.*
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
import com.clubz.ui.cv.ChipView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.newsfeed.adapter.AdapterAutoTextView
import com.clubz.ui.newsfeed.model.NewsFeedDetails
import com.clubz.utils.Constants
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.Util
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_news_feed.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class CreateNewsFeedActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {

    private var userRole: String? = "Admin"
    private var feedTitle: String? = null
    private var clubId: String? = null
    private var description: String? = null
    private var isCameraSelected: Boolean = false
    private var imageUri: Uri? = null
    private var feedImage: Bitmap? = null
    private val tagFilter: ArrayList<String>? = arrayListOf()
    private var adapter: AdapterAutoTextView? = null
    private var feed: Feed? = null
    private var tagList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_news_feed)

        run {
            if (intent.hasExtra("clubId")) clubId = intent.extras.getString("clubId")
            if (intent.hasExtra("feed")) feed = intent.extras.getSerializable("feed") as Feed
        }

        leadby.text = String.format("%s " + ClubZ.currentUser?.full_name, getString(R.string.lead_by))

        let {
            if (ClubZ.currentUser!!.profile_image.isNotEmpty()) {
                Picasso.with(image_member2.context).load(ClubZ.currentUser!!.profile_image).into(image_member2)
            } else {
                val padding = resources.getDimension(R.dimen._8sdp).toInt()
                image_member2.setPadding(padding, padding, padding, padding)
                image_member2.background = ContextCompat.getDrawable(this, R.drawable.bg_circle_blue)
                image_member2.setImageResource(R.drawable.ic_user_shape)
            }
        }

        for (views in arrayOf(img_newsFeed, backBtn, ivDone)) views.setOnClickListener(this)

        edFilterTag.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                val tag = edFilterTag.text.toString().trim { it <= ' ' }
                addTag(tag)
                if (tagList.size > 0) chip_grid.visibility = View.VISIBLE
            }
            false
        }

        spn_commentStatus!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (spn_commentStatus.selectedItem.toString() == getString(R.string.prompt_comment_disabled)) {
                    ivComment.setImageResource(R.drawable.ic_comment_disable)
                } else ivComment.setImageResource(R.drawable.ic_comment_enable)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        adapter = object : AdapterAutoTextView(this, tagFilter) {
            override fun getFilterItemFromServer(txt: String?) {
                getTagFilterSuggestion(txt!!)
            }
        }

        edFilterTag.onItemClickListener = this  // 'this' is Activity instance
        edFilterTag.threshold = 1
        edFilterTag.setAdapter(adapter)

        if (feed != null){
            updateViewIntoEditableMode()
        }/* else if(clubId.isNullOrBlank()) {
            divider_view.visibility = View.VISIBLE
            ll_clubList.visibility = View.VISIBLE
            getAllMyClubList()
        }*/
    }

   /* override fun refreshSpinner() {
        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, R.layout.spinner_item, R.id.spinnText, clubList)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(R.layout.spinner_item)
        // Set Adapter to Spinner
        spn_clubList?.adapter = aa

        spn_clubList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                clubId = clubList[p2].clubId.toString()
            }
        }
    }*/

    private fun addTag(tag: String) {
        if (!TextUtils.isEmpty(tag)) {
            val chip = object : ChipView(this@CreateNewsFeedActivity, chip_grid.childCount.toString()) {
                override fun getLayout(): Int {
                    return R.layout.z_cus_chip_view_newsfeed
                }

                override fun setDeleteListner(chipView: ChipView?) {
                    for (s in tagList) {
                        if (s.trim().toLowerCase() == chipView!!.text.trim().toLowerCase()) {
                            tagList.remove(s)
                            break
                        }
                    }
                }
            }
            chip.text = tag.trim()
            tagList.add(tag.trim())
            chip_grid.addView(chip)
            edFilterTag.setText("")
            KeyboardUtil.hideKeyboard(this@CreateNewsFeedActivity)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val tag = parent?.getItemAtPosition(position).toString()
        addTag(tag)
        if (tagList.size > 0) chip_grid.visibility = View.VISIBLE
    }

    private fun updateViewIntoEditableMode() {
        tv_header.text = getString(R.string.update_article)
        leadby.text = ClubZ.currentUser?.full_name
        titile_name.setText(feed!!.news_feed_title)
        usrerole.setText(getString(R.string.club_manager))
        etv_description.setText(feed!!.news_feed_description)
        spn_commentStatus.setSelection(feed!!.is_comment_allow)
        if (!feed?.news_feed_attachment.isNullOrEmpty())
            Picasso.with(img_newsFeed.context).load(feed!!.news_feed_attachment).fit().into(img_newsFeed)
        val tags = feed?.tagName?.split(",")?.map { it.trim() }
        for (t in tags!!) if (t.isNotEmpty()) addTag(t)
        if (tagList.size > 0) chip_grid.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.backBtn -> {
                onBackPressed()
            }

            R.id.img_newsFeed -> {
                permissionPopUp()
            }

            R.id.ivDone -> {
                if (isValidData()) {
                    if (feed == null) publishNewsFeed() else updateNewsFeed()
                }
            }
        }
    }


    private fun isValidData(): Boolean {
        feedTitle = titile_name.editableText.toString()
        userRole = usrerole.editableText.toString()
        description = etv_description.editableText.toString()

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
                etv_description.requestFocus()
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
                params["isCommentAllow"] = if (spn_commentStatus.selectedItem.toString().toLowerCase() == "comment disabled") "0" else "1"
                params["userRole"] = if (userRole.isNullOrBlank()) getString(R.string.club_manager) else userRole!!
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
                .child(feeDetails?.getFeedDetail()?.clubId)
                .child(feeDetails?.getFeedDetail()?.newsFeedId)
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
                params["userRole"] = if (userRole.isNullOrBlank()) getString(R.string.club_manager) else userRole!!
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

    private fun getTagFilterSuggestion(searchText: String = "") {
        ClubZ.instance.cancelPendingRequests(CreateNewsFeedActivity::class.java.name)
        val request = object : VolleyMultipartRequest(Request.Method.POST,
                WebService.feed_filter_tag,
                Response.Listener<NetworkResponse> { response ->
                    val data = String(response.data)
                    try {
                        val obj = JSONObject(data)
                        if (obj.getString("status") == "success") {
                            val dataArray = obj.getJSONArray("data")
                            for (i in 0 until dataArray.length()) {
                                tagFilter?.add(dataArray.getJSONObject(i).getString("feed_filter_tag_name"))
                            }
                        }
                        adapter?.notifyDataSetChanged()
                        if (!edFilterTag.isPopupShowing && edFilterTag.text.isNotEmpty()) {
                            edFilterTag.showDropDown()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["searchText"] = searchText
                params["clubId"] = if (clubId.isNullOrBlank()) feed!!.clubId else clubId!!
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["language"] = SessionManager.getObj().language
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }

        ClubZ.instance.addToRequestQueue(request, CreateNewsFeedActivity::class.java.name)
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
                ImagePicker.pickImage(this)
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

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropResultSize(300, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 200).start(this@CreateNewsFeedActivity)
                } else {
                    Toast.makeText(this@CreateNewsFeedActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == Constants.REQUEST_CAMERA) {
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropResultSize(300, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 200).start(this@CreateNewsFeedActivity)
                } else {
                    Toast.makeText(this@CreateNewsFeedActivity, R.string.swr, Toast.LENGTH_SHORT).show()
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
}
