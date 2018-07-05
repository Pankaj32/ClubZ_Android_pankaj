package com.clubz.helper.location

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast

class PermissionUtils {

    private var context : Context
    private var currentActivity : Activity
    private var callback : PermissionCallback

    private var permissionList : ArrayList<String> = arrayListOf()
    private var listPermissionsNeeded : ArrayList<String> = arrayListOf()

    private var dialogContent=""
    private var reqCode : Int = 0

    constructor(context : Context) {
        this.context = context
        this.currentActivity = context as Activity
        this.callback = context as PermissionCallback
    }

    constructor (context : Context, callback:PermissionCallback) : this(context) {
        this.context = context
        this.currentActivity = context as Activity
        this.callback = callback
    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialog_content
     * @param request_code
     */

    fun checkPermission(permissions : ArrayList<String> , dialog_content:String, request_code:Int) {

        this.permissionList = permissions
        this.dialogContent=dialog_content
        this.reqCode=request_code

        if(Build.VERSION.SDK_INT >= 23) {

            if (checkAndRequestPermissions(permissions, request_code)) {

                callback.permissionGranted(request_code)
                Log.i("all permissions", "granted")
                Log.i("proceed", "to callback")
            }
        }
        else {
            callback.permissionGranted(request_code)
            Log.i("all permissions", "granted")
            Log.i("proceed", "to callback")
        }
    }


    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */
    private fun checkAndRequestPermissions(permissions:ArrayList<String>, request_code : Int):Boolean {

        if(permissions.size>0) {

            listPermissionsNeeded.clear()

            for(i in 0 ..permissions.size) {

                val hasPermission = ContextCompat.checkSelfPermission(currentActivity, permissions[i])
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions[i])
                }
            }

            if (listPermissionsNeeded.isNotEmpty()) {
                val p0 = listPermissionsNeeded.toArray(arrayOfNulls<String>(listPermissionsNeeded.size))
                ActivityCompat.requestPermissions(currentActivity, p0, request_code)
                return false
            }
        }
        return true
    }

    /**
     *
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    fun onRequestPermissionsResult(requestCode:Int, permissions:Array<*>, grantResults:IntArray) {

        when (requestCode) {

            1-> {

                if(grantResults.isNotEmpty()) {

                    val perms = HashMap<String, Int>()
                    for (i in 0.. permissions.size)  perms[permissions[i].toString()] = grantResults[i]

                    val pendingPermissions = ArrayList<String>()

                    for (i in 0.. listPermissionsNeeded.size) {

                        if (perms[listPermissionsNeeded[i]] != PackageManager.PERMISSION_GRANTED) {

                            if(ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, listPermissionsNeeded[i]))
                                pendingPermissions.add(listPermissionsNeeded[i])
                            else {

                                Log.i("Go to settings","and enable permissions")
                                callback.neverAskAgain(reqCode)
                                Toast.makeText(currentActivity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show()
                                return
                            }
                        }

                    }

                    if(pendingPermissions.size>0) {

                        showMessageOKCancel(dialogContent, DialogInterface.OnClickListener { dialog, p1 ->
                            when (p1) {

                                DialogInterface.BUTTON_POSITIVE->
                                    checkPermission(permissionList, dialogContent, reqCode)

                                DialogInterface.BUTTON_NEGATIVE->{
                                    Log.i("permisson","not fully given")
                                    if(permissionList.size == pendingPermissions.size)
                                        callback.permissionDenied(reqCode)
                                    else
                                        callback.partialPermissionGranted(reqCode,pendingPermissions)

                                }
                            }
                            dialog.dismiss()
                        })
                    }

            } else {
                    Log.i("all","permissions granted")
                    Log.i("proceed","to next step")
                    callback.permissionGranted(reqCode)
                }
            }
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private fun showMessageOKCancel(message:String, okListener: DialogInterface.OnClickListener ) {
         AlertDialog.Builder(currentActivity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    interface PermissionCallback {
        fun permissionGranted(request_code : Int)
        fun partialPermissionGranted(request_code:Int, granted_permissions:ArrayList<String>)
        fun permissionDenied(request_code:Int)
        fun neverAskAgain(request_code:Int)
    }
}
