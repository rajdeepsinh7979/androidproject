package com.beatdrops.beatdrops_rhythmrush;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class game extends AppCompatActivity {

    private MediaPlayer player;
    private Handler handler;
    private Random random;

    private FrameLayout gameArea;
    private TextView songLabel;

    private int tileWidth;
    private int tileHeight;
    private final int laneCount = 4;

    private int score = 0;
    private boolean gameOver = false;

    private static final String PREFS_NAME = "BeatDropsPrefs";
    private static final String HIGH_SCORE_KEY = "HIGH_SCORE";

    private int bpm = 120;
    private int spawnInterval;
    private int fallDuration;

    private final int[] normalTiles = {
            R.drawable.dropblue,
            R.drawable.dropgreen,
            R.drawable.droppurple,
            R.drawable.dropyellow
    };

    private final int fireTile = R.drawable.dropbomb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        hideSystemUI();

        
        handler = new Handler(Looper.getMainLooper());
        random = new Random();

        songLabel = findViewById(R.id.songLabel);
        gameArea = findViewById(R.id.gameArea);

        songLabel.setText("Score: 0");

        bpm = getIntent().getIntExtra("bpm", 120);
        int music = getIntent().getIntExtra("music", 0);

        if (music != 0) {
            player = MediaPlayer.create(this, music);
            if (player != null) {
                player.start();
            }
        }

        gameArea.post(() -> {
            tileWidth = gameArea.getWidth() / laneCount;
            tileHeight = Math.min(
                    (int) (tileWidth * 1.8f),
                    gameArea.getHeight() / 3
            );

            calculateTimingFromBPM();
            startTileSpawner();
            startDifficultyRamp();
        });
    }

    private void calculateTimingFromBPM() {
        int beatMs = 60000 / bpm;
        spawnInterval = Math.max(beatMs * 2, 700);
        fallDuration = Math.max(beatMs * 7, 3500);
    }

    private void startDifficultyRamp() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameOver) return;

                spawnInterval = Math.max((int) (spawnInterval * 0.92f), 450);
                fallDuration = Math.max((int) (fallDuration * 0.94f), 2200);

                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    private void startTileSpawner() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!gameOver) {
                    spawnTile();
                    handler.postDelayed(this, spawnInterval);
                }
            }
        }, 1000);
    }

    private boolean shouldSpawnFireTile() {
        return random.nextInt(8) == 0;
    }

    private void spawnTile() {
        if (gameOver) return;

        ImageView tile = new ImageView(this);
        tile.setScaleType(ImageView.ScaleType.FIT_XY);

        boolean isFireTile = shouldSpawnFireTile();
        tile.setImageResource(isFireTile
                ? fireTile
                : normalTiles[random.nextInt(normalTiles.length)]
        );

        boolean[] isHit = {false};

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(tileWidth, tileHeight);

        int laneIndex = random.nextInt(laneCount);
        params.leftMargin = laneIndex * tileWidth;
        tile.setLayoutParams(params);

        tile.setOnClickListener(v -> {
            if (isHit[0] || gameOver) return;
            isHit[0] = true;

            if (isFireTile) {
                triggerGameOver();
                return;
            }

            score += 10;
            songLabel.setText("Score: " + score);

            tile.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction(() -> gameArea.removeView(tile))
                    .start();
        });

        gameArea.addView(tile);

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                tile,
                "translationY",
                -tileHeight,
                gameArea.getHeight()
        );

        animator.setDuration(fallDuration);
        animator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isHit[0] && !gameOver && !isFireTile) {
                    triggerGameOver();
                }
            }
        });

        animator.start();
    }

    private void saveHighScoreIfNeeded() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedHigh = prefs.getInt(HIGH_SCORE_KEY, 0);

        if (score > savedHigh) {
            prefs.edit().putInt(HIGH_SCORE_KEY, score).apply();
        }
    }

    private void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;

        handler.removeCallbacksAndMessages(null);

        if (player != null && player.isPlaying()) {
            player.pause();
        }

        saveHighScoreIfNeeded();

        int highScore = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getInt(HIGH_SCORE_KEY, 0);

        songLabel.setText(
                "GAME OVER\nScore: " + score + "\nHigh Score: " + highScore
        );
    }

    @Override
    public void onBackPressed() {
        saveHighScoreIfNeeded();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveHighScoreIfNeeded();
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
        handler.removeCallbacksAndMessages(null);

        if (player != null) {
            player.release();
            player = null;
        }
    }
}
