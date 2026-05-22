package com.example.buzzbreakphone.models;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.buzzbreakphone.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class for handling app usage data.
 * Encapsulates all business logic related to app usage tracking.
 */
public class AppUsageModel extends BaseModel {
    private static final String TAG = "AppUsageModel";
    
    public AppUsageModel(Context context) {
        super(context);
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
     * Check if the app has usage access permission
     */
    public boolean hasUsageAccess() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.unsafeCheckOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    
    /**
     * Load usage data for the last 24 hours
     */
    public AppUsageData loadUsageData() {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usm == null) {
            return new AppUsageData("Usage Stats service not available on this device.", new ArrayList<>(), 0, 0);
        }
        
        // Get usage data for the last 24 hours
        Calendar cal = Calendar.getInstance();
        long end = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long start = cal.getTimeInMillis();
        
        Log.d(TAG, "=== LOADING REAL USAGE DATA ===");
        Log.d(TAG, "Time range: " + new java.util.Date(start) + " to " + new java.util.Date(end));
        
        try {
            // Query real usage statistics
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
            if (stats == null) stats = new ArrayList<>();

            // Filter apps with actual usage time
            List<UsageStats> filtered = new ArrayList<>();
            int totalAppsProcessed = 0;
            int realAppsFound = 0;
            
            for (UsageStats s : stats) {
                totalAppsProcessed++;
                
                // Only include apps with actual usage time (more than 1 second)
                if (s.getTotalTimeInForeground() > 1000) {
                    String packageName = s.getPackageName();
                    
                    // Use enhanced app filtering from AppUtils
                    if (AppUtils.isRealUserApp(context, packageName)) {
                        filtered.add(s);
                        realAppsFound++;
                        
                        // Log first 10 apps for debugging
                        if (realAppsFound <= 10) {
                            long usageMinutes = s.getTotalTimeInForeground() / 60000;
                            Log.d(TAG, "Real app #" + realAppsFound + ": " + 
                                packageName + " - " + usageMinutes + " minutes");
                        }
                    } else {
                        // Log filtered out apps for first 5
                        if (totalAppsProcessed <= 5) {
                            Log.d(TAG, "Filtered out: " + packageName + " (system/non-user app)");
                        }
                    }
                }
            }
            
            Log.d(TAG, "Total stats processed: " + totalAppsProcessed);
            Log.d(TAG, "Real user apps found: " + realAppsFound);
            
            // Sort by actual time spent - most used apps first
            Collections.sort(filtered, new Comparator<UsageStats>() {
                @Override
                public int compare(UsageStats o1, UsageStats o2) {
                    return Long.compare(o2.getTotalTimeInForeground(), o1.getTotalTimeInForeground());
                }
            });

            // Count real app launches from usage events
            UsageEvents events = usm.queryEvents(start, end);
            Map<String, Integer> pkgToVisits = new HashMap<>();
            Map<String, Long> pkgToLastEvent = new HashMap<>();
            String currentAppPackage = context.getPackageName();
            
            int totalEventsProcessed = 0;
            int relevantEventsCount = 0;
            
            if (events != null) {
                UsageEvents.Event event = new UsageEvents.Event();
                while (events.hasNextEvent()) {
                    events.getNextEvent(event);
                    totalEventsProcessed++;
                    
                    String pkg = event.getPackageName();
                    
                    // Only process events for real user apps (exclude this app itself)
                    if (pkg != null && !pkg.equals(currentAppPackage) && AppUtils.isRealUserApp(context, pkg)) {
                        // Only count ACTIVITY_RESUMED events that represent real app launches
                        if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                            relevantEventsCount++;
                            Long lastEventTime = pkgToLastEvent.get(pkg);
                            
                            // Count as a new visit if:
                            // 1. First time seeing this app, OR
                            // 2. More than 2 minutes since last ACTIVITY_RESUMED (indicates new session)
                            if (lastEventTime == null || (event.getTimeStamp() - lastEventTime) > 120000) {
                                Integer currentVisits = pkgToVisits.get(pkg);
                                pkgToVisits.put(pkg, currentVisits == null ? 1 : currentVisits + 1);
                                pkgToLastEvent.put(pkg, event.getTimeStamp());
                                
                                // Log first 10 visit counts
                                if (pkgToVisits.size() <= 10) {
                                    Log.d(TAG, "Visit counted for " + pkg + 
                                        " - Total visits: " + pkgToVisits.get(pkg));
                                }
                            }
                        }
                        // Reset session tracking when app goes to background for more than 2 minutes
                        else if (event.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED || 
                                event.getEventType() == UsageEvents.Event.ACTIVITY_STOPPED) {
                            Long lastEventTime = pkgToLastEvent.get(pkg);
                            if (lastEventTime != null && (event.getTimeStamp() - lastEventTime) > 120000) {
                                pkgToLastEvent.put(pkg, 0L); // Reset session
                            }
                        }
                    }
                }
            }
            
            Log.d(TAG, "Total usage events processed: " + totalEventsProcessed);
            Log.d(TAG, "Relevant app launch events: " + relevantEventsCount);
            Log.d(TAG, "Apps with visit counts: " + pkgToVisits.size());

            PackageManager pm = context.getPackageManager();
            List<UsageRow> rows = new ArrayList<>();
            long totalScreenTime = 0;
            int totalAppsUsed = 0;
            
            for (UsageStats s : filtered) {
                String label;
                android.graphics.drawable.Drawable icon = null;
                String packageName = s.getPackageName();
                
                try {
                    ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                    label = pm.getApplicationLabel(info).toString();
                    icon = pm.getApplicationIcon(info);
                    
                    // If we get a generic package name instead of user-friendly name, try to map it
                    if (label.equals(packageName) || label.startsWith("com.")) {
                        label = getFriendlyAppName(packageName, label);
                    }
                    
                } catch (PackageManager.NameNotFoundException e) {
                    // Use friendly name if app info not available
                    if (packageName.contains(".") && !packageName.startsWith("android")) {
                        label = getFriendlyAppName(packageName, packageName);
                        icon = null;
                    } else {
                        continue; // Skip if we can't identify the app
                    }
                }
                
                // Convert real usage time to readable format
                String time = DateUtils.formatElapsedTime(s.getTotalTimeInForeground() / 1000);
                // Get real visit count from usage events
                int visits = pkgToVisits.getOrDefault(s.getPackageName(), 0);
                
                // For this app, show real usage time but ensure reasonable visit count
                if (s.getPackageName().equals(context.getPackageName())) {
                    visits = Math.max(1, visits); // Show at least 1 visit if there's any usage
                }
                
                rows.add(new UsageRow(icon, label, time, visits));
                totalScreenTime += s.getTotalTimeInForeground();
                totalAppsUsed++;
            }
            
            Log.d(TAG, "Final results: " + rows.size() + " apps shown");
            Log.d(TAG, "Total screen time: " + (totalScreenTime / 60000) + " minutes");
            Log.d(TAG, "=== USAGE DATA LOADING COMPLETE ===");
            
            String message = "";
            if (rows.isEmpty()) {
                message = "No app usage data found for the last 24 hours.\n\n" +
                         "📱 This app tracks REAL usage from your phone!\n\n" +
                         "Possible reasons:\n" +
                         "• Very light phone usage today\n" +
                         "• Recently granted usage access\n" +
                         "• System still collecting data\n\n" +
                         "Use some apps and return later to see your actual usage statistics!";
            }
            
            return new AppUsageData(message, rows, totalScreenTime, totalAppsUsed);
            
        } catch (SecurityException e) {
            Log.e(TAG, "Permission error: " + e.getMessage());
            return new AppUsageData("Permission required: Please grant Usage Access to track real usage data.", 
                                  new ArrayList<>(), 0, 0);
        } catch (Exception e) {
            Log.e(TAG, "Error loading usage data: " + e.getMessage());
            return new AppUsageData("Error loading real usage data: " + e.getMessage(), 
                                  new ArrayList<>(), 0, 0);
        }
    }
    
    private String getFriendlyAppName(String packageName, String fallbackName) {
        String pkg = packageName.toLowerCase();
        
        // Map common Google and system apps to friendly names
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
        if (pkg.equals("com.google.android.apps.docs")) return "Google Docs";
        if (pkg.equals("com.google.android.apps.docs.editors.sheets")) return "Google Sheets";
        if (pkg.equals("com.google.android.apps.docs.editors.slides")) return "Google Slides";
        if (pkg.equals("com.google.android.talk")) return "Google Chat";
        if (pkg.equals("com.google.android.apps.translate")) return "Google Translate";
        if (pkg.equals("com.google.android.keep")) return "Google Keep";
        if (pkg.equals("com.google.android.calendar")) return "Google Calendar";
        if (pkg.equals("com.google.android.apps.podcasts")) return "Google Podcasts";
        if (pkg.equals("com.google.android.apps.fitness")) return "Google Fit";
        if (pkg.equals("com.google.android.apps.chromecast.app")) return "Google Home";
        if (pkg.equals("com.google.android.play.games")) return "Google Play Games";
        if (pkg.equals("com.android.vending")) return "Google Play Store";
        if (pkg.equals("com.google.android.apps.nexuslauncher")) return "Pixel Launcher";
        if (pkg.equals("com.google.android.setupwizard")) return "Setup Wizard";
        if (pkg.equals("com.google.android.partnersetup")) return "Partner Setup";
        
        // Handle partial matches for apps that might have variations
        if (pkg.contains("youtube")) return "YouTube";
        if (pkg.contains("chrome")) return "Chrome";
        if (pkg.contains("gmail")) return "Gmail";
        if (pkg.contains("photos")) return "Google Photos";
        if (pkg.contains("maps")) return "Google Maps";
        if (pkg.contains("camera")) return "Camera";
        if (pkg.contains("gallery")) return "Gallery";
        if (pkg.contains("music")) return "Music";
        if (pkg.contains("video")) return "Video Player";
        if (pkg.contains("messenger")) return "Messenger";
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
    
    /**
     * Data class to hold app usage information
     */
    public static class AppUsageData {
        private String errorMessage;
        private List<UsageRow> usageRows;
        private long totalScreenTime;
        private int totalAppsUsed;
        
        public AppUsageData(String errorMessage, List<UsageRow> usageRows, long totalScreenTime, int totalAppsUsed) {
            this.errorMessage = errorMessage;
            this.usageRows = usageRows;
            this.totalScreenTime = totalScreenTime;
            this.totalAppsUsed = totalAppsUsed;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public List<UsageRow> getUsageRows() {
            return usageRows;
        }
        
        public long getTotalScreenTime() {
            return totalScreenTime;
        }
        
        public int getTotalAppsUsed() {
            return totalAppsUsed;
        }
        
        public boolean hasData() {
            return usageRows != null && !usageRows.isEmpty();
        }
    }
    
    /**
     * Data class to represent a single usage row
     */
    public static class UsageRow {
        private android.graphics.drawable.Drawable icon;
        private String appName;
        private String screenTime;
        private int frequency;
        
        public UsageRow(android.graphics.drawable.Drawable icon, String appName, String screenTime, int frequency) {
            this.icon = icon;
            this.appName = appName;
            this.screenTime = screenTime;
            this.frequency = frequency;
        }
        
        public android.graphics.drawable.Drawable getIcon() {
            return icon;
        }
        
        public String getAppName() {
            return appName;
        }
        
        public String getScreenTime() {
            return screenTime;
        }
        
        public int getFrequency() {
            return frequency;
        }
    }
}