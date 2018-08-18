package com.clubz.ui.user_activities.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.EventItemClickListioner;
import com.clubz.ui.user_activities.model.AllActivitiesBean;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class AllActivitiesEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private ArrayList<AllActivitiesBean.DataBean.EventsBean> eventsBeans;
    private EventItemClickListioner eventItemClickListioner;

    public AllActivitiesEventsAdapter(Context context,/*String type,*/ArrayList<AllActivitiesBean.DataBean.EventsBean> eventsBeans, EventItemClickListioner eventItemClickListioner) {
        this.context = context;
        // this.mType = type;
        this.eventsBeans = eventsBeans;
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
        AllActivitiesEventsAdapter.MyViewHolder h = (AllActivitiesEventsAdapter.MyViewHolder) holder;
        AllActivitiesBean.DataBean.EventsBean eventsBean = eventsBeans.get(position);
        h.eventDate.setText(eventsBean.getEvent_date());
        h.eventTime.setText(eventsBean.getEvent_time());
        if (!TextUtils.isEmpty(eventsBean.getDescription())) {
            h.eventDesc.setText(eventsBean.getDescription());
        } else {
            h.eventDesc.setText(eventsBean.getEvent_title());
        }
        try {
            h.eventPogress.setProgress(Math.round(Integer.parseInt(eventsBean.getJoined_users()) * 100 / Integer.parseInt(eventsBean.getTotal_users())));
        } catch (ArithmeticException e) {
            Log.e("onBindViewHolder: ", e.getMessage());
        }
        if (eventsBean.is_confirm().equals("0")) {
            h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.nav_gray));
            //h.confirmTxt.setVisibility(View.GONE);
            h.confirmTxt.setText("");
        } else if (eventsBean.is_confirm().equals("1")) {
            h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.primaryColor));
           // h.confirmTxt.setVisibility(View.VISIBLE);
            h.confirmTxt.setText("Confirmed");
        }
    }

    @Override
    public int getItemCount() {
        return eventsBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgCheck;
        private TextView eventDate, eventTime, eventDesc, confirmTxt;
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