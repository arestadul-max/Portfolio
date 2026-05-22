package com.example.buzzbreakphone.controllers;

import android.content.Context;

import com.example.buzzbreakphone.models.TipsModel;

import java.util.List;

/**
 * Controller class for handling tips functionality.
 * Coordinates between views and the tips model.
 */
public class TipsController {
    private TipsModel tipsModel;
    
    public TipsController(Context context) {
        this.tipsModel = new TipsModel(context);
    }
    
    /**
     * Initialize the controller and its model
     */
    public void initialize() {
        tipsModel.initialize();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        tipsModel.cleanup();
    }
    
    /**
     * Get all digital wellness tips
     */
    public List<TipsModel.Tip> getAllTips() {
        return tipsModel.getAllTips();
    }
}