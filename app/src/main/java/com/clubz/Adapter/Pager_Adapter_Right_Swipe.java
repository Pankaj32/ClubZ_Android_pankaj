package com.clubz.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.clubz.R;

/**
 * Created by mindiii on १२/४/१८.
 */

public class Pager_Adapter_Right_Swipe extends PagerAdapter {

    Activity context;

    public Pager_Adapter_Right_Swipe(Activity context) {
        this.context = context;
    }

    public Object instantiateItem(ViewGroup collection, int position) {

        int resId = 0;
        /*switch (position) {
            case 0:
                resId = R.id.page_one;
                break;
            case 1:
                resId = R.id.page_two;
                break;
        }*/
        View view = context.findViewById(resId);
        view.setOnClickListener(null);
        return view;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }



}
