package com.example.buzzbreakphone;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageStatsService extends Service {
    
    private static final String TAG = "UsageStatsService";
    private static final String PREFS_NAME = "usage_cache";
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting background usage data collection");
        
        // Collect and cache current usage data
        collectAndCacheUsageData();
        
        return START_NOT_STICKY; // Don't restart if killed
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }
    
    private void collectAndCacheUsageData() {
        try {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            if (usm == null) {
                Log.e(TAG, "UsageStatsManager not available");
                return;
            }
            
            Calendar cal = Calendar.getInstance();
            long end = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            long start = cal.getTimeInMillis();
            
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
            if (stats == null || stats.isEmpty()) {
                Log.w(TAG, "No usage stats available");
                return;
            }
            
            // Process and cache data
            Map<String, Long> categoryUsage = computeUsageByCategory(stats);
            long totalScreenTime = calculateTotalScreenTime(stats);
            
            // Cache the processed data
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putLong("last_update", System.currentTimeMillis());
            editor.putLong("total_screen_time", totalScreenTime);
            editor.putLong("social_media_time", categoryUsage.getOrDefault("Social Media", 0L));
            editor.putLong("games_time", categoryUsage.getOrDefault("Games", 0L));
            editor.putLong("productivity_time", categoryUsage.getOrDefault("Productivity", 0L));
            editor.putLong("others_time", categoryUsage.getOrDefault("Others", 0L));
            
            editor.apply();
            
            Log.d(TAG, "Usage data cached successfully. Total screen time: " + totalScreenTime + "ms");
            
        } catch (Exception e) {
            Log.e(TAG, "Error collecting usage data", e);
        }
    }
    
    private long calculateTotalScreenTime(List<UsageStats> stats) {
        long total = 0;
        
        for (UsageStats s : stats) {
            if (s.getTotalTimeInForeground() > 1000) { // More than 1 second
                String packageName = s.getPackageName();
                
                // Use enhanced app filtering for consistency
                if (AppUtils.isRealUserApp(this, packageName)) {
                    total += s.getTotalTimeInForeground();
                }
            }
        }
        
        return total;
    }
    
    private Map<String, Long> computeUsageByCategory(List<UsageStats> stats) {
        Map<String, Long> buckets = new HashMap<>();
        buckets.put("Social Media", 0L);
        buckets.put("Games", 0L);
        buckets.put("Productivity", 0L);
        buckets.put("Others", 0L);
        
        PackageManager pm = getPackageManager();
        
        for (UsageStats s : stats) {
            long t = s.getTotalTimeInForeground();
            if (t <= 1000) continue; // Skip apps with less than 1 second usage
            
            String packageName = s.getPackageName();
            
            // Use enhanced app filtering for consistency
            if (AppUtils.isRealUserApp(this, packageName)) {
                String category = categorizeApp(packageName);
                buckets.put(category, buckets.get(category) + t);
            }
        }
        
        return buckets;
    }
    
    private String categorizeApp(String packageName) {
        String pkg = packageName.toLowerCase();
        
        // Social Media
        if (pkg.contains("facebook") || pkg.contains("instagram") || pkg.contains("twitter") ||
            pkg.contains("snapchat") || pkg.contains("tiktok") || pkg.contains("whatsapp") ||
            pkg.contains("telegram") || pkg.contains("discord") || pkg.contains("reddit") ||
            pkg.contains("linkedin") || pkg.contains("pinterest") || pkg.contains("tumblr")) {
            return "Social Media";
        }
        
        // Games
        if (pkg.contains("game") || pkg.contains("play") && pkg.contains("games") ||
            pkg.contains("candy") || pkg.contains("puzzle") || pkg.contains("clash") ||
            pkg.contains("pokemon") || pkg.contains("minecraft") || pkg.contains("pubg") ||
            pkg.contains("fortnite") || pkg.contains("roblox") || pkg.contains("chess")) {
            return "Games";
        }
        
        // Productivity
        if (pkg.contains("office") || pkg.contains("docs") || pkg.contains("sheets") ||
            pkg.contains("slides") || pkg.contains("calendar") || pkg.contains("gmail") ||
            pkg.contains("outlook") || pkg.contains("notion") || pkg.contains("evernote") ||
            pkg.contains("trello") || pkg.contains("slack") || pkg.contains("zoom") ||
            pkg.contains("teams") || pkg.contains("drive") || pkg.contains("dropbox")) {
            return "Productivity";
        }
        
        return "Others";
    }
}