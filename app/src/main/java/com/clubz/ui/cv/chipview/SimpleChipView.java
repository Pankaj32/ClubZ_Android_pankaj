package com.clubz.ui.cv.chipview;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubz.R;

public class SimpleChipView extends RelativeLayout implements View.OnClickListener{

    Context context;
    TextView label;
    ImageView delete_button;
    private boolean isDeleteBtnEnable;

    public SimpleChipView(Context context) {
        super(context);
        this.context = context;
        initiateview();
    }

    void initiateview() {
        View view = inflate(context, R.layout.z_simple_chip_view, this);
        delete_button =  view.findViewById(R.id.delete_button);
        delete_button.setVisibility(isDeleteBtnEnable?VISIBLE:GONE);
        label =  view.findViewById(R.id.label);
        delete_button.setOnClickListener(this);
       // ((ShadowView) view.findViewById(R.id.shadow_view)).setShadowDy( context.getResources().getDimension(R.dimen._3sdp));

    }

    @Override
    public void onClick(View v) {

    }


    public void setText(String str) {
        this.label.setText(str);
    }

    public void setTextColor(@ColorRes int id) {
        this.label.setTextColor(getResources().getColor(id));
    }
}
