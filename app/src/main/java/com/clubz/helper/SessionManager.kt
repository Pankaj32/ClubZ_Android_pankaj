package com.clubz.helper

import android.content.Context
import android.content.SharedPreferences
import com.clubz.ClubZ
import com.clubz.model.User
import com.clubz.util.Constants

/**
 * Created by mindiii on 2/8/18.
 */

class SessionManager private constructor() {


    private val Pref_Name = "ClubZ_app"
    var mypref: SharedPreferences
    var editor: SharedPreferences.Editor
    init {
        obj = this
        mypref = _context.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE)
        editor = mypref.edit()
        editor.apply()
        println("init complete")
    }

    companion object {
        lateinit var obj: SessionManager
        var _context: Context = ClubZ.instance.applicationContext
        init {
            SessionManager()
        }
    }

    fun createSession(user :User): Boolean {
        editor.putString(Constants._id, user.id.trim())
        editor.putString(Constants._first_name  , user.first_name  .trim())
        editor.putString(Constants._last_name   , user.last_name   .trim())
        editor.putString(Constants._social_id   , user.social_id   .trim())
        editor.putString(Constants._social_type , user.social_type .trim())
        editor.putString(Constants._email       , user.email       .trim())
        editor.putString(Constants._country_code, user.country_code.trim())
        editor.putString(Constants._contact_no  , user.contact_no  .trim())
        editor.putString(Constants._profile_image, user.profile_image.trim())
        editor.putString(Constants._is_verified , user.is_verified .trim())
        editor.putString(Constants._auth_token  , user.auth_token  .trim())
        editor.putString(Constants._device_type , user.device_type .trim())
        editor.putString(Constants._device_token, user.device_token.trim())
        editor.commit()
        return true;

    }

    fun getUser() :User{
        val user          = User();
        user.id           = mypref.getString(Constants._id, "")
        user.first_name   = mypref.getString(Constants._first_name  , "")
        user.last_name    = mypref.getString(Constants._last_name   , "")
        user.social_id    = mypref.getString(Constants._social_id   , "")
        user.social_type  = mypref.getString(Constants._social_type , "")
        user.email        = mypref.getString(Constants._email       , "")
        user.country_code = mypref.getString(Constants._country_code, "")
        user.contact_no   = mypref.getString(Constants._contact_no  , "")
        user.profile_image= mypref.getString(Constants._profile_image,"")
        user.is_verified  = mypref.getString(Constants._is_verified , "")
        user.auth_token   = mypref.getString(Constants._auth_token  , "")
        user.device_type  = mypref.getString(Constants._device_type , "")
        user.device_token = mypref.getString(Constants._device_token, "")
        return user;
    }

    fun getLanguage() : String{
        return mypref.getString(Constants._userLanguage, "en")
    }

    //To get last completed stage while user registration
    fun getLastStage(): Array<String> {
       return arrayOf(mypref.getString(Constants._stage, "") , mypref.getString(Constants._data, ""))
    }

    /**
     * To handle stage completed while registration
     */
    fun setLastStage(step :String , data :String){
        editor.putString(Constants._stage, step)
        editor.putString(Constants._data, data)
        editor.commit()
    }
}
