package com.clubz.ui.ads.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.clubz.R
import com.clubz.chat.fragments.FragmentChat
import com.clubz.ui.ads.fragment.FragAdsDetails
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.utils.KeyboardUtil
import kotlinx.android.synthetic.main.activity_add_details.*
import okhttp3.internal.Util

class AdDetailsActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    lateinit var adapter: ViewPagerAdapter
    private var adId: String = ""
    private var adTitle: String = ""
    private var clubId: String = ""
    private var clubName: String = ""
    private var adType: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        val bundle = intent.extras
        if (bundle != null) {
            adId = bundle.getString("adId")
            adTitle = bundle.getString("adTitle")
            clubId = bundle.getString("clubId")
            clubName = bundle.getString("clubName")
            adType = bundle.getString("adType")
        }
        headerTxt.text = adTitle
        clubNameTxt.text = clubName
        ivBack.setOnClickListener(this)
        setViewPager(viewPager)
        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)
        KeyboardUtil.hideKeyboard(this)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragAdsDetails.newInstance(adId, adType), resources.getString(R.string.a_activity_first_tab), " This is First")
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
}
