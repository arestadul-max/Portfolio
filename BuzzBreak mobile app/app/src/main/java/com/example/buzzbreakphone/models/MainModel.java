package com.example.buzzbreakphone.models;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.example.buzzbreakphone.DataUpdateReceiver;

/**
 * Model class for handling main activity data.
 * Encapsulates all business logic related to the main activity.
 */
public class MainModel extends BaseModel {
    private static final String NOTIFICATION_CHANNEL_ID = "buzzbreak_channel";
    
    public MainModel(Context context) {
        super(context);
    }
    
    @Override
    public void initialize() {
        createNotificationChannelIfNeeded();
        DataUpdateReceiver.initializeSchedules(context);
    }
    
    @Override
    public void cleanup() {
        // No cleanup needed
    }
    
    /**
     * Create notification channel if needed (Android O+)
     */
    private void createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "BuzzBreak Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Reminders and reports");
            NotificationManager manager = ContextCompat.getSystemService(context, NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Check if notification permission is granted
     */
    public boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permission not needed on older versions
    }
    
    /**
     * Check if alarm permission is granted
     */
    public boolean isAlarmPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                return alarmManager != null && alarmManager.canScheduleExactAlarms();
            } catch (Exception e) {
                return false;
            }
        }
        return true; // Permission not needed on older versions
    }
}