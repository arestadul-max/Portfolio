package com.example.buzzbreakphone.controllers;

import android.content.Context;
import android.graphics.Color;

import com.example.buzzbreakphone.models.DailyReportModel;

/**
 * Controller class for handling daily report functionality.
 * Coordinates between views and the daily report model.
 */
public class DailyReportController {
    private DailyReportModel dailyReportModel;
    // Define an average daily usage benchmark (3 hours in milliseconds)
    private static final long AVERAGE_DAILY_USAGE = 3 * 60 * 60 * 1000;
    
    public DailyReportController(Context context) {
        this.dailyReportModel = new DailyReportModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        dailyReportModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        dailyReportModel.cleanup();
    }
    
    /**
     * Load daily report data
     */
    public DailyReportModel.DailyReportData loadDailyReportData() {
        return dailyReportModel.loadDailyReportData();
    }
    
    /**
     * Determine if the usage report is good or bad based on total screen time
     * @param totalScreenTime Total screen time in milliseconds
     * @return true if screen time is below average, false otherwise
     */
    public boolean isGoodUsageReport(long totalScreenTime) {
        return totalScreenTime < AVERAGE_DAILY_USAGE;
    }
    
    /**
     * Get the appropriate message for a usage report
     * @param isGoodReport Whether the report is good or bad
     * @return A message describing the usage report status
     */
    public String getReportStatusMessage(boolean isGoodReport) {
        if (isGoodReport) {
            return "👍 Good job! Your screen time is below average.";
        } else {
            return "⚠️ Your screen time is above average. Try to reduce it.";
        }
    }
    
    /**
     * Get the appropriate color for a usage report status message
     * @param isGoodReport Whether the report is good or bad
     * @return A color code (green for good, red for bad)
     */
    public int getReportStatusColor(boolean isGoodReport) {
        if (isGoodReport) {
            return Color.parseColor("#4AFF88"); // Green color
        } else {
            return Color.parseColor("#FF6B6B"); // Red color
        }
    }
    
    /**
     * Generate additional details text with category breakdowns and tips
     * @param categoryTimes Array of time spent in each category (milliseconds)
     * @return Formatted string with details and a usage tip
     */
    public String generateAdditionalDetails(long[] categoryTimes) {
        StringBuilder details = new StringBuilder();
        String[] categoryNames = {"Social Media", "Games", "Productivity", "Others"};
        
        details.append("Category Breakdown:\n");
        for (int i = 0; i < categoryTimes.length; i++) {
            String catTime = android.text.format.DateUtils.formatElapsedTime(categoryTimes[i] / 1000);
            details.append(categoryNames[i]).append(": ").append(catTime).append("\n");
        }
        
        // Add usage tips based on the most used category
        int maxCategoryIndex = 0;
        for (int i = 1; i < categoryTimes.length; i++) {
            if (categoryTimes[i] > categoryTimes[maxCategoryIndex]) {
                maxCategoryIndex = i;
            }
        }
        
        details.append("\nTip: ");
        switch (maxCategoryIndex) {
            case 0: // Social Media
                details.append("Try setting time limits for social media apps to reduce usage.");
                break;
            case 1: // Games
                details.append("Consider scheduling specific game time to avoid excessive playing.");
                break;
            case 2: // Productivity
                details.append("Great job using productivity apps! Keep up the good work.");
                break;
            case 3: // Others
                details.append("Check which 'Other' apps are consuming your time the most.");
                break;
        }
        
        return details.toString();
    }
}