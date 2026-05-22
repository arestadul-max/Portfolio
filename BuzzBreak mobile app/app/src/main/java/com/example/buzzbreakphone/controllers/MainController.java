package com.example.buzzbreakphone.controllers;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.buzzbreakphone.models.MainModel;

/**
 * Controller class for handling main activity functionality.
 * Coordinates between the view (MainActivity) and the model (MainModel).
 */
public class MainController {
    private MainModel mainModel;
    private Context context;
    
    public MainController(Context context) {
        this.context = context;
        this.mainModel = new MainModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        mainModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        mainModel.cleanup();
    }
    
    /**
     * Request notification permission if needed
     */
    public void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!mainModel.isNotificationPermissionGranted()) {
                // Note: In a real implementation, the activity would need to handle the request
                // This is just a placeholder to show the logic
            }
        }
    }
    
    /**
     * Request schedule exact alarm permission if needed
     */
    public void requestScheduleExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!mainModel.isAlarmPermissionGranted()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "Please allow 'Schedule exact alarm' for real-time data updates", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    // Ignore if not available on this device
                }
            }
        }
    }
}