package com.example.buzzbreakphone.controllers;

import android.content.Context;

import com.example.buzzbreakphone.models.BreakModel;

/**
 * Controller class for handling break functionality.
 * Coordinates between views and the break model.
 */
public class BreakController {
    private BreakModel breakModel;
    
    public BreakController(Context context) {
        this.breakModel = new BreakModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        breakModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        breakModel.cleanup();
    }
    
    /**
     * Get the focus time in minutes
     */
    public int getFocusTime() {
        return breakModel.getFocusTime();
    }
    
    /**
     * Set the focus time in minutes
     */
    public void setFocusTime(int minutes) {
        breakModel.setFocusTime(minutes);
    }
    
    /**
     * Get the break time in minutes
     */
    public int getBreakTime() {
        return breakModel.getBreakTime();
    }
    
    /**
     * Set the break time in minutes
     */
    public void setBreakTime(int minutes) {
        breakModel.setBreakTime(minutes);
    }
    
    /**
     * Check if we're in focus mode
     */
    public boolean isFocusMode() {
        return breakModel.isFocusMode();
    }
    
    /**
     * Set focus mode
     */
    public void setFocusMode(boolean isFocus) {
        breakModel.setFocusMode(isFocus);
    }
}