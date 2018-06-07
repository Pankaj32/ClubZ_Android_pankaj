package com.clubz.ui.user_activities.expandable_recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;
import com.clubz.ui.user_activities.model.GetActivitiesResponce;

import java.util.List;

public class TomorrowActivitiesCategoryAdapter extends ExpandableRecyclerAdapter<TomorrowActivitiesCategoryViewHolder, TomorrowEventsViewHolder> {

    private LayoutInflater mInflator;
    private ParentViewClickListioner parentViewClickListioner;
    private ChildViewClickListioner childViewClickListioner;

    public TomorrowActivitiesCategoryAdapter(Context context, List<? extends ParentListItem> parentItemList, ParentViewClickListioner parentViewClickListioner, ChildViewClickListioner childViewClickListioner) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.parentViewClickListioner = parentViewClickListioner;
        this.childViewClickListioner = childViewClickListioner;
    }

    @Override
    public TomorrowActivitiesCategoryViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View movieCategoryView = mInflator.inflate(R.layout.activities_items_layout, parentViewGroup, false);
        return new TomorrowActivitiesCategoryViewHolder(movieCategoryView);
    }

    @Override
    public TomorrowEventsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View moviesView = mInflator.inflate(R.layout.activities_event_items_layout, childViewGroup, false);
        return new TomorrowEventsViewHolder(moviesView);
    }

    @Override
    public void onBindParentViewHolder(TomorrowActivitiesCategoryViewHolder tomorrowActivitiesCategoryAdapter, int position, ParentListItem parentListItem) {
        GetActivitiesResponce.DataBean.TomorrowBean movieCategory = (GetActivitiesResponce.DataBean.TomorrowBean) parentListItem;
        tomorrowActivitiesCategoryAdapter.bind(movieCategory, position, parentViewClickListioner);
    }

    @Override
    public void onBindChildViewHolder(TomorrowEventsViewHolder todaysEventsViewHolder, int position, Object childListItem) {
        GetActivitiesResponce.DataBean.TomorrowBean.EventsBeanX movies = (GetActivitiesResponce.DataBean.TomorrowBean.EventsBeanX) childListItem;
        todaysEventsViewHolder.bind(movies, position, childViewClickListioner);
    }
}
