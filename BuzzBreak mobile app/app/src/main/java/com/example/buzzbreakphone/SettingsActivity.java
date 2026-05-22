package com.example.buzzbreakphone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buzzbreakphone.controllers.ThemeController;
import com.example.buzzbreakphone.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private ThemeController themeController;
    private String selectedThemeId;
    
    // Theme view references
    private View joeyTheme, oceanTheme, sunsetTheme, forestTheme, cyberTheme, midnightTheme, treeTheme;
    private TextView joeySelected, oceanSelected, sunsetSelected, forestSelected, cyberSelected, midnightSelected, treeSelected;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        // Initialize MVC components
        themeController = new ThemeController(this);
        themeController.initialize();
        selectedThemeId = themeController.getSelectedTheme();
        
        initializeViews();
        setupThemeSelection();
        updateSelectedThemeUI();
    }
    
    private void initializeViews() {
        // Theme cards
        joeyTheme = findViewById(R.id.theme_joey_tripp);
        oceanTheme = findViewById(R.id.theme_ocean_breeze);
        sunsetTheme = findViewById(R.id.theme_sunset_glow);
        forestTheme = findViewById(R.id.theme_forest_night);
        cyberTheme = findViewById(R.id.theme_cyber_neon);
        midnightTheme = findViewById(R.id.theme_midnight_purple);
        treeTheme = findViewById(R.id.theme_tree_design);
        
        // Selected indicators
        joeySelected = findViewById(R.id.joey_selected);
        oceanSelected = findViewById(R.id.ocean_selected);
        sunsetSelected = findViewById(R.id.sunset_selected);
        forestSelected = findViewById(R.id.forest_selected);
        cyberSelected = findViewById(R.id.cyber_selected);
        midnightSelected = findViewById(R.id.midnight_selected);
        treeSelected = findViewById(R.id.tree_selected);
        
        // Apply button
        Button applyButton = findViewById(R.id.btn_apply_theme);
        applyButton.setOnClickListener(v -> applySelectedTheme());
    }
    
    private void setupThemeSelection() {
        joeyTheme.setOnClickListener(v -> selectTheme(ThemeManager.THEME_JOEY_TRIPP));
        oceanTheme.setOnClickListener(v -> selectTheme(ThemeManager.THEME_OCEAN_BREEZE));
        sunsetTheme.setOnClickListener(v -> selectTheme(ThemeManager.THEME_SUNSET_GLOW));
        forestTheme.setOnClickListener(v -> selectTheme(ThemeManager.THEME_FOREST_NIGHT));
        cyberTheme.setOnClickListener(v -> selectTheme(ThemeManager.THEME_CYBER_NEON));
        midnightTheme.setOnClickListener(v -> selectTheme(ThemeManager.THEME_MIDNIGHT_PURPLE));
        treeTheme.setOnClickListener(v -> selectTheme(ThemeManager.THEME_TREE_DESIGN));
    }
    
    private void selectTheme(String themeId) {
        selectedThemeId = themeId;
        updateSelectedThemeUI();
        
        // Provide visual feedback
        ThemeManager.ThemeConfig config = themeController.getThemeManager().getThemeConfig(themeId);
        Toast.makeText(this, config.getEmoji() + " " + config.getName() + " selected!", Toast.LENGTH_SHORT).show();
    }
    
    private void updateSelectedThemeUI() {
        // Hide all selected indicators
        joeySelected.setVisibility(View.GONE);
        oceanSelected.setVisibility(View.GONE);
        sunsetSelected.setVisibility(View.GONE);
        forestSelected.setVisibility(View.GONE);
        cyberSelected.setVisibility(View.GONE);
        midnightSelected.setVisibility(View.GONE);
        treeSelected.setVisibility(View.GONE);
        
        // Show selected indicator for current theme
        switch (selectedThemeId) {
            case ThemeManager.THEME_JOEY_TRIPP:
                joeySelected.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_OCEAN_BREEZE:
                oceanSelected.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_SUNSET_GLOW:
                sunsetSelected.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_FOREST_NIGHT:
                forestSelected.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_CYBER_NEON:
                cyberSelected.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_MIDNIGHT_PURPLE:
                midnightSelected.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_TREE_DESIGN:
                treeSelected.setVisibility(View.VISIBLE);
                break;
        }
    }
    
    private void applySelectedTheme() {
        // Save the selected theme through the controller
        themeController.setSelectedTheme(selectedThemeId);
        
        // Get theme config for display name
        ThemeManager.ThemeConfig config = themeController.getThemeManager().getThemeConfig(selectedThemeId);
        
        // Show success message
        Toast.makeText(this, 
            config.getEmoji() + " " + config.getName() + " theme applied successfully!\nRestart the app to see full changes.", 
            Toast.LENGTH_LONG).show();
        
        // You could also restart the app or activity here if desired
        // For now, let's just go back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeController != null) {
            themeController.cleanup();
        }
    }
}