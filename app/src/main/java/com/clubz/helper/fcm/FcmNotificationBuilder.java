package com.clubz.helper.fcm;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author: Chiranjib Ganguly
 * Created on: 15/03/2018
 * Project: FirebaseChat
 */

public class FcmNotificationBuilder {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String SERVER_API_KEY = "AAAAzE8ixOU:APA91bFlzrg1IGtQGeM3f6ZanXuEhXkNkm9a4fhPy3V0nbTYYspBYUB9aAxxGCInZqu9Lgk_phnNltlo6xYg0Ow8XGFyrsIYi5dXZMiK8Nyb_6GwbohF11Wap7bvWZL4fh9hIlZ3tUUiySyfVDkYOM9nJH2r6SCvUA";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_DATA = "data";

    /*private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "message";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_UID = "uid";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_FCM_TYPE = "type";*/

    private String mTitle;
    private String mMessage;
    private String mNotficationType;
    private String mChatFor;
    private String mClubId;
    private String mHistoryId;
    private String mHistoryName;
    private String mFirebaseToken;


    private FcmNotificationBuilder() {

    }

    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }

    public FcmNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FcmNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }

    public FcmNotificationBuilder notificationType(String notficationType) {
        mNotficationType = notficationType;
        return this;
    }

    public FcmNotificationBuilder chatFor(String chatFor) {
        mChatFor = chatFor;
        return this;
    }

    public FcmNotificationBuilder clubId(String clubId) {
        mClubId = clubId;
        return this;
    }


    public FcmNotificationBuilder historyId(String historyId) {
        mHistoryId = historyId;
        return this;
    }

    public FcmNotificationBuilder historyName(String historyName) {
        mHistoryName = historyName;
        return this;
    }
    public FcmNotificationBuilder firebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
        return this;
    }



    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }
        });
    }
    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, mFirebaseToken);
        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(NotificatioKeyUtil.Companion.getKey_NotiFication_Title(), mTitle);
        jsonObjectData.put(NotificatioKeyUtil.Companion.getKey_NotiFication_Body(), mMessage);
        jsonObjectData.put(NotificatioKeyUtil.Companion.getKey_NotiFication_Type(), mNotficationType);
        jsonObjectData.put(NotificatioKeyUtil.Companion.getKey_ChatFor(), mChatFor);
        jsonObjectData.put(NotificatioKeyUtil.Companion.getKey_Club_Id(), mClubId);
        jsonObjectData.put(NotificatioKeyUtil.Companion.getKey_HistoryId(), mHistoryId);
        jsonObjectData.put(NotificatioKeyUtil.Companion.getKey_HistoryName(), mHistoryName);
        /*jsonObjectData.put(NotificatioKeyUtil.Companion.notifactionType, "chat");
        jsonObjectData.put(NotificatioKeyUtil.Companion.requestId, mRequestId);
        jsonObjectData.put(NotificatioKeyUtil.Companion.requestType, mRequestType);*/


        JSONObject jsonObjectNotification = new JSONObject();
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.getKey_NotiFication_Title(), mTitle);
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.getKey_NotiFication_Body(), mMessage);
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.getKey_NotiFication_Type(), mNotficationType);
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.getKey_ChatFor(), mChatFor);
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.getKey_Club_Id(), mClubId);
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.getKey_HistoryId(), mHistoryId);
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.getKey_HistoryName(), mHistoryName);
        /*jsonObjectNotification.put(NotificatioKeyUtil.Companion.notifactionType, "chat");
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.requestId, mRequestId);
        jsonObjectNotification.put(NotificatioKeyUtil.Companion.requestType, mRequestType);
        jsonObjectNotification.put("chatRoomId", mchatRoomId);*/
        jsonObjectNotification.put("icon", "icon");
        jsonObjectNotification.put("sound", "default");
        jsonObjectNotification.put("badge", "1");
        jsonObjectNotification.put("click_action", "AllChatActivity");
        jsonObjectBody.put(KEY_DATA, jsonObjectData);
        jsonObjectBody.put(KEY_NOTIFICATION, jsonObjectNotification);
        jsonObjectBody.put("sound", "default");
        jsonObjectBody.put("priority", "high");
        return jsonObjectBody;
    }
}