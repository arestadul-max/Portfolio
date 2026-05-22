package com.example.buzzbreakphone.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Model class for handling break functionality data.
 * Encapsulates all business logic related to break functionality.
 */
public class BreakModel extends BaseModel {
    private static final String TAG = "BreakModel";
    private static final String PREFS_NAME = "break_prefs";
    private static final String KEY_FOCUS_TIME = "focus_time";
    private static final String KEY_BREAK_TIME = "break_time";
    private static final String KEY_IS_FOCUS_MODE = "is_focus_mode";
    
    private SharedPreferences prefs;
    
    public BreakModel(Context context) {
        super(context);
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    @Override
    public void initialize() {
        // Set default values if not already set
        if (!prefs.contains(KEY_FOCUS_TIME)) {
            setFocusTime(25); // 25 minutes default
        }
        if (!prefs.contains(KEY_BREAK_TIME)) {
            setBreakTime(5); // 5 minutes default
        }
    }
    
    @Override
    public void cleanup() {
        // No cleanup needed
    }
    
    /**
     * Get the focus time in minutes
     */
    public int getFocusTime() {
        return prefs.getInt(KEY_FOCUS_TIME, 25);
    }
    
    /**
     * Set the focus time in minutes
     */
    public void setFocusTime(int minutes) {
        prefs.edit().putInt(KEY_FOCUS_TIME, minutes).apply();
    }
    
    /**
     * Get the break time in minutes
     */
    public int getBreakTime() {
        return prefs.getInt(KEY_BREAK_TIME, 5);
    }
    
    /**
     * Set the break time in minutes
     */
    public void setBreakTime(int minutes) {
        prefs.edit().putInt(KEY_BREAK_TIME, minutes).apply();
    }
    
    /**
     * Check if we're in focus mode
     */
    public boolean isFocusMode() {
        return prefs.getBoolean(KEY_IS_FOCUS_MODE, false);
    }
    
    /**
     * Set focus mode
     */
    public void setFocusMode(boolean isFocus) {
        prefs.edit().putBoolean(KEY_IS_FOCUS_MODE, isFocus).apply();
    }
}