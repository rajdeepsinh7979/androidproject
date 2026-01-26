package com.beatdrops.beatdrops_rhythmrush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class gameover extends AppCompatActivity {

    private TextView scoreText, highscore;
    private ImageButton homebtn, playagainbtn;

    private MediaPlayer clickSound;
    private boolean isNavigating = false;

    private int bpm;
    private int music;

    private static final String PREFS_NAME = "BeatDropsPrefs";
    private static final String HIGH_SCORE_KEY = "HIGH_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_gameover);

        // Views
        scoreText = findViewById(R.id.scoreText);
        highscore = findViewById(R.id.highscore);
        homebtn = findViewById(R.id.homebtn);
        playagainbtn = findViewById(R.id.playagainbtn);

        // Sounds
        clickSound = MediaPlayer.create(this, R.raw.click);

        // Data
        int score = getIntent().getIntExtra("score", 0);
        bpm = getIntent().getIntExtra("bpm", 120);
        music = getIntent().getIntExtra("music", 0);

        // High score
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedHighScore = prefs.getInt(HIGH_SCORE_KEY, 0);
        highscore.setText(String.valueOf(savedHighScore));

        // Animated score
        animateScore(scoreText, score);

        // Home button
        homebtn.setOnClickListener(v -> {
            if (isNavigating) return;
            isNavigating = true;

            playClick();

            v.postDelayed(() -> {
                Intent intent = new Intent(gameover.this, songselection.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 160);
        });


        // Play again button
        playagainbtn.setOnClickListener(v -> {
            if (isNavigating) return;
            isNavigating = true;

            playClick();

            v.postDelayed(() -> {
                Intent intent = new Intent(gameover.this, game.class);
                intent.putExtra("bpm", bpm);
                intent.putExtra("music", music);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 160);
        });

    }

    private void playClick() {
        if (clickSound != null) {
            clickSound.start();
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }
}
