package com.clubz

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout

import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.DrawerLayout
import android.support.v4.app.ActionBarDrawerToggle
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
import com.clubz.util.DrawerMarginFixer
import com.clubz.util.Util
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import kotlinx.android.synthetic.main.activity_home_new.*
import kotlinx.android.synthetic.main.menu_club_selection.*
import java.util.*

class HomeActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener, View.OnClickListener , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{



    lateinit var mDrawerLayout: DrawerLayout
    var open: Boolean = false
    var doublebackpress: Boolean = false
    var lastDrawerGravity :Int= Gravity.START;

    var isPrivate: Int = 0  // 0: Both option available , 1:public ,2:private
    var filterListner: FilterListner? = null;
    var textChnageListner: Textwatcher_Statusbar? = null


    var latitude: Double = 0.toDouble()
    var longitude:Double = 0.toDouble()
    protected var mGoogleApiClient: GoogleApiClient? = null
    lateinit var locationManager: LocationManager

    private  var isGPSEnabled = false       // flag for GPS status
    private  var isNetworkEnabled = false   // flag for network status

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mCurrentLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_new)

        tablayout.addOnTabSelectedListener(this)
        for (views in arrayOf(menu, logout, search, cancel, bubble_menu, addsymbol, filter_list, tv_private, tv_public , back)) views.setOnClickListener(this)

        setTab(tablayout.getTabAt(0)!!, R.drawable.ic_news_active, true)
        replaceFragment(Frag_News_List());
        ///addFragment_new(Frag_Search_Club(),true ,R.id.frag_container2);
        checkLocationUpdate()

        Util.e("authtoken", SessionManager.getObj().user.auth_token);
        //TODO disable drawer.
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout ,R.drawable.ic_menu_black_24dp, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(view: View) {

                open = false
                stausBarHandler(getCurrentFragment()!!)
                bottomtabHandler(getCurrentFragment()!!)
                supportInvalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                supportInvalidateOptionsMenu()
                open = true
                if(drawerView.id==R.id.drawerView2){
                    val far = getSupportFragmentManager().findFragmentById(R.id.fragment2) as Frag_Search_Club
                    stausBarHandler(far)
                    bottomtabHandler(far)
                    far.checkLocation()
                    lastDrawerGravity = Gravity.END
                }
                else lastDrawerGravity = Gravity.START

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

        try{
            latitude = ClubZ.latitude
            longitude = ClubZ.longitude
        }catch (ex:Exception){}
        DrawerMarginFixer.fixMinDrawerMargin(mDrawerLayout)

    }

    override fun onDestroy() {

        super.onDestroy()

    }

    override fun onResume() {
        super.onResume()

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
            R.id.search -> {}//{addFragment(Frag_Search_Club(), 0);}
            R.id.cancel -> {
                search_text.setText("")
                hideKeyBoard()
            }
            R.id.bubble_menu -> clubOptions(0);
            R.id.menu -> {
                draweHandler(Gravity.START)
            }
            R.id.addsymbol -> {
                draweHandler(lastDrawerGravity)
                addFragment(Frag_Create_club(),0)
                object : Purchase_membership_dialog(this) {
                    override fun viewplansListner() {
                        this.dismiss();
                    }
                }.show()
            };
            R.id.filter_list -> closeOption()
            R.id.tv_private -> {
                when(isPrivate){
                    1->{isPrivate = 0; chk_priavte.setChecked(true);        chk_public.setChecked(true); if (filterListner != null) filterListner!!.onFilterChnge()}
                    0,2->{isPrivate = 1; chk_priavte.setChecked(false);     chk_public.setChecked(true); if (filterListner != null) filterListner!!.onFilterChnge()}
                }
            }
            R.id.tv_public -> {
                when(isPrivate){
                    2->{isPrivate = 0; chk_priavte.setChecked(true);        chk_public.setChecked(true); if (filterListner != null) filterListner!!.onFilterChnge()}
                    0,1->{isPrivate = 2; chk_priavte.setChecked(true);      chk_public.setChecked(false);if (filterListner != null) filterListner!!.onFilterChnge()}
                }
            }
            R.id.back-> onBackPressed()

        }

    }

    /**
     * Open the specified drawer by animating it out of view.
     *
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     */
    fun draweHandler(gravity :Int = lastDrawerGravity){
        if (!open) {
            mDrawerLayout.openDrawer(gravity)
            open = true
        } else {
            mDrawerLayout.closeDrawer(gravity)
            open = false
        }
    }


    fun clubOptions(position: Int) {
        var canshow = false
        when(getCurrentFragment()!!::class.java.simpleName.toString()){
            Frag_Search_Club::class.java.simpleName -> canshow = true
        }
        if(!canshow) return
            filter_list.visibility = View.VISIBLE
        filter_list.getChildAt(position).visibility = View.VISIBLE
        if (position == 0) {
            if(isPrivate==0){
                chk_priavte.isChecked = true; chk_public.isChecked = true;
            }
            else {chk_priavte.isChecked = (isPrivate==2); chk_public.isChecked = (isPrivate==1);} }
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
            bottomtabHandler(fragmentHolder)
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
            bottomtabHandler(fragmentHolder)
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
            bottomtabHandler(fragmentHolder)
            stausBarHandler(fragmentHolder)
            hideKeyBoard()
            return fragmentHolder
        } catch (e: Exception) {
            return null
        }
    }


    fun addFragment_new(fragment: Fragment, addToBackStack: Boolean, containerId: Int) {
        val backStackName = fragment::class.java.simpleName
        val fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0)
        if (!fragmentPopped) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            transaction.add(containerId, fragment, backStackName) //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName)
            transaction.commit()
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
                search_text.setText("")
                //search_text.setCursorVisible(false)
            }
            Frag_ClubDetails::class.java.simpleName -> {
                for (i in 0..cus_status.childCount - 1) cus_status.getChildAt(i).visibility = View.GONE
                for (view in arrayOf(back, title_tv, bubble_menu)) view.visibility = View.VISIBLE
                title_tv.setText(" "+(fragemet as Frag_ClubDetails).clubz.club_name)
            }
        }
    }

    fun bottomtabHandler(fragemet: Fragment){
       try{ when (fragemet::class.java.simpleName) {

            Frag_News_List::class.java.simpleName -> {
                tablayout.visibility = View.VISIBLE
            }
            else->{tablayout.visibility = View.GONE}
        }
       }catch (ex:Exception){
           ex.printStackTrace()
       }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    fun showStatusBar() {
        cus_status.visibility = View.VISIBLE
    }


    override fun onBackPressed() {
        if(open) {
            draweHandler()
            return }

        closeOption()
        hideKeyBoard()

        val handler = Handler()
        var runnable: Runnable? = null
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        try {
            Util.e("Current Fragment", getCurrentFragment()!!::class.java.simpleName.toString())
            bottomtabHandler(getCurrentFragment()!!)
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


    /************************************************/

    private fun checkLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Permission(this,this).checkLocationPermission()) {
                buildGoogleApiClient()
                mGoogleApiClient!!.connect()
            }
        } else {
            if (checkLocationPermissionLowerApi()) {
                buildGoogleApiClient()
                mGoogleApiClient!!.connect()
            } else {
                showSettingsAlert()
            }
        }
    }

    @Synchronized protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build()

    }

    private fun checkLocationPermissionLowerApi(): Boolean {
        var isEnable = true
        try {
            locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                isEnable = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isEnable
    }

    fun showSettingsAlert() {}


    override fun onConnected(bundle: Bundle?) {
        try {
            val UPDATE_INTERVAL: Long = 15*1000  /* 10 secs */
            val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL)
            // Request location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }

            if (mGoogleApiClient == null)
                return
            if (!mGoogleApiClient!!.isConnected())
                return
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location) {
        if (mGoogleApiClient!!.isConnected()) {
            startLocationUpdates(location.latitude, location.longitude)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)

        }
    }

    private fun getAddress(latitude: Double, longitude: Double): Array<String> {
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
    }

    private fun startLocationUpdates(latitude: Double, longitude: Double) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        this.latitude = latitude
        ClubZ.latitude = latitude;
        this.longitude = longitude
        ClubZ.longitude = longitude;
        Util.showToast(latitude.toString()+" : "+longitude,this)

    }

    /*******************************************************************************************/


}
