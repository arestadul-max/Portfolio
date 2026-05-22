package com.example.buzzbreakphone.models;

import android.content.Context;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
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
 * Model class for handling daily report data.
 * Encapsulates all business logic related to daily report functionality.
 */
public class DailyReportModel extends BaseModel {
    private static final String TAG = "DailyReportModel";
    
    public DailyReportModel(Context context) {
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
     * Load daily report data
     */
    public DailyReportData loadDailyReportData() {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usm == null) {
            return new DailyReportData("Usage Stats service not available", new ArrayList<>(), 0, 0, new long[4]);
        }
        
        // Get usage data for the last 24 hours
        Calendar cal = Calendar.getInstance();
        long end = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long start = cal.getTimeInMillis();
        
        try {
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
            if (stats == null) stats = new ArrayList<>();

            // Filter apps with actual usage time
            List<UsageStats> filtered = new ArrayList<>();
            for (UsageStats s : stats) {
                // Only include apps with actual usage time (more than 1 second)
                if (s.getTotalTimeInForeground() > 1000) {
                    // Use enhanced app filtering from AppUtils
                    if (AppUtils.isRealUserApp(context, s.getPackageName())) {
                        filtered.add(s);
                    }
                }
            }
            
            // Sort by actual time spent - most used apps first
            Collections.sort(filtered, new Comparator<UsageStats>() {
                @Override
                public int compare(UsageStats o1, UsageStats o2) {
                    return Long.compare(o2.getTotalTimeInForeground(), o1.getTotalTimeInForeground());
                }
            });

            PackageManager pm = context.getPackageManager();
            List<ReportRow> rows = new ArrayList<>();
            long totalScreenTime = 0;
            int totalAppsUsed = 0;
            
            // Category time tracking (Social, Games, Productivity, Others)
            long[] categoryTimes = new long[4];
            
            for (UsageStats s : filtered) {
                String label;
                String packageName = s.getPackageName();
                
                try {
                    ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                    label = pm.getApplicationLabel(info).toString();
                    
                    // If we get a generic package name instead of user-friendly name, try to map it
                    if (label.equals(packageName) || label.startsWith("com.")) {
                        label = getFriendlyAppName(packageName, label);
                    }
                    
                } catch (PackageManager.NameNotFoundException e) {
                    // Use friendly name if app info not available
                    if (packageName.contains(".") && !packageName.startsWith("android")) {
                        label = getFriendlyAppName(packageName, packageName);
                    } else {
                        continue; // Skip if we can't identify the app
                    }
                }
                
                // Convert real usage time to readable format
                String time = DateUtils.formatElapsedTime(s.getTotalTimeInForeground() / 1000);
                
                // Categorize app
                int category = categorizeApp(packageName, label);
                categoryTimes[category] += s.getTotalTimeInForeground();
                
                rows.add(new ReportRow(label, time, category, s.getTotalTimeInForeground()));
                totalScreenTime += s.getTotalTimeInForeground();
                totalAppsUsed++;
            }
            
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
            
            return new DailyReportData(message, rows, totalScreenTime, totalAppsUsed, categoryTimes);
            
        } catch (SecurityException e) {
            Log.e(TAG, "Permission error: " + e.getMessage());
            return new DailyReportData("Permission required: Please grant Usage Access to track real usage data.", 
                                  new ArrayList<>(), 0, 0, new long[4]);
        } catch (Exception e) {
            Log.e(TAG, "Error loading usage data: " + e.getMessage());
            return new DailyReportData("Error loading real usage data: " + e.getMessage(), 
                                  new ArrayList<>(), 0, 0, new long[4]);
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
    
    private int categorizeApp(String packageName, String appName) {
        String pkg = packageName.toLowerCase();
        String name = appName.toLowerCase();
        
        // Social Media category
        if (pkg.contains("facebook") || pkg.contains("instagram") || pkg.contains("twitter") || 
            pkg.contains("snapchat") || pkg.contains("tiktok") || pkg.contains("linkedin") ||
            pkg.contains("reddit") || pkg.contains("pinterest") || pkg.contains("whatsapp") ||
            pkg.contains("telegram") || pkg.contains("discord") || pkg.contains("wechat") ||
            pkg.contains("messenger") || pkg.contains("skype") || name.contains("social") ||
            pkg.equals("com.google.android.youtube") || pkg.equals("com.google.android.apps.photos")) {
            return 0; // Social Media
        }
        
        // Games category
        if (pkg.contains("game") || name.contains("game") || pkg.contains("playstation") ||
            pkg.contains("xbox") || pkg.contains("nintendo") || pkg.contains("minecraft") ||
            pkg.contains("pubg") || pkg.contains("fortnite") || pkg.contains("among us") ||
            pkg.contains("candy crush") || pkg.contains("angry birds") || name.contains("puzzle") ||
            name.contains("arcade") || name.contains("adventure")) {
            return 1; // Games
        }
        
        // Productivity category
        if (pkg.contains("office") || pkg.contains("google docs") || pkg.contains("drive") ||
            pkg.contains("dropbox") || pkg.contains("slack") || pkg.contains("teams") ||
            pkg.contains("zoom") || pkg.contains("skype") || pkg.contains("calendar") ||
            pkg.contains("calculator") || pkg.contains("notes") || pkg.contains("mail") ||
            pkg.contains("gmail") || pkg.contains("outlook") || name.contains("productivity") ||
            name.contains("work") || name.contains("business") || pkg.contains("todo") ||
            pkg.contains("task") || pkg.contains("planner") || pkg.contains("clock") ||
            pkg.contains("timer") || pkg.contains("alarm")) {
            return 2; // Productivity
        }
        
        // Others category (default)
        return 3; // Others
    }
    
    /**
     * Data class to hold daily report information
     */
    public static class DailyReportData {
        private String errorMessage;
        private List<ReportRow> reportRows;
        private long totalScreenTime;
        private int totalAppsUsed;
        private long[] categoryTimes;
        
        public DailyReportData(String errorMessage, List<ReportRow> reportRows, long totalScreenTime, 
                              int totalAppsUsed, long[] categoryTimes) {
            this.errorMessage = errorMessage;
            this.reportRows = reportRows;
            this.totalScreenTime = totalScreenTime;
            this.totalAppsUsed = totalAppsUsed;
            this.categoryTimes = categoryTimes;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public List<ReportRow> getReportRows() {
            return reportRows;
        }
        
        public long getTotalScreenTime() {
            return totalScreenTime;
        }
        
        public int getTotalAppsUsed() {
            return totalAppsUsed;
        }
        
        public long[] getCategoryTimes() {
            return categoryTimes;
        }
        
        public boolean hasData() {
            return reportRows != null && !reportRows.isEmpty();
        }
    }
    
    /**
     * Data class to represent a single report row
     */
    public static class ReportRow {
        private String appName;
        private String screenTime;
        private int category;
        private long timeInMillis;
        
        public ReportRow(String appName, String screenTime, int category, long timeInMillis) {
            this.appName = appName;
            this.screenTime = screenTime;
            this.category = category;
            this.timeInMillis = timeInMillis;
        }
        
        public String getAppName() {
            return appName;
        }
        
        public String getScreenTime() {
            return screenTime;
        }
        
        public int getCategory() {
            return category;
        }
        
        public long getTimeInMillis() {
            return timeInMillis;
        }
    }
}