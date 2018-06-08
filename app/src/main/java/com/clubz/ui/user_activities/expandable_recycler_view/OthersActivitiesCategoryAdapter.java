package com.clubz.ui.user_activities.expandable_recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

import java.util.List;

public class OthersActivitiesCategoryAdapter extends ExpandableRecyclerAdapter<OthersActivitiesCategoryViewHolder, OthersEventsViewHolder> {

    private LayoutInflater mInflator;
    private ParentViewClickListioner parentViewClickListioner;
    private ChildViewClickListioner childViewClickListioner;

    public OthersActivitiesCategoryAdapter(Context context, List<? extends ParentListItem> parentItemList, ParentViewClickListioner parentViewClickListioner, ChildViewClickListioner childViewClickListioner) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.parentViewClickListioner = parentViewClickListioner;
        this.childViewClickListioner = childViewClickListioner;
    }

    @Override
    public OthersActivitiesCategoryViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View movieCategoryView = mInflator.inflate(R.layout.activities_items_layout, parentViewGroup, false);
        return new OthersActivitiesCategoryViewHolder(movieCategoryView);
    }

    @Override
    public OthersEventsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View moviesView = mInflator.inflate(R.layout.activities_event_items_layout, childViewGroup, false);
        return new OthersEventsViewHolder(moviesView);
    }

    @Override
    public void onBindParentViewHolder(OthersActivitiesCategoryViewHolder othersActivitiesCategoryViewHolder, int position, ParentListItem parentListItem) {
        GetOthersActivitiesResponce.DataBean.OthersBean movieCategory = (GetOthersActivitiesResponce.DataBean.OthersBean) parentListItem;
        othersActivitiesCategoryViewHolder.bind(movieCategory, position, parentViewClickListioner);
    }

    @Override
    public void onBindChildViewHolder(OthersEventsViewHolder othersEventsViewHolder, int position, Object childListItem) {
        GetOthersActivitiesResponce.DataBean.OthersBean.EventsBeanXXX movies = (GetOthersActivitiesResponce.DataBean.OthersBean.EventsBeanXXX) childListItem;
        othersEventsViewHolder.bind(movies, position, childViewClickListioner);
    }
}
