package com.clubz.ui.newsfeed.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.model.Profile
import com.clubz.data.model.UserInfo
import com.clubz.data.remote.WebService
import com.clubz.ui.dialogs.ProfileDialog
import com.clubz.ui.dialogs.ZoomDialog
import com.clubz.ui.newsfeed.NewsFeedDetailActivity
import com.clubz.ui.profile.ProfileActivity
import com.clubz.utils.VolleyGetPost
import kotlinx.android.synthetic.main.fragment_feed_detail.*


class FeedDetailFragment : Fragment(), View.OnClickListener {

    private var feed: Feed? = null
    private var mContext: Context? = null
    private var smlProgress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            feed = it.getSerializable("feed") as Feed
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)

        likeIcon.setOnClickListener {
            likeNewsFeed()
            val isCheck = likeIcon.isChecked
            feed?.isLiked = if (isCheck) 1 else 0
            if (isCheck) feed!!.likes++ else feed!!.likes--
            tvLikeCount.text = String.format(getString(R.string.likes_count), feed!!.likes)
            val activity = context as NewsFeedDetailActivity
            activity.updateNewsfeed(feed)
        }
    }

    private fun initView(view: View) {
        //tvClubname.text = feed!!.club_name
        smlProgress = view!!.findViewById<ProgressBar>(R.id.smlProgress)
        tvUsername.text = feed!!.user_name
        tvTitle.text = feed!!.news_feed_title
        tvUsreRole.text = getString(R.string.manager)
        tvDescription.text = feed!!.news_feed_description
        tvLikeCount.text = String.format(getString(R.string.likes_count), feed!!.likes)
        //tvCommentCount.text = String.format(getString(R.string.comments_count), feed!!.comments)
        tvCreateTime.text = feed?.getFormatedDate()
        likeIcon.isChecked = feed?.isLiked == 1

        //ivChat.setImageResource(if(feed!!.comments>0) R.drawable.chat_icon else R.drawable.ic_chat_outline)
        /* if(feed?.news_feed_attachment.isNullOrEmpty()) ivBanner.visibility = View.GONE
         else Picasso.with(ivBanner.context).load(feed?.news_feed_attachment).fit().into(ivBanner)*/

        if (!feed?.news_feed_attachment.isNullOrEmpty()) {
            imgLay.visibility = View.VISIBLE
            Glide.with(ivBanner.context)
                    .load(feed?.news_feed_attachment)
                    /*.placeholder(R.drawable.new_img)
                    .fitCenter()*/
                    .into(ivBanner)
        } else {
            smlProgress!!.visibility = View.GONE
            imgLay.visibility = View.GONE
        }

        if (feed?.profile_image.isNullOrEmpty()) Glide.with(image_member.context).load(feed?.profile_image)/*.fitCenter()*/.into(image_member)
        if (feed?.profile_image.isNullOrEmpty()) {
            Glide.with(image_member.context).load(feed?.profile_image)/*.fitCenter()*/.into(image_member)
        } else {
            image_member.setImageResource(R.drawable.user_place_holder)
        }

        tvUsername.setOnClickListener(this)
        ivBanner.setOnClickListener(this)
        /* val result: List<String> = feed!!.tagName.split(",").map { it.trim() }
         chip_grid.visibility = if(result.size>0) View.VISIBLE else View.GONE
         for(t in result)  if(t.isNotEmpty()) addTag(t)*/
    }


    /* private fun addTag(tag : String){
         if (!TextUtils.isEmpty(tag)){
             val chip = object : ChipView(context, chip_grid.childCount.toString(), false){
                 override fun getLayout(): Int { return R.layout.z_cus_chip_view_newsfeed }
                 override fun setDeleteListner(chipView: ChipView?) {
                 }
             }
             chip.text = tag.trim()
             chip_grid.addView(chip)
         }
     }*/

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


    private fun likeNewsFeed() {
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

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tvUsername -> {
                if (!feed!!.user_id.equals(ClubZ.currentUser!!.id)) showProfile()
            }
            R.id.ivBanner -> {
                val dialog = ZoomDialog(mContext!!, feed!!.news_feed_attachment!!)
                dialog.show()
            }
        }
    }

    private fun showProfile() {
        val user = UserInfo()
        user.profile_image = feed!!.profile_image
        user.userId = feed!!.user_id
        user.full_name = feed!!.user_name
        user.isLiked = feed!!.isLiked
        user.country_code = ""
        user.contact_no = feed!!.creator_phone
        user.contact_no_visibility = feed!!.contact_no_visibility

        val dialog = object : ProfileDialog(mContext!!, user) {
            override fun OnFabClick(user: UserInfo) {
                Toast.makeText(mContext, "OnFabClick", Toast.LENGTH_SHORT).show()
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
                    mContext?.startActivity(Intent(mContext, ProfileActivity::class.java).putExtra("profile", profile))
                } else {
                    Toast.makeText(mContext, getString(R.string.under_development), Toast.LENGTH_SHORT).show()
                }
            }

        }
        //  dialog.setCancelable(true)
        dialog.show()
    }

}
