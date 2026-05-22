package com.example.buzzbreakphone.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for handling tips data.
 * Encapsulates all business logic related to tips functionality.
 */
public class TipsModel extends BaseModel {
    
    public TipsModel(Context context) {
        super(context);
    }
    
    @Override
    public void initialize() {
        // No initialization needed
    }
    
    @Override
    public void cleanup() {
        // No cleanup needed
    }
    
    /**
     * Get all digital wellness tips
     */
    public List<Tip> getAllTips() {
        List<Tip> tips = new ArrayList<>();
        
        tips.add(new Tip(
            "🌙 Night Mode",
            "Enable night mode on your devices to reduce blue light exposure before bedtime. This helps improve sleep quality and reduces eye strain.",
            "🌙"
        ));
        
        tips.add(new Tip(
            "⏰ Scheduled Breaks",
            "Set regular intervals to take breaks from your screen. Try the 20-20-20 rule: every 20 minutes, look at something 20 feet away for 20 seconds.",
            "⏰"
        ));
        
        tips.add(new Tip(
            "🔇 Do Not Disturb",
            "Use 'Do Not Disturb' mode during focused work or family time. Customize it to allow calls from important contacts while silencing other notifications.",
            "🔇"
        ));
        
        tips.add(new Tip(
            "📵 App Limits",
            "Set daily limits for time-consuming apps. Most smartphones have built-in screen time management features to help you stay within your limits.",
            "📵"
        ));
        
        tips.add(new Tip(
            "🔄 Digital Declutter",
            "Regularly review and uninstall apps you no longer use. A cleaner phone leads to less distraction and better focus.",
            "🔄"
        ));
        
        tips.add(new Tip(
            "📚 Reading Time",
            "Dedicate some screen time to reading digital books instead of social media. This provides mental stimulation without the constant notifications.",
            "📚"
        ));
        
        tips.add(new Tip(
            "🚶 Physical Activity",
            "Balance screen time with physical activity. Take walks, exercise, or engage in hobbies that don't involve screens.",
            "🚶"
        ));
        
        tips.add(new Tip(
            "🍽️ Mindful Usage",
            "Avoid using phones during meals. Focus on your food and conversations with family or friends instead of scrolling through feeds.",
            "🍽️"
        ));
        
        tips.add(new Tip(
            "📳 Airplane Mode",
            "Use airplane mode during important tasks or relaxation time. This completely disconnects you from notifications and calls.",
            "📳"
        ));
        
        tips.add(new Tip(
            "📊 Track Your Usage",
            "Regularly check your screen time statistics to identify usage patterns. Awareness is the first step toward healthier digital habits.",
            "📊"
        ));
        
        return tips;
    }
    
    /**
     * Data class to represent a single tip
     */
    public static class Tip {
        private String title;
        private String description;
        private String emoji;
        
        public Tip(String title, String description, String emoji) {
            this.title = title;
            this.description = description;
            this.emoji = emoji;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getEmoji() {
            return emoji;
        }
    }
}