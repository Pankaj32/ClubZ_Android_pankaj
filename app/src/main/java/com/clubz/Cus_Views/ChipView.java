package com.clubz.Cus_Views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubz.R;
import com.loopeer.shadow.ShadowView;


/**
 * Created by mindiii on 9/5/17.
 */

public abstract class ChipView extends RelativeLayout implements View.OnClickListener {
    public boolean addAsotherView;
    Context context;
    TextView label;
    ImageView delete_button;
    String Id;

    // ChipDeleteListner chipDeleteListner;
    public ChipView(Context context, String Id) {
        super(context);
        this.context = context;
        this.Id = Id;
        initiateview();
    }


    void initiateview() {
        View view = inflate(context, R.layout.z_cus_chip_view, this);
        delete_button = (ImageView) view.findViewById(R.id.delete_button);
        delete_button.setVisibility(VISIBLE);
        label = (TextView) view.findViewById(R.id.label);
        delete_button.setOnClickListener(this);
        ((ShadowView) view.findViewById(R.id.shadow_view)).setShadowDy( context.getResources().getDimension(R.dimen._3sdp));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_button:
                setDeleteListner(this);
                // if(chipDeleteListner != null) setOnDeleteListner(chipDeleteListner);
                ((FlowLayout) this.getParent()).removeView(this);
                break;

        }
    }

    public void setText(String text) {
        label.setText(text);
        /*System.out.println();*/
    }

    public String getText(){
        return label.getText().toString();
    }

    public TextView getTextView() {
        return label;
    }

    public abstract void setDeleteListner(ChipView chipView);

    public String getIdChip() {
        return Id;
    }

    public ChipView hideDelete(){
        delete_button.setVisibility(GONE);
        return this;
    }
}
