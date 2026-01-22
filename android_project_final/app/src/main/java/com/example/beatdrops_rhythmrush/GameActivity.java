package com.beatdrops.beatdrops_rhythmrush;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class game extends AppCompatActivity {

    MediaPlayer player;
    Handler handler = new Handler();
    Random random = new Random();
    FrameLayout gameArea;
    TextView songLabel;

    int tileWidth;
    int tileHeight = 300;
    int laneCount = 4;

    boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        hideSystemUI();

        int music = getIntent().getIntExtra("music", 0);
        if (music != 0) {
            player = MediaPlayer.create(this, music);
            player.start();
        }

        songLabel = findViewById(R.id.songLabel);
        gameArea = findViewById(R.id.gameArea);

        gameArea.setOnClickListener(v -> triggerGameOver());

        gameArea.post(() -> {
            tileWidth = gameArea.getWidth() / laneCount;
            startTileSpawner();
        });
    }

    private void startTileSpawner() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!gameOver) {
                    spawnTile();
                    handler.postDelayed(this, 800);
                }
            }
        }, 1000);
    }

    private void spawnTile() {
        Button tile = new Button(this);
        tile.setBackgroundColor(Color.BLACK);

        boolean[] isHit = {false};

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                tileWidth,
                tileHeight
        );

        int laneIndex = random.nextInt(laneCount);
        params.leftMargin = laneIndex * tileWidth;
        tile.setLayoutParams(params);

        tile.setOnClickListener(v -> {
            isHit[0] = true;
            gameArea.removeView(tile);
        });

        gameArea.addView(tile);

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                tile,
                "translationY",
                -tileHeight,
                gameArea.getHeight()
        );

        animator.setDuration(3000);

        animator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isHit[0] && !gameOver) {
                    triggerGameOver();
                }
            }
        });

        animator.start();
    }

    private void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;

        handler.removeCallbacksAndMessages(null);

        if (player != null && player.isPlaying()) {
            player.pause();
        }

        songLabel.setText("GAME OVER");
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
