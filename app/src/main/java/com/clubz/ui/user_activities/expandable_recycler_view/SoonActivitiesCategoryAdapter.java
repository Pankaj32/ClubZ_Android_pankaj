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

public class SoonActivitiesCategoryAdapter extends ExpandableRecyclerAdapter<SoonActivitiesCategoryViewHolder, SoonEventsViewHolder> {

    private LayoutInflater mInflator;
    private ParentViewClickListioner parentViewClickListioner;
    private ChildViewClickListioner childViewClickListioner;

    public SoonActivitiesCategoryAdapter(Context context, List<? extends ParentListItem> parentItemList, ParentViewClickListioner parentViewClickListioner, ChildViewClickListioner childViewClickListioner) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.parentViewClickListioner = parentViewClickListioner;
        this.childViewClickListioner = childViewClickListioner;
    }

    @Override
    public SoonActivitiesCategoryViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View movieCategoryView = mInflator.inflate(R.layout.activities_items_layout, parentViewGroup, false);
        return new SoonActivitiesCategoryViewHolder(movieCategoryView);
    }

    @Override
    public SoonEventsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View moviesView = mInflator.inflate(R.layout.activities_event_items_layout, childViewGroup, false);
        return new SoonEventsViewHolder(moviesView);
    }

    @Override
    public void onBindParentViewHolder(SoonActivitiesCategoryViewHolder soonActivitiesCategoryViewHolder, int position, ParentListItem parentListItem) {
        GetOthersActivitiesResponce.DataBean.SoonBean movieCategory = (GetOthersActivitiesResponce.DataBean.SoonBean) parentListItem;
        soonActivitiesCategoryViewHolder.bind(movieCategory, position, parentViewClickListioner);
    }

    @Override
    public void onBindChildViewHolder(SoonEventsViewHolder soonEventsViewHolder, int position, Object childListItem) {
        GetOthersActivitiesResponce.DataBean.SoonBean.EventsBeanXX movies = (GetOthersActivitiesResponce.DataBean.SoonBean.EventsBeanXX) childListItem;
        soonEventsViewHolder.bind(movies, position, childViewClickListioner);
    }
}
