package com.example.buzzbreakphone;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

public class AppUtils {
    private static final String TAG = "AppUtils";
    
    /**
     * Enhanced app filtering for real user applications only
     * Excludes system services, launchers, and non-user-facing components
     */
    public static boolean isRealUserApp(Context context, String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            return false;
        }
        
        // Skip Android system internals and components that are not user apps
        if (isSystemComponent(packageName)) {
            return false;
        }
        
        PackageManager pm = context.getPackageManager();
        
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            
            // Check if it's an actual user-facing application
            boolean isActualApp = false;
            
            // 1. Has launch intent (most important criteria for user apps)
            if (pm.getLaunchIntentForPackage(packageName) != null) {
                isActualApp = true;
            }
            // 2. User-installed apps (not system apps)
            else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                isActualApp = true;
            }
            // 3. Updated system apps (system apps that were updated by user)
            else if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                isActualApp = true;
            }
            // 4. Essential system apps that users actually interact with
            else if (isEssentialUserApp(packageName)) {
                isActualApp = true;
            }
            
            // Additional filtering for obvious non-user apps
            if (isActualApp) {
                // Exclude launchers, input methods, and other system services
                if (isSystemService(pm, info, packageName)) {
                    isActualApp = false;
                }
            }
            
            return isActualApp;
            
        } catch (PackageManager.NameNotFoundException e) {
            // App might be uninstalled, but include if it looks like a user app package
            return packageName.contains(".") && !packageName.startsWith("android") && !isSystemComponent(packageName);
        }
    }
    
    /**
     * Check if package is a system component that should never be shown to users
     */
    public static boolean isSystemComponent(String packageName) {
        String pkg = packageName.toLowerCase();
        
        // Android system packages
        if (pkg.startsWith("android.") ||
            pkg.startsWith("com.android.internal") ||
            pkg.startsWith("com.android.cts") ||
            pkg.startsWith("com.android.shell") ||
            pkg.equals("android") ||
            pkg.contains(".test")) {
            return true;
        }
        
        // Hardware/OEM specific system packages
        if (pkg.startsWith("com.qualcomm.qti") ||
            pkg.startsWith("com.samsung.android.") ||
            pkg.startsWith("com.huawei.") ||
            pkg.startsWith("com.xiaomi.") ||
            pkg.startsWith("com.oppo.") ||
            pkg.startsWith("com.vivo.")) {
            return true;
        }
        
        // System services and components
        if (pkg.contains("systemui") ||
            pkg.contains("launcher") && !pkg.contains("game") ||
            pkg.contains("inputmethod") ||
            pkg.contains("wallpaper") && !pkg.contains("live") ||
            pkg.contains("keyboard") && !pkg.contains("game") ||
            pkg.contains("provider") && pkg.startsWith("com.android")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if app is a system service (launcher, input method, etc.)
     */
    private static boolean isSystemService(PackageManager pm, ApplicationInfo info, String packageName) {
        try {
            String pkg = packageName.toLowerCase();
            
            // Get app label to check for system service indicators
            String appLabel = "";
            try {
                appLabel = pm.getApplicationLabel(info).toString().toLowerCase();
            } catch (Exception e) {
                // Ignore if can't get label
            }
            
            // Check for system service indicators
            if (appLabel.contains("launcher") || 
                appLabel.contains("input method") ||
                appLabel.contains("keyboard") && !appLabel.contains("game") ||
                appLabel.contains("wallpaper") && !appLabel.contains("live") ||
                appLabel.contains("system ui") ||
                appLabel.contains("provider")) {
                return true;
            }
            
            // Check package name patterns for system services
            if (pkg.contains("launcher") && !pkg.contains("game") ||
                pkg.contains("ime") ||
                pkg.contains("inputmethod")) {
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    public static int countUserApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        int count = 0;
        for (ApplicationInfo info : apps) {
            if (isRealUserApp(context, info.packageName)) {
                count++;
            }
        }
        Log.d(TAG, "Total real user apps found: " + count);
        return count;
    }
    
    public static int countAllApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        int count = 0;
        for (ApplicationInfo info : apps) {
            if (isRealUserApp(context, info.packageName)) {
                count++;
            }
        }
        Log.d(TAG, "Total real user apps (countAllApps): " + count);
        return count;
    }
    
    private static boolean isEssentialUserApp(String packageName) {
        String pkg = packageName.toLowerCase();
        return pkg.equals("com.google.android.youtube") ||
               pkg.equals("com.android.chrome") ||
               pkg.equals("com.google.android.gm") ||
               pkg.equals("com.google.android.apps.photos") ||
               pkg.contains("youtube") || pkg.contains("chrome") || pkg.contains("gmail");
    }
}


