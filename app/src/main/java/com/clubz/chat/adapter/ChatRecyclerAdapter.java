package com.clubz.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clubz.ClubZ;
import com.clubz.R;
import com.clubz.chat.model.ChatBean;
import com.clubz.utils.DateTimeUtil;
import com.clubz.utils.KeyboardUtil;
import com.loopeer.shadow.ShadowView;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiInformation;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.EmojiUtils;

import java.util.List;

/**
 * Created by chiranjib on 6/06/18.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    //private ChatAdapterClickListner chatAdapterClickListner;
    private Context mContext;
    private List<ChatBean> mChatBeen;
    private String mUid;
    private onClick onClick;

    public ChatRecyclerAdapter(Context mContext, List<ChatBean> chatBeen,onClick onClick/*, ChatAdapterClickListner chatAdapterClickListner*/) {
        this.mContext = mContext;
        //  this.chatAdapterClickListner = chatAdapterClickListner;
        this.mChatBeen = chatBeen;
        this.mUid = ClubZ.Companion.getCurrentUser().getId();
        this.onClick = onClick;
    }

    public void add(ChatBean chatBean) {
        mChatBeen.add(chatBean);
        notifyDataSetChanged();
        notifyItemInserted(mChatBeen.size());
    }

    public void clear() {
        mChatBeen.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatBean chatBean = mChatBeen.get(position);
        if (TextUtils.equals(chatBean.getSenderId(), mUid)) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, final int position) {
        final ChatBean chatBean = mChatBeen.get(position);

        final String message = chatBean.getMessage();
        myChatViewHolder.shadowTxt.setVisibility(View.VISIBLE);
        myChatViewHolder.shadowImg.setVisibility(View.VISIBLE);
        myChatViewHolder.smlProgress.setVisibility(View.VISIBLE);
        if (chatBean.getImage() == 1) {
            myChatViewHolder.shadowTxt.setVisibility(View.GONE);
            Picasso.with(myChatViewHolder.chatImageview.getContext()).load(chatBean.getImageUrl()).fit().into(myChatViewHolder.chatImageview);
        } else {
            myChatViewHolder.shadowImg.setVisibility(View.GONE);
            myChatViewHolder.smlProgress.setVisibility(View.GONE);
           // myChatViewHolder.txtChatMessage.setText(message);
            setTextEmoji(myChatViewHolder.txtChatMessage, message);
        }
        if (position==0) {
            myChatViewHolder.userTxt.setText(R.string.you);
        }else {
            if (mChatBeen.get(position - 1).getSenderName().equals(chatBean.getSenderName())) {
                myChatViewHolder.userTxt.setVisibility(View.GONE);
            } else {
                myChatViewHolder.userTxt.setVisibility(View.VISIBLE);
                myChatViewHolder.userTxt.setText(R.string.you);
            }
        }
        //   chatBean.timeForDelete
        myChatViewHolder.dateTime.setText(DateTimeUtil.getDayDifference(mContext, DateTimeUtil.ConvertMilliSecondsToFormattedDate(String.valueOf(chatBean.getTimestamp())),
                DateTimeUtil.getCurrentDate() + " " + DateTimeUtil.getCurrentTime()));
     //   myChatViewHolder.dateTime.setText(DateTimeUtil.ConvertMilliSecondsToDateAndTime(String.valueOf(chatBean.getTimestamp())));
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, final int position) {
        final ChatBean chatBean = mChatBeen.get(position);

        final String message = chatBean.getMessage();
        otherChatViewHolder.shadowTxt.setVisibility(View.VISIBLE);
        otherChatViewHolder.shadowImg.setVisibility(View.VISIBLE);
        otherChatViewHolder.smlProgress.setVisibility(View.VISIBLE);
        if (position==0) {
            otherChatViewHolder.userTxt.setText(chatBean.getSenderName());
            otherChatViewHolder.userTxt.setVisibility(View.VISIBLE);
        }else {
            if (mChatBeen.get(position - 1).getSenderName().equals(chatBean.getSenderName())) {
                otherChatViewHolder.userTxt.setVisibility(View.GONE);
            } else {
                otherChatViewHolder.userTxt.setVisibility(View.VISIBLE);
                otherChatViewHolder.userTxt.setText(chatBean.getSenderName());
            }
        }
        if (chatBean.getImage() == 1) {
            otherChatViewHolder.shadowTxt.setVisibility(View.GONE);
            Picasso.with(otherChatViewHolder.chatImageview.getContext()).load(chatBean.getImageUrl()).fit().into(otherChatViewHolder.chatImageview);
        } else {
            otherChatViewHolder.shadowImg.setVisibility(View.GONE);
            otherChatViewHolder.smlProgress.setVisibility(View.GONE);
            setTextEmoji(otherChatViewHolder.txtChatMessage, message);
           // otherChatViewHolder.txtChatMessage.setText(message);
        }
        otherChatViewHolder.dateTime.setText(DateTimeUtil.getDayDifference(mContext, DateTimeUtil.ConvertMilliSecondsToFormattedDate(String.valueOf(chatBean.getTimestamp())),
                DateTimeUtil.getCurrentDate() + " " + DateTimeUtil.getCurrentTime()));
    //    otherChatViewHolder.dateTime.setText(DateTimeUtil.ConvertMilliSecondsToDateAndTime(String.valueOf(chatBean.getTimestamp())));


    }

    @Override
    public int getItemCount() {
        if (mChatBeen != null) {
            return mChatBeen.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChatBeen.get(position).getSenderId(), mUid)) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTime, userTxt;
        private ImageView chatImageview;
        private EmojiTextView txtChatMessage;
      //  private TextView txtChatMessage;
        private ProgressBar smlProgress;
        private ShadowView shadowTxt;
        private ShadowView shadowImg;


        MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
            userTxt = itemView.findViewById(R.id.userTxt);
            dateTime = itemView.findViewById(R.id.dateTime);
            chatImageview = itemView.findViewById(R.id.chat_imageview);
            smlProgress = itemView.findViewById(R.id.smlProgress);
            shadowTxt = itemView.findViewById(R.id.shadowTxt);
            shadowImg = itemView.findViewById(R.id.shadowImg);

            chatImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatBean chatBean = mChatBeen.get(getAdapterPosition());
                    onClick.onImageClick(chatBean.getImageUrl());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onItemClick();
                }
            });
        }
    }

    private class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTime, userTxt;
        private ImageView chatImageview;
       private EmojiTextView txtChatMessage;
       // private TextView txtChatMessage;
        private ProgressBar smlProgress;
        private ShadowView shadowTxt;
        private ShadowView shadowImg;

        OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
            userTxt = itemView.findViewById(R.id.userTxt);
            dateTime = itemView.findViewById(R.id.dateTime);
            chatImageview = itemView.findViewById(R.id.chat_imageview);
            smlProgress = itemView.findViewById(R.id.smlProgress);
            shadowTxt = itemView.findViewById(R.id.shadowTxt);
            shadowImg = itemView.findViewById(R.id.shadowImg);
            chatImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatBean chatBean = mChatBeen.get(getAdapterPosition());
                    onClick.onImageClick(chatBean.getImageUrl());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onItemClick();
                }
            });
        }
    }

    private void setTextEmoji(EmojiTextView emojiTextView, String message) {
        final EmojiInformation emojiInformation = EmojiUtils.emojiInformation(message);
        final int res;

        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() == 1) {
            res = R.dimen.emoji_size_single_emoji;
        } else if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() > 1) {
            res = R.dimen.emoji_size_only_emojis;
        } else {
            res = R.dimen.emoji_size_default;
        }
        emojiTextView.setEmojiSizeRes(res, false);
        emojiTextView.setText(message);
    }

   public interface onClick{
         void onImageClick(String imgUrl);
         void onItemClick();
    }
}
