package com.clubz

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.clubz.Cus_Views.Checked_Step_ImageView
import com.clubz.Cus_Views.Checked_Step_TextView
import com.clubz.Cus_Views.Checked_Step_TextView_active
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.fragment.signup.*
import com.clubz.util.Constants
import com.clubz.util.KeyboardUtil
import com.clubz.util.SoftKeyboard
import com.clubz.util.Util
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.LocationCallback
import kotlinx.android.synthetic.main.activity_signup.*

/**
 * Created by mindiii on 2/5/18.
 */

class Sign_up_Activity : AppCompatActivity() {

    public var _satus  : String = ""
    val mLocationCallback: LocationCallback? = null
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var dialog: CusDialogProg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        setContentView(R.layout.activity_signup)
        overridePendingTransition(R.anim.abc_slide_in_top,R.anim.abc_slide_out_bottom);
        dialog = CusDialogProg(this);
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Util.checklaunage(this)
        replaceFragment(Frag_Sign_Up_one())


    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    internal fun replaceFragment(fragmentHolder: Fragment) {
        hideKeyBoard()
        try {
            val fragmentManager = supportFragmentManager
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentName = fragmentHolder.javaClass.simpleName
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            fragmentTransaction.replace(R.id.container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
            setIndicator(fragmentName)

        } catch (e: Exception) {
            Util.e("value", e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         try {
            for (fragment in getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (e :Exception) {
            e.printStackTrace()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    fun addFragment(fragmentHolder: Fragment, animationValue: Int): Fragment? {
        try {
            val fragmentManager = supportFragmentManager
            val fragmentName = fragmentHolder.javaClass.simpleName
            val fragmentTransaction = fragmentManager.beginTransaction()
            //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            fragmentTransaction.add(R.id.container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
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

    fun setIndicator(frag_name : String){
        back.visibility = View.GONE
        when (frag_name){
            Frag_Sign_Up_one::class.java.simpleName->indicator(0);
            Frag_Sign_Up_One_2::class.java.simpleName->indicator(1);
            Frag_Sign_Up_Two::class.java.simpleName->{
                indicator(2)
               // back.visibility = View.VISIBLE
            };
            Frag_Sign_UP_Three::class.java.simpleName->indicator(3);
        }
    }

    fun indicator( postition : Int){
        try {
            val pager_indictor = findViewById<LinearLayout>(R.id.page_indicator)
            pager_indictor.removeAllViews()
                for(i in 0..3){
                if(postition>i)pager_indictor.addView(Checked_Step_ImageView(this));
                if(postition==i)pager_indictor.addView(Checked_Step_TextView_active(this).setText(i+1));
                if(postition<i)pager_indictor.addView(Checked_Step_TextView(this).setText(i+1));
            }
        }catch (ex :Exception){
            ex.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (supportFragmentManager.fragments != null) {
            for ( fragment in getSupportFragmentManager().getFragments()) {
                try {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    break
                } catch (e: Exception) {

                }
            }
        }
    }




    override fun onBackPressed() {
        hideKeyBoard()
        val handler = Handler()
        var runnable: Runnable? = null

        if (supportFragmentManager.backStackEntryCount > 1) {
            /*if (getCurrentFragment() is BackPresslistner) {
                if ((getCurrentFragment() as BackPresslistner).backPresses()) return; }*/
            super.onBackPressed()
        } else {
            finish();
           /* runnable = Runnable { doublebackpress = false }
            handler.postDelayed(runnable, 1000.toLong())
            if (doublebackpress) {
                handler.removeCallbacks(runnable)
                finish()
            } else {
                doublebackpress = true
            }
            */
        }
        fun getCurrentFragment(): Fragment? {
            try {
                val fragmentManager = supportFragmentManager
                val fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
                return fragmentManager.findFragmentByTag(fragmentTag)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
                return null
            }
        }

    }







}
