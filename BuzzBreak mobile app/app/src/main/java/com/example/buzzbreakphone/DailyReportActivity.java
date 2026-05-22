package com.example.buzzbreakphone;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzbreakphone.controllers.DailyReportController;
import com.example.buzzbreakphone.controllers.ThemeController;
import com.example.buzzbreakphone.models.DailyReportModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class DailyReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BarChart barChart;
    private TextView screenTimeText, reportStatusText, additionalDetailsText;
    private ThemeController themeController;
    private DailyReportController dailyReportController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_report);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Daily Report");
        }

        // Initialize MVC components
        themeController = new ThemeController(this);
        themeController.initialize();
        dailyReportController = new DailyReportController(this);
        dailyReportController.initialize();

        // Apply theme
        themeController.getThemeManager().applyFullTheme(this);

        // Initialize views
        recyclerView = findViewById(R.id.rv_daily_report);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        barChart = findViewById(R.id.bar_chart);
        screenTimeText = findViewById(R.id.tv_screen_time);
        reportStatusText = findViewById(R.id.tv_report_status);
        additionalDetailsText = findViewById(R.id.tv_additional_details);

        loadDailyReport();
    }

    private void loadDailyReport() {
        // Load daily report data through the controller
        DailyReportModel.DailyReportData reportData = dailyReportController.loadDailyReportData();
        
        if (reportData.hasData()) {
            // Show total real usage time
            TextView titleView = findViewById(R.id.tv_report_title);
            String totalTime = android.text.format.DateUtils.formatElapsedTime(reportData.getTotalScreenTime() / 1000);
            titleView.setText("Total: " + totalTime + " (" + reportData.getTotalAppsUsed() + " apps)");
            
            // Update category bars with theme-appropriate colors
            updateCategoryBars(reportData.getCategoryTimes());
            
            // Update bar chart
            updateBarChart(reportData.getCategoryTimes());
            
            // Update screen time info
            updateScreenTimeInfo(reportData.getTotalScreenTime(), reportData.getCategoryTimes());
            
            // Set up RecyclerView with report data
            List<DailyReportModel.ReportRow> rows = new ArrayList<>();
            // Add top 10 most used apps
            int count = Math.min(10, reportData.getReportRows().size());
            for (int i = 0; i < count; i++) {
                rows.add(reportData.getReportRows().get(i));
            }
            
            recyclerView.setAdapter(new DailyReportAdapter(rows));
        } else {
            // Show error or no data message
            showNoDataMessage(reportData.getErrorMessage());
        }
    }
    
    private void updateScreenTimeInfo(long totalScreenTime, long[] categoryTimes) {
        // Format total screen time
        String totalTime = android.text.format.DateUtils.formatElapsedTime(totalScreenTime / 1000);
        
        // Display total screen time info
        screenTimeText.setText("Total Screen Time Today: " + totalTime);
        
        // Use the controller to determine if usage is good or bad
        boolean isGoodReport = dailyReportController.isGoodUsageReport(totalScreenTime);
        
        // Use the controller to get the appropriate status message and color
        reportStatusText.setText(dailyReportController.getReportStatusMessage(isGoodReport));
        reportStatusText.setTextColor(dailyReportController.getReportStatusColor(isGoodReport));
        
        // Use the controller to generate additional details
        additionalDetailsText.setText(dailyReportController.generateAdditionalDetails(categoryTimes));
        additionalDetailsText.setVisibility(View.VISIBLE);
    }
    
    private void updateCategoryBars(long[] categoryTimes) {
        // Get theme-appropriate colors from ThemeManager
        int[] categoryColors = themeController.getThemeManager().getCategoryColors();
        
        // Calculate total time for percentage calculations
        long totalTime = 0;
        for (long time : categoryTimes) {
            totalTime += time;
        }
        
        if (totalTime > 0) {
            // Update each category bar
            updateCategoryBar(R.id.category_social, R.id.tv_social_percentage, categoryTimes[0], totalTime, categoryColors[0], "Social Media");
            updateCategoryBar(R.id.category_games, R.id.tv_games_percentage, categoryTimes[1], totalTime, categoryColors[1], "Games");
            updateCategoryBar(R.id.category_productivity, R.id.tv_productivity_percentage, categoryTimes[2], totalTime, categoryColors[2], "Productivity");
            updateCategoryBar(R.id.category_others, R.id.tv_others_percentage, categoryTimes[3], totalTime, categoryColors[3], "Others");
        }
    }
    
    private void updateCategoryBar(int categoryId, int percentageId, long categoryTime, long totalTime, int color, String label) {
        // Find the category container
        View categoryContainer = findViewById(categoryId);
        if (categoryContainer != null) {
            // Set the background color based on theme
            // Note: We're not changing the background of the container as it already has a themed background
            // Instead, we'll just update the percentage text
            
            // Update the percentage text
            TextView percentageView = findViewById(percentageId);
            if (percentageView != null) {
                // Calculate percentage
                int percentage = (int) ((categoryTime * 100) / totalTime);
                // Format time more compactly for screen fit
                String timeString;
                if (categoryTime < 60000) { // Less than a minute
                    timeString = "00:00";
                } else {
                    timeString = android.text.format.DateUtils.formatElapsedTime(categoryTime / 1000);
                }
                percentageView.setText(percentage + "%\n" + timeString);
            }
        }
    }
    
    private void updateBarChart(long[] categoryTimes) {
        // Convert milliseconds to minutes for better readability
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        // Add entries for each category
        entries.add(new BarEntry(0, categoryTimes[0] / 60000f)); // Social Media (minutes)
        entries.add(new BarEntry(1, categoryTimes[1] / 60000f)); // Games (minutes)
        entries.add(new BarEntry(2, categoryTimes[2] / 60000f)); // Productivity (minutes)
        entries.add(new BarEntry(3, categoryTimes[3] / 60000f)); // Others (minutes)
        
        labels.add("Social");
        labels.add("Games");
        labels.add("Productivity");
        labels.add("Others");
        
        // Create dataset
        BarDataSet dataSet = new BarDataSet(entries, "Usage Time (minutes)");
        
        // Get theme-appropriate colors from ThemeManager
        int[] categoryColors = themeController.getThemeManager().getCategoryColors();
        List<Integer> colors = new ArrayList<>();
        for (int color : categoryColors) {
            colors.add(color);
        }
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        
        // Create BarData and set it to the chart
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        
        // Customize the chart
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.animateY(1000);
        barChart.invalidate(); // refresh
    }
    
    private void showNoDataMessage(String message) {
        List<DailyReportModel.ReportRow> rows = new ArrayList<>();
        rows.add(new DailyReportModel.ReportRow(message, "", 0, 0));
        recyclerView.setAdapter(new DailyReportAdapter(rows));
        
        // Hide the chart and other UI elements when there's no data
        barChart.setVisibility(View.GONE);
        screenTimeText.setText("No screen time data available");
        reportStatusText.setText("Please check your usage permissions");
        additionalDetailsText.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onSupportNavigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeController != null) {
            themeController.cleanup();
        }
        if (dailyReportController != null) {
            dailyReportController.cleanup();
        }
    }
}