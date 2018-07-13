package com.clubz.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.clubz.ui.cv.Cus_dialog_material_design;
import com.clubz.R;
import com.clubz.utils.Constants;


/**
 * Created by mindiii on 20/4/17.
 */

public class Permission {

    private final static int PERMISSION_REQUEST_CODE = 12;

    private Activity activity;
    private Fragment fragment;
    private Listener listener;

    public interface Listener{
        void onPermissResult(Boolean isGranted);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Permission(Activity activity) {
        this.activity = activity;
    }

    public Permission(Activity activity, Fragment fragment) {
        this(activity);
        this.fragment = fragment;
    }

    public boolean askForGps() {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Cus_dialog_material_design customAlertDialog = new Cus_dialog_material_design(activity) {
                @Override
                public void onAgree() {
                    activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.GPS_RESULT_CODE);
                    this.dismiss();
                }

                @Override
                public void onDisagree() {
                    this.dismiss();
                }


            };
            customAlertDialog.setTextAlert_msg(R.string.t_gps_msg);
            customAlertDialog.setTextAlert_title(R.string.t_al_gps);
            customAlertDialog.setTextAgree(R.string.ok);
            customAlertDialog.setTextDisagree(R.string.cancel);
            customAlertDialog.show();
        } else return true;

        return false;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean takeSmsReadPermission(){
        return isSmsPermisssion(activity, fragment);
    }

    //For fragments
    private boolean isSmsPermisssion(Activity activity, Fragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (fragment == null) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                } else {
                    fragment.requestPermissions(
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if(listener!=null) listener.onPermissResult(true);
            } else if(listener!=null) listener.onPermissResult(false);
        }
    }

}
