package com.clubz.ui.club.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clubz.chat.model.MemberBean;
import com.clubz.ui.cv.ChipView;
import com.clubz.ui.cv.FlowLayout;
import com.clubz.ui.cv.chipview.TagView;
import com.clubz.R;
import com.clubz.data.model.ClubMember;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class AdapterClubMember extends RecyclerView.Adapter<AdapterClubMember.ViewHolder>{

    private Context mContext;
    private List<ClubMember> memberList;

    public AdapterClubMember(Context mContext, List<ClubMember> memberList) {
        this.mContext = mContext;
        this.memberList = memberList;
    }

    public void setMemberList(List<ClubMember> memberList) {
        this.memberList = memberList;
        notifyDataSetChanged();
    }

    @Override
    public AdapterClubMember.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_club_members_list, parent, false);
        return new AdapterClubMember.ViewHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(AdapterClubMember.ViewHolder h, int position) {
        final ClubMember member = memberList.get(position);
        h.tv_FullName.setText(member.getNickname());

        if(!TextUtils.isEmpty(member.getProfile_image())){
            Glide.with(h.iv_profileImage.getContext()).load(member.getProfile_image()).into(h.iv_profileImage);
        }else Picasso.with(h.iv_profileImage.getContext()).load(R.drawable.ic_user_white).into(h.iv_profileImage);

        if(TextUtils.isEmpty(member.getTag_name())){
            h.tagView.distroy();
            h.tagView.setVisibility(View.GONE);
            h.affilitesChip.setVisibility(View.GONE);
        }else {
            List<String> items = Arrays.asList(member.getTag_name().split(","));
          //  h.tagView.setVisibility(View.VISIBLE);
           //h.tagView.addTag(items);
            h.affilitesChip.setVisibility(View.VISIBLE);
            addChip(h.affilitesChip, items);
        }
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends ClubMemberHolder{

        private CircularImageView iv_profileImage;
        private TextView tv_FullName;
        private TagView tagView;
        private FlowLayout affilitesChip;

        public ViewHolder(View itemView, Context mContext) {
            super(itemView, mContext);
            iv_profileImage = itemView.findViewById(R.id.iv_profileImage);
            tv_FullName = itemView.findViewById(R.id.tv_FullName);
            tagView = itemView.findViewById(R.id.tagView);
            affilitesChip = itemView.findViewById(R.id.affilitesChip);
        }

        @Override
        protected ClubMember getProfile() {

            ClubMember club = memberList.get(getAdapterPosition());
            ClubMember member = new ClubMember();
            member.setFull_name(club.getFull_name());
            member.setUserId(club.getUserId());
            member.setProfile_image(club.getProfile_image());
            member.setContact_no(club.getContact_no());
            member.setContact_no_visibility(club.getContact_no_visibility());
            member.setClubId(club.getClubId());
            // member.isLiked = club.
            return member;


        }

        @Override
        protected void notyfyData(int pos) {
            notifyItemChanged(pos);
        }

        @Override
        protected boolean canEditNickName() {
            return false;
        }

        @Override
        protected void showProfileDetail(ClubMember member) {

        }

    }

    private void addChip(FlowLayout chipHolder, List<String> tagname) {

       // List<String> tagList = Arrays.asList(tagname.split("\\s*,\\s*"));
        for (String tag : tagname)
        {
            ChipView chip = new ChipView(mContext, ""+chipHolder.getChildCount(), false)
            {

                @Override
                public void setDeleteListner(ChipView chipView) {

                    Log.e("Chipid",chipView.getIdChip());
                }

                @Override
                public int getLayout() {
                    return R.layout.z_cus_chip_view_profile;
                }
            };
            chip.setText(tag);
            chipHolder.addView(chip);
        }
    }
}
