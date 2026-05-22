package com.example.buzzbreakphone.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.buzzbreakphone.ThemeManager;

/**
 * Model class for handling theme data.
 * Encapsulates all business logic related to themes.
 */
public class ThemeModel extends BaseModel {
    private static final String THEME_PREFS = "theme_preferences";
    private static final String SELECTED_THEME = "selected_theme";
    private static final String DEFAULT_THEME = "joey_tripp";
    
    private SharedPreferences preferences;
    private ThemeManager themeManager;
    
    public ThemeModel(Context context) {
        super(context);
        this.preferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE);
        this.themeManager = new ThemeManager(context);
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
     * Get the currently selected theme
     */
    public String getSelectedTheme() {
        return preferences.getString(SELECTED_THEME, DEFAULT_THEME);
    }
    
    /**
     * Set the selected theme
     */
    public void setSelectedTheme(String themeName) {
        preferences.edit().putString(SELECTED_THEME, themeName).apply();
    }
    
    /**
     * Get the theme manager
     */
    public ThemeManager getThemeManager() {
        return themeManager;
    }
    
    /**
     * Get all available themes
     */
    public ThemeManager.ThemeConfig[] getAllThemes() {
        return themeManager.getAllThemes();
    }
}