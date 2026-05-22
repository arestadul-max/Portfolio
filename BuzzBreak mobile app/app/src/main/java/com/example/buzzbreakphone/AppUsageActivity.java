package com.example.buzzbreakphone;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzbreakphone.controllers.AppUsageController;
import com.example.buzzbreakphone.controllers.ThemeController;
import com.example.buzzbreakphone.models.AppUsageModel;

import java.util.ArrayList;
import java.util.List;

public class AppUsageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View permissionView;
    private ThemeController themeController;
    private AppUsageController appUsageController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_usage);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize theme manager and apply current theme
        themeController = new ThemeController(this);
        themeController.initialize();
        themeController.getThemeManager().applyFullTheme(this);

        // Initialize MVC components
        appUsageController = new AppUsageController(this);
        appUsageController.initialize();

        recyclerView = findViewById(R.id.rv_usage);
        permissionView = findViewById(R.id.permission_container);
        Button btnGrant = findViewById(R.id.btn_grant_usage);
        btnGrant.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Reapply theme in case it was changed in settings
        if (themeController != null) {
            themeController.getThemeManager().applyFullTheme(this);
        }
        
        // Trigger background data collection for up-to-date info
        appUsageController.triggerDataCollection();
        
        if (appUsageController.hasUsageAccess()) {
            permissionView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            loadUsage();
        } else {
            permissionView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void loadUsage() {
        // Load usage data through the controller
        AppUsageModel.AppUsageData usageData = appUsageController.loadUsageData();
        
        if (usageData.hasData()) {
            // Show total real usage time
            TextView titleView = findViewById(R.id.tv_usage_title);
            String totalTime = android.text.format.DateUtils.formatElapsedTime(usageData.getTotalScreenTime() / 1000);
            titleView.setText("Total: " + totalTime + " (" + usageData.getTotalAppsUsed() + " apps)");
            
            recyclerView.setAdapter(new UsageAdapter(usageData.getUsageRows()));
        } else {
            // Show error or no data message
            showNoDataMessage(usageData.getErrorMessage());
        }
    }
    
    private void showNoDataMessage(String message) {
        List<AppUsageModel.UsageRow> rows = new ArrayList<>();
        rows.add(new AppUsageModel.UsageRow(null, message, "", 0));
        recyclerView.setAdapter(new UsageAdapter(rows));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeController != null) {
            themeController.cleanup();
        }
        if (appUsageController != null) {
            appUsageController.cleanup();
        }
    }
}