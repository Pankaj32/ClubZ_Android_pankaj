package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ActivityItemClickListioner;
import com.clubz.ui.user_activities.listioner.EventItemClickListioner;
import com.clubz.ui.user_activities.model.ActivitiesBean;
import com.clubz.ui.user_activities.model.AllActivitiesBean;
import com.clubz.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class ActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<ActivitiesBean.DataBean> activityBeans;
    private ActivityItemClickListioner activityItemClickListioner;

    public ActivitiesAdapter(Context context, List<ActivitiesBean.DataBean> activityBeans, ActivityItemClickListioner activityItemClickListioner) {
        this.context = context;
        // this.mType = type;
        this.activityBeans = activityBeans;
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
        final ActivitiesAdapter.MyViewHolder h = (ActivitiesAdapter.MyViewHolder) holder;
        ActivitiesBean.DataBean dataBean = activityBeans.get(position);

        if (!TextUtils.isEmpty(dataBean.getImage())) {
            Picasso.with(h.activityImge.getContext()).load(dataBean.getImage()).fit().placeholder(R.drawable.new_app_icon).into(h.activityImge);
        } else {
            h.activityImge.setImageResource(R.drawable.new_app_icon);
        }
        h.activityName.setText(dataBean.getActivityName());
        h.clubName.setText(dataBean.getClub_name());

        if (dataBean.getVisible()) {
            h.eventRecycler.setVisibility(View.VISIBLE);
            h.lineView.setVisibility(View.VISIBLE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_up_arrow);
        } else {
            h.eventRecycler.setVisibility(View.GONE);
            h.lineView.setVisibility(View.GONE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_down_arrow);
        }

        if (dataBean.getListType().equals("Others")) {
            h.likeLay.setVisibility(View.GONE);
            h.activityType.setText("Not scheduled");
        } else {
            h.activityType.setText(Util.Companion.toSentenceCase(dataBean.getListType()));
        }
        if (dataBean.is_my_activity().equals("1")) {
            h.likeLay.setVisibility(View.VISIBLE);
            h.itemLike.setImageResource(R.drawable.ic_cards_heart_active);
        } else {
            if (dataBean.is_like().equals("1")) {
                h.likeLay.setVisibility(View.VISIBLE);
                h.itemLike.setImageResource(R.drawable.ic_cards_heart_active);
            } else {
                h.likeLay.setVisibility(View.GONE);
            }
        }
        if (dataBean.getEvents() == null) {
            h.iv_arrow_expand.setVisibility(View.GONE);
        } else {
            if (dataBean.getEvents().size() == 0) {
                h.iv_arrow_expand.setVisibility(View.GONE);
            } else {
                h.iv_arrow_expand.setVisibility(View.VISIBLE);
            }
        }
        if (dataBean.getListType().equals("Today")) {
            h.activityType.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
        } else {
            h.activityType.setTextColor(ContextCompat.getColor(context, R.color.sixty_black));
        }
        if (dataBean.getEvents() != null) {
            h.eventRecycler.setAdapter(new ActivitiesEventsAdapter(context, dataBean.getEvents(), new EventItemClickListioner() {
                @Override
                public void onDateClick(int eventPosition) {
                    activityItemClickListioner.onEventDateClick(h.getAdapterPosition(), eventPosition);
                }

                @Override
                public void onConfirm(int eventPosition) {
                    activityItemClickListioner.onConfirm("today", h.getAdapterPosition(), eventPosition);
                }
            }));
        }
    }

    @Override
    public int getItemCount() {
        return activityBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView eventRecycler;
        private ImageView iv_arrow_expand, itemLike, activityImge;
        private View lineView;
        private FrameLayout likeLay;
        private RelativeLayout arrowLay;
        private TextView activityName, clubName, activityType;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventRecycler = itemView.findViewById(R.id.eventRecycler);
            iv_arrow_expand = itemView.findViewById(R.id.iv_arrow_expand);
            itemLike = itemView.findViewById(R.id.itemLike);
            lineView = itemView.findViewById(R.id.lineView);
            activityImge = itemView.findViewById(R.id.activityImge);
            activityName = itemView.findViewById(R.id.activityName);
            clubName = itemView.findViewById(R.id.clubName);
            activityType = itemView.findViewById(R.id.activityType);
            likeLay = itemView.findViewById(R.id.likeLay);
            arrowLay = itemView.findViewById(R.id.arrowLay);

            arrowLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivitiesBean.DataBean dataBean = activityBeans.get(getAdapterPosition());
                    if (dataBean.getVisible()) {
                        dataBean.setVisible(false);
                    } else {
                        dataBean.setVisible(true);
                    }
                    Util.Companion.setRotation(iv_arrow_expand, dataBean.getVisible());
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