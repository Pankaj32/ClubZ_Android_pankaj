package com.clubz.ui.user_activities.expandable_recycler_view;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;
import com.clubz.ui.user_activities.model.GetMyactivitiesResponce;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

public class MyEventsViewHolder extends ChildViewHolder {

    private TextView eventDate, eventTime, eventDesc;
    private ImageView item_child_join;

    public MyEventsViewHolder(View itemView) {
        super(itemView);
        item_child_join = itemView.findViewById(R.id.item_child_join);
        eventDate = itemView.findViewById(R.id.eventDate);
        eventTime = itemView.findViewById(R.id.eventTime);
        eventDesc = itemView.findViewById(R.id.eventDesc);
    }

    public void bind(final GetMyactivitiesResponce.DataBean.EventsBean events, final int childPos, final ChildViewClickListioner childViewClickListioner) {
        eventDate.setText(events.getDate());
        eventTime.setText(events.getTime());
        item_child_join.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(events.getDescription())) {
            eventDesc.setText(events.getDescription());
        } else {
            eventDesc.setText(events.getEvent_title());
        }

    }
}
