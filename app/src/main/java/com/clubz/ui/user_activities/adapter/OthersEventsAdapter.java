package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clubz.R;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class OthersEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<GetOthersActivitiesResponce.DataBean.OthersBean.EventsBeanXXX> othersEventsBeans;

    public OthersEventsAdapter(Context context,/*String type,*/List<GetOthersActivitiesResponce.DataBean.OthersBean.EventsBeanXXX> othersEventsBeans) {
        this.context = context;
        // this.mType = type;
        this.othersEventsBeans = othersEventsBeans;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_event_items_layout_copy, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OthersEventsAdapter.MyViewHolder h = (OthersEventsAdapter.MyViewHolder) holder;
        GetOthersActivitiesResponce.DataBean.OthersBean.EventsBeanXXX eventsBean = othersEventsBeans.get(position);
    }

    @Override
    public int getItemCount() {
        return othersEventsBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        /*private ImageView itemJoin, item_image;
        private TextView itemUserName;*/

        public MyViewHolder(View itemView) {
            super(itemView);
            /*itemJoin = itemView.findViewById(R.id.itemJoin);
            item_image = itemView.findViewById(R.id.item_image);
            itemUserName = itemView.findViewById(R.id.itemUserName);
            item_image = itemView.findViewById(R.id.item_image);*//*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetJoinAffliates.DataBean.AffiliatesBean affiliatesBean = affiliatesBeans.get(getAdapterPosition());
                    if (affiliatesBean.isJoined().equals("1")) {
                        affiliatesBean.setJoined("0");
                    } else {
                        affiliatesBean.setJoined("1");
                    }
                    notifyDataSetChanged();
                }
            });*/
        }
    }

}