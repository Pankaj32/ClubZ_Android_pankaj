package com.clubz.ui.club.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.clubz.ClubZ;
import com.clubz.data.model.ClubMember;
import com.clubz.data.model.User;
import com.clubz.ui.dialogs.UserProfileDialog;

import org.jetbrains.annotations.NotNull;

public abstract class ClubMemberHolder extends RecyclerView.ViewHolder{

    protected Context mContext;

    public ClubMemberHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });
    }

    protected void showProfile(){

        ClubMember member = getProfile();
        /*if(member.getClubUserId().equals(ClubZ.instance.getCurrentUser().getId())){

        }*/


        UserProfileDialog dialog = new UserProfileDialog(mContext, member) {
            @Override
            public void showError(@NotNull String msg) {
                showToast(msg);
            }

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

    protected abstract ClubMember getProfile();

    private void showToast(String text){
        Toast.makeText(mContext,  text, Toast.LENGTH_SHORT).show();
    }
}
