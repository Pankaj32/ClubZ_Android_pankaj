package com.clubz.ui.newsfeed

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
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
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.newsfeed.adapter.AdapterAutoTextView
import com.clubz.utils.Constants
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.Util
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.mvc.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_news_feed.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class CreateNewsFeedActivity : AppCompatActivity() , View.OnClickListener, AdapterView.OnItemClickListener {

    var userRole        : String? = "Admin"
    var feedTitle       : String? = null
    var clubId          : String? = null
    var description     : String? = null
    var isCameraSelected: Boolean = false
    var imageUri        : Uri?    = null
    var feedImage       : Bitmap? = null
    val tagFilter       : ArrayList<String>? = arrayListOf()
    var adapter : AdapterAutoTextView?  = null
    var feed : Feed? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_news_feed)

        run {
            if(intent.extras!=null)
                clubId = intent.extras.getString("clubId")
                feed = intent.extras.getSerializable("feed") as Feed
        }

     /*   if(intent.extras!=null)
            clubId = intent.extras.getString("clubId")*/

        leadby.text = ClubZ.currentUser?.full_name
        //leadby.setText(ClubZ.currentUser!!.full_name)
        for(views in arrayOf(img_newsFeed ,backBtn , ivDone))views.setOnClickListener(this)

        edFilterTag.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                val tag = edFilterTag.text.toString().trim { it <= ' ' }

                if (!TextUtils.isEmpty(tag)){
                    if(tagView.size()<3){
                        tagView.addTag(tag)
                        edFilterTag.setText("")
                        tagDivider.visibility = View.VISIBLE
                        tagView.setVisibility(View.VISIBLE)

                        if(tagView.size()>=3){
                            tagDivider.visibility = View.GONE
                            edFilterTag.visibility = View.GONE
                            KeyboardUtil.hideKeyboard(this@CreateNewsFeedActivity)
                        }
                    }else {
                        tagDivider.visibility = View.GONE
                        edFilterTag.visibility = View.GONE
                    }
                }
            }
            false
        }

        //edFilterTag.addTextChangedListener(this)

        spn_commentStatus!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               if(spn_commentStatus.selectedItem.toString().equals(getString(R.string.prompt_comment_disabled))){
                   ivComment.setImageResource(R.drawable.ic_comment_disable)
               }else  ivComment  .setImageResource(R.drawable.ic_comment_enable)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        //adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tagFilter)
        adapter = object : AdapterAutoTextView(this, tagFilter){
            override fun getFilterItemFromServer(txt: String?) {
                getTagFilterSuggestion(txt!!)
            }
        }
        edFilterTag.setOnItemClickListener(this)
        edFilterTag.setThreshold(1)
        edFilterTag.setAdapter(adapter) // 'this' is Activity instance

        if(feed!=null) updateViewIntoEditableMode()
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val tag = parent?.getItemAtPosition(position).toString()
        if (!TextUtils.isEmpty(tag)){
            if(tagView.size()<3){
                tagView.addTag(tag)
                edFilterTag.setText("")
                tagDivider.visibility = View.VISIBLE
                tagView.setVisibility(View.VISIBLE)

                if(tagView.size()>=3){
                    tagDivider.visibility = View.GONE
                    edFilterTag.visibility = View.GONE
                    KeyboardUtil.hideKeyboard(this@CreateNewsFeedActivity)
                }
            }else {
                tagDivider.visibility = View.GONE
                edFilterTag.visibility = View.GONE
            }
        }
    }

    fun updateViewIntoEditableMode(){
        leadby.text = ClubZ.currentUser?.full_name
        titile_name.setText(feed!!.news_feed_title)
        usrerole.setText(getString(R.string.admin))
        etv_description.setText(feed!!.news_feed_description)
        spn_commentStatus.setSelection(feed!!.is_comment_allow)
        if(!feed?.news_feed_attachment.isNullOrEmpty())
            Picasso.with(img_newsFeed.context).load(feed!!.news_feed_attachment).fit().into(img_newsFeed)
    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.backBtn-> { onBackPressed()}

            R.id.img_newsFeed-> { permissionPopUp() }

            R.id.ivDone-> {
                if(isValidData()) {
                    if (feed == null) publishNewsFeed() else updateNewsFeed()
                }
            }
        }
    }


    fun isValidData() : Boolean{
        feedTitle = titile_name.editableText.toString()
        userRole = usrerole.editableText.toString()
        description = etv_description.editableText.toString()

        if(feedTitle!!.isEmpty()){
            titile_name.requestFocus()
            showSneckBar(getString(R.string.error_article_name))
            return false
        }else if(description!!.isEmpty()){
            etv_description.requestFocus()
            showSneckBar(getString(R.string.error_article_desc))
            return false
        }/*else if(feedTitle!!.isEmpty()){
            titile_name.requestFocus()
            showSneckBar(getString(R.string.club_manager))
            return false
        }*/
        return true
    }

    fun showSneckBar(text : String){
        //Util.showSnake(this@CreateNewsFeedActivity,findViewById(R.id.clRootView), 1,text)
        Snackbar.make(findViewById(R.id.clRootView), text, Snackbar.LENGTH_SHORT).show()
    }


    fun publishNewsFeed(){
        val dialog = CusDialogProg(this@CreateNewsFeedActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST,
                WebService.create_newsFeed,
                object : Response.Listener<NetworkResponse> {

                    override fun onResponse(response: NetworkResponse) {
                        val data = String(response.data)
                        Util.e("data",data)
                        dialog.dismiss()
                        //{"status":"success","message":"Club added successfully"}
                        try {
                            val obj = JSONObject(data)
                            if(obj.getString("status").equals("success")){
                                Toast.makeText(this@CreateNewsFeedActivity,obj.getString("message"), Toast.LENGTH_LONG).show()
                                finish()
                            }else{
                                Toast.makeText(this@CreateNewsFeedActivity,obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }catch ( e : java.lang.Exception){
                            e.printStackTrace()
                            Toast.makeText(this@CreateNewsFeedActivity,R.string.swr, Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                dialog.dismiss()
                Toast.makeText(this@CreateNewsFeedActivity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["city"] =  ClubZ.city
                params["clubId"] =  clubId!!
                params["newsFeedId"] =  feed!!.newsFeedId.toString()
                params["newsFeedTitle"] = feedTitle!!
                params["newsFeedDescription"] = description!!
                params["tagName"] =  tagView.getTagString()
                params["isCommentAllow"] = if(spn_commentStatus.selectedItem.toString().toLowerCase().equals("Comment disabled"))"0" else "1"
                params["userRole"] = if(userRole.isNullOrBlank()) "Admin" else userRole!!
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (feedImage != null) {
                    params.put("newsFeedAttachment", DataPart("newsFeed_image.jpg",
                                    AppHelper.getFileDataFromDrawable(feedImage),
                            "image*//*"))
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

    fun updateNewsFeed(){
        val dialog = CusDialogProg(this@CreateNewsFeedActivity)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.POST,
                WebService.update_newsFeed,
                object : Response.Listener<NetworkResponse> {

                    override fun onResponse(response: NetworkResponse) {
                        val data = String(response.data)
                        Util.e("data",data)
                        dialog.dismiss()
                        //{"status":"success","message":"Club added successfully"}
                        try {
                            val obj = JSONObject(data)
                            if(obj.getString("status").equals("success")){
                                Toast.makeText(this@CreateNewsFeedActivity,obj.getString("message"), Toast.LENGTH_LONG).show()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }else{
                                Toast.makeText(this@CreateNewsFeedActivity,obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }catch ( e : java.lang.Exception){
                            e.printStackTrace()
                            Toast.makeText(this@CreateNewsFeedActivity,R.string.swr, Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
                }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                dialog.dismiss()
                Toast.makeText(this@CreateNewsFeedActivity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["city"] =  ClubZ.city
                params["clubId"] =  feed!!.clubId
                params["newsFeedId"] =  feed!!.newsFeedId.toString()
                params["newsFeedTitle"] = feedTitle!!
                params["newsFeedDescription"] = description!!
                params["tagName"] =  tagView.getTagString()
                params["isCommentAllow"] = if(spn_commentStatus.selectedItem.toString().toLowerCase().equals("Comment disabled"))"0" else "1"
                params["userRole"] = if(userRole.isNullOrBlank()) "Admin" else userRole!!
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (feedImage != null) {
                    params.put("newsFeedAttachment", DataPart("newsFeed_image.jpg",
                            AppHelper.getFileDataFromDrawable(feedImage),
                            "image*//*"))
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

    fun getTagFilterSuggestion(searchText : String = ""){
        ClubZ.instance.cancelPendingRequests(CreateNewsFeedActivity::class.java.name)
       // pb_loading_indicator.visibility = View.VISIBLE
        val request = object : VolleyMultipartRequest(Request.Method.POST,
                WebService.feed_filter_tag,
                object : Response.Listener<NetworkResponse> {

                    override fun onResponse(response: NetworkResponse) {
                        //pb_loading_indicator.visibility = View.GONE
                        val data = String(response.data)
                        try {
                            val obj = JSONObject(data)
                            if(obj.getString("status").equals("success")){
                                var data = obj.getJSONArray("data")
                                for (i in 0..data.length()-1){
                                    tagFilter?.add(data.getJSONObject(i).getString("feed_filter_tag_name"))
                                }
                            }
                            adapter?.notifyDataSetChanged()
                            if(!edFilterTag.isPopupShowing && edFilterTag.text.length > 0) {
                                edFilterTag.showDropDown()
                            }
                        }catch ( e : java.lang.Exception){
                            e.printStackTrace()
                        }
                    }
                }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                //pb_loading_indicator.visibility = View.GONE
            }
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params.put("searchText", searchText)
                params.put("clubId", clubId!!)
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }

        request.setRetryPolicy(
                DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        ClubZ.instance.addToRequestQueue(request, CreateNewsFeedActivity::class.java.name)
    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this@CreateNewsFeedActivity, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, img_newsFeed, Gravity.CENTER)
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.getItemId()) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        if (this@CreateNewsFeedActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTCAMERA)
                        }
                        else if (this@CreateNewsFeedActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                        if (this@CreateNewsFeedActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                val file  = File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg");
                imageUri =
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                            FileProvider.getUriForFile(this@CreateNewsFeedActivity, BuildConfig.APPLICATION_ID + ".provider",file)
                        }else {
                            Uri.fromFile(file)}
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this);
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@CreateNewsFeedActivity, arrayOf(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE),
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
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(this@CreateNewsFeedActivity, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@CreateNewsFeedActivity, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@CreateNewsFeedActivity, requestCode, resultCode, data);

                if(imageUri!=null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropResultSize(300, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 200).start(this@CreateNewsFeedActivity)
                }else {
                    Toast.makeText(this@CreateNewsFeedActivity ,R.string.swr , Toast.LENGTH_SHORT).show()
                }
            }else if (requestCode == Constants.REQUEST_CAMERA){
                if(imageUri!=null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropResultSize(300, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 200).start(this@CreateNewsFeedActivity)
                }else {
                    Toast.makeText(this@CreateNewsFeedActivity ,R.string.swr , Toast.LENGTH_SHORT).show()
                }
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result : CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    feedImage = MediaStore.Images.Media.getBitmap(this@CreateNewsFeedActivity.getContentResolver(), result.getUri())
                    if (feedImage != null) {
                        img_newsFeed.setImageBitmap(feedImage)
                    }

                } catch ( e : IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }
}
