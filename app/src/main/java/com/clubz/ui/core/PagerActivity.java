package com.clubz.ui.core;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by chiranjib on 23/5/18.
 */

public abstract class PagerActivity extends BaseActivity implements PagerActivityHelper{

    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        setViewPagerFragment(adapter);
        getTabLayout().setupWithViewPager(getPager());
        adapter.notifyDataSetChanged();
    }
}
