package com.example.buzzbreakphone;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzbreakphone.controllers.SelectAppsController;
import com.example.buzzbreakphone.controllers.ThemeController;
import com.example.buzzbreakphone.models.SelectAppsModel;

import java.util.ArrayList;
import java.util.List;

public class SelectAppsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SelectAppsAdapter adapter;
    private ThemeController themeController;
    private SelectAppsController selectAppsController;
    private List<SelectAppsModel.AppInfo> allApps;
    private List<String> previouslySelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_apps);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Allowed Apps");
        }

        // Initialize MVC components
        themeController = new ThemeController(this);
        themeController.initialize();
        selectAppsController = new SelectAppsController(this);
        selectAppsController.initialize();

        // Apply theme
        themeController.getThemeManager().applyFullTheme(this);

        recyclerView = findViewById(R.id.rv_select_apps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get previously selected apps
        previouslySelected = selectAppsController.getSelectedApps();

        // Load all user apps
        loadAllUserApps();

        // Set up buttons
        Button btnSave = findViewById(R.id.btn_save_selection);
        Button btnSelectAll = findViewById(R.id.btn_select_all);
        Button btnDeselectAll = findViewById(R.id.btn_deselect_all);

        btnSave.setOnClickListener(v -> saveSelection());
        btnSelectAll.setOnClickListener(v -> selectAll());
        btnDeselectAll.setOnClickListener(v -> deselectAll());
    }

    private void loadAllUserApps() {
        allApps = selectAppsController.getAllUserApps();
        
        // Mark previously selected apps
        for (SelectAppsModel.AppInfo app : allApps) {
            if (previouslySelected.contains(app.getPackageName())) {
                app.setSelected(true);
            }
        }

        adapter = new SelectAppsAdapter(this, allApps);
        recyclerView.setAdapter(adapter);

        updateSelectedCount();
    }

    private void updateSelectedCount() {
        int selectedCount = 0;
        for (SelectAppsModel.AppInfo app : allApps) {
            if (app.isSelected()) {
                selectedCount++;
            }
        }

        TextView countText = findViewById(R.id.tv_selected_count);
        countText.setText("Selected: " + selectedCount + " apps");
    }

    private void saveSelection() {
        List<String> selectedPackages = new ArrayList<>();
        for (SelectAppsModel.AppInfo app : allApps) {
            if (app.isSelected()) {
                selectedPackages.add(app.getPackageName());
            }
        }

        // Save through the controller
        selectAppsController.saveSelectedApps(selectedPackages);

        Toast.makeText(this, "Selection saved! " + selectedPackages.size() + " apps allowed.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void selectAll() {
        selectAppsController.selectAllApps(allApps);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateSelectedCount();
        Toast.makeText(this, "All apps selected", Toast.LENGTH_SHORT).show();
    }

    private void deselectAll() {
        selectAppsController.deselectAllApps(allApps);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateSelectedCount();
        Toast.makeText(this, "All apps deselected", Toast.LENGTH_SHORT).show();
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

    // Method called by adapter when an app selection changes
    public void onAppSelectionChanged() {
        updateSelectedCount();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeController != null) {
            themeController.cleanup();
        }
        if (selectAppsController != null) {
            selectAppsController.cleanup();
        }
    }
}


