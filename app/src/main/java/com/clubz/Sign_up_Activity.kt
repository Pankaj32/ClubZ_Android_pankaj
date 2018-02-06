package com.clubz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.clubz.fragment.signup.Frag_Sign_Up_One_2
import com.clubz.fragment.signup.Frag_Sign_Up_Two
import com.clubz.fragment.signup.Frag_Sign_Up_one
import com.clubz.util.Util
import kotlinx.android.synthetic.main.activity_signup.*

/**
 * Created by mindiii on 2/5/18.
 */

class Sign_up_Activity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        replaceFragment(Frag_Sign_Up_one())
    }

    internal fun replaceFragment(fragmentHolder: Fragment) {
        try {
            val fragmentManager = supportFragmentManager
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentName = fragmentHolder.javaClass.simpleName
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            fragmentTransaction.replace(R.id.container, fragmentHolder, fragmentName).addToBackStack(fragmentName)
            fragmentTransaction.commit()
            hideKeyBoard()
            setIndicator(fragmentName)


        } catch (e: Exception) {
            Util.e("value", e.toString())
        }

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
        when (frag_name){
            Frag_Sign_Up_one::class.java.simpleName->indicator(0);
            Frag_Sign_Up_Two::class.java.simpleName->indicator(1);
        }
    }

    fun indicator( postition : Int){
        for(i in 0..page_indicator.childCount-1){
            val view : TextView  = page_indicator.getChildAt(i) as TextView;
            view.setBackgroundResource(R.drawable.number_inactive)
            view.setTextColor(resources.getColor(R.color.white))
        }
        val view : TextView  = page_indicator.getChildAt(postition) as TextView;
        view.setBackgroundResource(R.drawable.number_active)
        view.setTextColor(resources.getColor(R.color.bg_violet))


    }






}
