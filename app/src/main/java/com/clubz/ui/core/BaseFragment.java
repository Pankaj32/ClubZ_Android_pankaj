package com.clubz.ui.core;

import android.content.Context;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment{

    protected FragmentListner listner;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BaseActivity)
            listner = (FragmentListner) context;
    }

    public interface FragmentListner{
        void hideKeyBoard();
        void onFragBackPress();
        void setTitle();
    }
}
