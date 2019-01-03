package com.clubz.data.local.pref;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.clubz.ClubZ;
import com.clubz.data.local.db.repo.AllActivitiesRepo;
import com.clubz.data.local.db.repo.AllAdsRepo;
import com.clubz.data.local.db.repo.AllClubRepo;
import com.clubz.data.local.db.repo.AllEventsRepo;
import com.clubz.data.local.db.repo.AllFabContactRepo;
import com.clubz.data.local.db.repo.AllFeedsRepo;
import com.clubz.data.local.db.repo.ClubNameRepo;
import com.clubz.data.model.ClubName;
import com.clubz.data.model.MembershipPlan;
import com.clubz.data.model.NotificationSesssion;
import com.clubz.data.model.UpdateAsync;
import com.clubz.data.model.UserLocation;
import com.clubz.ui.authentication.SignupActivity;
import com.clubz.data.model.User;
import com.clubz.utils.Constants;
import com.google.gson.Gson;

import java.util.Locale;

/**
 * Created by mindiii on 2/26/18.
 */

public class SessionManager {

    private SharedPreferences mypref;
    private SharedPreferences mypreflan;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorlan;
    private String Pref_Name = "ClubZ_app";
    private String Pref_Name_lan = "ClubZ_app_lan";
    private String IS_LOGED_IN = "is_logged_in ";
    private static SessionManager obj;

    private SessionManager(Context context) {
        mypref = context.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
        mypreflan = context.getSharedPreferences(Pref_Name_lan, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editorlan = mypreflan.edit();
        editor.apply();
        editorlan.apply();
        obj = this;
    }

    public static SessionManager getObj() {
        if (obj == null) new SessionManager(ClubZ.instance.getApplicationContext());
        return obj;
    }

    public boolean createSession(User user) {
        editor.putString(Constants._id, user.getId().trim());
        editor.putString(Constants._full_name, user.getFull_name().trim());
        editor.putString(Constants._first_name, user.getFirst_name().trim());
        editor.putString(Constants._last_name, user.getLast_name().trim());
        editor.putString(Constants._social_id, user.getSocial_id().trim());
        editor.putString(Constants._social_type, user.getSocial_type().trim());
        editor.putString(Constants._email, user.getEmail().trim());
        editor.putString(Constants._country_code, user.getCountry_code().trim());
        editor.putString(Constants._contact_no, user.getContact_no().trim());
        editor.putString(Constants._landline_no, user.getLandline_no().trim());
        editor.putString(Constants._profile_image, user.getProfile_image().trim());
        editor.putString(Constants._is_verified, user.is_verified().trim());
        editor.putString(Constants._auth_token, user.getAuth_token().trim());
        editor.putString(Constants._device_type, user.getDevice_type().trim());
        editor.putString(Constants._device_token, user.getDevice_token().trim());

        editor.putString(Constants._about_me, user.getAbout_me().trim());
        editor.putString(Constants._about_me_visibility, user.getAbout_me_visibility().trim());
        editor.putString(Constants._dob, user.getDob().trim());

        editor.putString(Constants._skills, user.getSkills().trim());
        editor.putString(Constants._interests, user.getInterests().trim());
        editor.putString(Constants._affiliates, user.getAffiliates().trim());

        editor.putString(Constants._dob_visibility, user.getDob_visibility().trim());
        editor.putString(Constants._contact_no_visibility, user.getContact_no_visibility().trim());
        editor.putString(Constants._landline_no_visibility, user.getLandline_no_visibility().trim());
        editor.putString(Constants._email_visibility, user.getEmail_visibility().trim());
        editor.putString(Constants._affiliates_visibility, user.getAffiliates_visibility().trim());
        editor.putString(Constants._interest_visibility, user.getInterest_visibility().trim());
        editor.putString(Constants._skills_visibility, user.getSkills_visibility().trim());
        editor.putString(Constants._news_notifications, user.getNews_notifications().trim());
        editor.putString(Constants._activities_notifications, user.getActivities_notifications().trim());
        editor.putString(Constants._ads_notifications, user.getAds_notifications().trim());
        editor.putString(Constants._show_profile, user.getShow_profile().trim());
        editor.putString(Constants._allow_anyone, user.getAllow_anyone().trim());
        editor.putString(Constants._hasAffiliates, user.getHasAffiliates().trim());

        editor.putString(Constants._club_owner_id, user.getClubz_owner_id().trim());
        editor.putString(Constants._club_owner_full_name, user.getClubz_owner_name().trim());
        editor.putString(Constants._club_owner_profile_pic, user.getClubz_owner_profileImage().trim());


        editor.putBoolean(IS_LOGED_IN, true);
        editor.apply();
        //ClubZ.instance.setCurrentUser(user);
        return true;
    }



    public User getUser() {

        if (mypref.getString(Constants._id, "").isEmpty()) {
            return null;
        } else {
            User user = new User();
            user.setId(mypref.getString(Constants._id, ""));
            user.setFull_name(mypref.getString(Constants._full_name, ""));
            user.setFirst_name(mypref.getString(Constants._first_name, ""));
            user.setLast_name(mypref.getString(Constants._last_name, ""));
            user.setSocial_id(mypref.getString(Constants._social_id, ""));
            user.setSocial_type(mypref.getString(Constants._social_type, ""));
            user.setEmail(mypref.getString(Constants._email, ""));
            user.setCountry_code(mypref.getString(Constants._country_code, ""));
            user.setContact_no(mypref.getString(Constants._contact_no, ""));
            user.setLandline_no(mypref.getString(Constants._landline_no, ""));
            user.setProfile_image(mypref.getString(Constants._profile_image, ""));
            user.set_verified(mypref.getString(Constants._is_verified, ""));
            user.setAuth_token(mypref.getString(Constants._auth_token, ""));
            user.setDevice_type(mypref.getString(Constants._device_type, ""));
            user.setDevice_token(mypref.getString(Constants._device_token, ""));
            user.setAbout_me(mypref.getString(Constants._about_me, ""));
            user.setAbout_me_visibility(mypref.getString(Constants._about_me_visibility, ""));
            user.setDob(mypref.getString(Constants._dob, ""));

            user.setSkills(mypref.getString(Constants._skills, ""));
            user.setInterests(mypref.getString(Constants._interests, ""));
            user.setAffiliates(mypref.getString(Constants._affiliates, ""));

            user.setDob_visibility(mypref.getString(Constants._dob_visibility, ""));
            user.setContact_no_visibility(mypref.getString(Constants._contact_no_visibility, ""));
            user.setLandline_no_visibility(mypref.getString(Constants._landline_no_visibility, ""));
            user.setEmail_visibility(mypref.getString(Constants._email_visibility, ""));
            user.setAffiliates_visibility(mypref.getString(Constants._affiliates_visibility, ""));
            user.setInterest_visibility(mypref.getString(Constants._interest_visibility, ""));
            user.setSkills_visibility(mypref.getString(Constants._skills_visibility, ""));
            user.setNews_notifications(mypref.getString(Constants._news_notifications, ""));
            user.setActivities_notifications(mypref.getString(Constants._activities_notifications, ""));
            user.setAds_notifications(mypref.getString(Constants._ads_notifications, ""));
            user.setShow_profile(mypref.getString(Constants._show_profile, ""));
            user.setAllow_anyone(mypref.getString(Constants._allow_anyone, ""));
            user.setHasAffiliates(mypref.getString(Constants._hasAffiliates, ""));
            user.setUserCity(mypref.getString(Constants._userCity, ""));

            user.setClubz_owner_id(mypref.getString(Constants._club_owner_id, ""));
            user.setClubz_owner_name(mypref.getString(Constants._club_owner_full_name, ""));
            user.setClubz_owner_profileImage(mypref.getString(Constants._club_owner_profile_pic, ""));
            return user;
        }
    }

    //********Membership plan*************

    public boolean createMembershipSession(MembershipPlan plan) {
        editor.putString(Constants._membershipPlanId, plan.getPlanDetails().getMembershipPlanId().trim());
        editor.putString(Constants._plan_name, plan.getPlanDetails().getPlan_name().trim());
        editor.putString(Constants._plan_price, plan.getPlanDetails().getPlan_price().trim());
        editor.putString(Constants._plan_type, plan.getPlanDetails().getPlan_type().trim());
        editor.putString(Constants._club_join, plan.getPlanDetails().getClub_join().trim());
        editor.putString(Constants._club_create, plan.getPlanDetails().getClub_create());
        editor.putString(Constants._news_read, plan.getPlanDetails().getNews_read());
        editor.putString(Constants._news_create, plan.getPlanDetails().getNews_create());
        editor.putString(Constants._activity_join, plan.getPlanDetails().getActivity_join());
        editor.putString(Constants._activity_create,plan.getPlanDetails().getActivity_create());
        editor.putString(Constants._chat_read, plan.getPlanDetails().getChat_read());
        editor.putString(Constants._chat_create, plan.getPlanDetails().getClub_join());
        editor.putString(Constants._ads_read, plan.getPlanDetails().getAds_read());
        editor.putString(Constants._ads_create, plan.getPlanDetails().getAds_create());
        editor.putString(Constants._status, plan.getPlanDetails().getStatus());
        editor.putString(Constants._crd, plan.getPlanDetails().getCrd());
        editor.apply();
        return true;
    }



    public MembershipPlan.PlanDetailsBean getMembershipPlan() {

        if (mypref.getString(Constants._membershipPlanId, "").isEmpty()) {
            return null;
        } else {
            MembershipPlan.PlanDetailsBean plan = new MembershipPlan.PlanDetailsBean();
            plan.setMembershipPlanId(mypref.getString(Constants._membershipPlanId,""));
            plan.setPlan_name(mypref.getString(Constants._plan_name, ""));
            plan.setPlan_price(mypref.getString(Constants._plan_price, ""));
            plan.setPlan_type(mypref.getString(Constants._plan_type, ""));
            plan.setClub_join(mypref.getString(Constants._club_join, ""));
            plan.setClub_create(mypref.getString(Constants._club_create, ""));
            plan.setNews_read(mypref.getString(Constants._news_read, ""));
            plan.setNews_create(mypref.getString(Constants._news_create, ""));
            plan.setActivity_join(mypref.getString(Constants._activity_join, ""));
            plan.setActivity_create(mypref.getString(Constants._activity_create, ""));
            plan.setChat_read(mypref.getString(Constants._chat_read, ""));
            plan.setChat_create(mypref.getString(Constants._chat_create, ""));
            plan.setAds_read(mypref.getString(Constants._ads_read, ""));
            plan.setAds_create(mypref.getString(Constants._ads_create, ""));

            return plan;
        }
    }

    //********Notification plan*************


    public boolean createNotificationSession(NotificationSesssion notify) {
        editor.putString(Constants._allow_notification, notify.getNotification_status().trim());
        editor.putString(Constants._news_notification, notify.getNews_notifications().trim());
        editor.putString(Constants._activity_notification, notify.getActivities_notifications().trim());
        editor.putString(Constants._chat_notification, notify.getChat_notifications().trim());
        editor.putString(Constants._ads_notification, notify.getAds_notifications().trim());
        editor.putString(Constants._activity_confirmed_notification, notify.getDate_confirmed_notification().trim());
        editor.putString(Constants._activity_chat_notification, notify.getActivity_chat_notification().trim());
        editor.putString(Constants._activity_cancel_notification, notify.getDate_cancelled_notification().trim());
        editor.apply();
        return true;
    }

    public NotificationSesssion getNotification(){

        NotificationSesssion notifys = new NotificationSesssion();
        notifys.setNotification_status(mypref.getString(Constants._allow_notification, ""));
        notifys.setNews_notifications(mypref.getString(Constants._news_notification, ""));
        notifys.setActivities_notifications(mypref.getString(Constants._activity_notification, ""));
        notifys.setChat_notifications(mypref.getString(Constants._chat_notification, ""));
        notifys.setAds_notifications(mypref.getString(Constants._ads_notification, ""));
        notifys.setDate_confirmed_notification(mypref.getString(Constants._activity_confirmed_notification, ""));
        notifys.setActivity_chat_notification(mypref.getString(Constants._activity_chat_notification, ""));
        notifys.setDate_cancelled_notification(mypref.getString(Constants._activity_cancel_notification, ""));
        return  notifys;

    }



    public void setUpdateAppData(UpdateAsync update) {
        if (update != null) {
            String str = new Gson().toJson(update);
            if (!TextUtils.isEmpty(str)) {
                editor.putString("updatePref", str);
                editor.apply();
            }
        }
    }

    public UpdateAsync getUpdate() {
        String str = mypref.getString("updatePref", null);
        if (str == null)
            return new UpdateAsync();
        else return new Gson().fromJson(str, UpdateAsync.class);
    }

    public String getLanguage() {
        return mypreflan.getString(Constants._userLanguage, Locale.getDefault().getLanguage());
    }

    public void setLanguage(String language) {
        editorlan.putString(Constants._userLanguage, language);
        editorlan.apply();
    }


    public void setAffiliates(String hasaffiliates) {
        editor.putString(Constants._hasAffiliates, hasaffiliates);
        editor.apply();
    }
    public String getAffiliates() {
        return mypref.getString(Constants._hasAffiliates, "");
    }
    public UserLocation getLastKnownLocation() {
        String text = mypref.getString(Constants._userLastLocation, "");
        if (!text.isEmpty()) {
            Gson gson = new Gson();
            return gson.fromJson(text, UserLocation.class);
        }
        return null;
    }

    public void setLocation(UserLocation location) {
        if (location != null) {
            /*ClubZ.Companion.setLatitude(location.getLatitude());
            ClubZ.Companion.setLongitude(location.getLongitude());*/
            Gson gson = new Gson();
            editor.putString(Constants._userLastLocation, gson.toJson(location));
            editor.apply();
        }
    }
    public void setCity(String city) {
        if (city != null) {
            editor.putString(Constants._userCity,city);
            editor.apply();
        }
    }
    public String getCity() {
         return mypref.getString(Constants._userCity, "");
    }

    public boolean isloggedin() {
        return mypref.getBoolean(IS_LOGED_IN, false);
    }

    public void logout(Context activity) {
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        editor.clear();
        editor.apply();
        new ClubNameRepo().deleteTable();
        new ClubNameRepo().deleteTable();
        new AllClubRepo().deleteTable();
        new AllAdsRepo().deleteTable();
        new AllFeedsRepo().deleteTable();
        new AllActivitiesRepo().deleteTable();
        new AllEventsRepo().deleteTable();
        new AllFabContactRepo().deleteTable();
        ClubZ.Companion.clearVirtualSession();
        Intent i = new Intent(activity, SignupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
    }

    public void logout(Activity activity) {
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        editor.clear();
        editor.apply();
        new ClubNameRepo().deleteTable();
        new ClubNameRepo().deleteTable();
        new AllClubRepo().deleteTable();
        new AllAdsRepo().deleteTable();
        new AllFeedsRepo().deleteTable();
        new AllActivitiesRepo().deleteTable();
        new AllEventsRepo().deleteTable();
        new AllFabContactRepo().deleteTable();
        ClubZ.Companion.clearVirtualSession();
        Intent i = new Intent(activity, SignupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
        activity.finish();
    }
}
