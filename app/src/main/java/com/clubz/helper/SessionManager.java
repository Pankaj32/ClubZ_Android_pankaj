package com.clubz.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mindiii on 2/8/18.
 */

public class SessionManager {

    private SessionManager sessionManager;
    Context context;

    SharedPreferences mypref;
    SharedPreferences.Editor    editor;

    private SessionManager( Context context) {
        this.context = context;
        this.sessionManager = this;
    }

    public SessionManager getSessionManager() {
        if(sessionManager==null){
            new SessionManager(context.getApplicationContext());
        }
        return sessionManager;
    }
}
