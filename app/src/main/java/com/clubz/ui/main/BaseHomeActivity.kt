package com.clubz.ui.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.ListPopupWindow
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.DialogMenu
import com.clubz.data.model.NotificationSesssion
import com.clubz.data.remote.WebService
import com.clubz.ui.ads.fragment.AdsFragment
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.core.BaseFragment
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Internet_Connection_dialog
import com.clubz.ui.newsfeed.fragment.FragNewsList
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import kotlinx.android.synthetic.main.club_more_menu.*
import kotlinx.android.synthetic.main.menu_activity_notification_filter.*
import kotlinx.android.synthetic.main.menu_chat_filter.*
import kotlinx.android.synthetic.main.menu_news_filter.*
import org.json.JSONObject

abstract class BaseHomeActivity : BaseActivity(),
        BaseFragment.FragmentListner, View.OnClickListener {

    //protected var dialog : Dialog? = null
    protected var menuDialog: Dialog? = null
    protected var newsFilterDialog: Dialog? = null
    protected var chatFilterDialog: Dialog? = null
    protected var activityFilterDialog: Dialog? = null
    protected var invalidateThreeDotMenu: Boolean = false
    // protected var myActivityDailog: Dialog? = null
    var activityfilterfor =""


    override fun replaceFragment(fragment: Fragment) {
        super.replaceFragment(fragment)
        try {
            val fragmentManager = supportFragmentManager
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentName = fragment.javaClass.name
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            fragmentTransaction.replace(R.id.frag_container, fragment, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
            bottomtabHandler(fragment)
            setActionbarMenu(fragment)
            hideKeyBoard()
        } catch (e: Exception) {
        }
    }


    @SuppressLint("RtlHardcoded")
    protected fun showLogoutPopup(v: View) {
        val products = arrayOf(getString(R.string.logout))
        val lpw = ListPopupWindow(this)
        lpw.anchorView = v
        lpw.setDropDownGravity(Gravity.RIGHT)
        lpw.height = ListPopupWindow.WRAP_CONTENT
        lpw.width = 300
        lpw.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, products)) // list_item is your textView with gravity.
        lpw.setOnItemClickListener { _, _, _, _ ->
            lpw.dismiss()
            SessionManager.getObj().logout(this)
        }
        lpw.show()
    }

    /*  @SuppressLint("RtlHardcoded")
      protected fun clubSelectionMenu(position: Int){
          if(dialog==null){
              dialog = Dialog(this)
              dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
              val dialogWindow = dialog?.window
              dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
              dialog?.setContentView(R.layout.menu_club_selection)
              for (views in arrayOf(dialog?.tv_private, dialog?.tv_public)) views?.setOnClickListener(getActivity())
              val lp = dialogWindow?.attributes
              dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
              lp?.y = -100
              dialogWindow?.attributes = lp
              dialog?.setCancelable(true)
          }

          if (position == 0) {
              if(ClubZ.isPrivate ==0){
                  dialog?.chk_priavte?.isChecked = true; dialog?.chk_public?.isChecked = true
              } else {
                  dialog?.chk_priavte?.isChecked = (ClubZ.isPrivate ==2)
                  dialog?.chk_public?.isChecked  = (ClubZ.isPrivate ==1)
              }
          }

          dialog?.show()
      }*/

    @SuppressLint("RtlHardcoded")
    private fun showFilterDialog() {

        if(newsFilterDialog == null) {
            newsFilterDialog = Dialog(this)
            newsFilterDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = newsFilterDialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            newsFilterDialog?.setContentView(R.layout.menu_news_filter)
            for (views in arrayOf(newsFilterDialog?.ch_myClubOnly/*, newsFilterDialog?.ch_byClubs*/, newsFilterDialog?.ch_byComments, newsFilterDialog?.ch_byLikes/*, newsFilterDialog?.ll_clearFilter*/))
                views?.setOnClickListener(getActivity())

            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            newsFilterDialog?.setCancelable(true)

        }
            newsFilterDialog?.show()
        // newsFilterDialog?.setOnDismissListener { updateMyNewsFeed() }
    }

    @SuppressLint("RtlHardcoded")
    private fun showChatFilterDialog() {

        if(chatFilterDialog == null) {
            chatFilterDialog = Dialog(this)
            chatFilterDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = chatFilterDialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            chatFilterDialog?.setContentView(R.layout.menu_chat_filter)


            for (views in arrayOf(chatFilterDialog?.ch_newschatOnly/*, newsFilterDialog?.ch_byClubs*/, chatFilterDialog?.ch_bypersonalchat, chatFilterDialog?.ch_byactivity, chatFilterDialog?.ch_byads))
                views?.setOnClickListener(getActivity())
            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            chatFilterDialog?.setCancelable(true)

        }
        chatFilterDialog?.show()
        // newsFilterDialog?.setOnDismissListener { updateMyNewsFeed() }
    }

    @SuppressLint("RtlHardcoded")
    private fun showActivityNotificationFilterDialog() {

            activityFilterDialog = Dialog(this)
            activityFilterDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = activityFilterDialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            activityFilterDialog?.setContentView(R.layout.menu_activity_notification_filter)


            /*for (views in arrayOf(activityFilterDialog?.ch_date_confirm*//*, newsFilterDialog?.ch_byClubs*//*, activityFilterDialog?.activity_chat, chatFilterDialog?.ch_cancelled))
                views?.setOnClickListener(getActivity())*/
            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp

            val notificationsession = SessionManager.getObj().notification

            if(notificationsession.date_confirmed_notification.equals("1")){
                activityFilterDialog?.ch_date_confirm!!.isChecked = true
            }
            if(notificationsession.date_cancelled_notification.equals("1")){
                activityFilterDialog?.ch_cancelled!!.isChecked = true
            }
            if(notificationsession.activity_chat_notification.equals("1")){
                activityFilterDialog?.activity_chat!!.isChecked = true
            }
            activityFilterDialog?.ch_date_confirm?.setOnClickListener( View.OnClickListener {

                activityfilterfor = "date_confirmed_notification"
                updateactivitynotification(activityfilterfor)
            })
           activityFilterDialog?.ch_cancelled?.setOnClickListener( View.OnClickListener {

            activityfilterfor = "date_cancelled_notification"
            updateactivitynotification(activityfilterfor)
            })
           activityFilterDialog?.activity_chat?.setOnClickListener( View.OnClickListener {

            activityfilterfor = "activity_chat_notification"
            updateactivitynotification(activityfilterfor)
          })




            activityFilterDialog?.setCancelable(true)


        activityFilterDialog?.show()
        // newsFilterDialog?.setOnDismissListener { updateMyNewsFeed() }
    }

    /* @SuppressLint("RtlHardcoded")
     protected fun showMyActivityDialog() {
         if (myActivityDailog == null) {
             myActivityDailog = Dialog(this)
             myActivityDailog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
             val dialogWindow = myActivityDailog?.window
             dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             myActivityDailog?.setContentView(R.layout.menu_my_activity)
             val lp = dialogWindow?.attributes
             dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
             lp?.y = -100
             dialogWindow?.attributes = lp
             myActivityDailog?.myActivity?.setOnClickListener(this)
             myActivityDailog?.show()
         }
         myActivityDailog?.show()
     }*/


    @SuppressLint("RtlHardcoded")
    protected fun showMenu(list: ArrayList<DialogMenu>?, frag: Fragment) {

        if (invalidateThreeDotMenu) menuDialog = null

        if (menuDialog == null) {
            menuDialog = Dialog(this)
            menuDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = menuDialog?.window
            dialogWindow?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            menuDialog?.setContentView(R.layout.club_more_menu)

            if (list != null) {
                menuDialog?.ll_menu0?.visibility = View.VISIBLE
                menuDialog?.view?.visibility = View.GONE
                menuDialog?.menu_iv0?.setImageResource(list[0].id)
                menuDialog?.menu_tv0?.text = list[0].title



                if (list.size > 1) {
                    menuDialog?.ll_menu1?.visibility = View.VISIBLE
                    menuDialog?.view?.visibility = View.VISIBLE
                    menuDialog?.menu_iv1?.setImageResource(list[1].id)
                    menuDialog?.menu_tv1?.text = list[1].title
                }
                if (list.size > 2) {
                    menuDialog?.ll_menu2?.visibility = View.VISIBLE
                    menuDialog?.menu_iv2?.setImageResource(list[2].id)
                    menuDialog?.menu_tv2?.text = list[2].title
                }
            }
            if (frag::class.java.simpleName == FragNewsList::class.java.simpleName) {
                menuDialog?.ll_menu2?.visibility = View.GONE
            }
            menuDialog?.ll_menu0?.setOnClickListener {
                handleMenuClick(list!![0],frag)
            }

            menuDialog?.ll_menu1?.setOnClickListener {
                handleMenuClick(list!![1],frag)
            }

            menuDialog?.ll_menu2?.setOnClickListener {
                handleMenuClick(list!![2],frag)
            }

            // for (views in arrayOf(menuDialog?.ll_menu1, menuDialog?.ll_menu2)) views?.setOnClickListener(this)
            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            menuDialog?.setCancelable(true)
        }
        menuDialog?.show()
    }

    private fun handleMenuClick(menu: DialogMenu, frag: Fragment) {
        menuDialog?.dismiss()
        when (menu.title) {
            getString(R.string.create_new_nwes) -> {
                if (Util.isConnectingToInternet(this)) {
                    navigateCreateNewsFeed()
                } else {
                    object : Internet_Connection_dialog(this) {
                        override fun tryaginlistner() {
                            this.dismiss()
                            navigateCreateNewsFeed()
                        }
                    }.show()
                }
            }
            getString(R.string.filter_clubs) -> {
                if (frag::class.java.simpleName == FragNewsList::class.java.simpleName) {
                    showFilterDialog()
                }
                else{
                    showChatFilterDialog()
                }

            }
            getString(R.string.renew_my_location) -> {
                checkLocationUpdate()
            }
            getString(R.string.t_new_activity) -> {
                if (Util.isConnectingToInternet(this)) {
                    navigateCreateActivity()
                } else {
                    object : Internet_Connection_dialog(this) {
                        override fun tryaginlistner() {
                            this.dismiss()
                            navigateCreateActivity()
                        }
                    }.show()
                }
            }
            getString(R.string.my_activity) -> {
                navigateMyActivity()
            }

            getString(R.string.create_chat_feed) -> {
               navigateContactActivity()
            }
            getString(R.string.my_ads) -> {
                navigateMyAds()
            }
            getString(R.string.create_new_ad) -> {
                if (Util.isConnectingToInternet(this)) {
                    navigateCreateAAd()
                } else {
                    object : Internet_Connection_dialog(this) {
                        override fun tryaginlistner() {
                            this.dismiss()
                            navigateCreateAAd()
                        }
                    }.show()
                }
            }
            getString(R.string.set_notification) -> {
                showActivityNotificationFilterDialog()
            }
        }
    }

    abstract fun navigateCreateActivity()
    abstract fun navigateCreateAAd()
    abstract fun navigateMyAds()
    abstract fun navigateCreateNewsFeed()
    abstract fun navigateMyActivity()
    abstract fun navigateContactActivity()
    //abstract fun navigateOthersActivity()
    abstract fun checkLocationUpdate()

    //abstract fun updateMyNewsFeed()
    abstract fun getActivity(): HomeActivity

    abstract fun bottomtabHandler(fragment: Fragment)
    abstract fun setActionbarMenu(fragment: Fragment)


    /*  private fun getAddress(latitude: Double, longitude: Double): Array<String> {
          val result = Array<String>(3, {i->""})
          result[0] = ""
          result[1] = ""
          result[2] = ""
          val geocoder: Geocoder
          val addresses: List<Address>
          geocoder = Geocoder(this, Locale.US)

          try {
              addresses = geocoder.getFromLocation(latitude, longitude, 1)
              val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
              val city = addresses[0].locality
              //  String addressLine = addresses.get(0).getAddressLine(1);
              result[0] = addresses[0].adminArea  //state
              result[1] = addresses[0].countryName  //country
              // String postalCode = addresses.get(0).getPostalCode();
              // String knownName = addresses.get(0).getFeatureName();
              //result = knownName + " ," + addressLine + " , " + city + "," + state + "," + country + " counter" + counter;// Here 1 represent max location result to returned, by documents it recommended 1 to 5
              result[2] = address// Here 1 represent max location result to returned, by documents it recommended 1 to 5

          } catch (e: Exception) {
              e.printStackTrace()
          }

          return result
      }*/

    /*fun addFragment(fragmentHolder: Fragment, animationValue: Int): Fragment? {
      try {
          val fragmentManager = supportFragmentManager
          val fragmentName = fragmentHolder.javaClass.name
          val fragmentTransaction = fragmentManager.beginTransaction()
          //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
          fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
          fragmentTransaction.add(R.id.frag_container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
          fragmentTransaction.commit()
          bottomtabHandler(fragmentHolder)
          //stausBarHandler(fragmentHolder)
          setActionbarMenu(fragmentHolder)
          hideKeyBoard()
          return fragmentHolder
      } catch (e: Exception) {
          return null
      }
  }
*/


    private fun updateactivitynotification(type:String){

        val notificationsession = SessionManager.getObj().notification

        if(type.equals("date_confirmed_notification")){
            if(notificationsession.activities_notifications.equals("1")){

                if(activityFilterDialog?.ch_date_confirm!!.isChecked){
                    setNotification("1",type)
                }
                else{
                    setNotification("0",type)
                }

            }
            else{

                if(notificationsession.date_confirmed_notification.equals("1")){
                    activityFilterDialog?.ch_date_confirm!!.isChecked = true
                }
                else{
                    activityFilterDialog?.ch_date_confirm!!.isChecked = false
                }

                showToast(resources.getString(R.string.allow_notifications_setting))
            }

        }
        if(type.equals("date_cancelled_notification")){
            if(notificationsession.activities_notifications.equals("1")){
                if(activityFilterDialog?.ch_cancelled!!.isChecked){
                    setNotification("1",type)
                }
                else{
                    setNotification("0",type)
                }


            }
            else{

                if(notificationsession.date_cancelled_notification.equals("1")){
                    activityFilterDialog?.ch_cancelled!!.isChecked = true
                }
                else{
                    activityFilterDialog?.ch_cancelled!!.isChecked = false
                }

                showToast(resources.getString(R.string.allow_notifications_setting))
            }
        }
        if(type.equals("activity_chat_notification")){
            if(notificationsession.chat_notifications.equals("1")){
                if(activityFilterDialog?.activity_chat!!.isChecked){
                    setNotification("1",type)
                }
                else{
                    setNotification("0",type)
                }

            }
            else{

                if(notificationsession.activity_chat_notification.equals("1")){
                    activityFilterDialog?.activity_chat!!.isChecked = true
                }
                else{
                    activityFilterDialog?.activity_chat!!.isChecked = false
                }

                showToast(resources.getString(R.string.allow_notifications_setting))
            }
        }

    }

    private fun setNotification(value: String, actionname :String) {
        val dialog = CusDialogProg(this@BaseHomeActivity)
        dialog.show()
        object : VolleyGetPost(this@BaseHomeActivity, WebService.manageNotification, false,
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

                    }
                    else{
                        Toast.makeText(this@BaseHomeActivity, obj.getString("message"), Toast.LENGTH_LONG).show()

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