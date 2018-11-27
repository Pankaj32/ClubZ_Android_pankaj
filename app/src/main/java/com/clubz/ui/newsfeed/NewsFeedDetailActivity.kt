package com.clubz.ui.newsfeed

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.fragments.FragmentChat
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.helper.fcm.NotificatioKeyUtil
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.newsfeed.fragment.FeedDetailFragment
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_news_feed_detail.*
import org.json.JSONObject


class NewsFeedDetailActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    var pos = 0
    lateinit var feed: Feed
    lateinit var adapter : ViewPagerAdapter
    private var from=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_feed_detail)
        intent?.let {
            try {
                from = it.extras.getString(NotificatioKeyUtil.Key_From)
            }catch (e:Exception){
                from= NotificatioKeyUtil.Value_From_Notification
            }

            if(from.equals(NotificatioKeyUtil.Value_From_Notification)){
                val newsFeedId=it.extras.getString(NotificatioKeyUtil.Key_News_Feed_Id)
                getFeedDetails(newsFeedId)
            }else {
                feed = it.extras.getSerializable("feed") as Feed
                pos = it.extras.getInt("pos")
                title_tv.text = feed.news_feed_title
                clubNameTxt.text = feed.club_name
                setViewPager(view_pager_cd)
            }
        }


        for (views in arrayOf(backBtn, bubble_menu)) views.setOnClickListener(this)
        view_pager_cd.addOnPageChangeListener(this)
    }

   private fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
       adapter.addFragment( FeedDetailFragment.newInstance(feed), resources.getString(R.string.t_content), "First")
       if(feed.is_comment_allow==1){
           adapter.addFragment( FragmentChat.newInstanceFeedsChat(""+feed.newsFeedId,feed.clubId,feed.news_feed_title),resources.getString(R.string.t_chat) , "second")
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
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        KeyboardUtil.hideKeyboard(this)
    }



    private fun getFeedDetails(newsFeedId:String) {
        val dialogProgress = CusDialogProg(this)
        dialogProgress.show()


        object : VolleyGetPost(this, this,
                "${WebService.feed_details}?newsFeedId=${newsFeedId}",
                true, true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {

                        feed = Gson().fromJson<Feed>(obj.getString("data"), Feed::class.java)
                        setData(feed)
                    } else {
                        //  nodataLay.visibility = View.VISIBLE
                    }
                    // searchAdapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialogProgress.dismiss()
            }

            override fun onNetError() {
                dialogProgress.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                /* params["eventTitle"] = eventTitle
                 params["eventDate"] = eventDate
                 params["eventTime"] = eventTime
                 params["location"] = location
                 params["latitude"] = latitude
                 params["longitude"] = longitute
                 params["activityId"] = activityId
                 params["description"] = description*/
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(NewsFeedDetailActivity::class.java.name)
    }

    private fun setData(feed: Feed?) {
        title_tv.text = feed!!.news_feed_title
        clubNameTxt.text = feed.club_name
        setViewPager(view_pager_cd)
    }
}
