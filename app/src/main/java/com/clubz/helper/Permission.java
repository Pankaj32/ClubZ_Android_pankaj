package com.clubz.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.clubz.Cus_Views.CustomAlertDialog;
import com.clubz.R;
import com.clubz.util.Constants;


/**
 * Created by mindiii on 20/4/17.
 */

public class Permission {

    Activity activity;
    Context context;

    public Permission(Activity activity, Context context) {
        this.activity = activity;
        context = context;
    }


    public boolean askForGps() {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            CustomAlertDialog customAlertDialog = new CustomAlertDialog(activity) {
                @Override
                public void onOkListner() {
                    activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.GPS_RESULT_CODE);
                    this.dismiss();
                }

                @Override
                public void onCancelListner() {
                    this.dismiss();
                }

            };
            customAlertDialog.setMessage(R.string.t_gps_msg);
            customAlertDialog.setTitile(R.string.t_al_gps);
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




}
