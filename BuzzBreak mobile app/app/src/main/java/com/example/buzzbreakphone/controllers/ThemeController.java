package com.example.buzzbreakphone.controllers;

import android.content.Context;

import com.example.buzzbreakphone.ThemeManager;
import com.example.buzzbreakphone.models.ThemeModel;

/**
 * Controller class for handling theme functionality.
 * Coordinates between views and the theme model.
 */
public class ThemeController {
    private ThemeModel themeModel;
    
    public ThemeController(Context context) {
        this.themeModel = new ThemeModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        themeModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        themeModel.cleanup();
    }
    
    /**
     * Get the currently selected theme
     */
    public String getSelectedTheme() {
        return themeModel.getSelectedTheme();
    }
    
    /**
     * Set the selected theme
     */
    public void setSelectedTheme(String themeName) {
        themeModel.setSelectedTheme(themeName);
    }
    
    /**
     * Get the theme manager
     */
    public ThemeManager getThemeManager() {
        return themeModel.getThemeManager();
    }
    
    /**
     * Get all available themes
     */
    public ThemeManager.ThemeConfig[] getAllThemes() {
        return themeModel.getAllThemes();
    }
}