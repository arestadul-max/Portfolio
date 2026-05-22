package com.example.buzzbreakphone.controllers;

import android.content.Context;

import com.example.buzzbreakphone.models.SelectAppsModel;

import java.util.List;

/**
 * Controller class for handling select apps functionality.
 * Coordinates between views and the select apps model.
 */
public class SelectAppsController {
    private SelectAppsModel selectAppsModel;
    
    public SelectAppsController(Context context) {
        this.selectAppsModel = new SelectAppsModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        selectAppsModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        selectAppsModel.cleanup();
    }
    
    /**
     * Get the list of currently selected apps
     */
    public List<String> getSelectedApps() {
        return selectAppsModel.getSelectedApps();
    }
    
    /**
     * Save the list of selected apps
     */
    public void saveSelectedApps(List<String> selectedApps) {
        selectAppsModel.saveSelectedApps(selectedApps);
    }
    
    /**
     * Get all installed user apps that can be selected
     */
    public List<SelectAppsModel.AppInfo> getAllUserApps() {
        return selectAppsModel.getAllUserApps();
    }
    
    /**
     * Select all apps
     */
    public void selectAllApps(List<SelectAppsModel.AppInfo> apps) {
        for (SelectAppsModel.AppInfo app : apps) {
            app.setSelected(true);
        }
    }
    
    /**
     * Deselect all apps
     */
    public void deselectAllApps(List<SelectAppsModel.AppInfo> apps) {
        for (SelectAppsModel.AppInfo app : apps) {
            app.setSelected(false);
        }
    }
}