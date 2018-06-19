package com.clubz.ui.user_activities.expandable_recycler_view;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;
import com.clubz.ui.user_activities.model.GetMyactivitiesResponce;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;
import com.squareup.picasso.Picasso;

public class MyActivitiesCategoryViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageView mArrowExpandImageView, itemMenu, itemJoin, itemLike, activityImge,itemChat;
    private TextView clubName, activityName;

    public MyActivitiesCategoryViewHolder(View itemView) {
        super(itemView);
        activityImge = itemView.findViewById(R.id.activityImge);
        mArrowExpandImageView = itemView.findViewById(R.id.iv_arrow_expand);
        itemMenu = itemView.findViewById(R.id.item_menu);
        itemJoin = itemView.findViewById(R.id.item_join);
        activityName = itemView.findViewById(R.id.activityName);
        clubName = itemView.findViewById(R.id.clubName);
        itemLike = itemView.findViewById(R.id.itemLike);
        itemChat = itemView.findViewById(R.id.itemChat);
    }

    public void bind(GetMyactivitiesResponce.DataBean activities, final int position, final ParentViewClickListioner parentViewClickListioner) {
        if (!TextUtils.isEmpty(activities.getImage())){
            Picasso.with(activityImge.getContext()).load(activities.getImage()).fit().placeholder(R.drawable.new_app_icon).into(activityImge);}
        activityName.setText(activities.getActivityName());
        clubName.setText(activities.getClub_name());

        itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentViewClickListioner.onItemMenuClick(position,itemMenu);
            }
        });
        itemJoin.setVisibility(View.GONE);
        itemLike.setVisibility(View.GONE);
        itemChat.setVisibility(View.GONE);

        mArrowExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentViewClickListioner.onItemClick(position, "today");
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
