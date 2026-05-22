package com.example.buzzbreakphone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        new Handler(getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }, 1500);
    }
}


