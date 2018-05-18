package com.clubz.ui.core

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager

abstract class BaseActivity : AppCompatActivity(), BaseFragment.FragmentListner{

    override fun hideKeyBoard() {
        try {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {

        }
    }

    override fun onFragBackPress() {
        onBackPressed()
    }
}