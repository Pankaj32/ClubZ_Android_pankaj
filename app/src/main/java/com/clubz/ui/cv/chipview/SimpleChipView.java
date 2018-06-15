package com.clubz.ui.cv.chipview;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubz.R;

public class SimpleChipView extends RelativeLayout {

    private Context context;
    private TextView label;
    private boolean isDeleteBtnEnable;
    private Listner listner;

    public interface Listner{
        void onDeleteBtnClick(SimpleChipView simpleChipView);
    }

    public SimpleChipView(Context context, boolean isDeleteBtnEnable) {
        super(context);
        this.context = context;
        this.isDeleteBtnEnable = isDeleteBtnEnable;
        initiateview();
    }

    public void setListner(Listner listner) {
        this.listner = listner;
    }

    void initiateview() {
        View view = inflate(context, R.layout.z_simple_chip_view, this);
        ImageView delete_button = view.findViewById(R.id.delete_button);
        delete_button.setVisibility(isDeleteBtnEnable?VISIBLE:GONE);
        label =  view.findViewById(R.id.label);
        delete_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              if(listner!=null) listner.onDeleteBtnClick(SimpleChipView.this);
            }
        });
       // ((ShadowView) view.findViewById(R.id.shadow_view)).setShadowDy( context.getResources().getDimension(R.dimen._3sdp));

    }

    public void setText(String str) {
        this.label.setText(str);
    }

    public void setTextColor(@ColorRes int id) {
        this.label.setTextColor(getResources().getColor(id));
    }

    public String getTag() {
        return this.label.getText().toString();
    }
}
