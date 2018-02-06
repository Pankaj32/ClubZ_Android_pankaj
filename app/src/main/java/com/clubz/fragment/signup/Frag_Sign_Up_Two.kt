package com.clubz.fragment.signup

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
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.view.*
import android.widget.Toast
import com.clubz.BuildConfig
import com.clubz.Cropper.CropImage
import com.clubz.Cropper.CropImageView
import com.clubz.R
import com.clubz.util.Constants
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.frag_sign_up_two.*
import java.io.File
import java.io.IOException

/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_Two : Fragment()  , View.OnClickListener {


    var isCameraSelected :Boolean=false ;
    var imageUri : Uri? = null;
    var profilieImage :Bitmap? = null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_two, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(view in arrayOf(iv_capture))view.setOnClickListener(this)

    }


    override fun onClick(p0: View?) {
     when(p0!!.id){
         R.id.iv_capture-> permissionPopUp();
         R.id.next -> {}
     }
    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(activity, R.style.popstyle);
        val popupMenu = PopupMenu(wrapper, image_picker, Gravity.BOTTOM)
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//USE file code in this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this);
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.CAMERA,  Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.INTERNET),
                        Constants.MY_PERMISSIONS_REQUEST_INTERNET)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {

                imageUri = com.clubz.Picker.ImagePicker.getImageURIFromResult(context, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(context,this);
                } else {
                    Toast.makeText(context ,R.string.swr, Toast.LENGTH_SHORT).show();

                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(160,160).setMaxCropResultSize(4000,4000).setAspectRatio(400, 400).start(context,this);
                } else {
                    Toast.makeText(context ,R.string.swr , Toast.LENGTH_SHORT).show();
                }


            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                var result : CropImage.ActivityResult = CropImage.getActivityResult(data);
                try {
                    if (result != null)
                        profilieImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), result.getUri());


                    if (profilieImage != null) {
                        image_picker.setImageBitmap(profilieImage)
                    }
                } catch ( e : IOException) {
                    e.printStackTrace();
                }

            }

        }

        isCameraSelected = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
//TODO in string resources
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(context, "Permission denied can't select image", Toast.LENGTH_LONG).show()
                }
            }

            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(context, "Camera  permission denied ", Toast.LENGTH_LONG).show()
            }

            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(context, "Permission not granted for Read", Toast.LENGTH_LONG).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}