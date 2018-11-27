package com.clubz.ui.ads.fragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Profile
import com.clubz.data.model.UserInfo
import com.clubz.data.remote.WebService
import com.clubz.ui.ads.model.AddDetailsBean
import com.clubz.ui.ads.model.AdsListBean
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.dialogs.ProfileDialog
import com.clubz.ui.dialogs.ZoomDialog
import com.clubz.ui.profile.ProfileActivity
import com.clubz.utils.DateTimeUtil
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_frag_ads_details.*

import org.json.JSONObject

class FragAdsDetails : Fragment(), View.OnClickListener {

    private var adId: String? = ""
    private var user_id: String? = ""
    private var adType: String? = ""
    private var mContext: Context? = null
    private var adDetails: AddDetailsBean? = null
    private var adBean: AdsListBean.DataBean? = null
    private val tempDetails = AddDetailsBean.DataBean()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_frag_ads_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            adBean = it.getParcelable(ADBEANKEY)
            adType = it.getString(ADTYPEKEY)

            adId = adBean!!.adId

            tempDetails.adId = adBean!!.adId
            tempDetails.title = adBean!!.title
            tempDetails.fee = adBean!!.fee
            tempDetails.is_renew = adBean!!.is_renew
            tempDetails.description = adBean!!.description
            tempDetails.user_id = adBean!!.user_id
            tempDetails.creator_phone = adBean!!.creator_phone
            tempDetails.contact_no_visibility = adBean!!.contact_no_visibility
            tempDetails.user_role = adBean!!.user_role
            tempDetails.image = adBean!!.image
            tempDetails.creator_name = adBean!!.full_name
            tempDetails.creator_profile_image = adBean!!.profile_image
            tempDetails.club_name = adBean!!.club_name
            tempDetails.clubId = adBean!!.club_id
            tempDetails.created = adBean!!.crd
            tempDetails.total_likes = adBean!!.total_likes
            tempDetails.creator_name = adBean!!.full_name
            tempDetails.creator_profile_image = adBean!!.profile_image
            updateUi(tempDetails, adBean!!.currentDatetime!!)
        }



        getAdDetails()
        username.setOnClickListener(this)
        adImg.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun getAdDetails() {
        val dialogProgress = CusDialogProg(mContext!!)
        dialogProgress.show()

        object : VolleyGetPost(activity!!, mContext,
                "${WebService.getAdsDetails}?adId=${adId}",
                true,false) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialogProgress.dismiss()
                        adDetails = Gson().fromJson(response, AddDetailsBean::class.java)
                        updateUi(adDetails!!.data, adDetails!!.dateTime!!)
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
                //  params["eventTitle"] = eventTitle
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(FragAdsDetails::class.java.name)
    }

    private fun updateUi(adDetails: AddDetailsBean.DataBean?, dateTime: String = "") {
        visibleLay.visibility = View.GONE
        user_id = adDetails?.user_id
        if (adDetails?.image!!.isNotEmpty()) {
            /*Glide.with(adImg.context)
                    .load(adDetails.data?.image)
                    .into(adImg)*/
            ImgLay.visibility = View.VISIBLE
            try {
                Glide.with(adImg.context)
                        .load(adDetails.image)
                        /*.placeholder(R.drawable.new_img)
                        .fitCenter()*/
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                smlProgress.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                smlProgress.visibility = View.GONE
                                return false
                            }
                        })
                        .into(adImg)
            } catch (e: Exception) {

            }

        } else {
            smlProgress.visibility = View.GONE
            ImgLay.visibility = View.GONE
        }
        if (!adDetails.title.isNullOrEmpty()) {
            adTitle.text = adDetails.title
        }
        if (!adDetails.fee.isNullOrEmpty()) {
            adValue.text = "$ " + adDetails.fee
        }
        if (!adDetails.total_likes.isNullOrEmpty()) {
            if (adDetails.total_likes.equals("0")) {
                likeImg.visibility = View.GONE
            } else {
                likeTxt.text = adDetails.total_likes + " Likes"
                likeImg.visibility = View.VISIBLE
            }
        }
        timeAgo.text = DateTimeUtil.getDayDifference(mContext, adDetails.created, dateTime)
        timeAgo.text = DateTimeUtil.getDayDifference(mContext, adDetails.created, dateTime)
        if (!adDetails.description.isNullOrEmpty()) {
            adDesc.text = adDetails.description
        }
        username.text = adDetails.creator_name

        if (adDetails.creator_profile_image?.isNotEmpty()!!) {
            /*Glide.with(image_member2.context)
                    .load(adDetails.data?.creator_profile_image)
                    .into(image_member2)*/
            Picasso.with(image_member2.context).load(adDetails.creator_profile_image).placeholder(R.drawable.user_place_holder).fit().into(image_member2)
        } else {
            // val padding = resources.getDimension(R.dimen._8sdp).toInt()
            // image_member2.setPadding(padding, padding, padding, padding)
            // image_member2.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_circle_blue)
            image_member2.setImageResource(R.drawable.user_place_holder)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.username -> {
                if (!user_id!!.equals(ClubZ.currentUser!!.id)) {
                    if (adDetails != null) {
                        showProfile(adDetails!!.data)
                    } else {
                        showProfile(tempDetails)
                    }
                }
            }
            R.id.adImg -> {
                var image = ""
                if (adDetails != null) {
                    image = adDetails?.data?.image.toString()
                } else image = tempDetails.image.toString()
                val dialog = ZoomDialog(mContext!!, image)
                dialog.show()
            }
        }
    }

    private fun showProfile(data: AddDetailsBean.DataBean?) {
        val user = UserInfo()
        user.userId = data?.user_id!!
        user.isLiked = "0"
        user.full_name = data.creator_name!!
        user.profile_image = data.creator_profile_image!!
        user.country_code = ""
        user.contact_no = data.creator_phone!!
        user.contact_no_visibility = data.contact_no_visibility!!
        user.clubId = data.clubId!!

        val dialog = object : ProfileDialog(mContext!!, user) {
            /*override fun OnFabClick(user: UserInfo) {
                Toast.makeText(mContext, "OnFabClick", Toast.LENGTH_SHORT).show()
            }*/

            /*override fun OnChatClick(user: UserInfo) {
                Toast.makeText(mContext,"OnChatClick",Toast.LENGTH_SHORT).show()
            }*/

            /*override fun OnCallClick(user: UserInfo) {
                Toast.makeText(mContext,"OnCallClick",Toast.LENGTH_SHORT).show()
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

    companion object {
        val ADBEANKEY = "adBean"
        val ADTYPEKEY = "adType"
        fun newInstance(adBean: AdsListBean.DataBean?, adType: String): FragAdsDetails {
            val fragment = FragAdsDetails()
            val args = Bundle()
            args.putParcelable(ADBEANKEY, adBean)
            args.putString(ADTYPEKEY, adType)
            fragment.arguments = args
            return fragment
        }
    }
}
