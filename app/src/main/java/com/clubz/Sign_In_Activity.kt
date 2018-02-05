package com.clubz

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_signin.*


/**
 * Created by mindiii on 2/2/18.
 */
class Sign_In_Activity : AppCompatActivity(), ViewPager.OnPageChangeListener, View.OnClickListener {



    lateinit var  viewPager : ViewPager ;
    lateinit var layouts: IntArray;
    lateinit var lnr_indicator: LinearLayout;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin);
        viewPager = findViewById(R.id.view_pager);
        lnr_indicator = findViewById(R.id.lnr_indicator);
        layouts = intArrayOf(R.layout.welcome_slide1, R.layout.welcome_slide2, R.layout.welcome_slide3, R.layout.welcome_slide4)
        viewPager.setAdapter(MyViewPagerAdapter())

        viewPager.addOnPageChangeListener(this)
        lnr_indicator.getChildAt(0).setBackgroundResource(R.drawable.indicator_active)


        for(view in arrayOf(sign_up))view.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.sign_up->startActivity(Intent(this@Sign_In_Activity,Sign_up_Activity::class.java))
        }
    }

    /**
     * View pager adapter
     */
    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = layoutInflater!!.inflate(layouts[position], container, false)
            container.addView(view)

            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
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


}