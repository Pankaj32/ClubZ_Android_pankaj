package com.clubz

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.PopupMenu
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.clubz.fragment.home.Frag_Create_club
import com.clubz.fragment.home.Frag_News_List
import com.clubz.helper.SessionManager
import com.clubz.util.Constants
import com.clubz.util.Util
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Created by mindiii on 2/23/18.
 */

class Home_Activity : AppCompatActivity(), TabLayout.OnTabSelectedListener, View.OnClickListener {

    lateinit var mDrawerLayout: DrawerLayout
    var open: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tablayout.addOnTabSelectedListener(this)
        for(view in arrayOf(menu ,logout, search ,cancel , bubble_menu , addsymbol))view.setOnClickListener(this)
        setTab(tablayout.getTabAt(0)!!,R.drawable.ic_news_active,true)
        replaceFragment(Frag_News_List());
        Util.e("authtoken", SessionManager.getObj().user.auth_token);
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
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()){
            0->{    }
            1->{    }
            2->{    }
            3->{    }
        }

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()){
            0->{ setTab(tab,R.drawable.ic_news,false)          }
            1->{ setTab(tab,R.drawable.ic_activity,false)      }
            2->{ setTab(tab,R.drawable.ic_chat_bubble,false)   }
            3->{ setTab(tab,R.drawable.ic_ads,false)           }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()){
            0->{ setTab(tab,R.drawable.ic_news_active,true)         }
            1->{ setTab(tab,R.drawable.ic_activity_active,true)     }
            2->{ setTab(tab,R.drawable.ic_chat_bubble_active,true)  }
            3->{ setTab(tab,R.drawable.ic_ads_active,true)          }
        }
    }

    internal fun setTab(tab: TabLayout.Tab , imageRes :Int , isActive :Boolean) {
        tab.customView!!.findViewById<AppCompatImageView>(android.R.id.icon).setImageResource(imageRes)
        tab.customView!!.findViewById<TextView>(android.R.id.text1).setTextColor(resources.getColor(if(isActive)R.color.active_tab else R.color.inactive_tab))
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.logout->SessionManager.getObj().logout(this)
            R.id.search->updateSearch()
            R.id.cancel->updateSearch()
            R.id.bubble_menu-> clubOptions();
            R.id.menu -> {
                if (!open) {
                    mDrawerLayout.openDrawer(Gravity.START)
                    open = true
                } else {
                    mDrawerLayout.closeDrawer(Gravity.START)
                    open = false
                }
            }
            //R.id.addsymbol-> replaceFragment(Frag_Create_club());
        }

    }


    fun updateSearch(){
        for(view in arrayOf(title_tv, bookmark ,menu , search))view.visibility =if(search_text.visibility==View.GONE) View.GONE else View.VISIBLE
        for(view in arrayOf(search_text, back ,addsymbol ,serch_box))view.visibility =if(title_tv.visibility!=View.GONE) View.GONE else View.VISIBLE
    }

    fun clubOptions() {
        /*val wrapper = ContextThemeWrapper(this, R.style.popstyle);
        val popupMenu = PopupMenu(wrapper, bubble_menu, Gravity.BOTTOM)
        popupMenu.getMenuInflater().inflate(R.menu.menu_club_options, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object  : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                return false;
            }
        }) ;
        popupMenu.show()*/
         val view : View = View.inflate(this,R.layout.menu_club_selection,null);
        val pop = PopupWindow(
                view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        pop.isOutsideTouchable = true
        pop.setBackgroundDrawable(null);
        pop.showAtLocation(bubble_menu,Gravity.END or Gravity.TOP , 0,0)

       // pop.elevation = 10f;

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
            hideKeyBoard()
           // fragment = fragmentHolder

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
           // fragment = fragmentHolder
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

    fun stausBarHandler(){

    }
}
