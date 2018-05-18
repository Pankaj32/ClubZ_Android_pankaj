package com.clubz.ui.club.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.clubz.ui.dialogs.UserProfileDialog;

public class ClubMemberHolder extends RecyclerView.ViewHolder{

    protected Context mContext;

    public ClubMemberHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileDialog dialog = new UserProfileDialog(mContext) {
                    @Override
                    public void onCallClicked() {
                        showToast("call clicked!");
                    }

                    @Override
                    public void onChatClicked() {
                        showToast("chat clicked!");
                    }

                    @Override
                    public void onLikeClicked() {
                        showToast("favorite clicked!");
                    }

                    @Override
                    public void onFlagClicked() {
                        showToast("flag clicked!");
                    }
                };
                dialog.setCancelable(true);
                dialog.show();
            }
        });
    }

    private void showToast(String text){
        Toast.makeText(mContext,  text, Toast.LENGTH_SHORT).show();
    }
}
