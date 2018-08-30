package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.model.GetConfirmAffiliates;
import com.clubz.ui.user_activities.model.GetJoinAffliates;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class ConfirmAffiliatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<GetConfirmAffiliates.DataBean.AffiliatesBean> affiliatesBeans;

    public ConfirmAffiliatesAdapter(Context context,/*String type,*/List<GetConfirmAffiliates.DataBean.AffiliatesBean> affiliatesBeans) {
        this.context = context;
        // this.mType = type;
        this.affiliatesBeans = affiliatesBeans;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_affiliate, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConfirmAffiliatesAdapter.MyViewHolder h = (ConfirmAffiliatesAdapter.MyViewHolder) holder;
        GetConfirmAffiliates.DataBean.AffiliatesBean affiliatesBean = affiliatesBeans.get(position);
        h.itemUserName.setText(affiliatesBean.getAffiliate_name());
        if (affiliatesBean.isConfirmed().equals("1")) {
           // h.itemJoin.setImageResource(R.drawable.hand_ico);
            h.itemJoin.setChecked(true);
        } else {
         //   h.itemJoin.setImageResource(R.drawable.ic_inactive_hand_ico);
            h.itemJoin.setChecked(false);
        }
        if (position == 0) h.lineView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return affiliatesBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView  item_image;
        private TextView itemUserName;
        private CheckBox itemJoin;
        private View lineView;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemJoin = itemView.findViewById(R.id.itemJoin);
            item_image = itemView.findViewById(R.id.item_image);
            itemUserName = itemView.findViewById(R.id.itemUserName);
            item_image = itemView.findViewById(R.id.item_image);
            lineView = itemView.findViewById(R.id.lineView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                    GetConfirmAffiliates.DataBean.AffiliatesBean affiliatesBean = affiliatesBeans.get(getAdapterPosition());
                    if (affiliatesBean.isConfirmed().equals("1")) {
                        affiliatesBean.setConfirmed("0");
                    } else {
                        affiliatesBean.setConfirmed("1");
                    }
                    notifyDataSetChanged();
                }catch (Exception e){
                    Log.e("onClick:",e.getMessage());
                }
                }
            });
        }
    }

}