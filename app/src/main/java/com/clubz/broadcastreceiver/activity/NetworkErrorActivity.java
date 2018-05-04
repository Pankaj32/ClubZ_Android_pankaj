package com.clubz.broadcastreceiver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.clubz.R;
import com.clubz.utils.Constants;

public class NetworkErrorActivity extends AppCompatActivity {
    private static boolean optedToOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_internet);
        Constants.NETWORK_CHECK = 1;
    }
    public static boolean isOptedToOffline() {
        return optedToOffline;
    }

    public static void setOptedToOffline(boolean optedToOffline){
        NetworkErrorActivity.optedToOffline = optedToOffline;
    }

    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable =  intent.getExtras().getBoolean("is_connected");
            if (isConnectionAvailable) {
                optedToOffline = false;

                finish();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(netSwitchReceiver, new IntentFilter(Constants.NETWORK_SWITCH_FILTER));
        }
        catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(netSwitchReceiver);
        }
        catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(NetworkErrorActivity.this,NetworkErrorActivity.class);
        startActivity(intent);
    }
}
