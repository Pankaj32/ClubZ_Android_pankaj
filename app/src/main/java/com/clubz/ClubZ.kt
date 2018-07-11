package com.clubz

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.User
import com.clubz.data.local.db.DBHelper
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.local.db.repo.ClubNameRepo
import com.clubz.data.remote.AppAsnycTask


/**
 * Created by mindiii on 2/12/18.
 */
class ClubZ  : Application() {

    /* lateinit var context: Context*/
    val TAG = ClubZ::class.java.simpleName

    /**
     * @param isPrivate mantions if isPrivate = 0 it means it's showing Both option available for clubs.
     *  and 1 : public and 2 : private clubs
     */
    companion object {
        lateinit var instance: ClubZ
        var latitude: Double    = 0.toDouble()
        var longitude: Double   = 0.toDouble()
        var city: String = ""
        var currentUser: User? = null
        var isNeedToUpdateNewsFeed = false
        var isPrivate: Int = 0
        private var dbHelper: DBHelper? = null

        fun clearVirtualSession(){
            latitude = 0.toDouble()
            longitude = 0.toDouble()
            city = ""
            currentUser = null
            isNeedToUpdateNewsFeed = true
            isPrivate = 0
        }
    }

    fun getClubType() : Int{
        return isPrivate
    }

    /*fun getCurrentUser(): User?{
        return currentUser
    }

    fun setCurrentUser(user: User){
         currentUser = user
    }*/

    var mRequestQueue: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()
        instance = this@ClubZ
        //Fabric.with(this, new Crashlytics())

        val sessionManager = SessionManager.getObj()
        currentUser = sessionManager.user
        val userLocation = sessionManager.lastKnownLocation

        dbHelper = DBHelper()
        DatabaseManager.initializeInstance(dbHelper)

        if(userLocation!=null){
            ClubZ.latitude = userLocation.latitude;
            ClubZ.longitude = userLocation.longitude
        }

        if(currentUser!=null && currentUser?.auth_token!!.isNotBlank())
             AppAsnycTask().syncAppData()
        else ClubNameRepo().deleteTable()
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