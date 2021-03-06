package com.clubz.ui.club.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.clubz.ClubZ;
import com.clubz.ui.cv.ChipView;
import com.clubz.ui.cv.FlowLayout;
import com.clubz.ui.cv.chipview.TagView;
import com.clubz.R;
import com.clubz.data.model.ClubMember;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AdapterOwnClubMember extends RecyclerView.Adapter<AdapterOwnClubMember.ViewHolder> {

    private Context mContext;
    private List<ClubMember> memberList;
    private Listner listner;
    private boolean isPrivateClub;

    public interface Listner {
        void onUpdateClubMember(ClubMember member, int pos);

        void onTagAdd(String tag, ClubMember member, int pos);
        void onTagDelete(String tag);


        void onFocus();
    }

    public AdapterOwnClubMember(Context mContext, List<ClubMember> memberList, Listner listner, boolean isPrivateClub) {
        this.mContext = mContext;
        this.memberList = memberList;
        this.listner = listner;
        this.isPrivateClub = isPrivateClub;
    }

    public void updateMember(ClubMember member, int pos) {
        ClubMember clubMember = memberList.get(pos);
        clubMember = member;
        notifyItemChanged(pos);
    }

    public void addTag(String tag, int pos, String tagid) {
        memberList.get(pos).addTag(tag);
        memberList.get(pos).addTagID(tagid);
        notifyItemChanged(pos);
    }
    public void removeTag(String tagiD) {
        int valepost=0;
        if(memberList.size()>0){
           for(int i=0;i<memberList.size();i++){

               List<String> itemstagid =new LinkedList<> (Arrays.asList(memberList.get(i).getTag_id().split(",")));
               List<String> itemstagname =new LinkedList<> (Arrays.asList(memberList.get(i).getTag_name().split(",")));

               for(int j =0;j<itemstagid.size();j++){

                   if(itemstagid.get(j).equals(tagiD)){
                       try{
                           itemstagid.remove(j);
                           itemstagname.remove(j);
                           valepost = i;
                       }
                       catch (Exception e){
                           e.printStackTrace();
                       }

                       memberList.get(valepost).setTag_id("");
                       memberList.get(valepost).setTag_name("");
                       for(int k =0;k<itemstagid.size();k++){

                           addTag(itemstagname.get(k),valepost,itemstagid.get(k));

                       }
                   }
                   else
                   {
                       //memberList.get(i).addTagID(itemstagid.get(j));
                   }
               }

           }

        }
        notifyItemChanged(valepost);
    }

    @Override
    public AdapterOwnClubMember.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_current_members_item_list, parent, false);
        ViewHolder adapter = new AdapterOwnClubMember.ViewHolder(v, mContext);
        adapter.setSwitchClick();
        return adapter;
    }

    public void setMemberList(List<ClubMember> memberList) {
        this.memberList = memberList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(AdapterOwnClubMember.ViewHolder h, int position) {
        final ClubMember member = memberList.get(position);

        h.tv_FullName.setText(member.getNickname());
        if (member.getUserId().equals(ClubZ.Companion.getCurrentUser().getId())) {
            h.edAffiliates.setVisibility(View.GONE);
            h.switch1.setVisibility(View.GONE);
            h.divider.setVisibility(View.GONE);
        } else {
            h.edAffiliates.setVisibility(View.VISIBLE);
            h.switch1.setVisibility(View.VISIBLE);
            h.divider.setVisibility(View.VISIBLE);
            h.switch1.setChecked(member.getMember_status().equals("1"));
            if (!TextUtils.isEmpty(member.getProfile_image())) {
                Picasso.with(h.iv_profileImage.getContext()).load(member.getProfile_image()).fit().into(h.iv_profileImage);
            } else
                Picasso.with(h.iv_profileImage.getContext()).load(R.drawable.ic_user_white).fit().into(h.iv_profileImage);

            if (TextUtils.isEmpty(member.getTag_name())) {
                h.tagView.distroy();
                h.tagView.setVisibility(View.GONE);
                h.divider.setVisibility(View.GONE);
                h.edAffiliates.setVisibility(View.VISIBLE);
                h.affilitesChip.setVisibility(View.GONE);
            } else {
                h.divider.setVisibility(View.VISIBLE);
                h.affilitesChip.setVisibility(View.VISIBLE);
                List<String> items = Arrays.asList(member.getTag_name().split(","));
                List<String> itemstagid = Arrays.asList(member.getTag_id().split(","));
                if (items.size() > 0)
                    h.affilitesChiplinearlayout.removeAllViews();
                    addChip(h.affilitesChiplinearlayout, items,itemstagid);

                if (isPrivateClub && items.size() >= 1) {
                    h.edAffiliates.setVisibility(View.GONE);
                    h.divider.setVisibility(View.GONE);
                } else if (items.size() >= 3) {
                    h.edAffiliates.setVisibility(View.GONE);
                    h.divider.setVisibility(View.GONE);
                }
            }
        }
    }


    private void addView(ViewHolder holder, List<String> items) {
        holder.tagView.setVisibility(View.VISIBLE);
        holder.tagView.addTag(items);
        holder.edAffiliates.setText("");
    }


    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class ViewHolder extends ClubMemberHolder {

        private ImageView iv_profileImage;
        private TextView tv_FullName;
        private Switch switch1;
        private EditText edAffiliates;
        private TagView tagView;
        private HorizontalScrollView affilitesChip;
        private LinearLayout affilitesChiplinearlayout;
        private View divider;

        public ViewHolder(View itemView, Context context) {
            super(itemView, context);
            iv_profileImage = itemView.findViewById(R.id.iv_profileImage);
            tv_FullName = itemView.findViewById(R.id.tv_FullName);
            switch1 = itemView.findViewById(R.id.switch1);
            edAffiliates = itemView.findViewById(R.id.edAffiliates);
            tagView = itemView.findViewById(R.id.tagView);
            divider = itemView.findViewById(R.id.divider);
            affilitesChip = itemView.findViewById(R.id.affilitesChip);
            affilitesChiplinearlayout= itemView.findViewById(R.id.affilitesChiplinear);

        }

        @Override
        protected ClubMember getProfile() {
            return memberList.get(getAdapterPosition());
        }

        @Override
        protected void notyfyData(int pos) {
            notifyItemChanged(pos);
        }

        @Override
        protected boolean canEditNickName() {
            return true;
        }

        @Override
        protected void showProfileDetail(ClubMember member) {

        }


        public void setSwitchClick() {

            switch1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listner != null) listner.onUpdateClubMember(memberList.get(pos), pos);
                }
            });

            edAffiliates.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (listner != null) listner.onFocus();
                }
            });

            edAffiliates.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        int pos = getAdapterPosition();
                        String tag = edAffiliates.getText().toString().trim();
                        if (!TextUtils.isEmpty(tag) && listner != null)
                            edAffiliates.setText("");
                            listner.onTagAdd(tag, memberList.get(pos), pos);
                    }
                    return false;
                }
            });
        }
    }

    private void addChip(LinearLayout chipHolder, List<String> tagname, List<String> tagid) {

        // List<String> tagList = Arrays.asList(tagname.split("\\s*,\\s*"));
         int count= 0;
        for (String tag : tagname)
        {

            ChipView chip = new ChipView(mContext, tagid.get(count), true,true)
            {

                @Override
                public void setDeleteListner(ChipView chipView) {
                    Log.e("Chipid",chipView.getIdChip());
                    listner.onTagDelete(chipView.getIdChip());

                }

                @Override
                public int getLayout() {
                    return R.layout.z_cus_chip_view_profile;
                }
            };
            chip.setText(tag);
            chipHolder.addView(chip);
            count++;
        }
    }
}
