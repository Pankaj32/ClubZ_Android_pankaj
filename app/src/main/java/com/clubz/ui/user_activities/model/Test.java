package com.clubz.ui.user_activities.model;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chiranjib on 22/5/18.
 */

public class Test extends Fragment {


    public Test(){

    }

    public static Test newInstance() {

        Bundle args = new Bundle();

        Test fragment = new Test();
        fragment.setArguments(args);
        return fragment;
    }
}
