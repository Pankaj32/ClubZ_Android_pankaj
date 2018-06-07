package com.clubz.ui.newsfeed

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.model.Clubs
import com.clubz.data.model.Feed
import com.clubz.ui.club.fragment.Frag_ClubDetails_1
import com.clubz.ui.club.fragment.Frag_ClubDetails_2
import com.clubz.ui.core.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_club_detail.*

class NewsFeedDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var feed: Feed
    lateinit var adapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_feed_detail)
        val intent = getIntent()
        feed = intent!!.extras.getSerializable("feed") as Feed
        requireNotNull(feed) { "no user_id provided in Intent extras" }

        title_tv.text = feed.club_name
        for (views in arrayOf(backBtn, bubble_menu)) views.setOnClickListener(this)
        setViewPager(view_pager_cd)
        tablayout_cd.setupWithViewPager(view_pager_cd)

        bubble_menu.visibility = if(feed.user_id.equals(ClubZ.currentUser?.id)) View.VISIBLE else View.GONE

    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment( Frag_ClubDetails_1().setData(feed),resources.getString(R.string.t_detils) , " This is First")
        adapter.addFragment( Frag_ClubDetails_2().setData(clubz),resources.getString(R.string.t_members) , " This is second")
        viewPager.adapter = adapter
    }


    override fun onClick(v: View?) {
        when(v?.id){

            R.id.backBtn ->{ onBackPressed() }

            R.id.bubble_menu ->{
            }
        }
    }
}
