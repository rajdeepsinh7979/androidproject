package com.example.beatdrops_rhythmrush;

import android.os.Bundle;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000; // 2 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full-screen immersive mode (optional)
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

        // Delay before moving to main content
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.splash_image).setVisibility(android.view.View.GONE);

            }
        } , SPLASH_DURATION);
    }


}