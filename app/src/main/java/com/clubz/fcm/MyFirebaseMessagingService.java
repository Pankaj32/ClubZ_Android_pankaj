package com.clubz.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Date;
import java.util.Random;


/**
 * Created by dharmraj on 22/9/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static String CHAT_ROOM = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       /* Util.e(TAG, "From : " + remoteMessage.getFrom() + remoteMessage.getNotification().getBody());
        Util.e(TAG, "Data : " + remoteMessage.getFrom() + remoteMessage.getData().toString());

        Random r = new Random();
        int random = r.nextInt(899 - 65) + 65;
        int m = ((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE)) + random;
        String ref_id = remoteMessage.getData().get("reference_id");
        String type = remoteMessage.getData().get("type");
        Intent intent = new Intent(this, Home_Activity.class);
        intent.putExtra(Constants.INTENT_MSG_DETAIL, remoteMessage.getData().toString());
        intent.putExtra(Constants.INTENT_TYPE,type );
        intent.putExtra(Constants.INTENT_MESSAGE, remoteMessage.getData().get("body"));
        intent.putExtra(Constants.INTENT_REF_ID,ref_id );
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_white)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle().bigText((remoteMessage.getData().get("body")))) // this message will be shown
                .setContentIntent(PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_ONE_SHOT));
        if(ref_id.equals(CHAT_ROOM) && type.equals(Constants.N_TYPE_CHAT)) return;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(m, notificationBuilder.build());*/
    }

}
