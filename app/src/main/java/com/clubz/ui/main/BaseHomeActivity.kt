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
import android.widget.ArrayAdapter
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.DialogMenu
import com.clubz.ui.ads.fragment.AdsFragment
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.core.BaseFragment
import com.clubz.ui.newsfeed.fragment.FragNewsList
import kotlinx.android.synthetic.main.club_more_menu.*
import kotlinx.android.synthetic.main.menu_news_filter.*

abstract class BaseHomeActivity : BaseActivity(),
        BaseFragment.FragmentListner, View.OnClickListener {

    //protected var dialog : Dialog? = null
    protected var menuDialog: Dialog? = null
    protected var newsFilterDialog: Dialog? = null
    protected var invalidateThreeDotMenu: Boolean = false
    // protected var myActivityDailog: Dialog? = null

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
        if (newsFilterDialog == null) {
            newsFilterDialog = Dialog(this)
            newsFilterDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = newsFilterDialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            newsFilterDialog?.setContentView(R.layout.menu_news_filter)
            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            newsFilterDialog?.setCancelable(true)
            for (views in arrayOf(newsFilterDialog?.ch_myClubOnly, newsFilterDialog?.ch_byClubs, newsFilterDialog?.ch_byComments, newsFilterDialog?.ch_byLikes, newsFilterDialog?.ll_clearFilter))
                views?.setOnClickListener(getActivity())
        }
        newsFilterDialog?.show()
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
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            menuDialog?.setContentView(R.layout.club_more_menu)

            if (list != null) {
                menuDialog?.ll_menu0?.visibility = View.VISIBLE
                menuDialog?.menu_iv0?.setImageResource(list[0].id)
                menuDialog?.menu_tv0?.text = list[0].title
                if (list.size > 1) {
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
            if (frag::class.java.simpleName == AdsFragment::class.java.simpleName) {
                menuDialog?.ll_menu1?.visibility = View.GONE
            }
            menuDialog?.ll_menu0?.setOnClickListener {
                handleMenuClick(list!![0])
            }

            menuDialog?.ll_menu1?.setOnClickListener {
                handleMenuClick(list!![1])
            }

            menuDialog?.ll_menu2?.setOnClickListener {
                handleMenuClick(list!![2])
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

    private fun handleMenuClick(menu: DialogMenu) {
        menuDialog?.dismiss()
        when (menu.title) {
            getString(R.string.create_new_nwes) -> {
                navigateCreateNewsFeed()
            }
            getString(R.string.filter_clubs) -> {
                showFilterDialog()
            }
            getString(R.string.renew_my_location) -> {
                checkLocationUpdate()
            }
            getString(R.string.t_new_activity) -> {
                navigateCreateActivity()
            }
            getString(R.string.my_activity) -> {
                navigateMyActivity()
            }
            getString(R.string.create_new_ad) -> {
                navigateCreateAAd()
            }
        /*getString(R.string.others_activity) -> {
            navigateOthersActivity()
        }*/
        }
    }

    abstract fun navigateCreateActivity()
    abstract fun navigateCreateAAd()
    abstract fun navigateCreateNewsFeed()
    abstract fun navigateMyActivity()
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
}