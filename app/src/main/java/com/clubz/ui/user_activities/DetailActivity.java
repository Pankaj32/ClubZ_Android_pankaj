package com.clubz.ui.user_activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.clubz.R;
import com.clubz.ui.user_activities.fragment.Frag_Find_Activities;
import com.clubz.ui.core.PagerActivity;

/**
 * Created by chiranjib on 23/5/18.
 */

public class DetailActivity extends PagerActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_club_detail;
    }

    @Override
    public TabLayout getTabLayout() {
        return findViewById(R.id.tablayout_cd);
    }

    @Override
    public ViewPager getViewPager() {
        return findViewById(R.id.view_pager_cd);
    }

    @Override
    public void setViewPagerFragment(PagerActivity.ViewPagerAdapter adapter) {
        adapter.addFragment(new Frag_Find_Activities(), getResources().getString(R.string.t_detils));
        adapter.addFragment(new Frag_Find_Activities(), getResources().getString(R.string.t_detils));
    }

}
