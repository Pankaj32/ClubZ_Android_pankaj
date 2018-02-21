package com.clubz

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.clubz.Adapter.MyViewPagerAdapter
import kotlinx.android.synthetic.main.actvity_intro_screen.*

/**
 * Created by mindiii on 2/20/18.
 */
class Inro_Activity : AppCompatActivity(), View.OnClickListener  , ViewPager.OnPageChangeListener{



    lateinit var  view_pager :ViewPager;
    lateinit var layouts: IntArray;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actvity_intro_screen)
        view_pager = findViewById(R.id.view_pager)
        layouts = intArrayOf(R.layout.welcome_slide1, R.layout.welcome_slide2, R.layout.welcome_slide3, R.layout.welcome_slide4)
        view_pager.setAdapter(MyViewPagerAdapter(this , layouts ,view_pager ,false));
        for(views in arrayOf(btn_next ,btn_skip)) views.setOnClickListener(this)
        view_pager.addOnPageChangeListener(this)
        lnr_indicator.getChildAt(0).setBackgroundResource(R.drawable.indicator_active)
    }


    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btn_next->{
                if(view_pager.currentItem<layouts.size-1) view_pager.setCurrentItem( view_pager.currentItem+1,true)
                else{
                    val intent = Intent(this@Inro_Activity , Sign_In_Activity::class.java)
                    startActivity(intent);
                    finish();
                }
            }
            R.id.btn_skip->{
                val intent = Intent(this@Inro_Activity , Sign_In_Activity::class.java)
                startActivity(intent);
                finish();
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if(position==layouts.size-1){
            btn_next.setText(R.string.got)
        }
        for(i in 0..lnr_indicator.childCount-1){
            lnr_indicator.getChildAt(i).setBackgroundResource(R.drawable.indicator_inactive)
        }
        lnr_indicator.getChildAt(position).setBackgroundResource(R.drawable.indicator_active)
    }




}