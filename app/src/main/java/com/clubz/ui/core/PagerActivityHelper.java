package com.clubz.ui.core;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

/**
 * Created by chiranjib on 23/5/18.
 */

public interface PagerActivityHelper {

    int getLayout();
    TabLayout getTabLayout();
    ViewPager getViewPager();
    void setViewPagerFragment(PagerActivity.ViewPagerAdapter adapter);
}
