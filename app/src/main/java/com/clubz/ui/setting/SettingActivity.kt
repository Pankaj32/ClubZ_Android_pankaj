package com.clubz.ui.setting

import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.support.v7.widget.PopupMenu
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.db.repo.AllFeedsRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.model.NotificationSesssion
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONObject
import java.util.*

class SettingActivity : AppCompatActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener{


    var ENGLISH_LOCALE="en"
    var SPANISH_LOCALE="es"
    var setvalue = "1"
    var actionfor =""
    private var isrefresh= true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.checklaunage(this)
        setContentView(R.layout.activity_setting)
        ivBack.setOnClickListener(this)
        rl_change_language.setOnClickListener(this)
        val notificationsessions = SessionManager.getObj().notification
        setNotificationData(notificationsessions)


        switch_allow_notifications.setOnCheckedChangeListener(this)

    }


    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.ivBack -> {

                super.onBackPressed()
            }
            R.id.rl_change_language -> {
                permissionPopUp(rl_change_language)
            }
        }
    }

    fun allowlistenr(notificationsession: NotificationSesssion){
        if(notificationsession.notification_status.equals("1")) {
            setlistnerallownotification(true)
        }
        else{
            setlistnerallownotification(false)
        }
    }

    fun setlistnerallownotification(isset:Boolean){

        switch_news_notification.setClickable(isset)
        switch_activities.setClickable(isset)
        switch_chat.setClickable(isset)
        switch_ads.setClickable(isset)


        if(isset){
            switch_news_notification.isChecked =isset
            switch_activities.isChecked = isset
            switch_chat.isChecked = isset
            switch_ads.isChecked =isset
            switch_news_notification.setOnCheckedChangeListener(this)
            switch_activities.setOnCheckedChangeListener(this)
            switch_chat.setOnCheckedChangeListener(this)
            switch_ads.setOnCheckedChangeListener(this)
        }
        else{
            switch_news_notification.setOnCheckedChangeListener(null)
            switch_activities.setOnCheckedChangeListener(null)
            switch_chat.setOnCheckedChangeListener(null)
            switch_ads.setOnCheckedChangeListener(null)
            switch_news_notification.isChecked =isset
            switch_activities.isChecked = isset
            switch_chat.isChecked = isset
            switch_ads.isChecked =isset
        }


    }

     fun setNotificationData(notificationsession: NotificationSesssion){






        if(notificationsession.notification_status.equals("1")){
            switch_allow_notifications.isChecked = true
            allowlistenr(notificationsession)
        }
         else{
            allowlistenr(notificationsession)
        }

        if(notificationsession.news_notifications.equals("1")){
            switch_news_notification.isChecked = true
        }
         else{
            switch_news_notification.isChecked = false
         }
        if(notificationsession.activities_notifications.equals("1")) {
            switch_activities.isChecked = true
        }
        else{
            switch_activities.isChecked = false
        }
        if(notificationsession.chat_notifications.equals("1")) {
            switch_chat.isChecked = true
        }
         else{
            switch_chat.isChecked = false
        }
         if(notificationsession.ads_notifications.equals("1")){
             switch_ads.isChecked = true
         }
         else{
             switch_ads.isChecked = false
         }



    }

    private fun permissionPopUp(view: View) {
        val wrapper = ContextThemeWrapper(this, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, view, Gravity.CENTER)
      // popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.getMenu().add(1, R.id.pop1, 1, "English");
        popupMenu.getMenu().add(1, R.id.pop2, 2, "Spanish");
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.pop1 -> {

                    SessionManager.getObj().language=ENGLISH_LOCALE
                    Util.checklaunage(this)
                    startActivity(Intent(this@SettingActivity, HomeActivity::class.java))
                    finish()

                }
                R.id.pop2 -> {
                    SessionManager.getObj().language=SPANISH_LOCALE
                    Util.checklaunage(this)
                    startActivity(Intent(this@SettingActivity, HomeActivity::class.java))
                    finish()
                }

            }
            true
        }

        popupMenu.show()
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

        if(p1){
            setvalue ="1"
        }
        else{
            setvalue= "0"
        }

        when (p0?.id) {

            R.id.switch_allow_notifications -> {
                actionfor = "notification_status"
                setNotification(setvalue,actionfor)

            }
            R.id.switch_news_notification -> {
                actionfor = "news_notifications"
                setNotification(setvalue,actionfor)
            }
            R.id.switch_activities -> {
                actionfor = "activities_notifications"
                setNotification(setvalue,actionfor)


            }
            R.id.switch_chat -> {
                actionfor = "chat_notifications"
                setNotification(setvalue,actionfor)


            }
            R.id.switch_ads -> {
                actionfor = "ads_notifications"
                setNotification(setvalue,actionfor)
            }

        }

    }


    private fun setNotification(value: String, actionname :String) {
        val dialog = CusDialogProg(this@SettingActivity)
        dialog.show()
        object : VolleyGetPost(this@SettingActivity, WebService.manageNotification, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {

                        val objnotification = obj.getJSONObject("notification")

                        val notificationsession : NotificationSesssion? =  NotificationSesssion();
                        notificationsession!!.notification_status =objnotification.getString("notification_status")
                        notificationsession!!.news_notifications =objnotification.getString("news_notifications")
                        notificationsession!!.activities_notifications =objnotification.getString("activities_notifications")
                        notificationsession!!.date_confirmed_notification =objnotification.getString("date_confirmed_notification")
                        notificationsession!!.date_cancelled_notification =objnotification.getString("date_cancelled_notification")
                        notificationsession!!.activity_chat_notification =objnotification.getString("activity_chat_notification")
                        notificationsession!!.chat_notifications =objnotification.getString("chat_notifications")
                        notificationsession!!.ads_notifications =objnotification.getString("ads_notifications")


                        SessionManager.getObj().createNotificationSession(notificationsession)
                        if(actionname.equals("notification_status")){
                            allowlistenr(notificationsession)

                        }
                    }
                    else{
                        Toast.makeText(this@SettingActivity, obj.getString("message"), Toast.LENGTH_LONG).show()

                    }

                } catch (ex: Exception) {
                    ex.printStackTrace()

                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["notification"] =actionname
                params["notification_status_change"] =  value

                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
               // params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

}
