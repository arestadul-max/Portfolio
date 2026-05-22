package com.example.buzzbreakphone.controllers;

import android.content.Context;

import com.example.buzzbreakphone.models.BuzzModeModel;

import java.util.List;

/**
 * Controller class for handling buzz mode functionality.
 * Coordinates between views and the buzz mode model.
 */
public class BuzzModeController {
    private BuzzModeModel buzzModeModel;
    
    public BuzzModeController(Context context) {
        this.buzzModeModel = new BuzzModeModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        buzzModeModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        buzzModeModel.cleanup();
    }
    
    /**
     * Get the current lock state
     */
    public boolean isLocked() {
        return buzzModeModel.isLocked();
    }
    
    /**
     * Set the lock state
     */
    public void setLocked(boolean locked) {
        buzzModeModel.setLocked(locked);
    }
    
    /**
     * Get the list of allowed apps
     */
    public List<String> getAllowedApps() {
        return buzzModeModel.getAllowedApps();
    }
    
    /**
     * Update the list of allowed apps
     */
    public void setAllowedApps(List<String> allowedApps) {
        buzzModeModel.setAllowedApps(allowedApps);
    }
    
    /**
     * Get the count of allowed apps
     */
    public int getAllowedAppsCount() {
        return buzzModeModel.getAllowedAppsCount();
    }
    
    /**
     * Validate and clean up the allowed apps list
     */
    public List<String> validateAndCleanAllowedApps() {
        return buzzModeModel.validateAndCleanAllowedApps();
    }
}