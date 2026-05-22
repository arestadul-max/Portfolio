package com.example.buzzbreakphone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.os.Build;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.activity.OnBackPressedCallback;

import com.example.buzzbreakphone.controllers.BuzzModeController;
import com.example.buzzbreakphone.controllers.ThemeController;

import java.util.ArrayList;
import java.util.List;

public class BuzzModeActivity extends AppCompatActivity implements AllowedAppsAdapter.OnAppLaunchListener {

    private BuzzModeController buzzModeController;
    private ThemeController themeController;
    private RecyclerView allowedAppsRecycler;
    private AllowedAppsAdapter allowedAppsAdapter;
    private boolean isLocked = false;
    private OnBackPressedCallback backPressedCallback;
    private boolean isLaunchingAllowedApp = false;
    private boolean hasShownLockMessage = false;
    private boolean hasShownReturnMessage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buzz_mode);

        // Initialize MVC components
        themeController = new ThemeController(this);
        themeController.initialize();
        buzzModeController = new BuzzModeController(this);
        buzzModeController.initialize();

        // Initialize theme manager and apply current theme
        themeController.getThemeManager().applyFullTheme(this);

        // Ensure app always starts UNLOCKED by default
        isLocked = false; // Always start unlocked
        
        setupBackPressHandling();

        TextView info = findViewById(R.id.tv_buzz_info);
        info.setText("Lock for full focus mode, or select apps for limited access. Unlocked by default.");

        ToggleButton toggle = findViewById(R.id.toggle_buzz);
        
        // Set initial state to match lock state (default unlocked)
        toggle.setChecked(isLocked);
        
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Allow locking even without selected apps - provides full focus mode
                
                isLocked = isChecked;
                buzzModeController.setLocked(isChecked);
                
                // Update back press handling
                updateBackPressHandling();
                
                // Enable/disable lock mode to prevent app exit
                setLockMode(isChecked);
                
                if (isLocked) {
                    int allowedCount = buzzModeController.getAllowedAppsCount();
                    if (allowedCount == 0) {
                        Toast.makeText(BuzzModeActivity.this, "Device Locked - Full focus mode (no exit allowed)", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(BuzzModeActivity.this, "Device Locked - Only allowed apps accessible (no exit)", Toast.LENGTH_LONG).show();
                    }
                    hasShownLockMessage = true;
                    hasShownReturnMessage = false; // Reset return message flag
                } else {
                    Toast.makeText(BuzzModeActivity.this, "Device Unlocked", Toast.LENGTH_SHORT).show();
                    hasShownLockMessage = false; // Reset flags when unlocked
                    hasShownReturnMessage = false;
                }
            }
        });

        TextView selected = findViewById(R.id.tv_selected_apps);
        updateCounts(selected);

        findViewById(R.id.btn_select_apps).setOnClickListener(v -> {
            // Always allow selecting apps, even when locked
            startActivity(new Intent(this, SelectAppsActivity.class));
        });

        setupAllowedAppsRecycler();
    }

    private void setupBackPressHandling() {
        backPressedCallback = new OnBackPressedCallback(isLocked) {
            @Override
            public void handleOnBackPressed() {
                if (isLocked) {
                    Toast.makeText(BuzzModeActivity.this, " Device is locked. Unlock to exit.", Toast.LENGTH_SHORT).show();
                } else {
                    // Allow normal back press behavior
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    private void updateBackPressHandling() {
        if (backPressedCallback != null) {
            backPressedCallback.setEnabled(isLocked);
        }
    }

    private void setLockMode(boolean enabled) {
        if (enabled) {
            // Enable lock mode - prevent exiting the app
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            
            // For newer Android versions, use immersive mode to hide navigation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        } else {
            // Disable lock mode - allow normal operation
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            
            // Restore normal UI visibility
            getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isLocked) {
            // Block home button, recent apps, and other system keys when locked
            if (keyCode == KeyEvent.KEYCODE_HOME ||
                keyCode == KeyEvent.KEYCODE_MENU ||
                keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
                Toast.makeText(this, "🔒 Device is locked. Unlock to use system keys.", Toast.LENGTH_SHORT).show();
                return true; // Consume the event
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Window is attached - lock mode flags are already set in setLockMode()
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Reapply theme in case it was changed in settings
        if (themeController != null) {
            themeController.getThemeManager().applyFullTheme(this);
        }
        
        // Only refresh lock state if it might have changed externally
        // Don't override the default unlocked state on first run
        boolean currentLockState = buzzModeController.isLocked();
        if (currentLockState != isLocked) {
            isLocked = currentLockState;
            updateBackPressHandling();
            setLockMode(isLocked); // Apply lock mode when resuming
        }
        
        TextView selected = findViewById(R.id.tv_selected_apps);
        updateCounts(selected);
        updateAllowedAppsList();
        
        // If locked and we're resuming from an allowed app, show message only once
        if (isLocked && !isLaunchingAllowedApp && !hasShownReturnMessage) {
            // Small delay to avoid showing message during normal navigation
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isLocked && !hasShownReturnMessage) {
                        Toast.makeText(BuzzModeActivity.this, "🔒 Device is locked. Unlock to exit.", Toast.LENGTH_SHORT).show();
                        hasShownReturnMessage = true;
                    }
                }
            }, 500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Only auto-return if locked and NOT launching an allowed app
        // This prevents interrupting allowed app usage
        if (isLocked && !isLaunchingAllowedApp) {
            // Only return if user tries to access non-allowed areas
            // Don't auto-return when using allowed apps
        }
        
        // Reset the flag after user finishes with the app
        if (isLaunchingAllowedApp) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isLaunchingAllowedApp = false;
                }
            }, 30000); // 30 seconds to allow proper app usage
        }
    }

    private void setupAllowedAppsRecycler() {
        allowedAppsRecycler = findViewById(R.id.rv_allowed_apps);
        allowedAppsRecycler.setLayoutManager(new LinearLayoutManager(this));
        List<String> allowedPackages = new ArrayList<>();
        allowedAppsAdapter = new AllowedAppsAdapter(this, allowedPackages);
        allowedAppsRecycler.setAdapter(allowedAppsAdapter);
    }

    private void updateAllowedAppsList() {
        List<String> allowedPackages = buzzModeController.validateAndCleanAllowedApps();
        
        // Update the adapter data instead of creating new adapter
        if (allowedAppsAdapter == null) {
            allowedAppsAdapter = new AllowedAppsAdapter(this, allowedPackages);
            allowedAppsAdapter.setOnAppLaunchListener(this);
            allowedAppsRecycler.setAdapter(allowedAppsAdapter);
        } else {
            allowedAppsAdapter.updateAppList(allowedPackages);
        }
    }

    private void updateCounts(TextView selected) {
        int allowedCount = buzzModeController.getAllowedAppsCount();
        selected.setText("Allowed apps: " + allowedCount);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isLocked) {
            Toast.makeText(this, "🔒 Device is locked. Unlock to exit.", Toast.LENGTH_SHORT).show();
            return true; // Block navigation up
        }
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return onSupportNavigateUp();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAppLaunching() {
        // Set flag to prevent auto-return when launching allowed app
        isLaunchingAllowedApp = true;
        // Reset return message flag so it can show again when returning
        hasShownReturnMessage = false;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeController != null) {
            themeController.cleanup();
        }
        if (buzzModeController != null) {
            buzzModeController.cleanup();
        }
    }
}