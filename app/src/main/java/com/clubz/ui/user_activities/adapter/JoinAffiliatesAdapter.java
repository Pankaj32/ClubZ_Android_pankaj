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
import com.clubz.ui.user_activities.model.GetJoinAffliates;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class JoinAffiliatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<GetJoinAffliates.DataBean.AffiliatesBean> affiliatesBeans;

    public JoinAffiliatesAdapter(Context context,/*String type,*/List<GetJoinAffliates.DataBean.AffiliatesBean> affiliatesBeans) {
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
        JoinAffiliatesAdapter.MyViewHolder h = (JoinAffiliatesAdapter.MyViewHolder) holder;
        GetJoinAffliates.DataBean.AffiliatesBean affiliatesBean = affiliatesBeans.get(position);
        h.itemUserName.setText(affiliatesBean.getAffiliate_name());
        if (affiliatesBean.isJoined().equals("1")) {
            // h.itemJoin.setImageResource(R.drawable.active_heart_ico);
            h.itemJoin.setChecked(true);
        } else {
            //   h.itemJoin.setImageResource(R.drawable.inactive_heart_ico);
            h.itemJoin.setChecked(false);
        }
        if (position == 0) h.lineView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return affiliatesBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView item_image;
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
                        GetJoinAffliates.DataBean.AffiliatesBean affiliatesBean = affiliatesBeans.get(getAdapterPosition());
                        if (affiliatesBean.isJoined().equals("1")) {
                            affiliatesBean.setJoined("0");
                        } else {
                            affiliatesBean.setJoined("1");
                        }
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("onClick:", e.getMessage());
                    }
                }
            });
        }
    }

}