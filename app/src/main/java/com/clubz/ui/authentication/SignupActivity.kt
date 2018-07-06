package com.clubz.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.LinearLayout
import com.clubz.ui.cv.Checked_Step_ImageView
import com.clubz.ui.cv.Checked_Step_TextView
import com.clubz.ui.cv.Checked_Step_TextView_active
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.ui.authentication.fragment.*
import com.clubz.ui.core.BaseActivity
import com.clubz.utils.Util
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_signup.*

/**
 * Created by mindiii on 2/5/18.
 */
class SignupActivity : BaseActivity() , SignupBaseFragment.Listner{

    lateinit var mGoogleSignInClient : GoogleSignInClient
    var dialog: CusDialogProg? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        FacebookSdk.setApplicationId(resources.getString(R.string.facebook_app_id))
        setContentView(R.layout.activity_signup)

        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)

        dialog = CusDialogProg(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        Util.checklaunage(this)
        replaceFragment(FragSignUp1())
    }

    override fun showDialog() {
        if(dialog==null){
            dialog = CusDialogProg(this)
        }
        dialog?.show()
    }

    override fun hideDialog() {
        dialog?.dismiss()
    }

    override fun replaceFragment(fragment: Fragment) {
        hideKeyBoard()
        try {
            val fragmentManager = supportFragmentManager
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentName = fragment.javaClass.simpleName
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            fragmentTransaction.replace(R.id.container, fragment, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
            setIndicator(fragmentName)

        } catch (e: Exception) {
            Util.e("value", e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         try {
            for (fragment in supportFragmentManager.fragments) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        } catch (e :Exception) {
            e.printStackTrace()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    fun addFragment(fragmentHolder: Fragment, animationValue: Int): Fragment? {
        return try {
            val fragmentManager = supportFragmentManager
            val fragmentName = fragmentHolder.javaClass.simpleName
            val fragmentTransaction = fragmentManager.beginTransaction()
            //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            fragmentTransaction.add(R.id.container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
            hideKeyBoard()
            fragmentHolder
        } catch (e: Exception) {
            null
        }
    }


    private fun setIndicator(frag_name : String){
        back.visibility = View.GONE
        when (frag_name){
            FragSignUp1::class.java.simpleName->indicator(0)
            Frag_Sign_Up_One_2::class.java.simpleName->indicator(1)
            FragSignUp2::class.java.simpleName->{
                indicator(2)
               // back.visibility = View.VISIBLE
            }
            FragSignUp3::class.java.simpleName->indicator(3)
        }
    }

    private fun indicator(postition : Int){
        try {
            val pagerIndictor = findViewById<LinearLayout>(R.id.page_indicator)
            pagerIndictor.removeAllViews()
                for(i in 0..3){
                if(postition>i)pagerIndictor.addView(Checked_Step_ImageView(this))
                if(postition==i)pagerIndictor.addView(Checked_Step_TextView_active(this).setText(i+1))
                    if(postition<i)pagerIndictor.addView(Checked_Step_TextView(this).setText(i+1))
                }
        }catch (ex :Exception){
            ex.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (supportFragmentManager.fragments != null) {
            for ( fragment in supportFragmentManager.fragments) {
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
        if (supportFragmentManager.backStackEntryCount > 1) {
            /*if (getCurrentFragment() is BackPresslistner) {
                if ((getCurrentFragment() as BackPresslistner).backPresses()) return; }*/
            super.onBackPressed()
        } else {
            finish()
        }
    }
}
