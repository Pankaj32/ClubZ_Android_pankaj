package com.clubz.ui.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;


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
        getTabLayout().setupWithViewPager(getViewPager());
        adapter.notifyDataSetChanged();
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private ArrayList<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        public void clearAdapter(){
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public void addFragment(Fragment fragment, String title, String menu){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void setTile(int position,Fragment fragment, String title) {
            mFragmentList.set(position, fragment);
            mFragmentTitleList.set(position, title);
        }
    }
}
