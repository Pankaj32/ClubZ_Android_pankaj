package com.clubz.ui.newsfeed.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.ListPopupWindow
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.*
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.repo.AllFeedsRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.*
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.helper.fcm.NotificatioKeyUtil
import com.clubz.ui.activities.fragment.ItemListDialogFragment
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Internet_Connection_dialog
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.ui.dialogs.DeleteNewsFeedDialog
import com.clubz.ui.dialogs.ProfileDialog
import com.clubz.ui.newsfeed.CreateNewsFeedActivity
import com.clubz.ui.newsfeed.NewsFeedDetailActivity
import com.clubz.ui.newsfeed.adapter.NewsFeedAdapter
import com.clubz.ui.profile.ProfileActivity
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_news.*
import kotlinx.android.synthetic.main.fragment_ads.*
import org.json.JSONObject


/**
 * Created by Dharmraj Acharya on 3/12/18.
 */

class FragNewsList : Fragment(), View.OnClickListener, NewsFeedAdapter.Listner,
        SwipeRefreshLayout.OnRefreshListener, ItemListDialogFragment.Listener {

    private var pageListner: RecyclerViewScrollListener? = null
    private var newsFeeds: ArrayList<Feed> = arrayListOf()
    private var adapter: NewsFeedAdapter? = null
    private var isMyFeed: Boolean = false

    private var isFilterByLike = false
    private var isFilterByComment = false
    private var isFilterByClub = false

    private var actionPos = 0

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param isMyFeed
         * @return A new instance of fragment FeedDetailFragment.
         */
        @JvmStatic
        fun newInstance(isMyFeed: Boolean = false) = FragNewsList().apply {
            arguments = Bundle().apply {
                putBoolean("isMyFeed", isMyFeed)
            }
        }
    }

    fun setFilter(isMyFeed: Boolean, club: Boolean = false, like: Boolean = false, comment: Boolean = false) {
        this.isMyFeed = isMyFeed
        this.isFilterByClub = club
        this.isFilterByLike = like
        this.isFilterByComment = comment
        newsFeeds.clear()
        pageListner?.resetState()
        getFeeds(0)
    }

    /*fun setFilter(club : Boolean = false, like : Boolean = false, comment : Boolean = false){
        this.isFilterByClub = club
        this.isFilterByLike = like
        this.isFilterByComment = comment
        newsFeeds.clear()
        pageListner?.resetState()
        getFeeds(0)
    }
*/   private var plan: MembershipPlan? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { isMyFeed = it.getBoolean("isMyFeed") }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_news, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        feedRecycleView.itemAnimator = null
        feedRecycleView.layoutManager = lm
        feedRecycleView.setHasFixedSize(true)

        // feedRecycleView.setItemViewCacheSize(20);
        feedRecycleView.setDrawingCacheEnabled(true);
        feedRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = NewsFeedAdapter(newsFeeds, context!!, this)
        feedRecycleView.adapter = adapter
        //val decor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        //feedRecycleView.addItemDecoration(decor)
        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {}
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getFeeds(page * 10)
            }
        }

        swipeRefreshLayout.setOnRefreshListener(this)
        feedRecycleView.addOnScrollListener(pageListner)
        //getFeeds(isPull = true)
        val tempFeedList = AllFeedsRepo().getAllFeeds()
        if (tempFeedList.size > 0) {
            for (feed in tempFeedList) {
                val feedBean = Feed()
                feedBean.newsFeedId = feed.newsFeedId
                feedBean.news_feed_title = feed.news_feed_title
                feedBean.news_feed_description = feed.news_feed_description
                feedBean.tagName = feed.tagName
                feedBean.datetime = feed.datetime
                feedBean.club_name = feed.club_name
                feedBean.user_name = feed.user_name
                feedBean.creator_phone = feed.creator_phone
                feedBean.contact_no_visibility = feed.contact_no_visibility
                feedBean.profile_image = feed.profile_image
                feedBean.user_id = feed.user_id
                feedBean.likes = feed.likes
                feedBean.comments = feed.comments
                feedBean.is_comment_allow = feed.is_comment_allow
                feedBean.bookmarks = feed.bookmarks
                feedBean.isLiked = feed.isLiked
                feedBean.isBookmarked = feed.isBookmarked
                feedBean.news_feed_attachment = feed.news_feed_attachment
                feedBean.club_image = feed.club_image
                feedBean.club_icon = feed.club_icon
                feedBean.currentDateTime = feed.currentDateTime
                feedBean.crd = feed.crd
                feedBean.clubUserId = feed.clubUserId

                newsFeeds.add(feedBean)
            }
            updateUI()
        }
        else{
            noFeedMsgUI.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        if (ClubZ.isNeedToUpdateNewsFeed && !isMyFeed) {
            ClubZ.isNeedToUpdateNewsFeed = false
            newsFeeds.clear()
            pageListner?.resetState()
            getFeeds(0, isPull = true,isloading= false)
        }else{
           // newsFeeds.clear()
            pageListner?.resetState()
            getFeeds(0, isPull = true,isloading=false)
        }
    }

    override fun onRefresh() {
        getFeeds(0, isPull = true)
        swipeRefreshLayout.isRefreshing = false
    }


    override fun onItemClick(feed: Feed, pos: Int) {
        val list: ArrayList<DialogMenu> = arrayListOf()
        /*list.add(DialogMenu(getString(R.string.add_date), R.drawable.ic_add_24))
        list.add(DialogMenu(getString(R.string.remove_activity), R.drawable.ic_delete_icon))
        list.add(DialogMenu(getString(R.string.hide_activity), R.drawable.ic_visibility_off))
        ItemListDialogFragment.newInstance(list).show(fragmentManager, "draj")*/

        startActivityForResult(Intent(context,
                NewsFeedDetailActivity::class.java)
                .putExtra(NotificatioKeyUtil.Key_From, "")
                .putExtra("feed", feed)
                .putExtra("pos", pos),
                1001)
    }

    override fun onProfileClick(feed: Feed) {
        if (!feed.user_id.equals(ClubZ.currentUser!!.id)) showProfile(feed)
    }

    private fun showProfile(feed: Feed) {
        val user = UserInfo()
        user.profile_image = feed.profile_image
        user.userId = feed.user_id
        user.full_name = feed.user_name
        user.isLiked = feed.isLiked
        user.country_code = ""
        user.contact_no = feed.creator_phone
        user.contact_no_visibility = feed.contact_no_visibility
        user.clubUserId = feed.clubUserId
        user.clubId = feed.clubId

        val dialog = object : ProfileDialog(context!!, user) {
            override fun OnProfileClick(user: UserInfo) {
                if (user.userId.isNotEmpty()) {
                    val profile = Profile()
                    profile.userId = user.userId
                    profile.full_name = user.full_name
                    profile.profile_image = user.profile_image
                    context?.startActivity(Intent(context, ProfileActivity::class.java).putExtra("profile", profile))
                } else {
                    Toast.makeText(context, getString(R.string.under_development), Toast.LENGTH_SHORT).show()
                }
            }

        }
        //  dialog.setCancelable(true)
        dialog.show()
    }

    @SuppressLint("RtlHardcoded")
    override fun onFeedEditClick(view: View, feed: Feed, pos: Int) {
        val products = arrayOf(getString(R.string.edit), getString(R.string.delete))
        val lpw = ListPopupWindow(context!!)
        lpw.anchorView = view
        lpw.setDropDownGravity(Gravity.RIGHT)
        lpw.height = ListPopupWindow.WRAP_CONTENT
        lpw.width = 200
        lpw.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, products)) // list_item is your textView with gravity.
        lpw.setOnItemClickListener { parent, v, position, id ->
            lpw.dismiss()
            if (position == 0) {
                startActivityForResult(Intent(context,
                        CreateNewsFeedActivity::class.java)
                        .putExtra("feed", feed)
                        .putExtra("pos", pos),
                        1002)
            } else if (position == 1) {
                object : DeleteNewsFeedDialog(context!!) {
                    override fun onCloseClicked(dialog: DeleteNewsFeedDialog) {
                        dialog.dismiss()
                    }

                    override fun onDeleteNewsFeed(dialog: DeleteNewsFeedDialog) {
                        dialog.dismiss()
                        deleteFeeds(feed, pos)
                    }
                }.show()
            }
        }
        lpw.show()
    }

    private val ARG_CHATFOR = "chatFor"
    private val ARG_CLUB_ID = "clubId"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"
    override fun onChatClick(feed: Feed) {
        if (feed.is_comment_allow == 0) {
            Util.showToast("comment disable", context)
        } else {
            startActivity(Intent(context, AllChatActivity::class.java)
                    .putExtra(ARG_CHATFOR, ChatUtil.ARG_NEWS_FEED)
                    .putExtra(ARG_CLUB_ID, feed.clubId)
                    .putExtra(ARG_HISTORY_ID, feed.newsFeedId.toString())
                    .putExtra(ARG_HISTORY_NAME, feed.news_feed_title)
            )
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            //R.id.search_t-> (activity as HomeActivity).draweHandler(Gravity.END)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) run {
            val feed: Feed = data?.extras?.getSerializable("feed") as Feed
            val position = data.extras?.getInt("pos")
            newsFeeds[position!!] = feed
            adapter?.notifyItemChanged(position)
        } else if (requestCode == 1002 && resultCode == Activity.RESULT_OK) run {
            newsFeeds.clear()
            pageListner?.resetState()
            getFeeds(0)
        }
    }

    override fun onLongPress(feed: Feed, pos: Int) {
        actionPos = pos
        val list: ArrayList<DialogMenu> = arrayListOf()

        list.add(DialogMenu(getString(R.string.edit), R.drawable.ic_edit))
        list.add(DialogMenu(getString(R.string.delete), R.drawable.ic_delete_icon))
        val a = ItemListDialogFragment()
        a.setInstanceMyFeed(this, list)
        a.show(fragmentManager, "draj")
    }

    //bottom sheet
    override fun onItemClicked(position: Int) {
        val feed = newsFeeds[actionPos]
        when (position) {
            0 -> {
                if (Util.isConnectingToInternet(context!!)) {
                    startActivityForResult(Intent(context,
                            CreateNewsFeedActivity::class.java)
                            .putExtra("feed", feed)
                            .putExtra("pos", actionPos),
                            1002)
                } else {
                    object : Internet_Connection_dialog(context!!) {
                        override fun tryaginlistner() {
                            this.dismiss()
                            startActivityForResult(Intent(context,
                                    CreateNewsFeedActivity::class.java)
                                    .putExtra("feed", feed)
                                    .putExtra("pos", actionPos),
                                    1002)
                        }
                    }.show()
                }


            }
            1 -> {
                object : DeleteNewsFeedDialog(context!!) {
                    override fun onCloseClicked(dialog: DeleteNewsFeedDialog) {
                        dialog.dismiss()
                    }

                    override fun onDeleteNewsFeed(dialog: DeleteNewsFeedDialog) {
                        dialog.dismiss()
                        deleteFeeds(feed, actionPos)
                    }
                }.show()
            }

        }
    }

    private fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        if (newsFeeds.size > 0) {
            feedRecycleView.visibility = View.VISIBLE
            noFeedMsgUI.visibility = View.GONE
        } else {
            noFeedMsgUI.visibility = View.VISIBLE
            feedRecycleView.visibility = View.GONE
        }
        adapter?.notifyDataSetChanged()
    }

    private fun getFeeds(pageNo: Int = 0, isPull: Boolean = false,isloading: Boolean = true) {
        val dialog = CusDialogProg(context)

        if (pageNo != 10)
        {
            if(isloading){
                dialog.show()
            }
        }
        object : VolleyGetPost(activity, WebService.feed_getNewsFeedLsit, false,
                false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        if (isPull) newsFeeds.clear()
                        newsFeeds.addAll(Gson().fromJson<ArrayList<Feed>>(obj.getJSONArray("data").toString(), Type_Token.feed_list))
                        updateUI()
                        if (isPull) updateDB()
                        getMembershipPlan();
                    }
                    if(obj.getString("status") == "fail"&&isPull){
                        newsFeeds.clear()
                        AllFeedsRepo().deleteTable()
                        updateUI()
                        getMembershipPlan();
                    }






                } catch (ex: Exception) {
                    ex.printStackTrace()

                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["offset"] = pageNo.toString()
                params["limit"] = "10"
                params["likes"] = if (isFilterByLike) "1" else "0"
                params["clubs"] = if (isFilterByClub) "1" else "0"
                params["comments"] = if (isFilterByComment) "1" else "0"
                if (isMyFeed) params["isMyFeed"] = "1"
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    private fun updateDB() {
        AllFeedsRepo().deleteTable()
        for (i in newsFeeds.indices) {


            val feed = newsFeeds[i]
            val allfeeds = AllFeeds()
            allfeeds.newsFeedId = feed.newsFeedId
            allfeeds.news_feed_title = feed.news_feed_title
            allfeeds.news_feed_description = feed.news_feed_description
            allfeeds.tagName = feed.tagName
            allfeeds.datetime = feed.datetime
            allfeeds.club_name = feed.club_name
            allfeeds.clubId = feed.clubId
            allfeeds.user_name = feed.user_name
            allfeeds.creator_phone = feed.creator_phone
            allfeeds.contact_no_visibility = feed.contact_no_visibility
            allfeeds.profile_image = feed.profile_image
            allfeeds.user_id = feed.user_id
            allfeeds.likes = feed.likes
            allfeeds.comments = feed.comments
            allfeeds.is_comment_allow = feed.is_comment_allow
            allfeeds.bookmarks = feed.bookmarks
            allfeeds.isLiked = feed.isLiked
            allfeeds.isBookmarked = feed.isBookmarked
            allfeeds.news_feed_attachment = feed.news_feed_attachment
            allfeeds.club_image = feed.club_image
            allfeeds.club_icon = feed.club_icon
            allfeeds.currentDateTime = feed.currentDateTime
            allfeeds.crd = feed.crd
            allfeeds.clubUserId = feed.clubUserId
            AllFeedsRepo().insert(allfeeds)

            if(i==9)break
        }
    }

    private fun deleteFeeds(feed: Feed, pos: Int) {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, WebService.delete_newsFeed, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        newsFeeds.removeAt(pos)
                        adapter?.notifyItemRemoved(pos)
                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, context!!)

                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["newsFeedId"] = feed.newsFeedId.toString()
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    private fun getMembershipPlan() {
       // val dialog = CusDialogProg(activity)
        object : VolleyGetPost(activity, WebService.getMembershipPlanList, true,
                false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    //dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {

                        plan = Gson().fromJson<MembershipPlan>(response, MembershipPlan::class.java)
                        SessionManager.getObj().createMembershipSession(plan)
                    }


                } catch (ex: Exception) {

                }
            }

            override fun onVolleyError(error: VolleyError?) {
               // dialog.dismiss()
            }

            override fun onNetError() {
               // dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {

                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute()
    }



}
