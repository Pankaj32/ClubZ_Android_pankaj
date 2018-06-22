package com.clubz.ui.user_activities.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.clubz.R
import com.clubz.ui.chat.ChatFragment
import com.clubz.ui.club.fragment.FragMyClubs
import com.clubz.ui.club.fragment.FragNearClubs
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.user_activities.fragment.FragActivitiesDetails
import com.clubz.ui.user_activities.fragment.Frag_Activity_Member
import kotlinx.android.synthetic.main.activities_details.*

class ActivitiesDetails : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    lateinit var adapter: ViewPagerAdapter
    private var activityId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activities_details)
        val bundle = intent.extras
        if (bundle != null) {
            activityId = bundle.getString("activityId")
        }
        // headerTxt.text = resources.getString(R.string.hint_activity_name)
        headerTxt.text = "Activity Name"
        ivBack.setOnClickListener(this)
        setViewPager(viewPager)
        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragActivitiesDetails.newInstance(activityId), resources.getString(R.string.a_activity_first_tab), " This is First")
        adapter.addFragment(ChatFragment(), resources.getString(R.string.a_activity_snd_tab), " This is second")
        adapter.addFragment(Frag_Activity_Member.newInstance(activityId), resources.getString(R.string.a_activity_rd_tab), " This is Third")
        viewPager.adapter = adapter
        //Chiranjib
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        headerTxt.text = if (position == 0)
            "Activity Name"
        else "Activity Name"
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}
