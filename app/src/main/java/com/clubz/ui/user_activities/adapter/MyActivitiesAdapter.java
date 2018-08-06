package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ActivityItemClickListioner;
import com.clubz.ui.user_activities.listioner.EventItemClickListioner;
import com.clubz.ui.user_activities.model.GetMyactivitiesResponce;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;
import com.clubz.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class MyActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<GetMyactivitiesResponce.DataBean> myActivityBeans;
    private ActivityItemClickListioner activityItemClickListioner;

    public MyActivitiesAdapter(Context context, List<GetMyactivitiesResponce.DataBean> myActivityBeans, ActivityItemClickListioner activityItemClickListioner) {
        this.context = context;
        // this.mType = type;
        this.myActivityBeans = myActivityBeans;
        this.activityItemClickListioner = activityItemClickListioner;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_activities_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyActivitiesAdapter.MyViewHolder h = (MyActivitiesAdapter.MyViewHolder) holder;
        GetMyactivitiesResponce.DataBean activity = myActivityBeans.get(position);
        if (!TextUtils.isEmpty(activity.getImage())) {
            Picasso.with(h.activityImge.getContext()).load(activity.getImage()).fit().placeholder(R.drawable.new_app_icon).into(h.activityImge);
        }
        h.activityName.setText(activity.getActivityName());
        h.clubName.setText(activity.getClub_name());
        if (activity.getVisible()) {
            h.eventRecycler.setVisibility(View.VISIBLE);
            h.lineView.setVisibility(View.VISIBLE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_up_arrow);
        } else {
            h.eventRecycler.setVisibility(View.GONE);
            h.lineView.setVisibility(View.GONE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_down_arrow);
        }
        h.eventRecycler.setAdapter(new MyEventsAdapter(context, activity.getEvents(), new EventItemClickListioner() {
            @Override
            public void onConfirm(int eventPosition) {
                Log.d("TAG", "onConfirm: ");
               // activityItemClickListioner.onConfirm("", position, eventPosition);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return myActivityBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView eventRecycler;
        private ImageView iv_arrow_expand,itemLike,activityImge;
        private View lineView;
        private FrameLayout likeLay;
        private TextView activityName,clubName;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventRecycler = itemView.findViewById(R.id.eventRecycler);
            iv_arrow_expand = itemView.findViewById(R.id.iv_arrow_expand);
            itemLike = itemView.findViewById(R.id.itemLike);
            lineView = itemView.findViewById(R.id.lineView);
            likeLay = itemView.findViewById(R.id.likeLay);
            activityImge = itemView.findViewById(R.id.activityImge);
            activityName = itemView.findViewById(R.id.activityName);
            clubName = itemView.findViewById(R.id.clubName);
            likeLay.setVisibility(View.GONE);

            iv_arrow_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetMyactivitiesResponce.DataBean activity = myActivityBeans.get(getAdapterPosition());
                    if (activity.getVisible()) {
                        activity.setVisible(false);
                    } else {
                        activity.setVisible(true);
                    }
                    Util.Companion.setRotation(iv_arrow_expand, activity.getVisible());
                    notifyItemChanged(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityItemClickListioner.onItemClick("today", getAdapterPosition());
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