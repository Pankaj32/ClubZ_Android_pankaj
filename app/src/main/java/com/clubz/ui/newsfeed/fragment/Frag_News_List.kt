package com.clubz.ui.newsfeed.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.main.HomeActivity

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.ui.newsfeed.adapter.NewsFeedAdapter
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_news.*
import org.json.JSONObject

/**
 * Created by mindiii on 3/12/18.
 */

class Frag_News_List : Fragment(), View.OnClickListener {

    var pageListner : RecyclerViewScrollListener? = null
    var newsFeeds : ArrayList<Feed> = arrayListOf()
    var adapter : NewsFeedAdapter? = null


    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_news, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_t.setOnClickListener(this)

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        feedRecycleView.setItemAnimator(null)
        feedRecycleView.setLayoutManager(lm)
        feedRecycleView.setHasFixedSize(true)
        adapter = NewsFeedAdapter(newsFeeds, context)
        feedRecycleView.adapter = adapter
        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {

            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {

            }
        }
        feedRecycleView.addOnScrollListener(pageListner)
        getFeeds()
    }


    override fun onClick(v: View) {
        when(v.id){
            R.id.search_t-> (activity as HomeActivity).draweHandler(Gravity.END)
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
        //getNewsFeedLsit
        val dialog = CusDialogProg(context);
        dialog.show();   // ?clubId=66&offset=0&limit=10
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
                dialog.dismiss();
            }

            override fun onNetError() {
                dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("offset", pageNo.toString());
                params.put("limit", "20");
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", ClubZ.currentUser!!.auth_token);
                params.put("language", SessionManager.getObj().getLanguage());
                return params
            }
        }.execute()
    }
}
