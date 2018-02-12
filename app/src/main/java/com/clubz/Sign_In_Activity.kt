package com.clubz

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.clubz.Adapter.MyViewPagerAdapter
import com.clubz.util.Util
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_signin.*


/**
 * Created by mindiii on 2/2/18.
 */
class Sign_In_Activity : AppCompatActivity(), ViewPager.OnPageChangeListener, View.OnClickListener {



    lateinit var  viewPager : ViewPager ;
    lateinit var layouts: IntArray;
    lateinit var lnr_indicator: LinearLayout;
    var isvalidate: Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin);
        Util.checklaunage(this)
        viewPager = findViewById(R.id.view_pager);
        lnr_indicator = findViewById(R.id.lnr_indicator);
        layouts = intArrayOf(R.layout.welcome_slide1, R.layout.welcome_slide2, R.layout.welcome_slide3, R.layout.welcome_slide4)
        viewPager.setAdapter(MyViewPagerAdapter(this@Sign_In_Activity , layouts , viewPager))

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

    fun verfiy() :Boolean{

        return true;
    }
    private fun checkPhoneNumber( countryCode : String) {
        val contactNo = phone_no.getText().toString()
        try {
            val phoneUtil = PhoneNumberUtil.createInstance(this)
            if (countryCode != null) {
                val code = countryCode.toUpperCase()
                val swissNumberProto = phoneUtil.parse(contactNo, code)
                isvalidate = phoneUtil.isValidNumber(swissNumberProto)
            }
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: " + e.toString())
        }

    }



}