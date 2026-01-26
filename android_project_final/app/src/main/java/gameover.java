package com.example.beatdrops_rhythmrush;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class gameover extends AppCompatActivity {

    private TextView scoreText, highscore;

    private static final String PREFS_NAME = "BeatDropsPrefs";
    private static final String HIGH_SCORE_KEY = "HIGH_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSystemUI();
        setContentView(R.layout.activity_gameover);

        scoreText = findViewById(R.id.scoreText);
        highscore = findViewById(R.id.highscore);

        // 🎯 Current score from intent
        int score = getIntent().getIntExtra("score", 0);
        scoreText.setText(String.valueOf(score));

        // 🏆 High score from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedHighScore = prefs.getInt(HIGH_SCORE_KEY, 0);
        highscore.setText(String.valueOf(savedHighScore));
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }
}
