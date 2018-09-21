package com.clubz.ui.ads.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.data.model.UserInfo;
import com.clubz.ui.ads.listioner.AdsClickListioner;
import com.clubz.ui.ads.model.AdsListBean;
import com.clubz.utils.DateTimeUtil;
import com.clubz.utils.Util;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class AdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String mType;
    private List<AdsListBean.DataBean> adBeans;
    private AdsClickListioner adsClickListioner;

    public AdsAdapter(Context context, List<AdsListBean.DataBean> adBeans, AdsClickListioner adsClickListioner) {
        this.context = context;
        // this.mType = type;
        this.adBeans = adBeans;
        this.adsClickListioner=adsClickListioner;
    }
    //This is comment ot cjec'

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_items_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AdsAdapter.MyViewHolder h = (AdsAdapter.MyViewHolder) holder;
        AdsListBean.DataBean dataBean = adBeans.get(position);

        h.adTitle.setText(dataBean.getTitle());
        h.adValue.setText("$ " + dataBean.getFee());

        h.adTime.setText(DateTimeUtil.getDayDifference(context,dataBean.getCrd(),dataBean.getCurrentDatetime()));

       // h.adTime.setText(dataBean.getDayDifference());
        h.username.setText(dataBean.getFull_name());
        h.usrerole.setText(dataBean.getUser_role());
        if (!TextUtils.isEmpty(dataBean.getImage())) {
            Picasso.with(h.adImg.getContext()).load(dataBean.getImage()).fit().placeholder(R.drawable.ic_new_img).into(h.adImg);
        }else {
            h.adImg.setImageResource(R.drawable.ic_new_img);
        }
        if (!TextUtils.isEmpty(dataBean.getImage())) {
            Picasso.with(h.userImg.getContext()).load(dataBean.getProfile_image()).fit().placeholder(R.drawable.user_place_holder).into(h.userImg);
        }else {
            h.userImg.setImageResource(R.drawable.user_place_holder);
        }
        if (dataBean.isFav().equals("1")) {
            h.iv_like.setVisibility(View.VISIBLE);
        } else {
            h.iv_like.setVisibility(View.GONE);
        }
        if (dataBean.getVisible()) {
            h.belloLay.setVisibility(View.VISIBLE);
            h.lineView.setVisibility(View.VISIBLE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_up_arrow);
        } else {
            h.belloLay.setVisibility(View.GONE);
            h.lineView.setVisibility(View.GONE);
            h.iv_arrow_expand.setImageResource(R.drawable.ic_event_down_arrow);
        }

        if (dataBean.is_New().equals("1")) {
            h.adType.setText(context.getString(R.string.new_txt));
            h.adType.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
        }else {
            h.adType.setText(R.string.recent_txt);
            h.adType.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
        }
        if (dataBean.getExpire_ads().equals("Yes")) {
            h.adType.setText(R.string.expired);
            h.adType.setTextColor(ContextCompat.getColor(context, R.color.nav_gray));
        }
    }

    @Override
    public int getItemCount() {
        return adBeans.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView adType;
        private TextView adTitle;
        private TextView adValue;
        private TextView adTime;
        private TextView username;
        private TextView usrerole;
        private ImageView iv_like;
        private ImageView adImg;
        private ImageView userImg;
        private ImageView iv_arrow_expand;
        private View lineView;
        private RelativeLayout belloLay;

        public MyViewHolder(View itemView) {
            super(itemView);
            adType = itemView.findViewById(R.id.adType);
            adTitle = itemView.findViewById(R.id.adTitle);
            adValue = itemView.findViewById(R.id.adValue);
            iv_like = itemView.findViewById(R.id.iv_like);
            lineView = itemView.findViewById(R.id.lineView);
            belloLay = itemView.findViewById(R.id.belloLay);
            adImg = itemView.findViewById(R.id.adImg);
            adTime = itemView.findViewById(R.id.adTime);
            userImg = itemView.findViewById(R.id.userImg);
            iv_arrow_expand = itemView.findViewById(R.id.iv_arrow_expand);
            username = itemView.findViewById(R.id.username);
            usrerole = itemView.findViewById(R.id.usrerole);

            iv_arrow_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdsListBean.DataBean dataBean = adBeans.get(getAdapterPosition());
                    if (dataBean.getVisible()) {
                        dataBean.setVisible(false);
                    } else {
                        dataBean.setVisible(true);
                    }
                    Util.Companion.setRotation(iv_arrow_expand, dataBean.getVisible());
                    notifyItemChanged(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adsClickListioner.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    adsClickListioner.onLongPress(getAdapterPosition());
                    return false;
                }
            });
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdsListBean.DataBean dataBean = adBeans.get(getAdapterPosition());
                    UserInfo userInfo= new UserInfo();
                    userInfo.setUserId(dataBean.getUser_id());
                    userInfo.setLiked(0);
                    userInfo.setFull_name(dataBean.getFull_name());
                    userInfo.setProfile_image(dataBean.getProfile_image());
                    userInfo.setCountry_code("+91");
                    userInfo.setContact_no("+811674365");
                    adsClickListioner.onUserClick(userInfo);
                }
            });
        }
    }

}