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

public class TodaysActivitiesCategoryAdapter extends ExpandableRecyclerAdapter<TodaysActivitiesCategoryViewHolder, TodaysEventsViewHolder> {

    private LayoutInflater mInflator;
    private ParentViewClickListioner parentViewClickListioner;
    private ChildViewClickListioner childViewClickListioner;

    public TodaysActivitiesCategoryAdapter(Context context, List<? extends ParentListItem> parentItemList, ParentViewClickListioner parentViewClickListioner, ChildViewClickListioner childViewClickListioner) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.parentViewClickListioner = parentViewClickListioner;
        this.childViewClickListioner = childViewClickListioner;
    }

    @Override
    public TodaysActivitiesCategoryViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View movieCategoryView = mInflator.inflate(R.layout.activities_items_layout, parentViewGroup, false);
        return new TodaysActivitiesCategoryViewHolder(movieCategoryView);
    }

    @Override
    public TodaysEventsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View moviesView = mInflator.inflate(R.layout.activities_event_items_layout, childViewGroup, false);
        return new TodaysEventsViewHolder(moviesView);
    }

    @Override
    public void onBindParentViewHolder(TodaysActivitiesCategoryViewHolder todaysActivitiesCategoryViewHolder, int position, ParentListItem parentListItem) {
        GetOthersActivitiesResponce.DataBean.TodayBean movieCategory = (GetOthersActivitiesResponce.DataBean.TodayBean) parentListItem;
        todaysActivitiesCategoryViewHolder.bind(movieCategory, position, parentViewClickListioner);
    }

    @Override
    public void onBindChildViewHolder(TodaysEventsViewHolder todaysEventsViewHolder, int position, Object childListItem) {
        GetOthersActivitiesResponce.DataBean.TodayBean.EventsBean movies = (GetOthersActivitiesResponce.DataBean.TodayBean.EventsBean) childListItem;
        todaysEventsViewHolder.bind(movies, position, childViewClickListioner);
    }
}
