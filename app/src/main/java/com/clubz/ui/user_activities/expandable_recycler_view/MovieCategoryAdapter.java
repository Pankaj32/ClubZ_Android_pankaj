package com.clubz.ui.user_activities.expandable_recycler_view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;

import java.util.List;

public class MovieCategoryAdapter extends ExpandableRecyclerAdapter<MovieCategoryViewHolder, MoviesViewHolder> {

    private LayoutInflater mInflator;
    private ParentViewClickListioner parentViewClickListioner;
    private ChildViewClickListioner childViewClickListioner;
    private int parentPosition;

    public MovieCategoryAdapter(Context context, List<? extends ParentListItem> parentItemList, ParentViewClickListioner parentViewClickListioner, ChildViewClickListioner childViewClickListioner) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.parentViewClickListioner = parentViewClickListioner;
        this.childViewClickListioner = childViewClickListioner;
    }

    @Override
    public MovieCategoryViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View movieCategoryView = mInflator.inflate(R.layout.activities_items_layout, parentViewGroup, false);
        return new MovieCategoryViewHolder(movieCategoryView);
    }

    @Override
    public MoviesViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View moviesView = mInflator.inflate(R.layout.activities_event_items_layout, childViewGroup, false);
        return new MoviesViewHolder(moviesView);
    }

    @Override
    public void onBindParentViewHolder(MovieCategoryViewHolder movieCategoryViewHolder, int position, ParentListItem parentListItem) {
        MovieCategory movieCategory = (MovieCategory) parentListItem;
        parentPosition = position;
        movieCategoryViewHolder.bind(movieCategory, position, parentViewClickListioner);
    }

    @Override
    public void onBindChildViewHolder(MoviesViewHolder moviesViewHolder, int position, Object childListItem) {
        Movies movies = (Movies) childListItem;
        moviesViewHolder.bind(movies, parentPosition, position, childViewClickListioner);
    }
}
