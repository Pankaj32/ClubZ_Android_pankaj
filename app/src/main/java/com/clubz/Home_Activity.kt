package com.clubz

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.clubz.Cus_Views.Purchase_membership_dialog
import com.clubz.fragment.FilterListner
import com.clubz.fragment.Textwatcher_Statusbar
import com.clubz.fragment.home.Frag_ClubDetails
import com.clubz.fragment.home.Frag_Create_club
import com.clubz.fragment.home.Frag_News_List
import com.clubz.fragment.home.Frag_Search_Club
import com.clubz.helper.Permission
import com.clubz.helper.SessionManager
import com.clubz.util.Util
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.menu_club_selection.*
import java.util.*


/**
 * Created by mindiii on 2/23/18.
 */

class Home_Activity : AppCompatActivity(), TabLayout.OnTabSelectedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener {



    lateinit var mDrawerLayout: DrawerLayout
    var open: Boolean = false
    var doublebackpress: Boolean = false

    internal var LocationCounter: Int = 0
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null

    var isPrivate: Int = 0
    var filterListner: FilterListner? = null;
    var textChnageListner: Textwatcher_Statusbar? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tablayout.addOnTabSelectedListener(this)

        for (view in arrayOf(menu, logout, search, cancel, bubble_menu, addsymbol, filter_list, tv_private, tv_public , back)) view.setOnClickListener(this)

        setTab(tablayout.getTabAt(0)!!, R.drawable.ic_news_active, true)
        replaceFragment(Frag_News_List());
        val permission = Permission(this, this)
        permission.askForGps()
        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        locationRequest = LocationRequest()
        locationRequest!!.setInterval((10 * 1000).toLong())
        locationRequest!!.setFastestInterval((15 * 1000).toLong())
        locationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        googleApiClient!!.connect()
        permission.checkLocationPermission()


        Util.e("authtoken", SessionManager.getObj().user.auth_token);
        //TODO disable drawer.
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        var mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_menu_black_24dp, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(view: View) {
                supportInvalidateOptionsMenu()
                open = false
            }

            override fun onDrawerOpened(drawerView: View) {
                supportInvalidateOptionsMenu()
                open = true
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                mDrawerLayout.bringChildToFront(drawerView)
                mDrawerLayout.requestLayout()
            }
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle)
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));

        search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (textChnageListner != null) {
                    textChnageListner!!.afterchangeText(p0)
                    search_text.setCursorVisible(true)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length > 0) {
                    search_back_icon.visibility = View.GONE
                } else {
                    search_back_icon.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroy() {
        stopFusedLocation()
        super.onDestroy()

    }

    override fun onResume() {
        if(latitude==0.0 && longitude==0.0){
            startLocationTrack()
        }
        super.onResume()

    }

    /************* Location Listner *******************/
    override fun onConnected(p0: Bundle?) {
        requestLocationUpdates()
        startLocationTrack()
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            latitude = location.getLatitude()
            longitude = location.getLongitude()
            Util.e("Locations ",latitude.toString()+" : "+longitude)
            if(latitude!=0.0 && longitude!=0.0){
                locationRequest!!.setInterval((60 * 1000).toLong())
                locationRequest!!.setFastestInterval((60 * 1000).toLong())
                locationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            }
        }
    }

    fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Permission(this@Home_Activity,this).checkLocationPermission()
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
    }

    fun stopFusedLocation() {
       try {
           if (googleApiClient != null) {
               googleApiClient!!.disconnect()
           }
       } catch (ex:Exception){}
    }


    fun startLocationTrack(){
       var timer = Timer()
               timer.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                runOnUiThread(Runnable {
                    if(latitude==0.0 && longitude==0.0){
                        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        requestLocationUpdates()

                }
                else{
                    locationRequest!!.setInterval((60 * 1000).toLong())
                    locationRequest!!.setFastestInterval((60 * 1000).toLong())
                    locationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    timer.cancel();
                    timer.purge();
                }})
            }
        },1000,1000)
    }
    /********************************************************/
    override fun onTabReselected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()) {
            0 -> {
            }
            1 -> {
            }
            2 -> {
            }
            3 -> {
            }
        }

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()) {
            0 -> {
                setTab(tab, R.drawable.ic_news, false)
            }
            1 -> {
                setTab(tab, R.drawable.ic_activity, false)
            }
            2 -> {
                setTab(tab, R.drawable.ic_chat_bubble, false)
            }
            3 -> {
                setTab(tab, R.drawable.ic_ads, false)
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        hideKeyBoard()
        when (tab!!.getPosition()) {
            0 -> {
                setTab(tab, R.drawable.ic_news_active, true)
            }
            1 -> {
                setTab(tab, R.drawable.ic_activity_active, true)
            }
            2 -> {
                setTab(tab, R.drawable.ic_chat_bubble_active, true)
            }
            3 -> {
                setTab(tab, R.drawable.ic_ads_active, true)
            }
        }
    }

    internal fun setTab(tab: TabLayout.Tab, imageRes: Int, isActive: Boolean) {
        tab.customView!!.findViewById<AppCompatImageView>(android.R.id.icon).setImageResource(imageRes)
        tab.customView!!.findViewById<TextView>(android.R.id.text1).setTextColor(resources.getColor(if (isActive) R.color.active_tab else R.color.inactive_tab))
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.logout -> SessionManager.getObj().logout(this)
            R.id.search -> {addFragment(Frag_Search_Club(), 0);}
            R.id.cancel -> replaceFragment(Frag_News_List())
            R.id.bubble_menu -> clubOptions(0);
            R.id.menu -> {
                if (!open) {
                    mDrawerLayout.openDrawer(Gravity.START)
                    open = true
                } else {
                    mDrawerLayout.closeDrawer(Gravity.START)
                    open = false
                }
            }
            R.id.addsymbol -> {
                object : Purchase_membership_dialog(this) {
                    override fun viewplansListner() {
                        this.dismiss();
                    }
                }.show()
                addFragment(Frag_Create_club(),0)
            };
            R.id.filter_list -> closeOption()
            R.id.tv_private -> {
                if(isPrivate!=2){isPrivate = 2; chk_priavte.setChecked(true); chk_public.setChecked(false); if (filterListner != null) filterListner!!.onFilterChnge()}
                else{isPrivate = 0; chk_priavte.setChecked(false); chk_public.setChecked(false); if (filterListner != null) filterListner!!.onFilterChnge()}
            }
            R.id.tv_public -> {
                if(isPrivate!=1){ isPrivate = 1; chk_priavte.setChecked(false); chk_public.setChecked(true); if (filterListner != null) filterListner!!.onFilterChnge()}
                else {isPrivate = 0; chk_priavte.setChecked(false); chk_public.setChecked(false); if (filterListner != null) filterListner!!.onFilterChnge()}
            }
            R.id.back->onBackPressed()
        }

    }


    fun clubOptions(position: Int) {
        filter_list.visibility = View.VISIBLE
        filter_list.getChildAt(position).visibility = View.VISIBLE
        if (position == 0) {
            chk_priavte.isChecked = (isPrivate==2); chk_public.isChecked = (isPrivate==1); }
    }

    fun closeOption() {
        for (i in 0..filter_list.childCount - 1) filter_list.getChildAt(i).visibility = View.GONE
        filter_list.visibility = View.GONE
    }


    internal fun replaceFragment(fragmentHolder: Fragment) {
        try {
            val fragmentManager = supportFragmentManager
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentName = fragmentHolder.javaClass.name
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            fragmentTransaction.replace(R.id.frag_container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
            stausBarHandler(fragmentHolder)
            hideKeyBoard()

        } catch (e: Exception) {
            //  Util.e("value", e.toString())
        }

    }

    /**
     * commiting with state loss
     */
    internal fun replaceFragmentLoss(fragmentHolder: Fragment) {
        try {
            val fragmentManager = supportFragmentManager
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentName = fragmentHolder.javaClass.name
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.frag_container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commitAllowingStateLoss() // important
            stausBarHandler(fragmentHolder)
            hideKeyBoard()
        } catch (e: Exception) {
            //Util.e("value", e.toString())
        }

    }


    fun addFragment(fragmentHolder: Fragment, animationValue: Int): Fragment? {
        try {
            val fragmentManager = supportFragmentManager
            val fragmentName = fragmentHolder.javaClass.name
            val fragmentTransaction = fragmentManager.beginTransaction()
            //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            fragmentTransaction.add(R.id.frag_container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
            stausBarHandler(fragmentHolder)
            hideKeyBoard()
            return fragmentHolder
        } catch (e: Exception) {
            return null
        }
    }

    fun hideKeyBoard() {
        try {
            val inputManager = getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {

        }

    }

    fun stausBarHandler(fragemet: Fragment) {

        when (fragemet::class.java.simpleName) {

            Frag_News_List::class.java.simpleName -> {
                isPrivate = 0
                for (view in arrayOf(title_tv, bookmark, menu, search)) view.visibility = View.VISIBLE
                for (view in arrayOf(search_text, back, addsymbol, serch_box)) view.visibility = View.GONE
                title_tv.setText(R.string.t_stay_up)
            }

            Frag_Create_club::class.java.simpleName -> {
                cus_status.visibility = View.GONE
            }

            Frag_Search_Club::class.java.simpleName -> {
                title_tv.visibility = View.GONE
                for (view in arrayOf(title_tv, bookmark, menu, search)) view.visibility = View.GONE
                for (view in arrayOf(search_text, back, addsymbol, serch_box)) view.visibility = View.VISIBLE
                filterListner = (fragemet as Frag_Search_Club);
                textChnageListner = fragemet
                search_text.setCursorVisible(false)
            }

            Frag_ClubDetails::class.java.simpleName -> {
                for (i in 0..cus_status.childCount - 1) cus_status.getChildAt(i).visibility = View.GONE
                for (view in arrayOf(back, title_tv, bubble_menu)) view.visibility = View.VISIBLE
                title_tv.setText(" "+(fragemet as Frag_ClubDetails).clubz.club_name)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun showStatusBar() {
        cus_status.visibility = View.VISIBLE
    }


    override fun onBackPressed() {
        val handler = Handler()
        var runnable: Runnable? = null
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        try {
            Util.e("Current Fragment", getCurrentFragment()!!::class.java.simpleName.toString())
            stausBarHandler(getCurrentFragment()!!)
        } catch (ex: Exception) {
        }
        }
        else{
            runnable = Runnable { doublebackpress = false }
            handler.postDelayed(runnable, 1000.toLong())
            if (doublebackpress) {
                handler.removeCallbacks(runnable)
                finish()
            } else {
                doublebackpress = true
            }
        }
    }

    fun getCurrentFragment(): Fragment? {
        val fragments = supportFragmentManager.fragments
        return fragments[fragments.size - 1]
    }


}
