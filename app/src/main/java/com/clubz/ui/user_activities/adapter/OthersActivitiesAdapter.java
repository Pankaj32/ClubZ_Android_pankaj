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
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class OthersActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<GetOthersActivitiesResponce.DataBean.OthersBean> othersActivityBeans;
    private ActivityItemClickListioner activityItemClickListioner;

    public OthersActivitiesAdapter(Context context, List<GetOthersActivitiesResponce.DataBean.OthersBean> othersActivityBeans,ActivityItemClickListioner activityItemClickListioner) {
        this.context = context;
        // this.mType = type;
        this.othersActivityBeans = othersActivityBeans;
        this.activityItemClickListioner = activityItemClickListioner;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_items_layout_copy, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OthersActivitiesAdapter.MyViewHolder h = (OthersActivitiesAdapter.MyViewHolder) holder;
        GetOthersActivitiesResponce.DataBean.OthersBean othersBean = othersActivityBeans.get(position);
        if (!TextUtils.isEmpty(othersBean.getImage())) {
            Picasso.with(h.activityImge.getContext()).load(othersBean.getImage()).fit().placeholder(R.drawable.new_app_icon).into(h.activityImge);
        }
        h.activityName.setText(othersBean.getActivityName());
        h.clubName.setText(othersBean.getClub_name());
        h.itemLike.setImageResource(R.drawable.inactive_heart_ico);
    }

    @Override
    public int getItemCount() {
        return othersActivityBeans.size();
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
            activityName = itemView.findViewById(R.id.activityName);
            clubName = itemView.findViewById(R.id.clubName);
            activityImge = itemView.findViewById(R.id.activityImge);
            iv_arrow_expand.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityItemClickListioner.onItemClick("others", getAdapterPosition());
                }
            });
        }
    }

}