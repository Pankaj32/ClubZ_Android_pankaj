package com.clubz.ui.cv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubz.R;

/**
 * Created by mindiii on 2/22/18.
 */

public class Checked_Step_TextView extends RelativeLayout {

    Context context;
    TextView step_;

    public Checked_Step_TextView(@NonNull Context context) {
        super(context);
        this.context = context;
        initiateview();
    }

    void initiateview() {
        View view = inflate(context, R.layout.z_step_text, this);
        step_ = view.findViewById(R.id.step_);
    }
    public Checked_Step_TextView setText(int i){
        step_.setText(i+"");
        return this;
    }
}
