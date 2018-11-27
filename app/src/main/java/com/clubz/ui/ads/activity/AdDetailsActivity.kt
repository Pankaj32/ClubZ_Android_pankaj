package com.clubz.ui.ads.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.android.volley.VolleyError
import com.clubz.R
import com.clubz.chat.fragments.FragmentChat
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.helper.fcm.NotificatioKeyUtil
import com.clubz.ui.ads.fragment.FragAdsDetails
import com.clubz.ui.ads.model.AddDetailsBean
import com.clubz.ui.ads.model.AdsListBean
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.cv.CusDialogProg
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_details.*
import org.json.JSONObject

/*import okhttp3.internal.Util*/

class AdDetailsActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    lateinit var adapter: ViewPagerAdapter
    private var adId: String = ""
    private var adTitle: String = ""
    private var clubId: String = ""
    private var clubName: String = ""
    private var adType: String = ""
    private var from: String = ""
    private var adBean: AdsListBean.DataBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        val bundle = intent.extras
        if (bundle != null) {
            try {
                from = bundle.getString(NotificatioKeyUtil.Key_From)
            }catch (e:Exception){
                from=NotificatioKeyUtil.Value_From_Notification
            }

            if(from.equals(NotificatioKeyUtil.Value_From_Notification)){
                val adsId=bundle.getString(NotificatioKeyUtil.Key_Ads_Id)
                getAdDetails(adsId)
            }else{
                adBean = bundle.getParcelable("adBean")
               /* adType = bundle.getString("adType")*/
                adId = adBean!!.adId!!
                adTitle = adBean!!.title!!
                clubId = adBean!!.club_id!!
                clubName = adBean!!.club_name!!
                setViewPager(viewPager)
            }

        }
        headerTxt.text = adTitle
        clubNameTxt.text = clubName
        ivBack.setOnClickListener(this)
        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)
        KeyboardUtil.hideKeyboard(this)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragAdsDetails.newInstance(adBean, adType), resources.getString(R.string.a_activity_first_tab), " This is First")
        adapter.addFragment(FragmentChat.newInstanceAdChat(adId, clubId, adTitle), resources.getString(R.string.a_activity_snd_tab), " This is second")
        viewPager.adapter = adapter
        //Chiranjib
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        KeyboardUtil.hideKeyboard(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    private fun getAdDetails(adsId:String) {
        val dialogProgress = CusDialogProg(this@AdDetailsActivity)
        dialogProgress.show()

        object : VolleyGetPost(this, this,
                "${WebService.getAdsDetails}?adId=${adsId}",
                true,true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialogProgress.dismiss()
                       val adDetails = Gson().fromJson(response, AddDetailsBean::class.java)
                        setdata(adDetails.data,adDetails.dateTime)
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
        }.execute(AdDetailsActivity::class.java.name)
    }

    private fun setdata(adDetails: AddDetailsBean.DataBean?, dateTime: String?) {
        adBean=AdsListBean.DataBean()
        adBean!!.adId=adDetails?.adId
        adBean!!.title=adDetails?.title
        adBean!!.fee=adDetails?.fee
        adBean!!.is_renew=adDetails?.is_renew
        adBean!!.description=adDetails?.description
        adBean!!.club_id=adDetails?.clubId
        adBean!!.user_id=adDetails?.user_id
        adBean!!.creator_phone=adDetails?.creator_phone
        adBean!!.contact_no_visibility==adDetails?.contact_no_visibility
        adBean!!.user_role=adDetails?.user_role
        adBean!!.crd=adDetails?.created
        adBean!!.image=adDetails?.image
        adBean!!.profile_image=adDetails?.creator_profile_image
        adBean!!.club_name=adDetails?.club_name
        adBean!!.full_name=adDetails?.creator_name
      //  adBean!!.isFav=adDetails?.is
        adBean!!.currentDatetime=dateTime
       // adBean!!.is_my_ads=adDetails?.
        adBean!!.is_renew=adDetails?.is_renew
      //  adBean!!.expire_ads: String
        adBean!!.total_likes=adDetails?.total_likes

        adId = adBean!!.adId!!
        adTitle = adBean!!.title!!
        clubId = adBean!!.club_id!!
        clubName = adBean!!.club_name!!

        setViewPager(viewPager)
        headerTxt.text = adTitle
        clubNameTxt.text = clubName
    }
}
