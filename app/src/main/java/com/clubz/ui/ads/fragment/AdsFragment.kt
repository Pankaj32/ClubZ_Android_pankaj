package com.clubz.ui.ads.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.repo.AllAdsRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.AllAds
import com.clubz.data.model.DialogMenu
import com.clubz.data.model.Profile
import com.clubz.data.model.UserInfo
import com.clubz.data.remote.WebService
import com.clubz.helper.fcm.NotificatioKeyUtil
import com.clubz.ui.activities.fragment.ItemListDialogFragment
import com.clubz.ui.ads.activity.AdDetailsActivity
import com.clubz.ui.ads.activity.CreateAdActivity
import com.clubz.ui.ads.adapter.AdsAdapter
import com.clubz.ui.ads.listioner.AdsClickListioner
import com.clubz.ui.ads.model.AdsListBean
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.ui.dialogs.ProfileDialog
import com.clubz.ui.profile.ProfileActivity
import com.clubz.utils.VolleyGetPost
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_ads.*
import org.json.JSONObject


class AdsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
        AdsClickListioner, ItemListDialogFragment.Listener {

    override fun onRefresh() {
        pageListner?.resetState()
        getAdsList(isPull = true)
    }

    private val ARG_CHATFOR = "chatFor"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"
    private val ARG_HISTORY_PIC = "historyPic"

    private var pageListner: RecyclerViewScrollListener? = null
    private var param1: String? = null
    private var param2: String? = null
    private var mContext: Context? = null
    private var userId: String = ""
    private var userName: String = ""
    private var userImage: String = ""
    //  private var adList = ArrayList<AdsListBean.DataBean>()
    private var adList = ArrayList<Any>()
    // List of native ads that have been successfully loaded.
    private val mNativeAds = java.util.ArrayList<UnifiedNativeAd>()

    private var tempAdList = ArrayList<AdsListBean.DataBean>()
    private var adsAdapter: AdsAdapter? = null
    private var isResume = false
    var isMyAds: Boolean = false
    private var actionPosition = 0

    // The number of native ads to load.
    val NUMBER_OF_ADS = 5

    // The AdLoader used to load ads.
    private var adLoader: AdLoader? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ads, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    fun initializeView() {
        userId = ClubZ.currentUser!!.id
        userName = ClubZ.currentUser!!.full_name
        userImage = ClubZ.currentUser!!.profile_image
        swiperefresh.setOnRefreshListener(this)

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewAds.itemAnimator = null
        recyclerViewAds.layoutManager = lm
        recyclerViewAds.setHasFixedSize(true)

        /*nodataLay.visibility = if (adList.isEmpty()) View.VISIBLE else View.GONE*/
        adsAdapter = AdsAdapter(mContext, adList, this)
        recyclerViewAds.adapter = adsAdapter

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {}
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getAdsList(offset = page * 10)
            }
        }
        recyclerViewAds.addOnScrollListener(pageListner)
        // getAdsList(isPull = true)
       val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("Ads:", "onAdLoaded")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.e("Ads:", "onAdFailedToLoad")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("Ads:", "onAdOpened")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.e("Ads:", "onAdLeftApplication")
            }

            override fun onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.e("Ads:", "onAdClosed")
            }
        }


        val tempAdsList = AllAdsRepo().getAllAds()
        val adList = ArrayList<AdsListBean.DataBean>()
        if (tempAdsList.size > 0) {
            for (ads in tempAdsList) {
                val adsBean = AdsListBean.DataBean()
                adsBean.adId = ads.adId
                adsBean.title = ads.title
                adsBean.fee = ads.fee
                adsBean.is_renew = ads.is_renew
                adsBean.description = ads.description
                adsBean.club_id = ads.club_id
                adsBean.user_id = ads.user_id
                adsBean.creator_phone = ads.creator_phone
                adsBean.contact_no_visibility = ads.contact_no_visibility
                adsBean.user_role = ads.user_role
                adsBean.crd = ads.crd
                adsBean.image = ads.image
                adsBean.profile_image = ads.profile_image
                adsBean.club_name = ads.club_name
                adsBean.full_name = ads.full_name
                adsBean.isFav = ads.isFav
                adsBean.currentDatetime = ads.currentDatetime
                adsBean.is_my_ads = ads.is_my_ads
                adsBean.is_New = ads.is_New
                adsBean.expire_ads = ads.expire_ads
                adsBean.total_likes = ads.total_likes
                adList.add(adsBean)
            }

        }
        updateUi(adList, true)
        //   loadNativeAds()

    }

    override fun onResume() {
        super.onResume()
        if (isResume) {
            getAdsList(isPull = true)
        } else {
            getAdsList(isPull = true)
            isResume = true
        }
    }

    fun getAdsList(listType: String = "", limit: String = "10", offset: Int = 0, isPull: Boolean? = false) {
        /*val dialog = CusDialogProg(mContext)
        if (!swiperefresh.isRefreshing || !isResume) dialog.show()*/
        object : VolleyGetPost(mContext,
                "${WebService.getAdsList}?limit=${limit}&offset=${offset}&listType=${listType}", true, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    if (swiperefresh.isRefreshing) swiperefresh.setRefreshing(false)
                    //  dialog.dismiss()

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        val adsBean: AdsListBean = Gson().fromJson(response, AdsListBean::class.java)
                        updateUi(adsBean.data, isPull)
                        if (isPull!!) updateInDb(adsBean)
                    } else {
                        nodataLay.visibility = View.VISIBLE
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                //   dialog.dismiss()
            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                /*params.put("listType", listType)
                params.put("offset", offset.toString())
                params.put("limit", limit)*/
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                Log.e("Auth:", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(AdsFragment::class.java.name)
    }

    private fun updateInDb(adsBean: AdsListBean) {
        AllAdsRepo().deleteTable()
        for (ads in adsBean.data!!) {
            val allAds = AllAds()
            allAds.adId = ads.adId
            allAds.title = ads.title
            allAds.fee = ads.fee
            allAds.is_renew = ads.is_renew
            allAds.description = ads.description
            allAds.club_id = ads.club_id
            allAds.user_id = ads.user_id
            allAds.creator_phone = ads.creator_phone
            allAds.contact_no_visibility = ads.contact_no_visibility
            allAds.user_role = ads.user_role
            allAds.crd = ads.crd
            allAds.image = ads.image
            allAds.profile_image = ads.profile_image
            allAds.club_name = ads.club_name
            allAds.full_name = ads.full_name
            allAds.isFav = ads.isFav
            allAds.currentDatetime = ads.currentDatetime
            allAds.is_my_ads = ads.is_my_ads
            allAds.is_New = ads.is_New
            allAds.expire_ads = ads.expire_ads
            allAds.total_likes = ads.total_likes
            AllAdsRepo().insert(allAds)
        }
    }

    private fun updateUi(adsBean: List<AdsListBean.DataBean>?, pull: Boolean?) {
        adList.clear()
        if (pull!!) {
            tempAdList.clear()
            adsAdapter?.notifyDataSetChanged()
        }
        tempAdList.addAll(adsBean!!)

        if (pull) adList.clear()
        if (isMyAds) {
            for (dataBean in tempAdList) {
                if (dataBean.is_my_ads.equals("1")) adList.add(dataBean)
            }
        } else {
            adList.addAll(tempAdList)
        }

        /*val data=AdsListBean.DataBean()
        data.isgoogleAdd=true
        if(adList.size<3){
            adList.add(data)
        }else{
            adList.add(3,data)
        }*/
        // insertAdsInMenuItems()
        // adList.addAll(adsBean.data!!)
        adsAdapter?.notifyDataSetChanged()
        nodataLay.visibility = if (adList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onItemClick(position: Int) {
        val adaBean = adList[position] as AdsListBean.DataBean
        startActivity(Intent(mContext, AdDetailsActivity::class.java)
                /*.putExtra("adId", adaBean.adId)
                .putExtra("adTitle", adaBean.title)
                .putExtra("clubId", adaBean.club_id)
                .putExtra("clubName", adaBean.club_name)*/
                .putExtra(NotificatioKeyUtil.Key_From, "")
                .putExtra("adBean", adaBean)
                .putExtra("adType", "")

        )
    }

    override fun onFabClick(position: Int) {

    }

    override fun onLongPress(position: Int) {
        actionPosition = position
        val adBean = adList[position] as AdsListBean.DataBean
        val list: ArrayList<DialogMenu> = arrayListOf()
        if (adBean.is_my_ads.equals("1")) {
            list.add(DialogMenu(getString(R.string.edit_ad), R.drawable.ic_edit))
            list.add(DialogMenu(getString(R.string.delete_ad), R.drawable.ic_delete_forever))
            if (adBean.expire_ads.equals("Yes")) {
                list.add(DialogMenu(getString(R.string.renew_ad), R.drawable.ic_refresh))
            }
        } else {
            if (adBean.isFav.equals("1")) {
                list.add(DialogMenu(getString(R.string.remove_ad_fav), R.drawable.ic_favorite_fill))
            } else {
                list.add(DialogMenu(getString(R.string.add_ad_fav), R.drawable.ic_favorite_fill))
            }
            list.add(DialogMenu(getString(R.string.private_chat_with_advertiser), R.drawable.ic_chat_bt_sht))
            list.add(DialogMenu(getString(R.string.delete_ad_from_my_feed), R.drawable.ic_delete_forever))
        }

        val a = ItemListDialogFragment()
        a.setInstanceMyAd(this, list)
        a.show(fragmentManager, "draj")
    }

    override fun onUserClick(user: UserInfo) {
        if (!user.userId.equals(ClubZ.currentUser!!.id)) showProfile(user)
    }

    fun showProfile(user: UserInfo) {

        val dialog = object : ProfileDialog(mContext!!, user) {
            /*override fun OnFabClick(user: UserInfo) {
                Toast.makeText(mContext,"OnFabClick",Toast.LENGTH_SHORT).show()
            }
*/
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

    //bottom sheet click
    override fun onItemClicked(position: Int) {
        val adBean = adList[actionPosition] as AdsListBean.DataBean
        when (position) {
            0 -> {
                if (adBean.is_my_ads.equals("1")) {
                    startActivity(Intent(mContext, CreateAdActivity::class.java)
                            .putExtra("adBean", adBean)


                    )
                } else {
                    showConfirmationDialog("fav", adBean)
                }
            }
            1 -> {
                if (adBean.is_my_ads.equals("1")) {
                    showConfirmationDialog("deleteMy", adBean)
                } else {


                    mContext?.startActivity(Intent(mContext, AllChatActivity::class.java)
                            .putExtra(ARG_CHATFOR, ChatUtil.ARG_IDIVIDUAL)
                            .putExtra(ARG_HISTORY_ID,adBean.user_id)
                            .putExtra(ARG_HISTORY_NAME, adBean.full_name)
                            .putExtra(ARG_HISTORY_PIC, adBean.profile_image))

                    //Toast.makeText(mContext, adBean.title + " go for chat", Toast.LENGTH_SHORT).show()
                }
            }
            2 -> {
                if (adBean.is_my_ads.equals("1")) {
                    showConfirmationDialog("renew", adBean)
                } else {
                    showConfirmationDialog("deleteOthers", adBean)
                }
            }
        }
    }

    private fun deleteAd(adsBean: AdsListBean.DataBean, dialog1: DialogInterface? = null) {
        val dialog = CusDialogProg(mContext)
        if (!swiperefresh.isRefreshing || !isResume) dialog.show()
        object : VolleyGetPost(mContext,
                WebService.adsDelete, false, true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    if (swiperefresh.isRefreshing) swiperefresh.setRefreshing(false)
                    dialog.dismiss()

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        if (dialog1 != null) dialog1.dismiss()
                        getAdsList(isPull = true)
                    } else {
                        nodataLay.visibility = View.VISIBLE
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
                params.put("adId", adsBean.adId!!)
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                Log.e("Auth:", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(AdsFragment::class.java.name)

    }

    private fun reNewAd(adsBean: AdsListBean.DataBean, dialog1: DialogInterface? = null) {
        val dialog = CusDialogProg(mContext)
        if (!swiperefresh.isRefreshing || !isResume) dialog.show()
        object : VolleyGetPost(mContext,
                WebService.renewAds, false, true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    if (swiperefresh.isRefreshing) swiperefresh.setRefreshing(false)
                    dialog.dismiss()

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        if (dialog1 != null) dialog1.dismiss()
                        getAdsList(isPull = true)
                    } else {
                        nodataLay.visibility = View.VISIBLE
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
                params.put("adId", adsBean.adId!!)
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                Log.e("Auth:", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(AdsFragment::class.java.name)

    }

    private fun addRemoveAdFromFab(adsBean: AdsListBean.DataBean, dialog1: DialogInterface? = null) {
        val dialog = CusDialogProg(mContext)
        if (!swiperefresh.isRefreshing || !isResume) dialog.show()
        object : VolleyGetPost(mContext,
                WebService.adsFab, false, true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    if (swiperefresh.isRefreshing) swiperefresh.setRefreshing(false)
                    dialog.dismiss()

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        if (dialog1 != null) dialog1.dismiss()
                        getAdsList(isPull = true)
                        getAdsList(isPull = true)
                    } else {
                        nodataLay.visibility = View.VISIBLE
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
                params.put("adId", adsBean.adId!!)
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                Log.e("Auth:", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(AdsFragment::class.java.name)

    }

    private fun showConfirmationDialog(action: String = "", adsBean: AdsListBean.DataBean) {
        try {
            var title = ""
            when (action) {
                "fav" -> {
                    if (adsBean.isFav.equals("1")) {
                        title = getString(R.string.remove_ad_fav_title)
                    } else {
                        title = getString(R.string.add_ad_fav_title)
                    }
                }
                "deleteMy" -> {
                    title = getString(R.string.delete_my_ad_title)
                }
                "deleteOthers" -> {
                    title = getString(R.string.delete_other_ad_title)
                }
                "renew" -> {
                    title = getString(R.string.renew_ad_title)
                }
            }

            val builder1 = AlertDialog.Builder(mContext)
            builder1.setTitle("Alert")
            builder1.setMessage(title)
            builder1.setCancelable(false)
            builder1.setPositiveButton("Ok", { dialog, id ->
                when (action) {
                    "fav" -> {
                        addRemoveAdFromFab(adsBean, dialog)
                    }
                    "deleteMy",
                    "deleteOthers" -> {
                        deleteAd(adsBean, dialog)
                    }
                    "renew" -> {
                        reNewAd(adsBean, dialog)
                    }
                }
            })

            builder1.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })

            val alert11 = builder1.create()
            alert11.show()
            alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext!!, R.color.nav_gray))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doFilter() {
        adList.clear()
        adsAdapter?.notifyDataSetChanged()
        if (isMyAds) {
            isMyAds = false
            adList.addAll(tempAdList)
        } else {
            isMyAds = true
            for (dataBean in tempAdList) {
                if (dataBean.is_my_ads.equals("1")) adList.add(dataBean)
            }
        }
        nodataLay.visibility = if (adList.isEmpty()) View.VISIBLE else View.GONE
        adsAdapter?.notifyDataSetChanged()
    }

    fun onSwitchClub() {
        getAdsList(isPull = true)
    }

    private fun loadNativeAds() {
        val builder = AdLoader.Builder(mContext, getString(R.string.ad_unit_id))
        adLoader = builder.forUnifiedNativeAd(
                UnifiedNativeAd.OnUnifiedNativeAdLoadedListener { unifiedNativeAd ->
                    // A native ad loaded successfully, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    mNativeAds.add(unifiedNativeAd)
                    if (!adLoader!!.isLoading()) {
                        insertAdsInMenuItems()
                    }
                }).withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to" + " load another.")
                        if (!adLoader!!.isLoading()) {
                            insertAdsInMenuItems()
                        }
                    }
                }).build()

        // Load the Native ads.
        adLoader!!.loadAds(AdRequest.Builder().build(), NUMBER_OF_ADS)
    }

    private fun insertAdsInMenuItems() {
        //commentedAdd
        if (mNativeAds.size <= 0) {
            return
        }
        val offset = adList.size / mNativeAds.size + 1
        var index = 0
        for (ad in mNativeAds) {
            adList.add(index, ad)
            index = index + offset
        }
    }
}
