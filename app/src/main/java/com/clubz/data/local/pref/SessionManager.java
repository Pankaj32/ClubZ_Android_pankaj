package com.clubz.data.local.pref;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.clubz.ClubZ;
import com.clubz.data.local.db.repo.ClubNameRepo;
import com.clubz.data.model.ClubName;
import com.clubz.data.model.UpdateAsync;
import com.clubz.data.model.UserLocation;
import com.clubz.ui.authentication.SignupActivity;
import com.clubz.data.model.User;
import com.clubz.utils.Constants;
import com.google.gson.Gson;

/**
 * Created by mindiii on 2/26/18.
 */

public class SessionManager {

    private SharedPreferences           mypref;
    private SharedPreferences.Editor    editor;
    private String Pref_Name = "ClubZ_app";
    private String IS_LOGED_IN = "is_logged_in ";
    private static SessionManager obj;

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
        editor.putString(Constants._full_name  , user.getFull_name().trim());
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
        editor.apply();
        //ClubZ.instance.setCurrentUser(user);
        return true;
    }


    public User getUser(){
        if(mypref.getString(Constants._id, "").isEmpty()){
            return null;
        }else {
            User user  = new User();
            user.setId(mypref.getString(Constants._id, ""));
            user.setFull_name   (mypref.getString(Constants._full_name  , ""));
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
    }


    public void setUpdateAppData(UpdateAsync update){
        if(update!=null){
            String str = new Gson().toJson(update);
            if(!TextUtils.isEmpty(str)){
                editor.putString("updatePref", str);
                editor.apply();
            }
        }
    }

    public UpdateAsync getUpdate(){
        String str = mypref.getString("updatePref", null);
        if(str==null)
            return new UpdateAsync();
        else return new Gson().fromJson(str, UpdateAsync.class);
    }

    public String getLanguage(){
        return mypref.getString(Constants._userLanguage, "en");
    }

    public void setLanguage(String language){
        editor.putString(Constants._userLanguage, language);
        editor.apply();
    }

    public UserLocation getLastKnownLocation(){
      String text = mypref.getString(Constants._userLastLocation,"");
      if(!text.isEmpty()){
          Gson gson = new Gson();
          return gson.fromJson(text, UserLocation.class);
      }
      return null;
    }

    public void setLocation(UserLocation location){
        if(location!=null){
            /*ClubZ.Companion.setLatitude(location.getLatitude());
            ClubZ.Companion.setLongitude(location.getLongitude());*/
            Gson gson = new Gson();
            editor.putString(Constants._userLastLocation, gson.toJson(location));
            editor.apply();
        }
    }

    public boolean isloggedin(){
        return mypref.getBoolean(IS_LOGED_IN, false);
    }

    public void logout(Activity activity){
        editor.clear();
        editor.apply();
        new ClubNameRepo().deleteTable();
        ClubZ.Companion.clearVirtualSession();
        Intent i = new Intent(activity , SignupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
        activity.finish();
    }

    public void logout(Context context){
        editor.clear();
        editor.apply();
        new ClubNameRepo().deleteTable();
        ClubZ.Companion.clearVirtualSession();
        Intent i = new Intent(context , SignupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}
