package com.clubz.ui.club.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.data.model.ClubMember;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterClubApplicant extends RecyclerView.Adapter<AdapterClubApplicant.ViewHolder>{

    private Context mContext;
    private List<ClubMember>applicantList;
    private Listner listner;

    public interface Listner{
        void onClickAction(ClubMember member, String status, int pos);
    }

    public AdapterClubApplicant(Context mContext, Listner listner) {
        this.mContext = mContext;
        this.listner = listner;
        applicantList = new ArrayList<>();
    }

    public void setApplicantList(List<ClubMember> applicantList) {
        this.applicantList = applicantList;
        notifyDataSetChanged();
    }

    public void removeApplicent(int pos){
        applicantList.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public AdapterClubApplicant.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_applicant_item_list, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.setUpClick();
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterClubApplicant.ViewHolder h, int position) {
        ClubMember member = applicantList.get(position);
        h.tv_FullName.setText(member.getFull_name());
        h.tv_location.setText(String.format("%s km | Requested on %s", member.distance(), member.getDate()));
        if(!TextUtils.isEmpty(member.getProfile_image())){
            Picasso.with(h.iv_profileImage.getContext()).load(member.getProfile_image()).into(h.iv_profileImage);
        }else Picasso.with(h.iv_profileImage.getContext()).load(R.drawable.ic_user_white).into(h.iv_profileImage);
    }


    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_FullName, tv_location, tv_discard, tv_accept;
        private ImageView iv_profileImage;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_FullName = itemView.findViewById(R.id.tv_FullName);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_discard = itemView.findViewById(R.id.tv_discard);
            tv_accept = itemView.findViewById(R.id.tv_accept);
            iv_profileImage = itemView.findViewById(R.id.iv_profileImage);

        }

        public void setUpClick(){
            tv_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(listner!=null) listner.onClickAction(applicantList.get(pos), "1", pos); // answerStatus:1 (1 accept, 2 means reject
                }
            });

            tv_discard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(listner!=null) listner.onClickAction(applicantList.get(pos), "2", pos);
                }
            });
        }
    }
}
