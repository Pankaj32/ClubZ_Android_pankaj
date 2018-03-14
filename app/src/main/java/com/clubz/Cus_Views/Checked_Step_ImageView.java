package com.clubz.Cus_Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.clubz.R;

/**
 * Created by mindiii on 2/22/18.
 */

public class Checked_Step_ImageView  extends RelativeLayout {

    Context context;
    AppCompatImageView step_;

    public Checked_Step_ImageView(@NonNull Context context) {
        super(context);
        this.context = context;
        initiateview();
    }

    void initiateview() {
        View view = inflate(context, R.layout.z_step_image_view, this);
        step_ = view.findViewById(R.id.step_);
        step_.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
