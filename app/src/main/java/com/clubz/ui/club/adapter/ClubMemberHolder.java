package com.clubz.ui.club.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.clubz.ClubZ;
import com.clubz.data.model.ClubMember;
import com.clubz.data.model.Profile;
import com.clubz.ui.dialogs.UserProfileDialog;
import com.clubz.ui.profile.ProfileActivity;

import org.jetbrains.annotations.NotNull;

public abstract class ClubMemberHolder extends RecyclerView.ViewHolder {

    protected Context mContext;

    public ClubMemberHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ClubMember member = getProfile();
                if (!member.getUserId().equals(ClubZ.Companion.getCurrentUser().getId())) {
                    showProfile();
                }
            }
        });
    }

    protected void showProfile(){

        final ClubMember member = getProfile();
        if(!member.getUserId().equals(ClubZ.Companion.getCurrentUser().getId())){
            final UserProfileDialog userProfileDialog = new UserProfileDialog(mContext, member, canEditNickName()) {
                @Override
                public void onProfileUpdate(String name) {
                    member.setUser_nickname(name);
                    notyfyData(getAdapterPosition());
                }

                @Override
                public void showError(@NotNull String msg) {
                    showToast(msg);
                }

                @Override
                public void onCallClicked() {
                   /* if (!user!!.userId.equals("")) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "${user?.country_code} ${user?.contact_no}"))
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show()*/
                }

            /*@Override
            public void onChatClicked() {
                showToast("chat clicked!");
            }*/

                @Override
                public void onLikeClicked(int isLIked) {
                    showToast("favorite clicked!");
                }

                @Override
                public void onFlagClicked() {
                    dismiss();


                    showProfileDetail(member);
                    Profile profile = new Profile();
                    profile.setUserId(member.getUserId());
                    profile.setFull_name(member.getFull_name());
                    profile.setProfile_image(member.getProfile_image());
                    mContext.startActivity(new Intent(mContext, ProfileActivity.class).putExtra("profile", profile));
                }
            };
            userProfileDialog.setCancelable(true);
            userProfileDialog.show();
        }
    }

    protected abstract ClubMember getProfile();
    protected abstract void notyfyData(int pos);
    protected abstract boolean canEditNickName();
    protected abstract void showProfileDetail(ClubMember member);

    private void showToast(String text){
        Toast.makeText(mContext,  text, Toast.LENGTH_SHORT).show();
    }
}
