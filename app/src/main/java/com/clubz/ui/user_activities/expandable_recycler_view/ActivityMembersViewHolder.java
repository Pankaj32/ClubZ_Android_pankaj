package com.clubz.ui.user_activities.expandable_recycler_view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.data.local.pref.SessionManager;
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
    private TextView tv_FullName/*, noTagTxt*/;
    private ImageView iv_profileImage;
    private ImageView iv_check;
    //private TagView tagView;
    // private View viewTop;
    private Context mContext;

    public interface Listner {
        public void onProfileClick(GetActivityMembersResponce.DataBean dataBean);
    }

    public ActivityMembersViewHolder(View itemView) {
        super(itemView);
        mArrowExpandImageView = itemView.findViewById(R.id.iv_arrow_expand);
        iv_profileImage = itemView.findViewById(R.id.iv_profileImage);
        tv_FullName = itemView.findViewById(R.id.tv_FullName);
        iv_check = itemView.findViewById(R.id.iv_check);
        /*tagView = itemView.findViewById(R.id.tagView);
        noTagTxt = itemView.findViewById(R.id.noTagTxt);*/
        //  viewTop = itemView.findViewById(R.id.viewTop);
    }

    public void bind(Context context, final GetActivityMembersResponce.DataBean dataBean, final int position, final Listner listner) {
        this.mContext = context;
        if (!dataBean.getProfile_image().isEmpty()) {
            Picasso.with(iv_profileImage.getContext()).load(dataBean.getProfile_image()).fit().placeholder(R.drawable.user_place_holder).into(iv_profileImage);
        }
        //  if (position == 0) viewTop.setVisibility(View.GONE);
        tv_FullName.setText(dataBean.getFull_name());
      //  if (!dataBean.getTag_name().isEmpty()) addChip(dataBean.getTag_name(), noTagTxt);

        if(dataBean.is_join()!=null&&!dataBean.is_join().equals("")){
            iv_check.setVisibility(View.VISIBLE);

        }
        if (dataBean.getAffiliates()!=null) {
            if (dataBean.getAffiliates().size() > 0) {
                mArrowExpandImageView.setVisibility(View.VISIBLE);
            } else {
                mArrowExpandImageView.setVisibility(View.GONE);
            }
        }else mArrowExpandImageView.setVisibility(View.GONE);

        iv_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetActivityMembersResponce.DataBean dataBeanclick = dataBean;
                if(!dataBeanclick.getUserId().equals(SessionManager.getObj().getUser().getId())){
                    listner.onProfileClick(dataBeanclick);
                }

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

    private void addChip(String tag_name, TextView noTagTxt) {
        ArrayList<String> stringList = new ArrayList<>();
        String tag_names[] = tag_name.split(",");
        for (String tags : tag_names) {
            stringList.add(tags);
        }
        if (tag_names.length == 0) noTagTxt.setVisibility(View.VISIBLE);
       // tagView.addTag(stringList);
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
