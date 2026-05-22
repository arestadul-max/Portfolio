package com.example.buzzbreakphone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllowedAppsAdapter extends RecyclerView.Adapter<AllowedAppsAdapter.AppViewHolder> {
    
    public interface OnAppLaunchListener {
        void onAppLaunching();
    }
    
    private final Context context;
    private final List<String> allowedPackages;
    private final PackageManager packageManager;
    private OnAppLaunchListener appLaunchListener;
    
    public AllowedAppsAdapter(Context context, List<String> allowedPackages) {
        this.context = context;
        this.allowedPackages = allowedPackages;
        this.packageManager = context.getPackageManager();
    }
    
    public void setOnAppLaunchListener(OnAppLaunchListener listener) {
        this.appLaunchListener = listener;
    }
    
    public void updateAppList(List<String> newPackages) {
        android.util.Log.d("AllowedAppsAdapter", "Updating adapter with " + newPackages.size() + " apps");
        this.allowedPackages.clear();
        this.allowedPackages.addAll(newPackages);
        notifyDataSetChanged();
        android.util.Log.d("AllowedAppsAdapter", "Adapter updated, notifyDataSetChanged() called");
    }
    
    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_allowed_app, parent, false);
        return new AppViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        String packageName = allowedPackages.get(position);
        android.util.Log.d("AllowedAppsAdapter", "Attempting to load app: " + packageName);
        
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            String appName = packageManager.getApplicationLabel(appInfo).toString();
            android.util.Log.d("AllowedAppsAdapter", "Successfully found app: " + packageName + " -> " + appName);
            
            // Use same friendly name mapping as other activities
            if (appName.equals(packageName) || appName.startsWith("com.")) {
                appName = getFriendlyAppName(packageName, appName);
            }
            
            final String finalAppName = appName; // Make effectively final for lambda
            
            // Try multiple icon loading methods for real developer icons
            Drawable appIcon = null;
            try {
                // Method 1: Get app icon from ApplicationInfo
                appIcon = packageManager.getApplicationIcon(appInfo);
                android.util.Log.d("AllowedAppsAdapter", "Successfully loaded icon for: " + packageName);
            } catch (Exception e) {
                // Method 2: Try package-based loading
                try {
                    appIcon = packageManager.getApplicationIcon(packageName);
                    android.util.Log.d("AllowedAppsAdapter", "Loaded icon via package name for: " + packageName);
                } catch (Exception e2) {
                    // Use default icon if loading fails
                    android.util.Log.w("AllowedAppsAdapter", "Failed to load icon for: " + packageName);
                    appIcon = null;
                }
            }
            
            holder.appName.setText(finalAppName);
            if (appIcon != null) {
                holder.appIcon.setImageDrawable(appIcon);
            } else {
                holder.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            
            holder.itemView.setOnClickListener(v -> launchApp(packageName, finalAppName));
            
        } catch (PackageManager.NameNotFoundException e) {
            // Handle apps that may have been uninstalled but still in allowed list
            android.util.Log.w("AllowedAppsAdapter", "App NOT FOUND: " + packageName + " - " + e.getMessage());
            String friendlyName = getFriendlyAppName(packageName, packageName);
            final String finalFriendlyName = friendlyName; // Make effectively final for lambda
            
            holder.appName.setText(finalFriendlyName + " (Not Found)");
            holder.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            
            // Still allow clicking to attempt launch or show error
            holder.itemView.setOnClickListener(v -> launchApp(packageName, finalFriendlyName));
        }
    }
    
    @Override
    public int getItemCount() {
        return allowedPackages.size();
    }
    
    private void launchApp(String packageName, String appName) {
        try {
            // Notify that we're about to launch an app
            if (appLaunchListener != null) {
                appLaunchListener.onAppLaunching();
            }
            
            Intent launchIntent = null;
            
            // Method 1: Standard launch intent
            launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
                Toast.makeText(context, "Opening " + appName, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Method 2: Try to find main activity with LAUNCHER category
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent);
                Toast.makeText(context, "Opening " + appName, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Method 3: Try to launch any activity from the package
            Intent fallbackIntent = new Intent();
            fallbackIntent.setPackage(packageName);
            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            if (fallbackIntent.resolveActivity(packageManager) != null) {
                context.startActivity(fallbackIntent);
                Toast.makeText(context, "Opening " + appName, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Method 4: Try to open app info/settings if can't launch
            Intent settingsIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingsIntent.setData(android.net.Uri.parse("package:" + packageName));
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            if (settingsIntent.resolveActivity(packageManager) != null) {
                context.startActivity(settingsIntent);
                Toast.makeText(context, "Cannot launch " + appName + " directly, opening app settings", Toast.LENGTH_LONG).show();
                return;
            }
            
            Toast.makeText(context, "Cannot launch " + appName + ". App may not be installed.", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Toast.makeText(context, "Error launching " + appName + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private String getFriendlyAppName(String packageName, String fallbackName) {
        String pkg = packageName.toLowerCase();
        
        // Map common Google and system apps to friendly names (same as other activities)
        if (pkg.equals("com.google.android.youtube")) return "YouTube";
        if (pkg.equals("com.google.android.youtube.tv")) return "YouTube TV";
        if (pkg.equals("com.google.android.youtube.music")) return "YouTube Music";
        if (pkg.equals("com.android.chrome")) return "Chrome";
        if (pkg.equals("com.google.android.gm")) return "Gmail";
        if (pkg.equals("com.google.android.apps.photos")) return "Google Photos";
        if (pkg.equals("com.google.android.apps.maps")) return "Google Maps";
        if (pkg.equals("com.android.camera2") || pkg.equals("com.android.camera")) return "Camera";
        if (pkg.equals("com.android.deskclock")) return "Clock";
        if (pkg.equals("com.android.calendar")) return "Calendar";
        if (pkg.equals("com.android.contacts")) return "Contacts";
        if (pkg.equals("com.android.dialer")) return "Phone";
        if (pkg.equals("com.android.mms")) return "Messages";
        if (pkg.equals("com.android.calculator2")) return "Calculator";
        if (pkg.equals("com.android.settings")) return "Settings";
        if (pkg.equals("com.google.android.apps.drive")) return "Google Drive";
        if (pkg.equals("com.google.android.music")) return "YouTube Music";
        if (pkg.equals("com.google.android.googlequicksearchbox")) return "Google";
        if (pkg.equals("com.google.android.apps.messaging")) return "Messages";
        if (pkg.equals("com.android.vending")) return "Google Play Store";
        
        // Handle partial matches for popular apps
        if (pkg.contains("youtube")) return "YouTube";
        if (pkg.contains("chrome")) return "Chrome";
        if (pkg.contains("gmail")) return "Gmail";
        if (pkg.contains("photos")) return "Google Photos";
        if (pkg.contains("maps")) return "Google Maps";
        if (pkg.contains("camera")) return "Camera";
        if (pkg.contains("gallery")) return "Gallery";
        if (pkg.contains("music")) return "Music";
        if (pkg.contains("whatsapp")) return "WhatsApp";
        if (pkg.contains("facebook")) return "Facebook";
        if (pkg.contains("instagram")) return "Instagram";
        if (pkg.contains("twitter")) return "Twitter";
        if (pkg.contains("tiktok")) return "TikTok";
        if (pkg.contains("snapchat")) return "Snapchat";
        if (pkg.contains("netflix")) return "Netflix";
        if (pkg.contains("spotify")) return "Spotify";
        
        // If no friendly name found, clean up the package name
        if (fallbackName.startsWith("com.")) {
            // Extract the last part of package name and capitalize it
            String[] parts = fallbackName.split("\\.");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                return lastPart.substring(0, 1).toUpperCase() + lastPart.substring(1);
            }
        }
        
        return fallbackName;
    }
    
    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        
        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
        }
    }
}