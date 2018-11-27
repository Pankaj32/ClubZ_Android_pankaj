package com.clubz.ui.core

import android.support.v7.app.AppCompatActivity
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import com.clubz.R
import android.widget.TextView
import android.widget.AutoCompleteTextView
import android.support.v4.content.ContextCompat
import android.widget.EditText
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageView


abstract class SearchActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var searchtollbar:Toolbar? = null
    var search_menu: Menu? = null
    var item_search: MenuItem? = null


    abstract fun getSearchView() : SearchView


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.search_menu, menu)
        return
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {

            R.id.action_search -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    //circleReveal(R.id.searchtoolbar, 1, true, true)
                else
                    searchtollbar?.visibility = View.VISIBLE

                item_search?.expandActionView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun initSearchView() {

        // Enable/Disable Submit button in the keyboard
        getSearchView().isSubmitButtonEnabled = false

        // Change search close button image
        val closeButton = getSearchView().findViewById(R.id.search_close_btn) as ImageView
        closeButton.setImageResource(R.drawable. ic_clock)


        // set hint and the text colors
        val txtSearch = getSearchView().findViewById(android.support.v7.appcompat.R.id.search_src_text) as EditText
        txtSearch.hint = "Search.."
        txtSearch.setHintTextColor(Color.DKGRAY)
        txtSearch.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))


        // set the curser_white
        val searchTextView = getSearchView().findViewById(android.support.v7.appcompat.R.id.search_src_text) as AutoCompleteTextView
        try {
            val mCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            mCursorDrawableRes.isAccessible = true
            //mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor) //This sets the curser_white resource ID to 0 or @null which will make it visible on white background
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getSearchView().setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                getSearchView().clearFocus()
                return true
            }

           override fun onQueryTextChange(newText: String): Boolean {
                callSearch(newText)
                return true
            }

            fun callSearch(query: String) {
                //Do searching
                Log.i("query", "" + query)

            }
        })
    }

    @SuppressLint("PrivateResource")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun circleReveal(viewID: Int, posFromRight: Int, containsOverflow: Boolean, isShow: Boolean) {
        val myView = findViewById<View>(viewID)

        var width = myView.width

        if (posFromRight > 0)
            width -= posFromRight * resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) - resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2
        if (containsOverflow)
            width -= resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)

        val cx = width
        val cy = myView.height / 2

        val anim: Animator
        anim = if (isShow)
            ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, width.toFloat())
        else
            ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0f)

        anim.duration = 220.toLong()

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            }
        })

        // make the view visible and start the animation
        if (isShow)
            myView.visibility = View.VISIBLE

        // start the animation
        anim.start()


    }

}