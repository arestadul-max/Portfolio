package com.example.buzzbreakphone.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for handling buzz mode data.
 * Encapsulates all business logic related to buzz mode functionality.
 */
public class BuzzModeModel extends BaseModel {
    private static final String TAG = "BuzzModeModel";
    
    private SharedPreferences prefs;
    
    public BuzzModeModel(Context context) {
        super(context);
        this.prefs = context.getSharedPreferences("buzz_prefs", Context.MODE_PRIVATE);
    }
    
    @Override
    public void initialize() {
        // No initialization needed
    }
    
    @Override
    public void cleanup() {
        // No cleanup needed
    }
    
    /**
     * Get the current lock state
     */
    public boolean isLocked() {
        return prefs.getBoolean("buzz_enabled", false);
    }
    
    /**
     * Set the lock state
     */
    public void setLocked(boolean locked) {
        prefs.edit().putBoolean("buzz_enabled", locked).apply();
    }
    
    /**
     * Get the list of allowed apps
     */
    public List<String> getAllowedApps() {
        String saved = prefs.getString("buzz_whitelist", "");
        List<String> allowedPackages = new ArrayList<>();
        
        Log.d(TAG, "=== GETTING ALLOWED APPS ===");
        Log.d(TAG, "Raw saved string: '" + saved + "'");
        
        if (!saved.isEmpty()) {
            String[] packages = saved.split(",");
            
            Log.d(TAG, "Total saved apps: " + packages.length);
            
            for (String packageName : packages) {
                if (packageName != null && !packageName.trim().isEmpty()) {
                    String cleanPackageName = packageName.trim();
                    allowedPackages.add(cleanPackageName);
                    Log.d(TAG, "Added to list: '" + cleanPackageName + "'");
                }
            }
        }
        
        Log.d(TAG, "Final list size: " + allowedPackages.size());
        Log.d(TAG, "Apps in list: " + allowedPackages.toString());
        Log.d(TAG, "=== ALLOWED APPS RETRIEVAL COMPLETE ===");
        
        return allowedPackages;
    }
    
    /**
     * Update the list of allowed apps
     */
    public void setAllowedApps(List<String> allowedApps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allowedApps.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(allowedApps.get(i));
        }
        prefs.edit().putString("buzz_whitelist", sb.toString()).apply();
    }
    
    /**
     * Get the count of allowed apps
     */
    public int getAllowedAppsCount() {
        String saved = prefs.getString("buzz_whitelist", "");
        return saved.isEmpty() ? 0 : saved.split(",").length;
    }
    
    /**
     * Validate and clean up the allowed apps list by removing invalid apps
     */
    public List<String> validateAndCleanAllowedApps() {
        List<String> currentApps = getAllowedApps();
        List<String> validApps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        
        for (String packageName : currentApps) {
            try {
                ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                // Only add apps that are still installed
                validApps.add(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Removing uninstalled app from whitelist: " + packageName);
                // Skip this app (it's uninstalled)
            }
        }
        
        // Update the stored list if we removed any apps
        if (validApps.size() != currentApps.size()) {
            setAllowedApps(validApps);
        }
        
        return validApps;
    }
}