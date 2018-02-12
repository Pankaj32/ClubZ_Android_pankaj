package com.clubz.Adapter

import android.app.Activity
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.Scroller
import java.lang.reflect.Field
import java.util.*

/**
 * Created by mindiii on 2/5/18.
 */
class MyViewPagerAdapter(val  activity :Activity , val layouts : IntArray , val viewPager :ViewPager) : PagerAdapter() {
    val timer = Timer();
    private var layoutInflater: LayoutInflater? = null

    init {
        timerauto()
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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

    fun timerauto(){
        timer.scheduleAtFixedRate(object : TimerTask() {

            override fun run() {
                activity.runOnUiThread(Runnable {

                    try {

                       viewPager.setCurrentItem(if(viewPager.currentItem>=3) 0 else viewPager.currentItem+1,true)
                    } catch (e: Exception) {

                    }
                })
            }

        },
                //Set how long before to start calling the TimerTask (in milliseconds)
                3000,
                //Set the amount of time between each execution (in milliseconds)
                4000)


        try {
            val mScroller: Field
            mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.setAccessible(true)
            val scroller = FixedSpeedScroller(viewPager.getContext())
            // scroller.setFixedDuration(5000);
            mScroller.set(viewPager, scroller)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }
    }


    inner class FixedSpeedScroller : Scroller {

        private val mDuration = 3000

        constructor(context: Context) : super(context) {}

        constructor(context: Context, interpolator: Interpolator) : super(context, interpolator) {}

        constructor(context: Context, interpolator: Interpolator, flywheel: Boolean) : super(context, interpolator, flywheel) {}


        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }
    }



}