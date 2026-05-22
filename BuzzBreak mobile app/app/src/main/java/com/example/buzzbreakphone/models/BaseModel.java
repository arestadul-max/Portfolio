package com.example.buzzbreakphone.models;

import android.content.Context;

/**
 * Base model class for the MVC architecture.
 * Provides common functionality for all models.
 */
public abstract class BaseModel {
    protected Context context;
    
    public BaseModel(Context context) {
        this.context = context;
    }
    
    /**
     * Initialize the model
     */
    public abstract void initialize();
    
    /**
     * Clean up resources
     */
    public abstract void cleanup();
}