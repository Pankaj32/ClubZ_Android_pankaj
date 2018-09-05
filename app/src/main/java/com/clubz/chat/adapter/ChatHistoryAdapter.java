package com.clubz.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.chat.model.ChatHistoryBean;
import com.clubz.utils.DateTimeUtil;

import com.clubz.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chiranjib on 01/09/18.
 */

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
    private List<ChatHistoryBean> mChatHistorylist;
 //   private MyOnCheckListioner myOnCheckListioner;
    private Context mContext;
    private OnItemClick onItemClick;

    public ChatHistoryAdapter(Context context, List<ChatHistoryBean> mChatHistorylist,OnItemClick onItemClick) {
        this.mContext = context;
        this.mChatHistorylist = mChatHistorylist;
        this.onItemClick=onItemClick;
      //  this.myOnCheckListioner = myOnCheckListioner;
    }

    public void add(ChatHistoryBean historyBean) {
        mChatHistorylist.add(historyBean);
        notifyItemInserted(mChatHistorylist.size() - 1);
    }

    @Override
    public ChatHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHistoryAdapter.ViewHolder holder, final int position) {
        final ChatHistoryBean historyBean = mChatHistorylist.get(position);
        String imgPath = "";

        imgPath = historyBean.getProfilePic();
        holder.title.setText(historyBean.getHistoryName());
        if (historyBean.getImage()==1) {
            holder.lastMsg.setVisibility(View.GONE);
            holder.msgImg.setVisibility(View.VISIBLE);
        } else {
            holder.lastMsg.setVisibility(View.VISIBLE);
            holder.msgImg.setVisibility(View.GONE);
            holder.lastMsg.setText(historyBean.getMessage());
        }

        /*holder.dateTime.setText(DateTimeUtil.getDayDifference(DateTimeUtil.ConvertMilliSecondsToFormattedDate(String.valueOf(historyBean.getTimestamp())),
                DateTimeUtil.getCurrentDate() + " " + DateTimeUtil.getCurrentTime()));*/
        holder.dateTime.setText(DateTimeUtil.ConvertMilliSecondsToDateAndTime(String.valueOf(historyBean.getTimestamp())));
        if (!TextUtils.isEmpty(imgPath)) {
            try {
                Picasso.with(holder.profileImage.getContext())
                        .load(imgPath)
                        .placeholder(R.drawable.user_place_holder)
                        .fit()
                        .into(holder.profileImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });
            } catch (Exception e) {

            }
        }else if (TextUtils.isEmpty(imgPath)){
            holder.profileImage.setImageResource(R.drawable.user_place_holder);
        }
        if (position==mChatHistorylist.size()-1){
            holder.bottomView.setVisibility(View.GONE);
        }else {
            holder.bottomView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mChatHistorylist != null) {
            return mChatHistorylist.size();
        }
        return 0;
    }

    public ChatHistoryBean getUser(int position) {
        return mChatHistorylist.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, lastMsg, dateTime;
        private ImageView profileImage,msgImg;
        private View bottomView;

        ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.lastMsg = itemView.findViewById(R.id.lastMsg);
            this.profileImage = itemView.findViewById(R.id.profileImage);
            this.dateTime = itemView.findViewById(R.id.dateTime);
            this.msgImg = itemView.findViewById(R.id.msgImg);
            this.bottomView = itemView.findViewById(R.id.bottomView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                onItemClick.onItemClick(mChatHistorylist.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClick{
        void onItemClick(ChatHistoryBean historyBean);
    }
}