package com.example.buzzbreakphone;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        if (message == null) message = "Break time!";

        Notification notification = new NotificationCompat.Builder(context, "buzzbreak_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("BuzzBreak")
                .setContentText(message)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = ContextCompat.getSystemService(context, NotificationManager.class);
        if (manager != null) {
            manager.notify(3001, notification);
        }
    }
}


