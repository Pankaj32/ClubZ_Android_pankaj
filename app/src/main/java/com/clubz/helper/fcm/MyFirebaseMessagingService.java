package com.clubz.helper.fcm;

import android.util.Log;

import com.clubz.utils.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by dharmraj on 22/9/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static String CHAT_ROOM = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      Log.e(TAG, "From : " + remoteMessage.getFrom() + remoteMessage.getNotification().getBody());
        Log.e(TAG, "Data : " + remoteMessage.getFrom() + remoteMessage.getData().toString());
       /* Util.e(TAG, "From : " + remoteMessage.getFrom() + remoteMessage.getNotification().getBody());
        Util.e(TAG, "Data : " + remoteMessage.getFrom() + remoteMessage.getData().toString());

        Random r = new Random();
        int random = r.nextInt(899 - 65) + 65;
        int m = ((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE)) + random;
        String ref_id = remoteMessage.getData().get("reference_id");
        String type = remoteMessage.getData().get("type");
        Intent intent = new Intent(this, HomeActivity.class);
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
