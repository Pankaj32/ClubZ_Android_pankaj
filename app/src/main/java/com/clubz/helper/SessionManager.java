package com.clubz.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.clubz.ClubZ;
import com.clubz.Sign_In_Activity;
import com.clubz.Sign_up_Activity;
import com.clubz.model.User;
import com.clubz.util.Constants;

/**
 * Created by mindiii on 2/26/18.
 */

public class SessionManager {

    SharedPreferences           mypref;
    SharedPreferences.Editor    editor;
    String Pref_Name = "ClubZ_app";
    String IS_LOGED_IN = "is_logged_in ";
    static SessionManager obj;

    private SessionManager(Context context){
        mypref = context.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();
        obj = this;
    }

    public static SessionManager getObj(){
        if(obj==null) new SessionManager(ClubZ.instance.getApplicationContext());
        return obj;
    }

    public boolean createSession(User user ){
        editor.putString(Constants._id, user.getId().trim());
        editor.putString(Constants._first_name  , user.getFirst_name().trim());
        editor.putString(Constants._last_name   , user.getLast_name().trim());
        editor.putString(Constants._social_id   , user.getSocial_id().trim());
        editor.putString(Constants._social_type , user.getSocial_type().trim());
        editor.putString(Constants._email       , user.getEmail().trim());
        editor.putString(Constants._country_code, user.getCountry_code().trim());
        editor.putString(Constants._contact_no  , user.getContact_no().trim());
        editor.putString(Constants._profile_image, user.getProfile_image().trim());
        editor.putString(Constants._is_verified , user.is_verified().trim());
        editor.putString(Constants._auth_token  , user.getAuth_token().trim());
        editor.putString(Constants._device_type , user.getDevice_type().trim());
        editor.putString(Constants._device_token, user.getDevice_token().trim());
        editor.putBoolean(IS_LOGED_IN, true);
        editor.commit();
        return true;
    }


    public User getUser(){
        User user          = new User();
        user.setId(mypref.getString(Constants._id, ""));
        user.setFirst_name   (mypref.getString(Constants._first_name  , ""));
        user.setLast_name  (mypref.getString(Constants._last_name   , ""));
        user.setSocial_id    (mypref.getString(Constants._social_id   , ""));
        user.setSocial_type  (mypref.getString(Constants._social_type , ""));
        user.setEmail        (mypref.getString(Constants._email       , ""));
        user.setCountry_code (mypref.getString(Constants._country_code, ""));
        user.setContact_no   (mypref.getString(Constants._contact_no  , ""));
        user.setProfile_image(mypref.getString(Constants._profile_image,""));
        user.set_verified  (mypref.getString(Constants._is_verified , ""));
        user.setAuth_token   (mypref.getString(Constants._auth_token  , ""));
        user.setDevice_type  (mypref.getString(Constants._device_type , ""));
        user.setDevice_token (mypref.getString(Constants._device_token, ""));
        return user;
    }

    public String getLanguage(){
        return mypref.getString(Constants._userLanguage, "en");
    }

    public boolean isloggedin(){
        return mypref.getBoolean(IS_LOGED_IN, false);
    }

    public void logout(Activity activity){
        editor.clear();
        editor.commit();
        Intent intent = new Intent(activity , Sign_up_Activity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
