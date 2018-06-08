package com.clubz.ui.main

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import android.support.v7.widget.ListPopupWindow
import android.text.Editable
import android.text.TextWatcher

import android.view.*
import android.widget.*
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.ui.core.FilterListner
import com.clubz.ui.core.Textwatcher_Statusbar
import com.clubz.ui.club.fragment.Frag_Create_club
import com.clubz.ui.newsfeed.fragment.Frag_News_List
import com.clubz.ui.club.fragment.Frag_Search_Club
import com.clubz.helper.Permission
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.GioAddressTask
import com.clubz.ui.ads.fragment.AdsFragment
import com.clubz.ui.chat.ChatFragment
import com.clubz.ui.club.ClubCreationActivity
import com.clubz.ui.club.ClubsActivity
import com.clubz.ui.core.BaseActivity
import com.clubz.ui.newsfeed.CreateNewsFeedActivity
import com.clubz.ui.user_activities.activity.MyActivities
import com.clubz.ui.user_activities.activity.NewActivities
import com.clubz.ui.user_activities.fragment.Frag_Find_Activities
import com.clubz.utils.DrawerMarginFixer
import com.clubz.utils.Util
import com.github.siyamed.shapeimageview.CircularImageView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home_test.*
import kotlinx.android.synthetic.main.menu_club_selection.*
import kotlinx.android.synthetic.main.nav_header.view.*

class HomeActivity : BaseActivity(), TabLayout.OnTabSelectedListener,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        var isPrivate: Int = 0
    }

    fun getClubType() : Int{
        return isPrivate
    }

    lateinit var mDrawerLayout: DrawerLayout
   // lateinit var mDrawer: DrawerLayout
    var isOpenMyClub: Boolean = false
    var open: Boolean = false
    var doublebackpress: Boolean = false
    var lastDrawerGravity :Int= Gravity.START;

    //var isPrivate: Int = 0  // 0: Both option available , 1:public ,2:private
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
    var dialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_test)
        ClubZ.currentUser = SessionManager.getObj().user
        setUp()

       // tablayout.addOnTabSelectedListener(this)
       // for (views in arrayOf(menu, search, cancel, bubble_menu, addsymbol, filter_list, tv_private, tv_public , back)) views.setOnClickListener(this)

        replaceFragment(Frag_News_List())
        ///addFragment_new(Frag_Search_Club(),true ,R.id.frag_container2);
        checkLocationUpdate()
        Util.e("authtoken", SessionManager.getObj().user.auth_token);

        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout , R.drawable.ic_menu_black_24dp, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(view: View) {
                open = false
                if(view.id == R.id.navigationView){

                }else{
                    val cFragment =  getCurrentFragment()
                    setActionbarMenu(cFragment!!)
                    bottomtabHandler(cFragment)
                    //stausBarHandler(cFragment)
                    //supportInvalidateOptionsMenu()
                    invalidateOptionsMenu()
                    //lockNavigation()
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                //supportInvalidateOptionsMenu()
                invalidateOptionsMenu()
                open = true
                if(drawerView.id== R.id.drawerView2){
                    val far = getSupportFragmentManager().findFragmentById(R.id.fragment2) as Frag_Search_Club
                    setActionbarMenu(far)
                    //stausBarHandler(far)
                    bottomtabHandler(far)
                    lastDrawerGravity = Gravity.END

                    if(isOpenMyClub){
                        far.setFragmentType(true)
                        isOpenMyClub = false
                    }else{
                        far.checkLocation()
                       // lockNavigation(true)
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);
                    }
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


    fun setUp(){
        isOpenMyClub = false
        tablayout.addOnTabSelectedListener(this)
        for (views in arrayOf(menu, search, cancel, bubble_menu, addsymbol, back)){
            views.setOnClickListener(this)
        }

        setTab(tablayout.getTabAt(0)!!, R.drawable.ic_news_active, true)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        val nav = navigationView.getHeaderView(0)
        nav.nav_tvTitle.setText(ClubZ.currentUser!!.full_name)
        nav.nav_tvStatus.text = getString(R.string.my_status)
        nav.nav_optionMenu.setOnClickListener {
            showLogoutPopup(nav.nav_optionMenu)
        }

        val navProfileimage = nav.findViewById<CircularImageView>(R.id.iv_profileImage)
        if(ClubZ.currentUser!!.profile_image.isNotEmpty()){
            Picasso.with(this).load(ClubZ.currentUser!!.profile_image).fit().into(navProfileimage)
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.navItemAbout -> {
                // Handle the camera action
            }
            R.id.navItemClubs -> {
                startActivity(Intent(this@HomeActivity, ClubsActivity::class.java))
                /*drawer_layout.closeDrawer(GravityCompat.START)
                mDrawerLayout.openDrawer(GravityCompat.END)
                isOpenMyClub = true
                return true*/
            }
            R.id.navItemHistory -> {

            }
            R.id.navItemSetting -> {

            }
            R.id.navItemActivity -> {
                startActivity(Intent(this@HomeActivity, MyActivities::class.java))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){

            R.id.menu_logout ->  SessionManager.getObj().logout(this);

            R.id.pop1 -> {
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                }
            }

            R.id.pop2 -> {
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                }else {
                    // If item is unchecked then checked it
                    item.setChecked(true);
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }

    // Display anchored popup menu based on view selected
    fun showLogoutPopup(v : View) {
        val products =  arrayOf("Logout")
        val lpw =  ListPopupWindow(this)
        lpw.setAnchorView(v);
        lpw.setDropDownGravity(Gravity.RIGHT);
        lpw.setHeight(ListPopupWindow.WRAP_CONTENT);
        lpw.setWidth(300);
        lpw.setAdapter( ArrayAdapter(this, android.R.layout.simple_list_item_1, products)); // list_item is your textView with gravity.
        lpw.setOnItemClickListener { parent, view, position, id ->
            lpw.dismiss()
            SessionManager.getObj().logout(this)
        }
        lpw.show();
    }


    private fun popupMenu(position: Int){

        if(dialog==null){
            dialog = Dialog(this)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogWindow = dialog?.getWindow()
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog?.setContentView(R.layout.menu_club_selection)

            val lp = dialogWindow?.getAttributes()
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            dialog?.setCancelable(true)

            for (views in arrayOf(dialog?.tv_private, dialog?.tv_public)) views?.setOnClickListener(this)
        }

        if (position == 0) {
            if(isPrivate==0){
                dialog?.chk_priavte?.isChecked = true; dialog?.chk_public?.isChecked = true;
            }
            else {dialog?.chk_priavte?.isChecked = (isPrivate==2); dialog?.chk_public?.isChecked = (isPrivate==1);}
        }
        dialog?.show()
    }


    fun setActionbarMenu(fragemet: Fragment){
        for (views in arrayOf(title_tv, menu, search, cancel, addsymbol, back, serch_box, bubble_menu))
            views.visibility = View.GONE

        when (fragemet::class.java.simpleName) {

            Frag_News_List::class.java.simpleName -> {
                isPrivate = 0
                for (view in arrayOf(title_tv, menu, search)) view.visibility = View.VISIBLE
               // for (view in arrayOf(search_text, back, addsymbol, serch_box)) view.visibility = View.GONE
                title_tv.setText(R.string.t_stay_up)
            }

            Frag_Create_club::class.java.simpleName -> {
                cus_status.visibility = View.GONE
            }

            Frag_Search_Club::class.java.simpleName -> {
                //title_tv.visibility = View.GONE
                //for (view in arrayOf(title_tv, bookmark, menu, search)) view.visibility = View.GONE
                for (view in arrayOf(search_text, back, addsymbol, serch_box, bubble_menu)) view.visibility = View.VISIBLE
                filterListner = (fragemet as Frag_Search_Club);
                textChnageListner = fragemet
                search_text.setText("")
                //search_text.setCursorVisible(false)
            }
            Frag_Find_Activities::class.java.simpleName->{
                title_tv.setText(R.string.t_find_activities)
                //for (view in arrayOf(search)) view.visibility = View.GONE
                for (view in arrayOf(menu, title_tv)) view.visibility = View.VISIBLE
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
        when (tab!!.getPosition()) {
            0 -> { }
            1 -> { }
            2 -> { }
            3 -> { }
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
                replaceFragment(Frag_News_List())
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

    internal fun setTab(tab: TabLayout.Tab, imageRes: Int, isActive: Boolean) {
        tab.customView!!.findViewById<AppCompatImageView>(android.R.id.icon).setImageResource(imageRes)
        tab.customView!!.findViewById<TextView>(android.R.id.text1).setTextColor(
                ContextCompat.getColor(this , if (isActive) R.color.active_tab else R.color.inactive_tab))
    }


    /*Handle view clicks*/
    override fun onClick(p0: View?) {
        when (p0!!.id) {
           // R.id.logout -> SessionManager.getObj().logout(this)
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

                if(open){
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

                        Frag_News_List::class.java.simpleName->{
                            startActivity(Intent(this@HomeActivity,
                                    CreateNewsFeedActivity::class.java).putExtra("clubId", ""))
                        }
                    }
                }
            };

            R.id.filter_list -> closeOption()

            R.id.tv_private -> {
                when(isPrivate){
                    1->{isPrivate = 0; dialog!!.chk_priavte.setChecked(true);       dialog!!.chk_public.setChecked(true); if (filterListner != null) filterListner!!.onFilterChnge()}
                    0,2->{isPrivate = 1; dialog!!.chk_priavte.setChecked(false);     dialog!!.chk_public.setChecked(true); if (filterListner != null) filterListner!!.onFilterChnge()}
                }
            }

            R.id.tv_public -> {
                when(isPrivate){
                    2->{isPrivate = 0; dialog!!.chk_priavte.setChecked(true);        dialog!!.chk_public.setChecked(true); if (filterListner != null) filterListner!!.onFilterChnge()}
                    0,1->{isPrivate = 2; dialog!!.chk_priavte.setChecked(true);      dialog!!.chk_public.setChecked(false);if (filterListner != null) filterListner!!.onFilterChnge()}
                }
            }

            R.id.back -> onBackPressed()
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
         when(getClubSearchFragment()!!::class.java.simpleName.toString()){
            Frag_Search_Club::class.java.simpleName -> canshow = true
        }
      //  if(!canshow) return
        if(open && lastDrawerGravity == Gravity.END) popupMenu(position)
        /*filter_list.visibility = View.VISIBLE
        filter_list.getChildAt(position).visibility = View.VISIBLE
        if (position == 0) {
            if(isPrivate==0){
                chk_priavte.isChecked = true; chk_public.isChecked = true;
            }
            else {chk_priavte.isChecked = (isPrivate==2); chk_public.isChecked = (isPrivate==1);}
        }*/
    }

    fun closeOption() {
        return;
        /*for (i in 0..filter_list.childCount - 1) filter_list.getChildAt(i).visibility = View.GONE
        filter_list.visibility = View.GONE*/
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
           // stausBarHandler(fragmentHolder)
            setActionbarMenu(fragmentHolder)
            hideKeyBoard()
        } catch (e: Exception) {
            //  Util.e("value", e.toString())
        }
    }


    /**
     * commiting with state loss
     */
    /*internal fun replaceFragmentLoss(fragmentHolder: Fragment) {
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

    }*/


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
            //stausBarHandler(fragmentHolder)
            setActionbarMenu(fragmentHolder)
            hideKeyBoard()
            return fragmentHolder
        } catch (e: Exception) {
            return null
        }
    }


   /* fun addFragment_new(fragment: Fragment, addToBackStack: Boolean, containerId: Int) {
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
    }*/

   /* fun hideKeyBoard() {
        try {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {

        }
    }*/

   /* fun stausBarHandler(fragemet: Fragment) {

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
            Frag_Find_Activities::class.java.simpleName->{
                title_tv.setText(R.string.t_find_activities)
                for (view in arrayOf(search)) view.visibility = View.GONE
                for (view in arrayOf(addsymbol, menu, bookmark, title_tv)) view.visibility = View.VISIBLE
            }
        *//*Frag_ClubDetails::class.java.simpleName -> {
            for (i in 0..cus_status.childCount - 1) cus_status.getChildAt(i).visibility = View.GONE
            for (view in arrayOf(back, title_tv, bubble_menu)) view.visibility = View.VISIBLE
            title_tv.setText(" "+(fragemet as Frag_ClubDetails).clubz.club_name)
        }*//*
        }
    }
*/
    fun bottomtabHandler(fragemet: Fragment){
       try{

           when (fragemet::class.java.simpleName) {
               Frag_Find_Activities::class.java.simpleName ->  tablayout.visibility = View.VISIBLE
               AdsFragment::class.java.simpleName -> tablayout.visibility = View.VISIBLE
               ChatFragment::class.java.simpleName -> tablayout.visibility = View.VISIBLE
               Frag_News_List::class.java.simpleName ->  tablayout.visibility = View.VISIBLE
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
        /*val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }*/
        hideKeyBoard()
        if(open) {
            draweHandler()
            return
        }

        closeOption()
        val handler = Handler()
        val runnable: Runnable?
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
            try {
                Util.e("Current Fragment", getCurrentFragment()!!::class.java.simpleName.toString())
                var fragemet = getCurrentFragment()
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

    fun getCurrentFragment(): Fragment? {
        val fragments = supportFragmentManager.fragments
        return fragments[fragments.size - 1]
        //return supportFragmentManager.findFragmentById(R.id.frag_container)
    }


    private fun getClubSearchFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.fragment2)
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
            val FASTEST_INTERVAL: Long = 5000 /* 2 sec */
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
            //LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(LocationCallback());
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)

        }
    }

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

        object : GioAddressTask(this@HomeActivity, LatLng(latitude, longitude)){
            override fun onSuccess(address: com.clubz.data.model.Address?) {
                ClubZ.city = address?.city.toString();
            }
        }.execute()
        Util.showToast(latitude.toString()+" : "+longitude,this)
    }
}
