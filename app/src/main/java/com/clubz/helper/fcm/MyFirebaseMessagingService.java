package com.clubz.helper.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clubz.ClubZ;
import com.clubz.R;
import com.clubz.chat.AllChatActivity;
import com.clubz.chat.util.ChatUtil;
import com.clubz.data.local.pref.SessionManager;
import com.clubz.data.model.NotificationSesssion;
import com.clubz.ui.ads.activity.AdDetailsActivity;
import com.clubz.ui.club.ClubDetailActivity;
import com.clubz.ui.newsfeed.NewsFeedDetailActivity;
import com.clubz.ui.user_activities.activity.ActivitiesDetails;
import com.clubz.utils.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by dharmraj on 22/9/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From : " + remoteMessage.getFrom() + remoteMessage.getNotification().getBody());
        Log.e(TAG, "Data : " + remoteMessage.getFrom() + remoteMessage.getData().toString());

        if (remoteMessage.getData() != null) {

               if(SessionManager.getObj().isloggedin())
               handleNotificationType(remoteMessage);

        }
    }

    private void handleNotificationType(RemoteMessage remoteMessage) {
        String title = "";
        String body = "";
        String type = "";

        try {
            title = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_NotiFication_Title());
            body = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_NotiFication_Body());
            type = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_NotiFication_Type());

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            type = "";
        }

        /*Intent intent = new Intent("NOTIFICATIONCOUNT");
        if (type.equals("chat")) {
            //ChatActivity
            if (className.equals("MainActivity")){
                if (!MainActivity.BaseFragment.equals("ChatFragment")){
                    MainActivity.chatCount = MainActivity.chatCount + 1;
                    PreferenceConnector.writeInteger(this,PreferenceConnector.chatCount,MainActivity.chatCount);
                }
            }else if (!className.equals("ChatActivity")){
                MainActivity.chatCount = MainActivity.chatCount + 1;
                PreferenceConnector.writeInteger(this,PreferenceConnector.chatCount,MainActivity.chatCount);
            }

            intent.putExtra(Constant.COUNTYPE, "chatCount");
        } else {
            MainActivity.notificationCount = MainActivity.notificationCount + 1;
            PreferenceConnector.writeInteger(this,PreferenceConnector.notiCount,MainActivity.notificationCount);
            intent.putExtra(Constant.COUNTYPE, "notiCount");
        }
        sendBroadcast(intent);*/

        switch (type) {
            case "ads":
                String adsId = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_Ads_Id());
                sendNotificationAds(title, body, adsId);
                break;
            case "activity":
                String activityId = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_Activity_Id());
                sendNotificationActivity(title, body, activityId);
                break;
            case "club":
                String clubId = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_Club_Id());
                sendNotificationClub(title, body, clubId);
                break;
            case "newsfeed":
                String newsFeedId = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_News_Feed_Id());
                sendNotificationFeed(title, body, newsFeedId);
                break;
            case "chat":

                NotificationSesssion notifucation = SessionManager.getObj().getNotification();

                if(notifucation.getChat_notifications().equals("1")){
                    String chatFor = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_ChatFor());

                    if(chatFor.equals("activities")){
                        if(notifucation.getActivity_chat_notification().equals("1")){
                            String clubIdFrChat = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_Club_Id());
                            String historyId = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_HistoryId());
                            String historyName = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_HistoryName());
                            String userID = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_UserID());

                            if(!userID.equals(SessionManager.getObj().getUser().getId())){
                                sendNotificationChat(title, body,chatFor,clubIdFrChat,historyId,historyName);
                            }
                        }
                    }
                    else{
                        String clubIdFrChat = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_Club_Id());
                        String historyId = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_HistoryId());
                        String historyName = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_HistoryName());
                        String userID = remoteMessage.getData().get(NotificatioKeyUtil.Companion.getKey_UserID());

                        if(!userID.equals(SessionManager.getObj().getUser().getId())){
                            sendNotificationChat(title, body,chatFor,clubIdFrChat,historyId,historyName);
                        }
                    }


                }


                break;
        }
    }

    private void sendNotificationAds(String title, String message, String adId) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, iUniqueId, new Intent(this, AdDetailsActivity.class)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_From(), NotificatioKeyUtil.Companion.getValue_From_Notification())
                        .putExtra(NotificatioKeyUtil.Companion.getKey_Ads_Id(), adId)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotificationActivity(String title, String message, String activityId) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, iUniqueId, new Intent(this, ActivitiesDetails.class)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_From(), NotificatioKeyUtil.Companion.getValue_From_Notification())
                        .putExtra(NotificatioKeyUtil.Companion.getKey_Activity_Id(), activityId)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotificationClub(String title, String message, String clubId) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, iUniqueId, new Intent(this, ClubDetailActivity.class)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_From(), NotificatioKeyUtil.Companion.getValue_From_Notification())
                        .putExtra(NotificatioKeyUtil.Companion.getKey_Club_Id(), clubId)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotificationFeed(String title, String message, String newsFeedId) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, iUniqueId, new Intent(this, NewsFeedDetailActivity.class)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_From(), NotificatioKeyUtil.Companion.getValue_From_Notification())
                        .putExtra(NotificatioKeyUtil.Companion.getKey_News_Feed_Id(), newsFeedId)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotificationChat(String title, String message, String chatFor, String clubId, String historyId, String historyName) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, iUniqueId, new Intent(this, AllChatActivity.class)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_From(), NotificatioKeyUtil.Companion.getValue_From_Notification())
                        .putExtra(NotificatioKeyUtil.Companion.getKey_ChatFor(), chatFor)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_Club_Id(), clubId)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_HistoryId(), historyId)
                        .putExtra(NotificatioKeyUtil.Companion.getKey_HistoryName(), historyName)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(iUniqueId, notificationBuilder.build());
    }

}
