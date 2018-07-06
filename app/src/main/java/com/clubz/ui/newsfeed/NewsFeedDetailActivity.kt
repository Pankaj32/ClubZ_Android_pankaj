package com.clubz.ui.newsfeed

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.fragments.FragmentChat
import com.clubz.data.model.Feed
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.newsfeed.fragment.FeedDetailFragment
import kotlinx.android.synthetic.main.activity_club_detail.*

class NewsFeedDetailActivity : AppCompatActivity(), View.OnClickListener {

    var pos = 0
    lateinit var feed: Feed
    lateinit var adapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_feed_detail)
        intent?.let {
            feed = it.extras.getSerializable("feed") as Feed
            pos = it.extras.getInt("pos")
        }

       /* val intent = getIntent()
        feed = intent!!.extras.getSerializable("feed") as Feed
        requireNotNull(feed) { "no user_id provided in Intent extras" }*/

        title_tv.text = feed.news_feed_title
        for (views in arrayOf(backBtn, bubble_menu)) views.setOnClickListener(this)
        setViewPager(view_pager_cd)
        //tablayout_cd.setupWithViewPager(view_pager_cd)
        bubble_menu.visibility = if(feed.user_id == ClubZ.currentUser?.id) View.VISIBLE else View.GONE
    }

   private fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
       adapter.addFragment( FeedDetailFragment.newInstance(feed), resources.getString(R.string.t_content), "First")
       if(feed.is_comment_allow==1){
           adapter.addFragment( FragmentChat.newInstanceFeedsChat(""+feed.newsFeedId,feed.clubId),resources.getString(R.string.t_chat) , "second")
           tablayout_cd.setupWithViewPager(view_pager_cd)
       }
       else if(feed.is_comment_allow==0){
           tablayout_cd.setSelectedTabIndicatorHeight(0)
           tablayout_cd.visibility = View.GONE
       }

        viewPager.adapter = adapter
    }


    fun updateNewsfeed(feed: Feed?){
        val result = intent
        result.putExtra("feed", feed)
        result.putExtra("pos", pos)
        setResult(Activity.RESULT_OK, result)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.backBtn ->{ onBackPressed() }

            R.id.bubble_menu ->{
            }
        }
    }
}
