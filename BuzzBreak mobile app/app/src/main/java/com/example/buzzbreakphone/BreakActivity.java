package com.example.buzzbreakphone;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buzzbreakphone.controllers.BreakController;
import com.example.buzzbreakphone.controllers.ThemeController;

public class BreakActivity extends AppCompatActivity {

    private static final long MILLIS_IN_MINUTE = 60000;
    
    private TextView timerText, breakInfoText, sessionStatusText;
    private EditText focusMinutesEditText, breakMinutesEditText;
    private Button startFocusBtn, startBreakBtn, pauseResumeBtn, resetBtn;
    private CountDownTimer currentTimer;
    private long timeRemaining;
    private boolean isRunning = false;
    private boolean isFocusMode = false;
    
    private ThemeController themeController;
    private BreakController breakController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_break);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pomodoro Break");
        }

        // Initialize MVC components
        themeController = new ThemeController(this);
        themeController.initialize();
        breakController = new BreakController(this);
        breakController.initialize();

        // Apply theme
        themeController.getThemeManager().applyFullTheme(this);

        initializeViews();
        setupClickListeners();
        
        // Load saved settings
        loadSavedSettings();
        updateTimerDisplay();
    }

    private void initializeViews() {
        timerText = findViewById(R.id.tv_timer);
        breakInfoText = findViewById(R.id.tv_break_info);
        sessionStatusText = findViewById(R.id.tv_session_status);
        focusMinutesEditText = findViewById(R.id.et_focus_min);
        breakMinutesEditText = findViewById(R.id.et_break_min);
        startFocusBtn = findViewById(R.id.btn_start_focus);
        startBreakBtn = findViewById(R.id.btn_start_break);
        pauseResumeBtn = findViewById(R.id.btn_pause_resume);
        resetBtn = findViewById(R.id.btn_reset);
    }

    private void setupClickListeners() {
        startFocusBtn.setOnClickListener(v -> startFocusTimer());
        startBreakBtn.setOnClickListener(v -> startBreakTimer());
        pauseResumeBtn.setOnClickListener(v -> togglePauseResume());
        resetBtn.setOnClickListener(v -> resetTimer());
    }

    private void loadSavedSettings() {
        // Load saved focus and break times
        int savedFocusTime = breakController.getFocusTime();
        int savedBreakTime = breakController.getBreakTime();
        
        focusMinutesEditText.setText(String.valueOf(savedFocusTime));
        breakMinutesEditText.setText(String.valueOf(savedBreakTime));
        
        // Set info text
        breakInfoText.setText("Set your focus and break times, then start a session.");
    }

    private void startFocusTimer() {
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        
        isFocusMode = true;
        breakController.setFocusMode(true);
        
        // Get focus time from EditText or use saved value
        int focusMinutes;
        try {
            String focusText = focusMinutesEditText.getText().toString().trim();
            if (focusText.isEmpty()) {
                focusMinutes = breakController.getFocusTime(); // Use saved value
            } else {
                focusMinutes = Integer.parseInt(focusText);
                // Validate input
                if (focusMinutes <= 0) {
                    focusMinutes = breakController.getFocusTime(); // Use saved value
                    Toast.makeText(this, "Invalid focus time, using saved value", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            focusMinutes = breakController.getFocusTime(); // Use saved value
            Toast.makeText(this, "Invalid focus time, using saved value", Toast.LENGTH_SHORT).show();
        }
        
        // Save the focus time
        breakController.setFocusTime(focusMinutes);
        focusMinutesEditText.setText(String.valueOf(focusMinutes)); // Update UI with validated value
        
        timeRemaining = focusMinutes * MILLIS_IN_MINUTE;
        
        sessionStatusText.setText("Focus session in progress...");
        startTimer();
        updateButtonStates();
    }

    private void startBreakTimer() {
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        
        isFocusMode = false;
        breakController.setFocusMode(false);
        
        // Get break time from EditText or use saved value
        int breakMinutes;
        try {
            String breakText = breakMinutesEditText.getText().toString().trim();
            if (breakText.isEmpty()) {
                breakMinutes = breakController.getBreakTime(); // Use saved value
            } else {
                breakMinutes = Integer.parseInt(breakText);
                // Validate input
                if (breakMinutes <= 0) {
                    breakMinutes = breakController.getBreakTime(); // Use saved value
                    Toast.makeText(this, "Invalid break time, using saved value", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            breakMinutes = breakController.getBreakTime(); // Use saved value
            Toast.makeText(this, "Invalid break time, using saved value", Toast.LENGTH_SHORT).show();
        }
        
        // Save the break time
        breakController.setBreakTime(breakMinutes);
        breakMinutesEditText.setText(String.valueOf(breakMinutes)); // Update UI with validated value
        
        timeRemaining = breakMinutes * MILLIS_IN_MINUTE;
        
        sessionStatusText.setText("Break time in progress...");
        startTimer();
        updateButtonStates();
    }

    private void startTimer() {
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        
        isRunning = true;
        currentTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeRemaining = 0;
                updateTimerDisplay();
                updateButtonStates();
                String message = isFocusMode ? "Focus session completed!" : "Break time is over!";
                sessionStatusText.setText(message);
                
                // Show notification when timer completes
                NotificationUtils.showBreakNotification(BreakActivity.this, message);
            }
        }.start();
        
        updateButtonStates();
    }

    private void togglePauseResume() {
        if (isRunning) {
            // Pause the timer
            if (currentTimer != null) {
                currentTimer.cancel();
            }
            isRunning = false;
            sessionStatusText.setText("Session paused");
        } else {
            // Resume the timer
            if (timeRemaining > 0) {
                String message = isFocusMode ? "Focus session resumed..." : "Break time resumed...";
                sessionStatusText.setText(message);
                startTimer();
            }
        }
        updateButtonStates();
    }

    private void resetTimer() {
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        isRunning = false;
        timeRemaining = 0;
        updateTimerDisplay();
        updateButtonStates();
        sessionStatusText.setText("Timer reset");
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeRemaining / MILLIS_IN_MINUTE);
        int seconds = (int) ((timeRemaining % MILLIS_IN_MINUTE) / 1000);
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateButtonStates() {
        // Update pause/resume button text
        pauseResumeBtn.setText(isRunning ? "⏸️ Pause" : "▶️ Resume");
        
        // Enable/disable buttons based on state
        startFocusBtn.setEnabled(!isRunning);
        startBreakBtn.setEnabled(!isRunning);
        pauseResumeBtn.setEnabled(timeRemaining > 0);
        resetBtn.setEnabled(timeRemaining > 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (currentTimer != null) {
            currentTimer.cancel();
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
    protected void onDestroy() {
        super.onDestroy();
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        if (themeController != null) {
            themeController.cleanup();
        }
        if (breakController != null) {
            breakController.cleanup();
        }
    }
}