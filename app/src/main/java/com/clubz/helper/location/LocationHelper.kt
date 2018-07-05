package com.clubz.helper.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import android.widget.Toast
import android.content.Intent
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import android.location.Geocoder
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

import android.location.Address
import android.util.Log
import java.io.IOException
import java.util.*


class LocationHelper(private var context: Context) : PermissionUtils.PermissionCallback {

    private var current_activity: Activity? = null

    private var isPermissionGranted: Boolean = false
    private var mLastLocation: Location? = null

    // Google client to interact with Google API
    private lateinit var mGoogleApiClient: GoogleApiClient

    // list of permissions
    private val permissions = ArrayList<String>()
    private var permissionUtils: PermissionUtils? = null

    private val PLAY_SERVICES_REQUEST = 1000
    private val REQUEST_CHECK_SETTINGS = 2000


    init {
        this.current_activity = context as Activity
        permissionUtils = PermissionUtils(context, this)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }


    /**
     * Method to check the availability of location permissions
     */

    fun checkpermission() {
        permissionUtils?.checkPermission(permissions, "Need GPS permission for getting your location", 1)
    }

    private fun isPermissionGranted(): Boolean {
        return isPermissionGranted
    }

    /**
     * Method to verify google play services on the device
     */

    fun checkPlayServices(): Boolean {

        val googleApiAvailability = GoogleApiAvailability.getInstance()

        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(current_activity, resultCode,
                        PLAY_SERVICES_REQUEST).show()
            } else {
                showToast("This device is not supported.")
            }
            return false
        }
        return true
    }

    /**
     * Method to display the location on UI
     */

    fun getLocation(): Location? {

        if (isPermissionGranted()) {

            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                return mLastLocation
            } catch (e: SecurityException) {
                e.printStackTrace()

            }

        }

        return null
    }

    fun getAddress(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses[0]

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * Method used to build GoogleApiClient
     */

    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(current_activity as GoogleApiClient.ConnectionCallbacks)
                .addOnConnectionFailedListener(current_activity as GoogleApiClient.OnConnectionFailedListener)
                .addApi(LocationServices.API).build()

        mGoogleApiClient.connect()

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)

        val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())

        result.setResultCallback { locationSettingsResult ->

            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                    // All location settings are satisfied. The client can initialize location requests here
                    mLastLocation = getLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(current_activity, REQUEST_CHECK_SETTINGS)

                } catch (e: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
            }
        }
    }

    /**
     * Method used to connect GoogleApiClient
     */
    fun connectApiClient() {
        mGoogleApiClient.connect()
    }

    /**
     * Method used to get the GoogleApiClient
     */
    fun getGoogleApiCLient(): GoogleApiClient {
        return mGoogleApiClient
    }


    /**
     * Handles the permission results
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Handles the activity results
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {

                Activity.RESULT_OK ->
                    // All required changes were successfully made
                    mLastLocation = getLocation()
                Activity.RESULT_CANCELED -> {
                }
                else -> {
                }
            }// The user was asked to change settings, but chose not to
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun permissionGranted(request_code: Int) {
        Log.i("PERMISSION", "GRANTED")
        isPermissionGranted = true
    }

    override fun partialPermissionGranted(request_code: Int, granted_permissions: ArrayList<String>) {
        Log.i("PERMISSION PARTIALLY", "GRANTED")
    }

    override fun permissionDenied(request_code: Int) {
        Log.i("PERMISSION", "DENIED")

    }

    override fun neverAskAgain(request_code: Int) {
        Log.i("PERMISSION", "NEVER ASK AGAIN")

    }
}