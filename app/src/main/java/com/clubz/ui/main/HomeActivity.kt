package com.clubz.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.*

import android.support.v4.widget.DrawerLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.app.ActionBarDrawerToggle

import android.view.*
import android.widget.*
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.UserBean
import com.clubz.chat.util.ChatUtil
import com.clubz.ui.core.FilterListner
import com.clubz.ui.newsfeed.fragment.FragNewsList
import com.clubz.helper.Permission
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Address
import com.clubz.data.model.DialogMenu
import com.clubz.data.model.Profile
import com.clubz.data.model.UserLocation
import com.clubz.data.remote.GioAddressTask
import com.clubz.ui.ads.fragment.AdsFragment
import com.clubz.ui.chat.ChatFragment
import com.clubz.ui.club.ClubCreationActivity
import com.clubz.ui.club.ClubsActivity
import com.clubz.ui.club.fragment.FragMyClubs
import com.clubz.ui.newsfeed.CreateNewsFeedActivity
import com.clubz.ui.profile.ContactListActivity
import com.clubz.ui.profile.ProfileActivity
import com.clubz.ui.setting.SettingActivity
import com.clubz.ui.user_activities.activity.MyActivities
import com.clubz.ui.user_activities.activity.NewActivities
import com.clubz.ui.user_activities.fragment.Frag_Find_Activities
import com.clubz.utils.DrawerMarginFixer
import com.clubz.utils.Util
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home_test.*
import kotlinx.android.synthetic.main.menu_club_selection.*
import kotlinx.android.synthetic.main.menu_news_filter.*
import kotlinx.android.synthetic.main.nav_header.view.*

class HomeActivity : BaseHomeActivity(), TabLayout.OnTabSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener{

   // private lateinit var mDrawerLayout: DrawerLayout
    private var isOpenMyClub: Boolean = false
    private var isRightNavDrawerOpen: Boolean = false
    private var doublebackpress: Boolean = false
    private var lastDrawerGravity :Int= Gravity.START


    var filterListner: FilterListner? = null
    //var textChnageListner: Textwatcher_Statusbar? = null

    private  var isGPSEnabled = false       // flag for GPS status
    private  var isNetworkEnabled = false   // flag for network status

    private lateinit var mCurrentLocation: Location
    private lateinit var locationManager: LocationManager
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var sessionManager: SessionManager // user session

    // filter for news feed page
    private var like = false
    private var comment = false
    private var club = false
    private var showMyNewsfeedOnly = false
    private var ifNeedTocallApi : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager.getObj()
        if(ClubZ.currentUser==null) ClubZ.currentUser = sessionManager.user

        setContentView(R.layout.activity_home_test)
        setSupportActionBar(toolbar)

        val userLocation = sessionManager.lastKnownLocation
        if(userLocation==null) checkLocationUpdate()
        else {
            ClubZ.latitude = userLocation.latitude;
            ClubZ.longitude = userLocation.longitude
            ClubZ.city = userLocation.city
        }

        Util.e("authtoken", ClubZ.currentUser!!.auth_token)

        initView()  // ui variables initialization and update
        updateFirebaseToken()
        replaceFragment(FragNewsList())

        val mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, toolbar,// R.drawable.ic_menu_black_24dp
                R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerClosed(view: View) {
                isRightNavDrawerOpen = false
                ClubZ.isPrivate = 0

                if(view.id == R.id.navigationView){ }
                else{
                    val cFragment =  getCurrentFragment()
                    setActionbarMenu(cFragment!!)
                    bottomtabHandler(cFragment)
                    invalidateOptionsMenu()
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END)
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                invalidateOptionsMenu()
                isRightNavDrawerOpen = true
                if(drawerView.id== R.id.drawerView2){
                    val far = supportFragmentManager.findFragmentById(R.id.fragment2) as FragMyClubs
                    setActionbarMenu(far)
                    bottomtabHandler(far)
                    lastDrawerGravity = Gravity.END
                    far.refreshList()

                    if(isOpenMyClub){
                        isOpenMyClub = false
                    }else{
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END)
                    }
                } else lastDrawerGravity = Gravity.START
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                mDrawerLayout.bringChildToFront(drawerView)
                mDrawerLayout.requestLayout()
            }
        }

        mDrawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerLayout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent))

        /*search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (textChnageListner != null) {
                    textChnageListner!!.afterchangeText(p0)
                    search_text.isCursorVisible = true
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                search_back_icon.visibility = if(p0!!.isNotEmpty()) View.GONE else View.VISIBLE
            }
        })*/

        DrawerMarginFixer.fixMinDrawerMargin(mDrawerLayout)
    }


    private fun initView(){
        isOpenMyClub = false
        tablayout.addOnTabSelectedListener(this)
        for (views in arrayOf(menu, search, bubble_menu, addsymbol, back)){
            views.setOnClickListener(this)
        }

        setTab(tablayout.getTabAt(0)!!, R.drawable.ic_news_active, true)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        val nav = navigationView.getHeaderView(0)
        //nav.rlMyProfile.setOnClickListener(this)
        nav.nav_tvTitle.text = ClubZ.currentUser!!.full_name
        nav.nav_tvStatus.text = getString(R.string.my_status)
        nav.nav_optionMenu.setOnClickListener {
            showLogoutPopup(nav.nav_optionMenu)
        }

        if(ClubZ.currentUser!!.profile_image.isNotEmpty()){
            Picasso.with(this).load(ClubZ.currentUser!!.profile_image).fit().into(nav.iv_profileImage)
        }
    }


    override fun getActivity(): HomeActivity {
        return this@HomeActivity
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.navItemAbout -> {
                val profile = Profile()
                profile.userId = ClubZ.currentUser!!.id
                profile.full_name = ClubZ.currentUser!!.full_name
                profile.profile_image = ClubZ.currentUser!!.profile_image
                startActivity(Intent(this@HomeActivity, ProfileActivity::class.java).putExtra("profile", profile))
            }

            R.id.navContact -> { startActivity(Intent(this@HomeActivity, ContactListActivity::class.java))}
            R.id.navMembership -> { }
            R.id.navItemClubs -> { startActivity(Intent(this@HomeActivity, ClubsActivity::class.java)) }
            R.id.navItemSetting -> { startActivity(Intent(this@HomeActivity, SettingActivity::class.java)) }
            R.id.navItemHistory -> { }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {}
            R.id.menu_logout ->  SessionManager.getObj().logout(this)
            R.id.pop1 -> { item.isChecked = !item.isChecked }
            R.id.pop2 -> { item.isChecked = !item.isChecked }
        }
        return super.onOptionsItemSelected(item)
    }


   /* override fun updateMyNewsFeed(){
        if(ifNeedTocallApi){
            ifNeedTocallApi = false
            val fragemet : List<Fragment> = supportFragmentManager.fragments
            val newsFeedFragment: FragNewsList? = fragemet
                    .firstOrNull { it::class.java.simpleName==FragNewsList::class.java.simpleName }
                    ?.let { it as FragNewsList }
            newsFeedFragment?.setFilter(club, like, comment)
        }
    }*/


    override fun setActionbarMenu(fragmentHolder: Fragment){
        for (views in arrayOf(title_tv, menu, search, addsymbol, back, bubble_menu))
            views.visibility = View.GONE

        when (fragmentHolder::class.java.simpleName) {

            FragNewsList::class.java.simpleName -> {
                ClubZ.isPrivate = 0
                for (view in arrayOf(title_tv, bubble_menu, menu)) view.visibility = View.VISIBLE
                // for (view in arrayOf(search_text, back, addsymbol, serch_box)) view.visibility = View.GONE
                title_tv.setText(R.string.t_stay_up)

                if(ClubZ.isNeedToUpdateNewsFeed) {
                    ClubZ.isNeedToUpdateNewsFeed = false
                    ifNeedTocallApi = true
                    updateNewsFeed()

                }
            }

            FragMyClubs::class.java.simpleName -> {
                for (view in arrayOf(title_tv, back, bubble_menu)) view.visibility = View.VISIBLE
                title_tv.setText(R.string.my_clubs)
                /*  filterListner = (fragemet as Frag_Search_Club)
                  textChnageListner = fragemet
                  search_text.setText("")*/
            }

        /* Frag_Search_Club::class.java.simpleName -> {
             for (view in arrayOf(search_text, back, addsymbol, serch_box, bubble_menu)) view.visibility = View.VISIBLE
             filterListner = (fragemet as Frag_Search_Club)
             textChnageListner = fragemet
             search_text.setText("")
         }*/

            Frag_Find_Activities::class.java.simpleName->{
                title_tv.setText(R.string.t_find_activities)
                //for (view in arrayOf(search)) view.visibility = View.GONE
                for (view in arrayOf(menu, title_tv, bubble_menu)) view.visibility = View.VISIBLE
            }

            ChatFragment::class.java.simpleName->{
                title_tv.setText(R.string.t_chat)
                for (view in arrayOf(menu, title_tv)) view.visibility = View.VISIBLE
            }

            AdsFragment::class.java.simpleName->{
                title_tv.setText(R.string.t_ads)
                for (view in arrayOf(menu, title_tv)) view.visibility = View.VISIBLE
            }
        /*Frag_ClubDetails::class.java.simpleName -> {
            for (i in 0..cus_status.childCount - 1) cus_status.getChildAt(i).visibility = View.GONE
            for (view in arrayOf(back, title_tv, bubble_menu)) view.visibility = View.VISIBLE
            title_tv.setText(" "+(fragemet as Frag_ClubDetails).clubz.club_name)
        }*/
        }
    }


    /********************************************************/
    override fun onTabReselected(tab: TabLayout.Tab?) {
        when (tab!!.position) {
            0 -> { }
            1 -> { }
            2 -> { }
            3 -> { }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when (tab!!.position) {
            0 -> { setTab(tab, R.drawable.ic_news, false) }
            1 -> { setTab(tab, R.drawable.ic_activity, false) }
            2 -> { setTab(tab, R.drawable.ic_chat_bubble, false) }
            3 -> { setTab(tab, R.drawable.ic_ads, false) }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        hideKeyBoard()
        when (tab!!.position) {
            0 -> {
                setTab(tab, R.drawable.ic_news_active, true)
                replaceFragment(FragNewsList())
            }
            1 -> {
                setTab(tab, R.drawable.ic_activity_active, true)
                replaceFragment(Frag_Find_Activities())
            }
            2 -> {
                setTab(tab, R.drawable.ic_chat_bubble_active, true)
                replaceFragment(AdsFragment())
            }
            3 -> {
                setTab(tab, R.drawable.ic_ads_active, true)
                replaceFragment(AdsFragment())
            }
        }
    }

    private fun setTab(tab: TabLayout.Tab, imageRes: Int, isActive: Boolean) {
        tab.customView!!.findViewById<AppCompatImageView>(android.R.id.icon).setImageResource(imageRes)
        tab.customView!!.findViewById<TextView>(android.R.id.text1).setTextColor(
                ContextCompat.getColor(this , if (isActive) R.color.active_tab else R.color.inactive_tab))
    }


    /*Handle view clicks*/
    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.search -> {}//{addFragment(Frag_Search_Club(), 0);}
          /*  R.id.cancel -> {
                search_text.setText("")
                hideKeyBoard()
            }*/

            R.id.bubble_menu -> {

                if(isRightNavDrawerOpen){
                    clubSelectionMenu(0)
                    //clubOptions(0)
                }else{
                    val frag = getCurrentFragment()
                    when(frag!!::class.java.simpleName){
                        FragNewsList::class.java.simpleName ->{
                            // showFilterDialog()
                            val list : ArrayList<DialogMenu> = arrayListOf()
                            list.add(DialogMenu("Filter clubs", R.drawable.ic_filter_list))
                            list.add(DialogMenu("Renew my location", R.drawable.ic_refresh))
                            showMenu(list)
                        }

                        Frag_Find_Activities::class.java.simpleName -> {
                            showMyActivityDialog()
                        }
                    }
                }
            }

            R.id.menu -> { draweHandler(Gravity.START) }

            R.id.addsymbol -> {

                if(isRightNavDrawerOpen){
                    draweHandler(lastDrawerGravity)
                    startActivity(Intent(this@HomeActivity, ClubCreationActivity::class.java))
                    /* addFragment(Frag_Create_club(),0)
                       object : Purchase_membership_dialog(this) {
                         override fun viewplansListner() {
                             this.dismiss();
                         }
                     }.show()*/
                }else{
                    val fragemet = getCurrentFragment()!!
                    when (fragemet::class.java.simpleName) {
                        Frag_Find_Activities::class.java.simpleName->{
                            startActivity(Intent(this@HomeActivity, NewActivities::class.java))
                        }

                        FragNewsList::class.java.simpleName->{
                            startActivity(Intent(this@HomeActivity,
                                    CreateNewsFeedActivity::class.java).putExtra("clubId", ""))
                        }
                    }
                }
            }

            R.id.tv_private -> {
                when(ClubZ.isPrivate){
                    1->{
                        ClubZ.isPrivate = 0
                        dialog!!.chk_priavte.isChecked = true
                        dialog!!.chk_public.isChecked = true
                        getMyClubFragment()?.refreshList()
                        //if (filterListner != null) filterListner!!.onFilterChnge()
                    }
                    0,2->{
                        ClubZ.isPrivate = 1
                        dialog!!.chk_priavte.isChecked = false
                        dialog!!.chk_public.isChecked = true
                        getMyClubFragment()?.refreshList()
                        //if (filterListner != null) filterListner!!.onFilterChnge()
                    }
                }
            }

            R.id.tv_public -> {
                when(ClubZ.isPrivate){
                    2->{
                        ClubZ.isPrivate = 0
                        dialog!!.chk_priavte.isChecked = true
                        dialog!!.chk_public.isChecked = true
                        getMyClubFragment()?.refreshList() //if (getMyClubFragment() != null) filterListner!!.onFilterChnge()
                    }
                    0,1->{
                        ClubZ.isPrivate = 2
                        dialog!!.chk_priavte.isChecked = true
                        dialog!!.chk_public.isChecked = false
                        getMyClubFragment()?.refreshList()
                        //if(filterListner != null) filterListner!!.onFilterChnge()
                    }
                }
            }

            R.id.back -> onBackPressed()

            R.id.ll_clearFilter->{
                club = false
                like = false
                comment = false
                ifNeedTocallApi = true
                showMyNewsfeedOnly = false
                newsFilterDialog?.ch_byClubs?.isChecked = false
                newsFilterDialog?.ch_byLikes?.isChecked = false
                newsFilterDialog?.ch_byComments?.isChecked = false
                newsFilterDialog?.ch_myClubOnly?.isChecked = false
                newsFilterDialog?.dismiss()
                updateNewsFeed()
            }

            R.id.ch_myClubOnly->{
                showMyNewsfeedOnly = newsFilterDialog?.ch_myClubOnly?.isChecked!!
                updateNewsFeed()
            }

            R.id.ch_byClubs->{
                ifNeedTocallApi = true
                club = newsFilterDialog?.ch_byClubs?.isChecked!!
                updateNewsFeed()
            }

            R.id.ch_byComments->{
                ifNeedTocallApi = true
                comment = newsFilterDialog?.ch_byComments?.isChecked!!
                updateNewsFeed()
            }

            R.id.ch_byLikes->{
                ifNeedTocallApi = true
                like = newsFilterDialog?.ch_byLikes?.isChecked!!
                updateNewsFeed()
            }

            R.id.myActivity -> {
                startActivity(Intent(this@HomeActivity, MyActivities::class.java))
                myActivityDailog?.dismiss()
            }

            R.id.ll_menu1 -> {
                menuDialog?.dismiss()
            }
            R.id.ll_menu2 -> {
                menuDialog?.dismiss()
                if(isRightNavDrawerOpen){

                }else{
                    val fragment = getCurrentFragment()!!
                    when (fragment::class.java.simpleName) {
                        FragNewsList::class.java.simpleName->{
                            showFilterDialog()
                        }
                    }
                }
            }
        }
    }

    private fun updateNewsFeed(){
        val newsFeedFragment: FragNewsList? = supportFragmentManager.fragments
                .firstOrNull { it::class.java.simpleName==FragNewsList::class.java.simpleName }
                ?.let { it as FragNewsList }
        newsFeedFragment?.setFilter(showMyNewsfeedOnly, club, like, comment)
    }

    /**
     * Open the specified drawer by animating it out of view.
     *
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     */
    private fun draweHandler(gravity :Int = lastDrawerGravity){
        isRightNavDrawerOpen = if (!isRightNavDrawerOpen) {
            mDrawerLayout.openDrawer(gravity)
            true
        } else {
            mDrawerLayout.closeDrawer(gravity)
            false
        }
    }


   /* private fun clubOptions(position: Int) {
        var canshow = false
        when(getMyClubFragment()!!::class.java.simpleName.toString()){
        //Frag_Search_Club::class.java.simpleName -> canshow = true
            FragMyClubs::class.java.simpleName -> canshow = true
        }
        //  if(!canshow) return
        if(isRightNavDrawerOpen && lastDrawerGravity == Gravity.END) clubSelectionMenu(position)
    }*/


    override fun onLowMemory() {
        super.onLowMemory()
        dialog = null
        menuDialog = null
        newsFilterDialog = null
        myActivityDailog = null

    }
    override fun bottomtabHandler(fragmentHolder: Fragment){
        try{
            when (fragmentHolder::class.java.simpleName) {
                Frag_Find_Activities::class.java.simpleName ->  tablayout.visibility = View.VISIBLE
                AdsFragment::class.java.simpleName -> tablayout.visibility = View.VISIBLE
                ChatFragment::class.java.simpleName -> tablayout.visibility = View.VISIBLE
                FragNewsList::class.java.simpleName ->  tablayout.visibility = View.VISIBLE
                else-> tablayout.visibility = View.GONE
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
        hideKeyBoard()
        if(isRightNavDrawerOpen) {
            draweHandler()
            return
        }

        val handler = Handler()
        val runnable: Runnable?
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
            try {
                Util.e("Current Fragment", getCurrentFragment()!!::class.java.simpleName.toString())
                val fragemet = getCurrentFragment()
                bottomtabHandler(fragemet!!)
                setActionbarMenu(fragemet)
                //stausBarHandler(fragemet!!)
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

   private fun getCurrentFragment(): Fragment? {
       val    fragments = supportFragmentManager.fragments
        return fragments[fragments.size - 1]
        //return supportFragmentManager.findFragmentById(R.id.frag_container)
    }


    private fun getMyClubFragment(): FragMyClubs? {
        return supportFragmentManager.findFragmentById(R.id.fragment2) as FragMyClubs
    }


    /************************************************/
    override fun checkLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Permission(this,this).checkLocationPermission()) {
                buildGoogleApiClient()
                mGoogleApiClient.connect()
            }
        } else {
            if (checkLocationPermissionLowerApi()) {
                buildGoogleApiClient()
                mGoogleApiClient.connect()
            } else {
                showSettingsAlert()
            }
        }
    }

    @Synchronized private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build() }

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

    private fun showSettingsAlert() {}

    override fun onConnected(bundle: Bundle?) {
        try {
            val p0: Long = 15*1000  /* 10 secs */
            val p1: Long = 5000 /* 2 sec */
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(p0)
                    .setFastestInterval(p1)
            // Request location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }

            if (mGoogleApiClient == null)
                return
            if (!mGoogleApiClient.isConnected)
                return
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onConnectionSuspended(i: Int) { }
    override fun onConnectionFailed(connectionResult: ConnectionResult) { }
    override fun onLocationChanged(location: Location) {
        if (mGoogleApiClient.isConnected) {
            startLocationUpdates(location.latitude, location.longitude)
            //LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(LocationCallback());
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)

        }
    }

    private fun startLocationUpdates(latitude: Double, longitude: Double) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        ClubZ.latitude = latitude
        ClubZ.longitude = longitude

        val userLocation = UserLocation()
        userLocation.city = ""
        userLocation.latitude = latitude
        userLocation.longitude =longitude
        userLocation.isLocationAvailable = true
        sessionManager.setLocation(userLocation)

        val task = @SuppressLint("StaticFieldLeak")
        object : GioAddressTask(this@HomeActivity) {
            override fun onSuccess(address: Address) {
                ClubZ.city = address.city.toString()
                userLocation.city = ClubZ.city
                sessionManager.setLocation(userLocation)
            }
        }
        task.execute(latitude, longitude)
    }

    private fun updateFirebaseToken() {
        val chatUserBean = UserBean()
        chatUserBean.uid = ClubZ.currentUser?.id
        chatUserBean.email = ClubZ.currentUser?.email
        chatUserBean.firebaseToken = FirebaseInstanceId.getInstance().token
        chatUserBean.name = ClubZ.currentUser?.full_name
        chatUserBean.profilePic = ClubZ.currentUser?.profile_image
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_USERS)
                .child(chatUserBean.uid)
                .setValue(chatUserBean).addOnCompleteListener { }
    }

}
