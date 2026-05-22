package com.example.buzzbreakphone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {
    private static final String CHANNEL_ID = "buzzbreak_channel";
    private static final String CHANNEL_NAME = "BuzzBreak Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for BuzzBreak app";

    public static void showSessionDone(Context context) {
        createNotificationChannel(context);
        
        Intent openMain = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context,
                4001,
                openMain,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Session Complete")
                .setContentText("Great job! Time to switch.")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        androidx.core.app.NotificationManagerCompat.from(context).notify(4002, notification);
    }
    
    public static void showDataRefreshNotification(Context context) {
        createNotificationChannel(context);
        
        Intent openReport = new Intent(context, DailyReportActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context,
                4003,
                openReport,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Usage Data Updated")
                .setContentText("Your hourly usage report has been refreshed!")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority for background updates
                .build();

        androidx.core.app.NotificationManagerCompat.from(context).notify(4004, notification);
    }
    
    public static void showBreakNotification(Context context, String message) {
        createNotificationChannel(context);
        
        Intent openBreak = new Intent(context, BreakActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context,
                4005,
                openBreak,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("BuzzBreak Timer")
                .setContentText(message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        androidx.core.app.NotificationManagerCompat.from(context).notify(4006, notification);
    }
    
    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}