package com.clubz.ui.user_activities.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.EventItemClickListioner;
import com.clubz.ui.user_activities.model.ActivitiesBean;
import com.clubz.ui.user_activities.model.AllActivitiesBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class ActivitiesEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<ActivitiesBean.DataBean.EventsBean> eventsBeans;
    private EventItemClickListioner eventItemClickListioner;

    public ActivitiesEventsAdapter(Context context, List<ActivitiesBean.DataBean.EventsBean> eventsBeans, EventItemClickListioner eventItemClickListioner) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ActivitiesEventsAdapter.MyViewHolder h = (ActivitiesEventsAdapter.MyViewHolder) holder;
        ActivitiesBean.DataBean.EventsBean eventsBean = eventsBeans.get(position);
        h.eventDate.setText(eventsBean.getDate());
        h.eventTime.setText(eventsBean.getTime());
        if (!TextUtils.isEmpty(eventsBean.getDescription())) {
            h.eventDesc.setText(eventsBean.getDescription());
        } else {
            h.eventDesc.setText(eventsBean.getEvent_title());
        }
        /*try {
            h.eventPogress.setProgress(Math.round(Integer.parseInt(eventsBean.getJoined_users()) * 100 / Integer.parseInt(eventsBean.getMin_users())));
        } catch (ArithmeticException e) {
            Log.e("onBindViewHolder: ", e.getMessage());
        }*/
        int userCount = Integer.parseInt(eventsBean.getMin_users()) - Integer.parseInt(eventsBean.getConfirm_users());
        if (userCount <= 0) {
            //    h.imgCheck.setVisibility(View.VISIBLE);
            //   h.confirmBtnTxt.setVisibility(View.GONE);
            h.confirmTxt.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
            h.confirmTxt.setText("CONFIRMED");
            //   h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.primaryColor));
        } else {
            h.confirmTxt.setText((userCount /*- 1*/) + " " + context.getString(R.string.left_to_confirm));
            h.confirmTxt.setTextColor(ContextCompat.getColor(context, R.color.hint_color_gray));
            //    h.imgCheck.setVisibility(View.GONE);
            /*if (eventsBean.is_confirm().equals("1")){
                h.confirmBtnTxt.setText("UNCONFIRM");
            }else {
                h.confirmBtnTxt.setText("CONFIRM");
            }
            h.confirmBtnTxt.setVisibility(View.VISIBLE);*/
            //  h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.primaryColor));
        }
        if (eventsBean.is_confirm().equals("1")) {
            h.confirmBtnTxt.setText("UNCONFIRM");
            h.confirmBtnTxt.setTextColor(ContextCompat.getColor(context, R.color.red_favroit));
        } else {
            h.confirmBtnTxt.setText("CONFIRM");
            h.confirmBtnTxt.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
        }
        if (eventsBean.is_cancel().equals("1")) {
            // h.imgCheck.setVisibility(View.VISIBLE);
            h.confirmBtnTxt.setVisibility(View.GONE);
            h.confirmTxt.setTextColor(ContextCompat.getColor(context, R.color.red_favroit));
            h.confirmTxt.setText("CANCELLED");
            // h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.nav_gray));
        }
        /*if (eventsBean.is_confirm().equals("0")) {
            h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.nav_gray));
            //h.confirmTxt.setVisibility(View.GONE);
            h.confirmTxt.setText("");
        } else if (eventsBean.is_confirm().equals("1")) {
            h.imgCheck.setColorFilter(ContextCompat.getColor(context, R.color.primaryColor));
           // h.confirmTxt.setVisibility(View.VISIBLE);
            h.confirmTxt.setText("Confirmed");
        }*/
    }

    @Override
    public int getItemCount() {
        return eventsBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        //private ImageView imgCheck;
        private TextView eventDate, eventTime, eventDesc, confirmTxt, confirmBtnTxt;
        private LinearLayout datelay;
        //   private ProgressBar eventPogress;

        public MyViewHolder(View itemView) {
            super(itemView);
            //imgCheck = itemView.findViewById(R.id.imgCheck);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventDesc = itemView.findViewById(R.id.eventDesc);
            confirmTxt = itemView.findViewById(R.id.confirmTxt);
            confirmBtnTxt = itemView.findViewById(R.id.confirmBtnTxt);
            datelay = itemView.findViewById(R.id.datelay);
            // eventPogress = itemView.findViewById(R.id.eventPogress);
            /*imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivitiesBean.DataBean.EventsBean eventsBean = eventsBeans.get(getAdapterPosition());
                    if (eventsBean.is_cancel().equals("0"))eventItemClickListioner.onConfirm(getAdapterPosition());
                }
            });*/
            confirmBtnTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivitiesBean.DataBean.EventsBean eventsBean = eventsBeans.get(getAdapterPosition());
                    if (eventsBean.is_cancel().equals("0"))
                        eventItemClickListioner.onConfirm(getAdapterPosition());
                }
            });
            datelay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventItemClickListioner.onDateClick(getAdapterPosition());
                }
            });
            eventDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventItemClickListioner.onDateClick(getAdapterPosition());
                }
            });
        }
    }

}