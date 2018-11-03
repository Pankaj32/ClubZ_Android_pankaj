package com.clubz.ui.ads.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clubz.R;
import com.clubz.data.model.UserInfo;
import com.clubz.ui.ads.listioner.AdsClickListioner;
import com.clubz.ui.ads.model.AdsListBean;
import com.clubz.utils.DateTimeUtil;
import com.clubz.utils.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chiranjib on 28/12/17.
 */

public class AdsAdapter extends RecyclerView.Adapter {
    private Context context;
    private String mType;
    private List<Object> adBeans;
    private AdsClickListioner adsClickListioner;
    private final int TYPE_ADD = 1;
    private final int TYPE_GOOGLE_ADD = 2;

    public AdsAdapter(Context context, List<Object> adBeans, AdsClickListioner adsClickListioner) {
        this.context = context;
        // this.mType = type;
        this.adBeans = adBeans;
        this.adsClickListioner = adsClickListioner;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_GOOGLE_ADD:
                /*View googleAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_ads_items_layout, parent, false);
                return new GoogleViewHolder(googleAdView);*/
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.ad_unified,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case TYPE_ADD:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_items_layout, parent, false);
                return new MyViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        /*int type = 1;
        AdsListBean.DataBean dataBean = (AdsListBean.DataBean)adBeans.get(position);
        if (dataBean.getIsgoogleAdd()) {
            type = TYPE_GOOGLE_ADD;
        } else type = TYPE_ADD;*/
        Object data = adBeans.get(position);
        if (data instanceof UnifiedNativeAd) {
            return TYPE_GOOGLE_ADD;
        }
        return TYPE_ADD;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_GOOGLE_ADD:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) adBeans.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
            case TYPE_ADD:
                Object data = adBeans.get(position);
                if (data instanceof AdsListBean.DataBean) {
                    AdsListBean.DataBean dataBean = (AdsListBean.DataBean) adBeans.get(position);
                    AdsAdapter.MyViewHolder h = (AdsAdapter.MyViewHolder) holder;
                    h.adTitle.setText(dataBean.getTitle());
                    h.adValue.setText("$ " + dataBean.getFee());

                    h.adTime.setText(DateTimeUtil.getDayDifference(context, dataBean.getCrd(), dataBean.getCurrentDatetime()));

                    // h.adTime.setText(dataBean.getDayDifference());
                    h.username.setText(dataBean.getFull_name());
                    //h.usrerole.setText(dataBean.getUser_role());
                    if (!TextUtils.isEmpty(dataBean.getImage())) {
                        h.imgLay.setVisibility(View.VISIBLE);
                Glide.with(h.adImg.getContext())
                        .load(dataBean.getImage())
                        .into(h.adImg);
                       /// Picasso.with(h.adImg.getContext()).load(dataBean.getImage()).fit().placeholder(R.drawable.ic_new_img).into(h.adImg);
                    } else {
                        // h.adImg.setImageResource(R.drawable.ic_new_img);
                        h.imgLay.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(dataBean.getImage())) {
                        //   Picasso.with(h.userImg.getContext()).load(dataBean.getProfile_image()).fit().placeholder(R.drawable.user_place_holder).into(h.userImg);
                        Glide.with(h.userImg.getContext())
                                .load(dataBean.getProfile_image())
                                .into(h.userImg);
                    } else {
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
                    } else {
                        h.adType.setText(R.string.recent_txt);
                        h.adType.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
                    }
                    if (dataBean.getExpire_ads().equals("Yes")) {
                        h.adType.setText(R.string.expired);
                        h.adType.setTextColor(ContextCompat.getColor(context, R.color.nav_gray));
                    }
                }
        }
       /* AdsListBean.DataBean dataBean = (AdsListBean.DataBean)adBeans.get(position);
        if (dataBean.getIsgoogleAdd()) {

           *//* AdsAdapter.GoogleViewHolder h = (AdsAdapter.GoogleViewHolder) holder;
            AdRequest adRequest = new AdRequest.Builder().build();
            h.adView.loadAd(adRequest);
            h.adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }
            });*//*
            UnifiedNativeAd nativeAd = (UnifiedNativeAd) adBeans.get(position);
            populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
        } else {
            AdsAdapter.MyViewHolder h = (AdsAdapter.MyViewHolder) holder;
            h.adTitle.setText(dataBean.getTitle());
            h.adValue.setText("$ " + dataBean.getFee());

            h.adTime.setText(DateTimeUtil.getDayDifference(context, dataBean.getCrd(), dataBean.getCurrentDatetime()));

            // h.adTime.setText(dataBean.getDayDifference());
            h.username.setText(dataBean.getFull_name());
            //h.usrerole.setText(dataBean.getUser_role());
            if (!TextUtils.isEmpty(dataBean.getImage())) {
                *//*Glide.with(h.adImg.getContext())
                        .load(dataBean.getImage())
                        .into(h.adImg);*//*
                  Picasso.with(h.adImg.getContext()).load(dataBean.getImage()).fit().placeholder(R.drawable.ic_new_img).into(h.adImg);
            } else {
               // h.adImg.setImageResource(R.drawable.ic_new_img);
                h.adImg.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(dataBean.getImage())) {
                //   Picasso.with(h.userImg.getContext()).load(dataBean.getProfile_image()).fit().placeholder(R.drawable.user_place_holder).into(h.userImg);
                Glide.with(h.userImg.getContext())
                        .load(dataBean.getProfile_image())
                        .into(h.userImg);
            } else {
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
            } else {
                h.adType.setText(R.string.recent_txt);
                h.adType.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
            }
            if (dataBean.getExpire_ads().equals("Yes")) {
                h.adType.setText(R.string.expired);
                h.adType.setTextColor(ContextCompat.getColor(context, R.color.nav_gray));
            }
        }
*/
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
        private RelativeLayout imgLay;
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
            imgLay = itemView.findViewById(R.id.imgLay);

            iv_arrow_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdsListBean.DataBean dataBean =(AdsListBean.DataBean) adBeans.get(getAdapterPosition());
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
                    AdsListBean.DataBean dataBean =(AdsListBean.DataBean) adBeans.get(getAdapterPosition());
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(dataBean.getUser_id());
                 //   userInfo.setClubUserId(dataBean.cl());
                    userInfo.setLiked("0");
                    userInfo.setFull_name(dataBean.getFull_name());
                    userInfo.setProfile_image(dataBean.getProfile_image());
                    userInfo.setCountry_code("");
                    userInfo.setContact_no(dataBean.getCreator_phone());
                    userInfo.setContact_no_visibility(dataBean.getContact_no_visibility());
                    adsClickListioner.onUserClick(userInfo);
                }
            });
        }
    }

    private class GoogleViewHolder extends RecyclerView.ViewHolder {
        private AdView adView;

        public GoogleViewHolder(View itemView) {
            super(itemView);
            adView = itemView.findViewById(R.id.adView);
        }
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
}