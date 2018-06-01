package com.clubz.ui.user_activities.expandable_recycler_view;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;

public class MovieCategoryViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageView mArrowExpandImageView, itemMenu, itemJoin;
    private TextView mMovieTextView;

    public MovieCategoryViewHolder(View itemView) {
        super(itemView);
        // mMovieTextView = (TextView) itemView.findViewById(R.id.tv_movie_category);

        mArrowExpandImageView = itemView.findViewById(R.id.iv_arrow_expand);
        itemMenu = itemView.findViewById(R.id.item_menu);
        itemJoin = itemView.findViewById(R.id.item_join);
    }

    public void bind(MovieCategory movieCategory, final int position, final ParentViewClickListioner parentViewClickListioner) {
        //  mMovieTextView.setText(movieCategory.getName());
        itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentViewClickListioner.onItemMenuClick(position);
            }
        });
        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentViewClickListioner.onItemClick(position);
            }
        });*/
        itemJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentViewClickListioner.onItemJoin(position);
            }
        });
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);

        if (expanded) {
            mArrowExpandImageView.setRotation(ROTATED_POSITION);
        } else {
            mArrowExpandImageView.setRotation(INITIAL_POSITION);
        }

    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);

        RotateAnimation rotateAnimation;
        if (expanded) { // rotate clockwise
            rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        } else { // rotate counterclockwise
            rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        mArrowExpandImageView.startAnimation(rotateAnimation);

    }
}
