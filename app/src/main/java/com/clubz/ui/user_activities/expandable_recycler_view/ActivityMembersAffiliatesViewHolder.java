package com.clubz.ui.user_activities.expandable_recycler_view;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;
import com.clubz.ui.user_activities.model.GetActivityMembersResponce;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

public class ActivityMembersAffiliatesViewHolder extends ChildViewHolder {

    private TextView tv_FullName;
    private ImageView iv_profileImage;

    public ActivityMembersAffiliatesViewHolder(View itemView) {
        super(itemView);
        iv_profileImage = itemView.findViewById(R.id.iv_profileImage);
        tv_FullName = itemView.findViewById(R.id.tv_FullName);
    }

    public void bind(final GetActivityMembersResponce.DataBean.AffiliatesBean affiliatesBean, final int childPos) {
        tv_FullName.setText(affiliatesBean.getAffiliate_name());
    }
}
