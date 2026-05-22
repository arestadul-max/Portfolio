package com.example.buzzbreakphone;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buzzbreakphone.controllers.ThemeController;
import com.example.buzzbreakphone.controllers.TipsController;
import com.example.buzzbreakphone.models.TipsModel;

import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private ThemeController themeController;
    private TipsController tipsController;
    private ListView tipsListView;
    private TextView tipsTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tips);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Digital Wellness Tips");
        }

        // Initialize MVC components
        themeController = new ThemeController(this);
        themeController.initialize();
        tipsController = new TipsController(this);
        tipsController.initialize();

        // Apply theme
        themeController.getThemeManager().applyFullTheme(this);

        // Initialize views
        tipsTitleView = findViewById(R.id.tv_tips_title);
        tipsListView = findViewById(R.id.list_tips);

        displayTips();
    }

    private void displayTips() {
        try {
            // Get all tips through the controller
            List<TipsModel.Tip> allTips = tipsController.getAllTips();
            
            if (allTips == null || allTips.isEmpty()) {
                Toast.makeText(this, "No tips available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create adapter for the ListView
            ArrayAdapter<TipsModel.Tip> adapter = new ArrayAdapter<TipsModel.Tip>(this, 
                    android.R.layout.simple_list_item_2, android.R.id.text1, allTips) {
                @Override
                public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                    android.view.View view = super.getView(position, convertView, parent);
                    
                    TipsModel.Tip tip = getItem(position);
                    
                    if (tip != null) {
                        // Set the title and description
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);
                        
                        text1.setText(tip.getEmoji() + " " + tip.getTitle());
                        text1.setTextColor(getResources().getColor(R.color.text_primary));
                        text1.setTextSize(16);
                        
                        text2.setText(tip.getDescription());
                        text2.setTextColor(getResources().getColor(R.color.text_secondary));
                        text2.setTextSize(14);
                    }
                    
                    return view;
                }
            };
            
            tipsListView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading tips: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        if (tipsController != null) {
            tipsController.cleanup();
        }
    }
}