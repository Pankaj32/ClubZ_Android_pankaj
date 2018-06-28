package com.clubz.data.remote

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

abstract class GioAddressTask : AsyncTask<Double, Boolean, Boolean>() {

    private var mContext: WeakReference<Context>? = null
    private var latLng: LatLng? = null
    private var address: Address? = null
    
    override fun doInBackground(vararg p0: Double?): Boolean {

        val geocoder = Geocoder(mContext?.get(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(p0[0]!!, p0[1]!!, 1)
            if (addresses.size > 0) address = addresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (address != null) {
            val adr = com.clubz.data.model.Address()
            adr.city = address?.locality
            adr.state = address!!.adminArea
            adr.country = address?.getCountryName()
            adr.postalCode = address?.getPostalCode()
            adr.stAddress1 = address?.getAddressLine(0)
            adr.stAddress2 = address?.getAddressLine(1)
            adr.latitude = latLng?.latitude.toString()
            adr.longitude = latLng?.longitude.toString()
            adr.placeName = adr.city + ", " + adr.country
            onSuccess(adr)
        }

    }
    
    abstract fun onSuccess(address: com.clubz.data.model.Address)
}