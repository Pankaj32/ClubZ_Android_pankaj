package com.clubz.ui.user_activities.expandable_recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;
import com.clubz.ui.user_activities.model.GetMyactivitiesResponce;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;

import java.util.List;

public class MyActivitiesCategoryAdapter extends ExpandableRecyclerAdapter<MyActivitiesCategoryViewHolder, MyEventsViewHolder> {

    private LayoutInflater mInflator;
    private ParentViewClickListioner parentViewClickListioner;
    private ChildViewClickListioner childViewClickListioner;

    public MyActivitiesCategoryAdapter(Context context, List<? extends ParentListItem> parentItemList, ParentViewClickListioner parentViewClickListioner, ChildViewClickListioner childViewClickListioner) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.parentViewClickListioner = parentViewClickListioner;
        this.childViewClickListioner = childViewClickListioner;
    }

    @Override
    public MyActivitiesCategoryViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View movieCategoryView = mInflator.inflate(R.layout.activities_items_layout, parentViewGroup, false);
        return new MyActivitiesCategoryViewHolder(movieCategoryView);
    }

    @Override
    public MyEventsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View moviesView = mInflator.inflate(R.layout.activities_event_items_layout, childViewGroup, false);
        return new MyEventsViewHolder(moviesView);
    }

    @Override
    public void onBindParentViewHolder(MyActivitiesCategoryViewHolder myActivitiesCategoryViewHolder, int position, ParentListItem parentListItem) {
        GetMyactivitiesResponce.DataBean movieCategory = (GetMyactivitiesResponce.DataBean) parentListItem;
        myActivitiesCategoryViewHolder.bind(movieCategory, position, parentViewClickListioner);
    }

    @Override
    public void onBindChildViewHolder(MyEventsViewHolder myEventsViewHolder, int position, Object childListItem) {
        GetMyactivitiesResponce.DataBean.EventsBean movies = (GetMyactivitiesResponce.DataBean.EventsBean) childListItem;
        myEventsViewHolder.bind(movies, position, childViewClickListioner);
    }
}
