package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get data from the intent
        String name = intent.getStringExtra("name");

        // Show notification or perform other actions
        NotificationHelper.showNotification(context, name);
    }
}
