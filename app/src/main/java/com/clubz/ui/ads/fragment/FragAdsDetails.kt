package com.clubz.ui.ads.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.android.volley.VolleyError

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.ads.model.AddDetailsBean
import com.clubz.ui.cv.CusDialogProg
import com.clubz.utils.DateTimeUtil
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_frag_ads_details.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FragAdsDetails : Fragment() {

    private var adId: String? = ""
    private var adType: String? = ""
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            adId = it.getString(IDKEY)
            adType = it.getString(ADTYPEKEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_ads_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getAdDetails()
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
                true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialogProgress.dismiss()
                        visibleLay.visibility = View.GONE
                        var adDetails = Gson().fromJson(response, AddDetailsBean::class.java)
                        updateUi(adDetails)
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

    private fun updateUi(adDetails: AddDetailsBean?) {
        if (adDetails?.data?.image!!.isNotEmpty()) {
            Picasso.with(adImg.context)
                    .load(adDetails.data?.image)
                    .placeholder(R.drawable.new_img)
                    .fit().into(adImg,object :Callback{
                        override fun onSuccess() {
                            smlProgress.visibility=View.GONE
                        }

                        override fun onError() {
                            smlProgress.visibility=View.GONE
                        }
                    })
        }else{
            smlProgress.visibility=View.GONE
        }
        if (!adDetails.data!!.title.isNullOrEmpty()) {
            adTitle.text = adDetails.data?.title
        }
        if (!adDetails.data!!.fee.isNullOrEmpty()) {
            adValue.text = "$ " + adDetails.data?.fee
        }
        if (!adDetails.data?.total_likes.isNullOrEmpty()) {
            if (adDetails.data?.total_likes.equals("0")) {
                likeImg.visibility = View.GONE
            } else {
                likeTxt.text = adDetails.data!!.total_likes + " Likes"
                likeImg.visibility = View.VISIBLE
            }
        }
        timeAgo.text = DateTimeUtil.getDayDifference(adDetails.data!!.created, adDetails.dateTime)
        if (!adDetails.data!!.description.isNullOrEmpty()) {
            adDesc.text = adDetails.data?.description
        }
        username.text = adDetails.data?.creator_name

        if (adDetails.data?.creator_profile_image?.isNotEmpty()!!) {
            Picasso.with(image_member2.context).load(adDetails.data?.creator_profile_image).fit().into(image_member2)
        } else {
            // val padding = resources.getDimension(R.dimen._8sdp).toInt()
            // image_member2.setPadding(padding, padding, padding, padding)
            // image_member2.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_circle_blue)
            image_member2.setImageResource(R.drawable.user_place_holder)
        }
    }

    companion object {
        val IDKEY = "adId"
        val ADTYPEKEY = "adType"
        fun newInstance(adId: String, adType: String): FragAdsDetails {
            val fragment = FragAdsDetails()
            val args = Bundle()
            args.putString(IDKEY, adId)
            args.putString(ADTYPEKEY, adType)
            fragment.arguments = args
            return fragment
        }
    }
}
