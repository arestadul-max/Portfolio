package com.example.buzzbreakphone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class DataUpdateReceiver extends BroadcastReceiver {
    
    private static final String TAG = "DataUpdateReceiver";
    public static final String ACTION_UPDATE_USAGE_DATA = "com.example.buzzbreakphone.UPDATE_USAGE_DATA";
    public static final String ACTION_HOURLY_REPORT_RESET = "com.example.buzzbreakphone.HOURLY_REPORT_RESET";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);
        
        if (ACTION_UPDATE_USAGE_DATA.equals(action)) {
            // Update usage data
            Intent serviceIntent = new Intent(context, UsageStatsService.class);
            context.startService(serviceIntent);
            
            // Schedule next update in 15 minutes
            scheduleNextDataUpdate(context);
            
        } else if (ACTION_HOURLY_REPORT_RESET.equals(action)) {
            // Reset and refresh daily report data
            Intent serviceIntent = new Intent(context, UsageStatsService.class);
            context.startService(serviceIntent);
            
            // Send notification about hourly refresh
            NotificationUtils.showDataRefreshNotification(context);
            
            // Schedule next hourly update
            scheduleNextHourlyUpdate(context);
            
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action) || 
                   Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            // App started or updated - initialize schedules
            initializeSchedules(context);
        }
    }
    
    public static void initializeSchedules(Context context) {
        Log.d(TAG, "Initializing background schedules");
        
        // Schedule regular data updates every 15 minutes
        scheduleNextDataUpdate(context);
        
        // Schedule hourly report refreshes
        scheduleNextHourlyUpdate(context);
        
        // Initial data collection
        Intent serviceIntent = new Intent(context, UsageStatsService.class);
        context.startService(serviceIntent);
    }
    
    private static void scheduleNextDataUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        
        Intent intent = new Intent(context, DataUpdateReceiver.class);
        intent.setAction(ACTION_UPDATE_USAGE_DATA);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, 
            1001, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Schedule update in 15 minutes
        long triggerTime = SystemClock.elapsedRealtime() + (15 * 60 * 1000); // 15 minutes
        
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            );
            Log.d(TAG, "Scheduled data update in 15 minutes");
        } catch (SecurityException e) {
            // Fallback for restrictive devices
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            );
        }
    }
    
    private static void scheduleNextHourlyUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        
        Intent intent = new Intent(context, DataUpdateReceiver.class);
        intent.setAction(ACTION_HOURLY_REPORT_RESET);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, 
            1002, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Calculate next hour boundary
        long currentTime = System.currentTimeMillis();
        long nextHour = ((currentTime / (60 * 60 * 1000)) + 1) * (60 * 60 * 1000);
        long delay = nextHour - currentTime;
        long triggerTime = SystemClock.elapsedRealtime() + delay;
        
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            );
            Log.d(TAG, "Scheduled hourly update in " + (delay / 1000) + " seconds");
        } catch (SecurityException e) {
            // Fallback for restrictive devices
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            );
        }
    }
    
    public static void cancelAllSchedules(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        
        // Cancel data updates
        Intent dataIntent = new Intent(context, DataUpdateReceiver.class);
        dataIntent.setAction(ACTION_UPDATE_USAGE_DATA);
        PendingIntent dataPendingIntent = PendingIntent.getBroadcast(
            context, 1001, dataIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );
        if (dataPendingIntent != null) {
            alarmManager.cancel(dataPendingIntent);
        }
        
        // Cancel hourly updates
        Intent hourlyIntent = new Intent(context, DataUpdateReceiver.class);
        hourlyIntent.setAction(ACTION_HOURLY_REPORT_RESET);
        PendingIntent hourlyPendingIntent = PendingIntent.getBroadcast(
            context, 1002, hourlyIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );
        if (hourlyPendingIntent != null) {
            alarmManager.cancel(hourlyPendingIntent);
        }
        
        Log.d(TAG, "Cancelled all scheduled updates");
    }
}