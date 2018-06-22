package com.clubz.ui.user_activities.expandable_recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;
import com.clubz.ui.user_activities.model.GetActivityMembersResponce;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

import java.util.List;

public class ActivityMembersAdapter extends ExpandableRecyclerAdapter<ActivityMembersViewHolder, ActivityMembersAffiliatesViewHolder> {

    private LayoutInflater mInflator;
private Context mContext;

    public ActivityMembersAdapter(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
this.mContext=context;
    }

    @Override
    public ActivityMembersViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View movieCategoryView = mInflator.inflate(R.layout.item_activity_member, parentViewGroup, false);
        return new ActivityMembersViewHolder(movieCategoryView);
    }

    @Override
    public ActivityMembersAffiliatesViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View moviesView = mInflator.inflate(R.layout.item_activity_members_affiliates, childViewGroup, false);
        return new ActivityMembersAffiliatesViewHolder(moviesView);
    }

    @Override
    public void onBindParentViewHolder(ActivityMembersViewHolder activityMembersViewHolder, int position, ParentListItem parentListItem) {
        GetActivityMembersResponce.DataBean dataBean = (GetActivityMembersResponce.DataBean) parentListItem;
        activityMembersViewHolder.bind(mContext,dataBean, position);
    }

    @Override
    public void onBindChildViewHolder(ActivityMembersAffiliatesViewHolder activityMembersAffiliatesViewHolder, int position, Object childListItem) {
        GetActivityMembersResponce.DataBean.AffiliatesBean affiliatesBean = (GetActivityMembersResponce.DataBean.AffiliatesBean) childListItem;
        activityMembersAffiliatesViewHolder.bind(affiliatesBean, position);
    }
}
