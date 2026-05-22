package com.example.buzzbreakphone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;

public class ThemeManager {
    
    private static final String THEME_PREFS = "theme_preferences";
    private static final String SELECTED_THEME = "selected_theme";
    private static final String DEFAULT_THEME = "joey_tripp";
    
    // Theme constants
    public static final String THEME_JOEY_TRIPP = "joey_tripp";
    public static final String THEME_OCEAN_BREEZE = "ocean_breeze";
    public static final String THEME_SUNSET_GLOW = "sunset_glow";
    public static final String THEME_FOREST_NIGHT = "forest_night";
    public static final String THEME_CYBER_NEON = "cyber_neon";
    public static final String THEME_MIDNIGHT_PURPLE = "midnight_purple";
    public static final String THEME_TREE_DESIGN = "tree_design";
    
    private Context context;
    private SharedPreferences preferences;
    
    public ThemeManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE);
    }
    
    public void setSelectedTheme(String themeName) {
        preferences.edit().putString(SELECTED_THEME, themeName).apply();
    }
    
    public String getSelectedTheme() {
        return preferences.getString(SELECTED_THEME, DEFAULT_THEME);
    }
    
    public ThemeConfig getCurrentTheme() {
        String selectedTheme = getSelectedTheme();
        return getThemeConfig(selectedTheme);
    }
    
    public ThemeConfig getThemeConfig(String themeName) {
        switch (themeName) {
            case THEME_JOEY_TRIPP:
                return new ThemeConfig(
                    "Joey Tripp",
                    "🌟",
                    R.color.navy_background,
                    R.color.card_background,
                    R.color.text_primary,
                    R.color.gradient_start_blue,
                    R.color.gradient_end_purple,
                    R.drawable.joey_card_bg
                );
                
            case THEME_OCEAN_BREEZE:
                return new ThemeConfig(
                    "Ocean Breeze",
                    "🌊",
                    R.color.navy_background,
                    R.color.card_background,
                    R.color.text_primary,
                    R.color.gradient_start_blue,
                    R.color.gradient_end_purple,
                    R.drawable.ocean_card_bg
                );
                
            case THEME_SUNSET_GLOW:
                return new ThemeConfig(
                    "Sunset Glow",
                    "🌅",
                    R.color.navy_background,
                    R.color.card_background,
                    R.color.text_primary,
                    R.color.gradient_start_blue,
                    R.color.gradient_end_purple,
                    R.drawable.sunset_card_bg
                );
                
            case THEME_FOREST_NIGHT:
                return new ThemeConfig(
                    "Forest Night",
                    "🌲",
                    R.color.navy_background,
                    R.color.card_background,
                    R.color.text_primary,
                    R.color.gradient_start_blue,
                    R.color.gradient_end_purple,
                    R.drawable.forest_card_bg
                );
                
            case THEME_CYBER_NEON:
                return new ThemeConfig(
                    "Cyber Neon",
                    "⚡",
                    R.color.navy_background,
                    R.color.card_background,
                    R.color.text_primary,
                    R.color.gradient_start_blue,
                    R.color.gradient_end_purple,
                    R.drawable.cyber_card_bg
                );
                
            case THEME_MIDNIGHT_PURPLE:
                return new ThemeConfig(
                    "Midnight Purple",
                    "🔮",
                    R.color.navy_background,
                    R.color.card_background,
                    R.color.text_primary,
                    R.color.gradient_start_blue,
                    R.color.gradient_end_purple,
                    R.drawable.midnight_card_bg
                );
                
            case THEME_TREE_DESIGN:
                return new ThemeConfig(
                    "Tree Design",
                    "🌳",
                    R.color.tree_background,
                    R.color.tree_card_bg,
                    R.color.text_primary,
                    R.color.tree_gradient_start,
                    R.color.tree_gradient_end,
                    R.drawable.tree_card_bg
                );
                
            default:
                return getThemeConfig(DEFAULT_THEME);
        }
    }
    
    public String[] getAllThemeIds() {
        return new String[]{
            THEME_JOEY_TRIPP,
            THEME_OCEAN_BREEZE,
            THEME_SUNSET_GLOW,
            THEME_FOREST_NIGHT,
            THEME_CYBER_NEON,
            THEME_MIDNIGHT_PURPLE,
            THEME_TREE_DESIGN
        };
    }
    
    public ThemeConfig[] getAllThemes() {
        String[] themeIds = getAllThemeIds();
        ThemeConfig[] themes = new ThemeConfig[themeIds.length];
        
        for (int i = 0; i < themeIds.length; i++) {
            themes[i] = getThemeConfig(themeIds[i]);
            themes[i].setThemeId(themeIds[i]);
        }
        
        return themes;
    }
    
    // Get category colors based on the current theme
    public int[] getCategoryColors() {
        String selectedTheme = getSelectedTheme();
        
        switch (selectedTheme) {
            case THEME_OCEAN_BREEZE:
                return new int[]{
                    Color.parseColor("#4FC3F7"), // Social Media - Light Blue
                    Color.parseColor("#4DD0E1"), // Games - Cyan
                    Color.parseColor("#4DB6AC"), // Productivity - Teal
                    Color.parseColor("#81C784")  // Others - Green
                };
                
            case THEME_SUNSET_GLOW:
                return new int[]{
                    Color.parseColor("#FF4081"), // Social Media - Pink
                    Color.parseColor("#E040FB"), // Games - Purple
                    Color.parseColor("#7C4DFF"), // Productivity - Deep Purple
                    Color.parseColor("#536DFE")  // Others - Indigo
                };
                
            case THEME_FOREST_NIGHT:
                return new int[]{
                    Color.parseColor("#81C784"), // Social Media - Green
                    Color.parseColor("#4DB6AC"), // Games - Teal
                    Color.parseColor("#4DD0E1"), // Productivity - Cyan
                    Color.parseColor("#4FC3F7")  // Others - Light Blue
                };
                
            case THEME_CYBER_NEON:
                return new int[]{
                    Color.parseColor("#00FFCC"), // Social Media - Cyan
                    Color.parseColor("#00FF99"), // Games - Green
                    Color.parseColor("#00FF66"), // Productivity - Light Green
                    Color.parseColor("#00FF33")  // Others - Lime
                };
                
            case THEME_MIDNIGHT_PURPLE:
                return new int[]{
                    Color.parseColor("#E040FB"), // Social Media - Purple
                    Color.parseColor("#7C4DFF"), // Games - Deep Purple
                    Color.parseColor("#536DFE"), // Productivity - Indigo
                    Color.parseColor("#40C4FF")  // Others - Light Blue
                };
                
            case THEME_TREE_DESIGN:
                return new int[]{
                    Color.parseColor("#8B4513"), // Social Media - SaddleBrown
                    Color.parseColor("#228B22"), // Games - ForestGreen
                    Color.parseColor("#32CD32"), // Productivity - LimeGreen
                    Color.parseColor("#9ACD32")  // Others - YellowGreen
                };
                
            case THEME_JOEY_TRIPP:
            default:
                // Default Joey Tripp theme colors
                return new int[]{
                    Color.parseColor("#E94FFF"), // Social Media - Pink/Purple
                    Color.parseColor("#4AFF88"), // Games - Green/Cyan
                    Color.parseColor("#4F8FF7"), // Productivity - Blue/Light Blue
                    Color.parseColor("#C74FFF")  // Others - Purple/Magenta
                };
        }
    }
    
    // Apply theme to an activity
    public void applyThemeToActivity(Activity activity) {
        ThemeConfig currentTheme = getCurrentTheme();
        
        // Apply background color to the main layout
        View mainLayout = activity.findViewById(R.id.main);
        if (mainLayout != null) {
            int backgroundColor = getColorForTheme(currentTheme.getBackgroundColor());
            mainLayout.setBackgroundColor(ContextCompat.getColor(activity, backgroundColor));
        }
    }
    
    // Get the actual color resource based on theme
    private int getColorForTheme(int colorResource) {
        String selectedTheme = getSelectedTheme();
        
        switch (selectedTheme) {
            case THEME_OCEAN_BREEZE:
                return R.color.ocean_background;
            case THEME_SUNSET_GLOW:
                return R.color.sunset_background;
            case THEME_FOREST_NIGHT:
                return R.color.forest_background;
            case THEME_CYBER_NEON:
                return R.color.cyber_background;
            case THEME_MIDNIGHT_PURPLE:
                return R.color.midnight_background;
            case THEME_TREE_DESIGN:
                return R.color.tree_background;
            case THEME_JOEY_TRIPP:
            default:
                return R.color.navy_background;
        }
    }
    
    // Get card background for current theme
    public int getCurrentCardBackground() {
        String selectedTheme = getSelectedTheme();
        
        switch (selectedTheme) {
            case THEME_OCEAN_BREEZE:
                return R.drawable.ocean_card_bg;
            case THEME_SUNSET_GLOW:
                return R.drawable.sunset_card_bg;
            case THEME_FOREST_NIGHT:
                return R.drawable.forest_card_bg;
            case THEME_CYBER_NEON:
                return R.drawable.cyber_card_bg;
            case THEME_MIDNIGHT_PURPLE:
                return R.drawable.midnight_card_bg;
            case THEME_TREE_DESIGN:
                return R.drawable.tree_card_bg;
            case THEME_JOEY_TRIPP:
            default:
                return R.drawable.dark_card_bg;
        }
    }
    
    // Get darker card background for enhanced focus activities (Pomodoro-Break, Tips)
    public int getCurrentDarkerCardBackground() {
        String selectedTheme = getSelectedTheme();
        
        switch (selectedTheme) {
            case THEME_OCEAN_BREEZE:
            case THEME_SUNSET_GLOW:
            case THEME_FOREST_NIGHT:
            case THEME_CYBER_NEON:
            case THEME_MIDNIGHT_PURPLE:
            case THEME_TREE_DESIGN:
                // For now, use the regular card background for other themes
                // You can create darker variants later if needed
                return getCurrentCardBackground();
            case THEME_JOEY_TRIPP:
            default:
                return R.drawable.darker_card_bg;
        }
    }
    
    // Apply comprehensive theme to any activity
    public void applyFullTheme(Activity activity) {
        // Apply background color
        applyThemeToActivity(activity);
        
        // Apply to common UI elements
        applyThemeToCards(activity);
        applyThemeToButtons(activity);
    }
    
    // Apply theme to card elements
    private void applyThemeToCards(Activity activity) {
        int cardBackground = getCurrentCardBackground();
        int darkerCardBackground = getCurrentDarkerCardBackground();
        
        // Common card IDs across activities
        int[] cardIds = {
            R.id.btn_app_usage, R.id.btn_break, R.id.btn_buzz_mode, 
            R.id.btn_tips, R.id.btn_daily_report, R.id.btn_settings
        };
        
        for (int cardId : cardIds) {
            View card = activity.findViewById(cardId);
            if (card != null) {
                card.setBackgroundResource(cardBackground);
            }
        }
        
        // Special handling for darker cards in focus activities
        applyDarkerCardsIfNeeded(activity, darkerCardBackground);
    }
    
    // Apply darker backgrounds for focus-intensive activities
    private void applyDarkerCardsIfNeeded(Activity activity, int darkerBackground) {
        String activityName = activity.getClass().getSimpleName();
        
        if ("BreakActivity".equals(activityName)) {
            // Apply darker background to BreakActivity buttons that should use darker styling
            int[] buttonIds = {
                R.id.btn_start_focus, R.id.btn_start_break, 
                R.id.btn_pause_resume, R.id.btn_reset
            };
            
            for (int buttonId : buttonIds) {
                View button = activity.findViewById(buttonId);
                if (button != null) {
                    button.setBackgroundResource(darkerBackground);
                }
            }
        }
        
        if ("TipsActivity".equals(activityName)) {
            // Apply darker background to Tips title
            View tipsTitle = activity.findViewById(R.id.tv_tips_title);
            if (tipsTitle != null) tipsTitle.setBackgroundResource(darkerBackground);
        }
    }
    
    // Apply theme to button elements
    private void applyThemeToButtons(Activity activity) {
        int cardBackground = getCurrentCardBackground();
        
        // Common button IDs that should use card styling
        int[] buttonIds = {
            R.id.btn_apply_theme, R.id.btn_grant_usage, 
            R.id.btn_start_focus, R.id.btn_start_break,
            R.id.btn_select_apps, R.id.toggle_buzz
        };
        
        for (int buttonId : buttonIds) {
            View button = activity.findViewById(buttonId);
            if (button != null) {
                button.setBackgroundResource(cardBackground);
            }
        }
    }
    
    // Theme configuration class
    public static class ThemeConfig {
        private String themeId;
        private String name;
        private String emoji;
        private int backgroundColor;
        private int cardBackgroundColor;
        private int textColor;
        private int gradientStart;
        private int gradientEnd;
        private int cardDrawable;
        
        public ThemeConfig(String name, String emoji, int backgroundColor, 
                          int cardBackgroundColor, int textColor, 
                          int gradientStart, int gradientEnd, int cardDrawable) {
            this.name = name;
            this.emoji = emoji;
            this.backgroundColor = backgroundColor;
            this.cardBackgroundColor = cardBackgroundColor;
            this.textColor = textColor;
            this.gradientStart = gradientStart;
            this.gradientEnd = gradientEnd;
            this.cardDrawable = cardDrawable;
        }
        
        // Getters
        public String getThemeId() { return themeId; }
        public String getName() { return name; }
        public String getEmoji() { return emoji; }
        public int getBackgroundColor() { return backgroundColor; }
        public int getCardBackgroundColor() { return cardBackgroundColor; }
        public int getTextColor() { return textColor; }
        public int getGradientStart() { return gradientStart; }
        public int getGradientEnd() { return gradientEnd; }
        public int getCardDrawable() { return cardDrawable; }
        
        // Setters
        public void setThemeId(String themeId) { this.themeId = themeId; }
    }
}