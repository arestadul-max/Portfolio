package com.example.buzzbreakphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buzzbreakphone.controllers.MainController;
import com.example.buzzbreakphone.controllers.ThemeController;

public class MainActivity extends AppCompatActivity {

    private static final String NOTIFICATION_CHANNEL_ID = "buzzbreak_channel";
    private static final int REQ_POST_NOTIFICATIONS = 1001;
    private ThemeController themeController;
    private MainController mainController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize MVC components
        themeController = new ThemeController(this);
        themeController.initialize();
        mainController = new MainController(this);
        mainController.initialize();

        mainController.requestScheduleExactAlarmPermission();

        View usageBtn = findViewById(R.id.btn_app_usage);
        View breakBtn = findViewById(R.id.btn_break);
        View buzzBtn = findViewById(R.id.btn_buzz_mode);
        View tipsBtn = findViewById(R.id.btn_tips);
        View reportBtn = findViewById(R.id.btn_daily_report);
        View settingsBtn = findViewById(R.id.btn_settings);

        usageBtn.setOnClickListener(v -> startActivity(new Intent(this, AppUsageActivity.class)));
        breakBtn.setOnClickListener(v -> startActivity(new Intent(this, BreakActivity.class)));
        buzzBtn.setOnClickListener(v -> startActivity(new Intent(this, BuzzModeActivity.class)));
        tipsBtn.setOnClickListener(v -> startActivity(new Intent(this, TipsActivity.class)));
        reportBtn.setOnClickListener(v -> startActivity(new Intent(this, DailyReportActivity.class)));
        settingsBtn.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTIFICATIONS);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void applyCurrentTheme() {
        // Apply comprehensive theme through the theme controller
        themeController.getThemeManager().applyFullTheme(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reapply theme when returning to the activity (e.g., from settings)
        applyCurrentTheme();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeController != null) {
            themeController.cleanup();
        }
    }
}