package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.EventItemClickListioner;
import com.clubz.ui.user_activities.model.GetJoinAffliates;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class TodayEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<GetOthersActivitiesResponce.DataBean.TodayBean.EventsBean> todayEventsBeans;
    private EventItemClickListioner eventItemClickListioner;

    public TodayEventsAdapter(Context context,/*String type,*/List<GetOthersActivitiesResponce.DataBean.TodayBean.EventsBean> todayEventsBeans, EventItemClickListioner eventItemClickListioner) {
        this.context = context;
        // this.mType = type;
        this.todayEventsBeans = todayEventsBeans;
        this.eventItemClickListioner = eventItemClickListioner;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_event_items_layout_copy, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TodayEventsAdapter.MyViewHolder h = (TodayEventsAdapter.MyViewHolder) holder;
        GetOthersActivitiesResponce.DataBean.TodayBean.EventsBean eventsBean = todayEventsBeans.get(position);
        h.eventDate.setText(eventsBean.getDate());
        h.eventTime.setText(eventsBean.getTime());
        if (!TextUtils.isEmpty(eventsBean.getDescription())) {
            h.eventDesc.setText(eventsBean.getDescription());
        } else {
            h.eventDesc.setText(eventsBean.getEvent_title());
        }
        h.eventPogress.setProgress(Math.round(Integer.parseInt(eventsBean.getJoined_users()) * 100 / Integer.parseInt(eventsBean.getTotal_users())));
        if (eventsBean.is_confirm().equals("0")){
            h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.nav_gray));
            h.confirmTxt.setVisibility(View.GONE);
        }else if (eventsBean.is_confirm().equals("1")){
            h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.primaryColor));
            h.confirmTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return todayEventsBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgCheck;
        private TextView eventDate, eventTime, eventDesc,confirmTxt;
        private ProgressBar eventPogress;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgCheck = itemView.findViewById(R.id.imgCheck);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventDesc = itemView.findViewById(R.id.eventDesc);
            confirmTxt = itemView.findViewById(R.id.confirmTxt);
            eventPogress = itemView.findViewById(R.id.eventPogress);
            imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventItemClickListioner.onConfirm(getAdapterPosition());
                }
            });
        }
    }

}