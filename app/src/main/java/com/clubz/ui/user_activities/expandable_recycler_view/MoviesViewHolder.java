package com.clubz.ui.user_activities.expandable_recycler_view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner;

public class MoviesViewHolder extends ChildViewHolder {

    private TextView mMoviesTextView;
    private ImageView item_child_join;

    public MoviesViewHolder(View itemView) {
        super(itemView);
     //   mMoviesTextView = (TextView) itemView.findViewById(R.id.tv_movies);
        item_child_join =itemView.findViewById(R.id.item_child_join);
    }

    public void bind(Movies movies, final int parentPos, final int childPos, final ChildViewClickListioner childViewClickListioner) {
       // mMoviesTextView.setText(movies.getName());
        item_child_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childViewClickListioner.onJoin(parentPos,childPos);
            }
        });
    }
}
