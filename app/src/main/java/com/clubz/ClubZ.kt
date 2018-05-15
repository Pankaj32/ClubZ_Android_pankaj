package com.clubz

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.clubz.data.model.User

/**
 * Created by mindiii on 2/12/18.
 */
class ClubZ  : Application() {

    companion object {
        lateinit var instance: ClubZ
        var latitude: Double    = 0.toDouble()
        var longitude: Double   = 0.toDouble()
        var currentUser: User? = null
    }




    var mRequestQueue: RequestQueue? = null

    /* lateinit var context: Context*/
    val TAG = ClubZ::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        instance = this@ClubZ
        //Fabric.with(this, new Crashlytics());
    }


    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        return mRequestQueue!!
    }




    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue().add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue().add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    fun cancelAllPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(TAG)
        }
    }
}