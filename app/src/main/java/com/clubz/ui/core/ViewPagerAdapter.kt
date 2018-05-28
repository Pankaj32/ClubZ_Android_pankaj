package com.clubz.ui.core

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun addFragment(fragment: Fragment, title: String, menu: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    //This is The method which is Overrided by us
    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }

    fun setTile(position: Int, fragment: Fragment, title: String) {
        mFragmentList[position] = fragment
        mFragmentTitleList[position] = title
    }
}