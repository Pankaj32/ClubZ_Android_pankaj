package com.clubz.ui.user_activities.expandable_recycler_view;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;
import com.squareup.picasso.Picasso;

public class OthersActivitiesCategoryViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageView mArrowExpandImageView, itemMenu, itemJoin, itemLike, activityImge, itemChat;
    private TextView clubName, activityName;

    public OthersActivitiesCategoryViewHolder(View itemView) {
        super(itemView);
        mArrowExpandImageView = itemView.findViewById(R.id.iv_arrow_expand);
        itemMenu = itemView.findViewById(R.id.item_menu);
        itemJoin = itemView.findViewById(R.id.item_join);
        activityName = itemView.findViewById(R.id.activityName);
        clubName = itemView.findViewById(R.id.clubName);
        itemLike = itemView.findViewById(R.id.itemLike);
        activityImge = itemView.findViewById(R.id.activityImge);
        itemChat = itemView.findViewById(R.id.itemChat);
    }

    public void bind(GetOthersActivitiesResponce.DataBean.OthersBean activities, final int position, final ParentViewClickListioner parentViewClickListioner) {
        if (!TextUtils.isEmpty(activities.getImage())) {
            Picasso.with(activityImge.getContext()).load(activities.getImage()).fit().placeholder(R.drawable.new_app_icon).into(activityImge);
        }
        activityName.setText(activities.getActivityName());
        clubName.setText(activities.getClub_name());
        itemMenu.setVisibility(View.GONE);
        mArrowExpandImageView.setVisibility(View.GONE);
        itemChat.setVisibility(View.GONE);
        itemJoin.setVisibility(View.GONE);
        if (activities.is_Confirm()) {
            itemJoin.setImageResource(R.drawable.hand_ico);
        } else {
            itemJoin.setImageResource(R.drawable.ic_inactive_hand_ico);
        }
        if (activities.is_like().equals("1")) {
            itemLike.setImageResource(R.drawable.active_heart_ico);
        } else {
            itemLike.setImageResource(R.drawable.inactive_heart_ico);
        }
        /*itemJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentViewClickListioner.onItemJoin(position,"others");
            }
        });*/
        itemLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentViewClickListioner.onItemLike(position, "others");
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
