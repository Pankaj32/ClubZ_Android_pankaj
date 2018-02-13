package com.clubz.helper

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by mindiii on 2/8/18.
 */

class SessionManager private constructor(internal var context: Context) {

    private val sessionManager: SessionManager?

    internal var mypref: SharedPreferences? = null
    internal var editor: SharedPreferences.Editor? = null

    init {
        this.sessionManager = this
    }

    fun getSessionManager(context: Context): SessionManager? {
        if (sessionManager == null) {
            SessionManager(context.applicationContext)
        }
        return sessionManager
    }
}
