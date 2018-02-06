package com.clubz.fragment.signup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.clubz.Adapter.MyViewPagerAdapter
import com.clubz.R
import com.clubz.Sign_up_Activity
import kotlinx.android.synthetic.main.frag_sign_up_one.*

/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_one : Fragment(), ViewPager.OnPageChangeListener, View.OnClickListener {


    lateinit var  viewPager : ViewPager;
    lateinit var layouts: IntArray;
    lateinit var lnr_indicator: LinearLayout;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.view_pager);
        lnr_indicator = view.findViewById(R.id.lnr_indicator);
        layouts = intArrayOf(R.layout.welcome_slide1, R.layout.welcome_slide2, R.layout.welcome_slide3, R.layout.welcome_slide4)
        viewPager.setAdapter(MyViewPagerAdapter(activity , layouts));

        viewPager.addOnPageChangeListener(this)
        lnr_indicator.getChildAt(0).setBackgroundResource(R.drawable.indicator_active)
        for( view in arrayOf(sign_up)) view.setOnClickListener(this)
    }
    /**
     *
     */
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        for(i in 0..lnr_indicator.childCount-1){
            lnr_indicator.getChildAt(i).setBackgroundResource(R.drawable.indicator_inactive)
        }
        lnr_indicator.getChildAt(position).setBackgroundResource(R.drawable.indicator_active)

    }

    override fun onClick(p0: View?) {
     when(p0!!.id){
         R.id.sign_up-> (activity as Sign_up_Activity).replaceFragment(Frag_Sign_Up_One_2())
     }
    }
}