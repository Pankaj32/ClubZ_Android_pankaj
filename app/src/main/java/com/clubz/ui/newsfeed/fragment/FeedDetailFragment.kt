package com.clubz.ui.newsfeed.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.utils.VolleyGetPost
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_feed_detail.*
import org.json.JSONObject


class FeedDetailFragment : Fragment() {

    private var feed : Feed? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            feed = it.getSerializable("feed") as Feed
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        likeIcon.setOnClickListener(View.OnClickListener { v: View? ->
            likeNewsFeed()
            val isCheck = likeIcon.isChecked
            feed?.isLiked = if(isCheck) 1 else 0
            if (isCheck) feed!!.likes++ else feed!!.likes--
            tvLikeCount.text = feed?.likes.toString()+" Likes"
        })
    }

    fun initView(){
        tvTitle.text = feed!!.news_feed_title
        tvClubname.text = feed!!.club_name
        tvDescription.text = feed!!.news_feed_description
        tvUsername.text = feed!!.user_name
        tvUsreRole.text = getString(R.string.admin)
        tvLikeCount.text = feed?.likes.toString()+" Likes"
        tvCommentCount.text = feed?.comments.toString()+" Comments"
        tvCreateTime.text = feed?.getFormatedDate()
        likeIcon.isChecked = feed?.isLiked==1

        if(feed?.news_feed_attachment.isNullOrEmpty())
            ivBanner.visibility = View.GONE
        else Picasso.with(ivBanner.context).load(feed?.news_feed_attachment).fit().into(ivBanner)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param feed
         * @return A new instance of fragment FeedDetailFragment.
         */
        @JvmStatic
        fun newInstance(feed: Feed) = FeedDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable("feed", feed)
            }
        }
    }


    fun likeNewsFeed(){
        //getNewsFeedLsit
       // val dialog = CusDialogProg(context);
       // dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, WebService.feed_like, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                   // dialog.dismiss();
                    Log.d("newsFeedsLike", response)
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {

                    }
                } catch (ex: Exception) {
                    // Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                //dialog.dismiss();
            }

            override fun onNetError() {
               // dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("newsFeedId", feed?.newsFeedId.toString());
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
