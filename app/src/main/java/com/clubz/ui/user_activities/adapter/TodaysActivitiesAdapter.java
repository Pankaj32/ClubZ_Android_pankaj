package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ActivityItemClickListioner;
import com.clubz.ui.user_activities.listioner.EventItemClickListioner;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;
import com.clubz.utils.Util;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class TodaysActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<GetOthersActivitiesResponce.DataBean.TodayBean> todayActivityBeans;
    private ActivityItemClickListioner activityItemClickListioner;

    public TodaysActivitiesAdapter(Context context, List<GetOthersActivitiesResponce.DataBean.TodayBean> todayActivityBeans, ActivityItemClickListioner activityItemClickListioner) {
        this.context = context;
        // this.mType = type;
        this.todayActivityBeans = todayActivityBeans;
        this.activityItemClickListioner = activityItemClickListioner;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_items_layout_copy, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TodaysActivitiesAdapter.MyViewHolder h = (TodaysActivitiesAdapter.MyViewHolder) holder;
        GetOthersActivitiesResponce.DataBean.TodayBean todayBean = todayActivityBeans.get(position);

        if (!TextUtils.isEmpty(todayBean.getImage())) {
            Picasso.with(h.activityImge.getContext()).load(todayBean.getImage()).fit().placeholder(R.drawable.new_app_icon).into(h.activityImge);
        }
        h.activityName.setText(todayBean.getActivityName());
        h.clubName.setText(todayBean.getClub_name());

        if (todayBean.getVisible()) {
            h.eventRecycler.setVisibility(View.VISIBLE);
            h.lineView.setVisibility(View.VISIBLE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_up_arrow);
        } else {
            h.eventRecycler.setVisibility(View.GONE);
            h.lineView.setVisibility(View.GONE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_down_arrow);
        }
        h.eventRecycler.setAdapter(new TodayEventsAdapter(context, todayBean.getEvents(), new EventItemClickListioner() {
            @Override
            public void onConfirm(int eventPosition) {
                activityItemClickListioner.onConfirm("today", position, eventPosition);
            }
        }));
        if (todayBean.is_like().equals("1")) {
            h.itemLike.setImageResource(R.drawable.active_heart_ico);
        } else {
            h.itemLike.setImageResource(R.drawable.inactive_heart_ico);
        }
        if (todayBean.getEvents()==null){
            h.iv_arrow_expand.setVisibility(View.GONE);
        }else {
            if (todayBean.getEvents().size()==0)h.iv_arrow_expand.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return todayActivityBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView eventRecycler;
        private ImageView iv_arrow_expand,itemLike,activityImge;
        private View lineView;
        private TextView activityName,clubName;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventRecycler = itemView.findViewById(R.id.eventRecycler);
            iv_arrow_expand = itemView.findViewById(R.id.iv_arrow_expand);
            itemLike = itemView.findViewById(R.id.itemLike);
            lineView = itemView.findViewById(R.id.lineView);
            activityImge = itemView.findViewById(R.id.activityImge);
            activityName = itemView.findViewById(R.id.activityName);
            clubName = itemView.findViewById(R.id.clubName);
            iv_arrow_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetOthersActivitiesResponce.DataBean.TodayBean todayBean = todayActivityBeans.get(getAdapterPosition());
                    if (todayBean.getVisible()) {
                        todayBean.setVisible(false);
                    } else {
                        todayBean.setVisible(true);
                    }
                    Util.Companion.setRotation(iv_arrow_expand, todayBean.getVisible());
                    notifyItemChanged(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityItemClickListioner.onItemClick("today", getAdapterPosition());
                }
            });
            itemLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityItemClickListioner.onJoinClick("today", getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    activityItemClickListioner.onLongPress("today", getAdapterPosition());
                    return false;
                }
            });
        }
    }

}