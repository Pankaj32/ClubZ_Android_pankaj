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
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.*
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.ui.cv.CusDialogProg
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
import org.json.JSONObject


/**
 * Created by Dharmraj Acharya on 3/12/18.
 */

class FragNewsList : Fragment(), View.OnClickListener, NewsFeedAdapter.Listner,
        SwipeRefreshLayout.OnRefreshListener {

    private var pageListner: RecyclerViewScrollListener? = null
    private var newsFeeds: ArrayList<Feed> = arrayListOf()
    private var adapter: NewsFeedAdapter? = null
    private var isMyFeed: Boolean = false

    private var isFilterByLike = false
    private var isFilterByComment = false
    private var isFilterByClub = false

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
*/
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
        getFeeds()
    }

    override fun onResume() {
        super.onResume()
        if (ClubZ.isNeedToUpdateNewsFeed && !isMyFeed) {
            ClubZ.isNeedToUpdateNewsFeed = false
            newsFeeds.clear()
            pageListner?.resetState()
            getFeeds(0)
        }
    }

    override fun onRefresh() {
        newsFeeds.clear()
        getFeeds(0)
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
                .putExtra("feed", feed)
                .putExtra("pos", pos),
                1001)
    }

    override fun onProfileClick(feed: Feed) {
        /*if (!feed.user_id.equals(ClubZ.currentUser!!.id)) {

            val user = ClubMember()
            user.profile_image = feed.profile_image
            user.userId = feed.user_id
            user.full_name = feed.user_name
            user.isLiked = feed.isLiked

            val dialog = object : UserProfileDialog(context!!, user, false) {
                override fun onProfileUpdate(name: String) {}
                override fun showError(msg: String) {
                    showToast(msg)
                }

                override fun onCallClicked() {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "${user.country_code} ${user.contact_no}"))
                    startActivity(intent)
                    //showToast("call clicked!")
                }

                *//*override fun onChatClicked() {
                showToast("chat clicked!")
            }*//*

                override fun onLikeClicked(isLIked: Int) {
                    feed.isLiked = isLIked
                    // Already updated into server nothing you want to do
                }

                override fun onFlagClicked() {
                    dismiss()
                    val profile = Profile()
                    profile.userId = feed.user_id
                    profile.full_name = feed.user_name
                    profile.profile_image = feed.profile_image
                    startActivity(Intent(context, ProfileActivity::class.java).putExtra("profile", profile))
                }
            }
            dialog.setCancelable(true)
            dialog.show()
        }*/

        if(!feed!!.user_id.equals(ClubZ.currentUser!!.id)) showProfile(feed)
    }
    private fun showProfile(feed: Feed) {
        val user = UserInfo()
        user.profile_image = feed!!.profile_image
        user.userId = feed!!.user_id
        user.full_name = feed!!.user_name
        user.isLiked = feed!!.isLiked
        user.country_code = "+91"
        user.contact_no = "8116174365"

        val dialog = object : ProfileDialog(context!!, user) {
            override fun OnFabClick(user: UserInfo) {
                Toast.makeText(context, "OnFabClick", Toast.LENGTH_SHORT).show()
            }

            /* override fun OnChatClick(user: UserInfo) {
                 Toast.makeText(mContext, "OnChatClick", Toast.LENGTH_SHORT).show()
             }*/

            /*override fun OnCallClick(user: UserInfo) {
                Toast.makeText(mContext, "OnCallClick", Toast.LENGTH_SHORT).show()
            }*/

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

    override fun onChatClick(feed: Feed) {
        if (feed.is_comment_allow == 0) {
            Util.showToast(R.string.error_comment_disabled, context!!)
        } else Util.showToast(R.string.under_development, context!!)
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

    private fun getFeeds(pageNo: Int = 0) {
        val dialog = CusDialogProg(context)
        if (pageNo != 10) dialog.show()
        object : VolleyGetPost(activity, WebService.feed_getNewsFeedLsit, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        newsFeeds.addAll(Gson().fromJson<ArrayList<Feed>>(obj.getJSONArray("data").toString(), Type_Token.feed_list))
                    }
                    updateUI()
                } catch (ex: Exception) {
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

    private fun deleteFeeds(feed: Feed, pos: Int) {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, WebService.delete_newsFeed, false) {
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
}
