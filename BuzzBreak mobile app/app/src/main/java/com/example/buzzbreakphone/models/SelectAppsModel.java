package com.example.buzzbreakphone.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.buzzbreakphone.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for handling select apps data.
 * Encapsulates all business logic related to app selection functionality.
 */
public class SelectAppsModel extends BaseModel {
    private static final String TAG = "SelectAppsModel";
    
    private SharedPreferences prefs;
    
    public SelectAppsModel(Context context) {
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
     * Get the list of currently selected apps
     */
    public List<String> getSelectedApps() {
        String saved = prefs.getString("buzz_whitelist", "");
        List<String> selectedApps = new ArrayList<>();
        
        if (!saved.isEmpty()) {
            String[] packages = saved.split(",");
            for (String packageName : packages) {
                if (packageName != null && !packageName.trim().isEmpty()) {
                    selectedApps.add(packageName.trim());
                }
            }
        }
        
        return selectedApps;
    }
    
    /**
     * Save the list of selected apps
     */
    public void saveSelectedApps(List<String> selectedApps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedApps.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(selectedApps.get(i));
        }
        prefs.edit().putString("buzz_whitelist", sb.toString()).apply();
    }
    
    /**
     * Get all installed user apps that can be selected
     */
    public List<AppInfo> getAllUserApps() {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<AppInfo> userApps = new ArrayList<>();
        
        Log.d(TAG, "=== GETTING ALL USER APPS ===");
        Log.d(TAG, "Total installed apps: " + apps.size());
        
        for (ApplicationInfo info : apps) {
            // Use the enhanced filtering from AppUtils
            if (AppUtils.isRealUserApp(context, info.packageName)) {
                try {
                    String label = pm.getApplicationLabel(info).toString();
                    userApps.add(new AppInfo(info.packageName, label, pm.getApplicationIcon(info)));
                    Log.d(TAG, "Added user app: " + label + " (" + info.packageName + ")");
                } catch (Exception e) {
                    // Skip apps where we can't get the label or icon
                    Log.w(TAG, "Skipping app (can't get info): " + info.packageName);
                }
            } else {
                Log.d(TAG, "Filtered out system app: " + info.packageName);
            }
        }
        
        Log.d(TAG, "Total user apps found: " + userApps.size());
        Log.d(TAG, "=== USER APPS RETRIEVAL COMPLETE ===");
        
        return userApps;
    }
    
    /**
     * Data class to represent app information
     */
    public static class AppInfo {
        private String packageName;
        private String appName;
        private android.graphics.drawable.Drawable icon;
        private boolean isSelected;
        
        public AppInfo(String packageName, String appName, android.graphics.drawable.Drawable icon) {
            this.packageName = packageName;
            this.appName = appName;
            this.icon = icon;
            this.isSelected = false;
        }
        
        public String getPackageName() {
            return packageName;
        }
        
        public String getAppName() {
            return appName;
        }
        
        public android.graphics.drawable.Drawable getIcon() {
            return icon;
        }
        
        public boolean isSelected() {
            return isSelected;
        }
        
        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}