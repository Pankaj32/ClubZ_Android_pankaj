package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clubz.R;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class AffiliatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
/*    private List<ServiceDetailsResponce.MyPromoCodeBean> promoCodeBeans;*/

    public AffiliatesAdapter(Context context/*, List<ServiceDetailsResponce.MyPromoCodeBean> promoCodeBeans*/,String type) {
        this.context = context;
        this.mType = type;
      //  this.promoCodeBeans = promoCodeBeans;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_affiliate, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AffiliatesAdapter.MyViewHolder h = (AffiliatesAdapter.MyViewHolder) holder;
        /*ServiceDetailsResponce.MyPromoCodeBean dataBean = promoCodeBeans.get(position);
        AffiliatesAdapter.MyViewHolder h = (AffiliatesAdapter.MyViewHolder) holder;
        h.itemCpneTxt.setText(dataBean.getTitle());
        if (dataBean.isSelected()) {
            h.mainLayout.setBackgroundResource(R.drawable.edittxt_fill_bg);
            h.itemCpneTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else if (!dataBean.isSelected()) {
            h.mainLayout.setBackgroundResource(R.drawable.edittxt_bg);
            h.itemCpneTxt.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));

        }*/
        if (mType.equals("hand")){
            h.itemJoin.setImageResource(R.drawable.ic_inactive_hand_ico);
        }else {
            h.itemJoin.setImageResource(R.drawable.inactive_heart_ico);
        }

    }

    @Override
    public int getItemCount() {
        /*return promoCodeBeans.size();*/
        return 5;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mainLayout;
        private TextView itemCpneTxt;
        private ImageView itemJoin;

        public MyViewHolder(View itemView) {
            super(itemView);
            /*mainLayout = (LinearLayout) itemView.findViewById(R.id.mainLayout);
            itemCpneTxt = (TextView) itemView.findViewById(R.id.itemCpneTxt);*/
            itemJoin = itemView.findViewById(R.id.itemJoin);
        }
    }

}