package com.clubz.ui.activities.activity

import android.Manifest
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
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.clubz.BuildConfig
import com.clubz.R
import com.clubz.utils.CircleTransform_NoRecycle
import com.clubz.utils.Constants
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_new_activities.*
import java.io.File
import java.io.IOException

class NewActivities : AppCompatActivity(), View.OnClickListener {


    private var spinnActivityLeaderAdapter: ArrayAdapter<String>? = null
    private var spinnFeeTypeAdapter: ArrayAdapter<String>? = null
    private var activityLeaderList: ArrayList<String>? = null
    private var feestypeList: ArrayList<String>? = null
    var isCameraSelected : Boolean = false
    var imageUri : Uri? = null
    var activityImage : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_activities)
        activityLeaderList=ArrayList()
        feestypeList=ArrayList()
        activityLeaderList!!.add("Activity Leader")
        activityLeaderList!!.add("Lorem Ipsum")
        activityLeaderList!!.add("Lorem Ipsum")
        activityLeaderList!!.add("Lorem Ipsum")
        activityLeaderList!!.add("Lorem Ipsum")
        activityLeaderList!!.add("Lorem Ipsum")

        feestypeList!!.add("Fees type")
        feestypeList!!.add("Fixed")
        feestypeList!!.add("Voluntary")
        feestypeList!!.add("Free")
        feestypeList!!.add("Dynamic")

        spinnActivityLeaderAdapter = ArrayAdapter<String>(this@NewActivities, R.layout.spinner_item, R.id.spinnText, activityLeaderList)
        spinnFeeTypeAdapter = ArrayAdapter<String>(this@NewActivities, R.layout.spinner_item, R.id.spinnText, feestypeList)
        spinnerLeader.adapter = spinnActivityLeaderAdapter
        spinnerFeesType.adapter = spinnFeeTypeAdapter

        imageLay.setOnClickListener(this@NewActivities)
        back_f.setOnClickListener(this@NewActivities)
        done.setOnClickListener(this@NewActivities)
    }

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.imageLay->{
                permissionPopUp()
            }
            R.id.back_f->{
                finish()
            }
            R.id.done->{
                finish()
            }
        }

    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper,imgActivity, Gravity.CENTER)
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.getItemId()) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        if (this@NewActivities.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTCAMERA)
                        }
                        else if (this@NewActivities.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                var file  = File(Environment.getExternalStorageDirectory().toString()+ File.separator + "image.jpg");
                imageUri =
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                            FileProvider.getUriForFile(this@NewActivities, BuildConfig.APPLICATION_ID + ".provider",file)
                        }else {
                            Uri.fromFile(file)}
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            /*Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this@NewActivities);
            }*/
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@NewActivities, arrayOf(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE),
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
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300,200).setMaxCropResultSize(4000,4000).setAspectRatio(300, 200).start(this@NewActivities)
                } else {
                    Toast.makeText(this@NewActivities ,R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300,200).setMaxCropResultSize(4000,4000).setAspectRatio(300, 200).start(this@NewActivities)
                } else {
                    Toast.makeText(this@NewActivities ,R.string.swr , Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                var result : CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    if (result != null)
                           activityImage = MediaStore.Images.Media.getBitmap(this@NewActivities.getContentResolver(), result.getUri())


                            if (activityImage != null) {
                                imgActivity.setImageBitmap(activityImage)
                            }
                } catch ( e : IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }
}
