package com.clubz.ui.club

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.clubz.R
import com.clubz.data.model.Clubs
import com.clubz.ui.club.`interface`.MyClubInteraction
import com.clubz.ui.club.fragment.FragMyClubs
import com.clubz.ui.club.fragment.FragNearClubs
import com.clubz.ui.core.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_clubs.*

class ClubsActivity : AppCompatActivity(), View.OnClickListener, MyClubInteraction,
        ViewPager.OnPageChangeListener {

    lateinit var adapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clubs)
        headerTxt.text = resources.getString(R.string.t_manage_your_clubs)
        ivBack.setOnClickListener(this)
        setViewPager(viewPager)
        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment( FragMyClubs(),resources.getString(R.string.t_my_clubs) , " This is First")
        adapter.addFragment( FragNearClubs(),resources.getString(R.string.t_near_clubs) , " This is second")
        viewPager.adapter = adapter
    }

    override fun onJoinClub(club: Clubs) {
        val frag = adapter.getItem(0) as FragMyClubs
        frag?.updateAdapter(club)
    }

    override fun onLeaveClub(club: Clubs) {
        val frag = adapter.getItem(1) as FragNearClubs
        frag?.updateAdapter(club)
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivBack ->{ onBackPressed() }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        headerTxt.text = if(position==0)
            resources.getString(R.string.t_manage_your_clubs)
        else resources.getString(R.string.t_join_the_force)
    }

}
