package com.clubz.ui.user_activities.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.view.Window
import com.clubz.R
import com.clubz.chat.fragments.FragmentChat
import com.clubz.data.model.DialogMenu
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.user_activities.fragment.FragActivitiesDetails
import com.clubz.ui.user_activities.fragment.FragActivityDetailsNew
import com.clubz.ui.user_activities.fragment.Frag_Activity_Member
import kotlinx.android.synthetic.main.activities_details.*
import kotlinx.android.synthetic.main.club_more_menu.*

class ActivitiesDetails : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    lateinit var adapter: ViewPagerAdapter
    private var activityId = ""
    private var from = ""
    private var type = ""
    private var userId = ""
    private var userName = ""
    private var userProfileImg = ""
    private var activityName = ""
    private var clubName = ""
    private var hasAffliates = 0
    protected var menuDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activities_details)
        val bundle = intent.extras
        if (bundle != null) {
            activityId = bundle.getString("activityId")
            activityName = bundle.getString("activityName")
            clubName = bundle.getString("clubName")
            from = bundle.getString("From")
            type = bundle.getString("type")
            hasAffliates = bundle.getInt("hasAffliates")
        }

        if (from.equals("OthersActivity")) {
            userId = bundle.getString("userId")
            userName = bundle.getString("userName")
            activityId = bundle.getString("activityId")
            activityName = bundle.getString("activityName")
            clubName = bundle.getString("clubName")
            userProfileImg = bundle.getString("userProfileImg")
            bubble_menu.visibility = View.GONE
        }
        // headerTxt.text = resources.getString(R.string.hint_activity_name)
        headerTxt.text = activityName
        clubNameTxt.text = clubName
        ivBack.setOnClickListener(this)
        bubble_menu.setOnClickListener(this)
        setViewPager(viewPager)
        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        /* if (from.equals("MyActivities")) {
             adapter.addFragment(FragActivityDetailsNew.newInstance(activityId, type,hasAffliates), resources.getString(R.string.a_activity_first_tab), " This is First")
             adapter.addFragment(Frag_Activity_Member.newInstance(activityId), resources.getString(R.string.a_activity_rd_tab), " This is Second")
         } else {*/
        adapter.addFragment(FragActivityDetailsNew.newInstance(activityId, type, hasAffliates), resources.getString(R.string.a_activity_first_tab), " This is First")
        adapter.addFragment(FragmentChat.newInstanceActivityChat(activityId), resources.getString(R.string.a_activity_snd_tab), " This is second")
        adapter.addFragment(Frag_Activity_Member.newInstance(activityId), resources.getString(R.string.a_activity_rd_tab), " This is Third")
        //  }
        viewPager.adapter = adapter
        //Chiranjib
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        /*headerTxt.text = if (position == 0)
            "Activity Name"
        else "Activity Name"*/
        bubble_menu.visibility = if (position == 0) View.VISIBLE else View.GONE
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.bubble_menu -> {
                val list: ArrayList<DialogMenu> = arrayListOf()
                list.add(DialogMenu(getString(R.string.edit), R.drawable.ic_edit))
                showMenu(list)
            }
        }
    }

    @SuppressLint("RtlHardcoded")
    protected fun showMenu(list: ArrayList<DialogMenu>?) {

        if (menuDialog == null) {
            menuDialog = Dialog(this)
            menuDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = menuDialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            menuDialog?.setContentView(R.layout.club_more_menu)

            menuDialog?.ll_menu1?.visibility = View.GONE
            if (list != null) {
                menuDialog?.ll_menu0?.visibility = View.VISIBLE
                menuDialog?.menu_iv0?.setImageResource(list[0].id)
                menuDialog?.menu_tv0?.text = list[0].title
            }

            menuDialog?.ll_menu0?.setOnClickListener {
                //handleMenuClick(list!![0])
            }

            // for (views in arrayOf(menuDialog?.ll_menu1, menuDialog?.ll_menu2)) views?.setOnClickListener(this)
            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            menuDialog?.setCancelable(true)
        }
        menuDialog?.show()
    }
}
