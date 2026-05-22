package com.example.buzzbreakphone.controllers;

import android.content.Context;
import android.content.Intent;

import com.example.buzzbreakphone.UsageStatsService;
import com.example.buzzbreakphone.models.AppUsageModel;

/**
 * Controller class for handling app usage functionality.
 * Coordinates between the view (AppUsageActivity) and the model (AppUsageModel).
 */
public class AppUsageController {
    private AppUsageModel appUsageModel;
    private Context context;
    
    public AppUsageController(Context context) {
        this.context = context;
        this.appUsageModel = new AppUsageModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        appUsageModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        appUsageModel.cleanup();
    }
    
    /**
     * Trigger background data collection for up-to-date info
     */
    public void triggerDataCollection() {
        Intent serviceIntent = new Intent(context, UsageStatsService.class);
        context.startService(serviceIntent);
    }
    
    /**
     * Check if the app has usage access permission
     */
    public boolean hasUsageAccess() {
        return appUsageModel.hasUsageAccess();
    }
    
    /**
     * Load usage data
     */
    public AppUsageModel.AppUsageData loadUsageData() {
        return appUsageModel.loadUsageData();
    }
}