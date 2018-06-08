package com.clubz.ui.newsfeed.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.ui.newsfeed.CreateNewsFeedActivity
import com.clubz.ui.newsfeed.NewsFeedDetailActivity
import com.clubz.ui.newsfeed.adapter.NewsFeedAdapter
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_news.*
import org.json.JSONObject

/**
 * Created by mindiii on 3/12/18.
 */

class Frag_News_List : Fragment(), View.OnClickListener, NewsFeedAdapter.Listner,
        SwipeRefreshLayout.OnRefreshListener {

    var pageListner : RecyclerViewScrollListener? = null
    var newsFeeds : ArrayList<Feed> = arrayListOf()
    var adapter : NewsFeedAdapter? = null
    var isMyFeed : Boolean = false

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param feed
         * @return A new instance of fragment FeedDetailFragment.
         */
        @JvmStatic
        fun newInstance(isMyFeed : Boolean = false) = Frag_News_List().apply {
            arguments = Bundle().apply {
                putBoolean("isMyFeed", isMyFeed)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isMyFeed = it.getBoolean("isMyFeed");
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_news, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //search_t.setOnClickListener(this)

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        feedRecycleView.setItemAnimator(null)
        feedRecycleView.setLayoutManager(lm)
        feedRecycleView.setHasFixedSize(true)
        adapter = NewsFeedAdapter(newsFeeds, context, this)
        feedRecycleView.adapter = adapter
        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {

            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getFeeds(page*10)
            }
        }

        swipeRefreshLayout.setOnRefreshListener(this)
        feedRecycleView.addOnScrollListener(pageListner)
        getFeeds()
    }


    override fun onRefresh() {
        newsFeeds.clear()
        getFeeds(0)
        swipeRefreshLayout.isRefreshing = false
    }


    override fun onItemClick(feed: Feed) {
        startActivityForResult(Intent(context,
                NewsFeedDetailActivity::class.java).putExtra("feed",feed),
                1001)
    }

    override fun onFeedEditClick(feed: Feed) {
        startActivityForResult(Intent(context,
                CreateNewsFeedActivity::class.java)
                .putExtra("feed",feed),
                1002)
    }

    override fun onClick(v: View) {
        when(v.id){
            //R.id.search_t-> (activity as HomeActivity).draweHandler(Gravity.END)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==1001 && resultCode==-1) run {
            val feed: Feed = data?.extras?.getSerializable("feed") as Feed
            val position = data?.extras?.getInt("pos")
        }else if(resultCode==1001 && resultCode==-1){

        }
    }

    fun manageView(){
        if(newsFeeds.size==0){
            feedRecycleView.visibility = View.GONE
        }else{
            noFeedMsgUI.visibility = if(newsFeeds.size==0)View.VISIBLE else View.GONE
            feedRecycleView.visibility = View.VISIBLE
            adapter?.notifyDataSetChanged()
        }
    }


    fun getFeeds(pageNo : Int = 0){

        val dialog = CusDialogProg(context)
        if(pageNo!=10)  dialog.show()

        object : VolleyGetPost(activity, WebService.feed_getNewsFeedLsit, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        newsFeeds.addAll(Gson().fromJson<ArrayList<Feed>>(obj.getJSONArray("data").toString() , Type_Token.feed_list))
                    }
                    manageView()
                } catch (ex: Exception) {
                    // Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog?.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["offset"] = pageNo.toString()
                params["limit"] =  "10"
                if (isMyFeed) params["isMyFeed"] = "1"
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().getLanguage()
                return params
            }
        }.execute()
    }
}
