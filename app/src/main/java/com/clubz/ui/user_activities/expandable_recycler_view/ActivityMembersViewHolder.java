package com.clubz.ui.user_activities.expandable_recycler_view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.ui.cv.ChipView;
import com.clubz.ui.cv.FlowLayout;
import com.clubz.ui.cv.chipview.TagView;
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner;
import com.clubz.ui.user_activities.model.GetActivityMembersResponce;
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActivityMembersViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageView mArrowExpandImageView;
    private TextView tv_FullName;
    private ImageView iv_profileImage;
    private TagView tagView;
    private Context mContext;


    public ActivityMembersViewHolder(View itemView) {
        super(itemView);
        mArrowExpandImageView = itemView.findViewById(R.id.iv_arrow_expand);
        iv_profileImage = itemView.findViewById(R.id.iv_profileImage);
        tv_FullName = itemView.findViewById(R.id.tv_FullName);
        tagView = itemView.findViewById(R.id.tagView);
    }

    public void bind(Context context, GetActivityMembersResponce.DataBean dataBean, final int position) {
        this.mContext = context;
        if (!dataBean.getProfile_image().isEmpty()) {
            Picasso.with(iv_profileImage.getContext()).load(dataBean.getProfile_image()).into(iv_profileImage);
        }
        tv_FullName.setText(dataBean.getFull_name());
        addChip();
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

    private void addChip() {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("User Tag 1");
        stringList.add("User Tag 2");
        stringList.add("User Tag 3");
        tagView.addTag(stringList);
        /*for (String s : stringList) {
            ChipView chipView = new ChipView(mContext,
                    String.valueOf(chipHolder.getChildCount()), false) {
                @Override
                public void setDeleteListner(ChipView chipView) {

                }

                @Override
                public int getLayout() {
                    return R.layout.z_cus_chip_view_newsfeed;
                }
            };
            chipView.setText(s);
            chipHolder.addView(chipView);
        }*/
    }

}
