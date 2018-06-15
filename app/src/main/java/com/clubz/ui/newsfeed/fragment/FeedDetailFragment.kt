package com.clubz.ui.newsfeed.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.ChipView
import com.clubz.ui.newsfeed.NewsFeedDetailActivity
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.VolleyGetPost
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_feed_detail.*


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

        likeIcon.setOnClickListener({
            likeNewsFeed()
            val isCheck = likeIcon.isChecked
            feed?.isLiked = if(isCheck) 1 else 0
            if (isCheck) feed!!.likes++ else feed!!.likes--
            tvLikeCount.text = String.format(getString(R.string.likes_count), feed!!.likes)
            val activity = context as NewsFeedDetailActivity
            activity.updateNewsfeed(feed)
        })
    }

    private fun initView(){
        tvClubname.text = feed!!.club_name
        tvUsername.text = feed!!.user_name
        tvTitle.text = feed!!.news_feed_title
        tvUsreRole.text = getString(R.string.club_manager)
        tvDescription.text = feed!!.news_feed_description
        tvLikeCount.text = String.format(getString(R.string.likes_count), feed!!.likes)
        tvCommentCount.text = String.format(getString(R.string.comments_count), feed!!.comments)
        tvCreateTime.text = feed?.getFormatedDate()
        likeIcon.isChecked = feed?.isLiked==1

        ivChat.setImageResource(if(feed!!.comments>0) R.drawable.chat_icon else R.drawable.ic_chat_outline)
        if(feed?.news_feed_attachment.isNullOrEmpty()) ivBanner.visibility = View.GONE
        else Picasso.with(ivBanner.context).load(feed?.news_feed_attachment).fit().into(ivBanner)

        if(feed?.profile_image.isNullOrEmpty())
            Picasso.with(ivBanner.context).load(R.drawable.ic_user_white).fit().into(image_member)
        else
            Picasso.with(ivBanner.context).load(feed?.profile_image).fit().into(image_member)

        val result: List<String> = feed!!.tagName.split(",").map { it.trim() }
        chip_grid.visibility = if(result.size>0) View.VISIBLE else View.GONE
        for(t in result)  if(t.isNotEmpty()) addTag(t)
    }


    private fun addTag(tag : String){
        if (!TextUtils.isEmpty(tag)){
            val chip = object : ChipView(context, chip_grid.childCount.toString(), false){
                override fun getLayout(): Int { return R.layout.z_cus_chip_view_newsfeed }
                override fun setDeleteListner(chipView: ChipView?) {
                }
            }
            chip.text = tag.trim()
            chip_grid.addView(chip)
        }
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


    private fun likeNewsFeed(){
        object : VolleyGetPost(activity, WebService.feed_like, false) {
            override fun onVolleyResponse(response: String?) {
                /*try {
                    Log.d("newsFeedsLike", response)
                    val obj = JSONObject(response)
                    if (obj.getString("status")=="success") {

                    }
                } catch (ex: Exception) {
                    // Util.showToast(R.string.swr, context)
                }*/
            }

            override fun onVolleyError(error: VolleyError?) {
            }

            override fun onNetError() {
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["newsFeedId"] = feed?.newsFeedId.toString()
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
