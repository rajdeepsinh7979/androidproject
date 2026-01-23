package com.beatdrops.beatdrops_rhythmrush;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class game extends AppCompatActivity {

    // Core
    private MediaPlayer player;
    private Handler handler;
    private Random random;

    // UI
    private FrameLayout gameArea;
    private TextView songLabel;

    // Tile sizing
    private int tileWidth;
    private int tileHeight;
    private final int laneCount = 4;

    // Game state
    private boolean gameOver = false;

    // BPM
    private int bpm = 120;           // default BPM
    private int spawnInterval;       // ms
    private int fallDuration;        // ms

    // Tiles
    private final int[] normalTiles = {
            R.drawable.dropblue,
            R.drawable.dropgreen,
            R.drawable.droppurple,
            R.drawable.dropyellow
    };
    private final int fireTile = R.drawable.dropfire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        hideSystemUI();

        handler = new Handler();
        random = new Random();

        songLabel = findViewById(R.id.songLabel);
        gameArea = findViewById(R.id.gameArea);

        // 🎵 Get music & BPM from previous screen
        int music = getIntent().getIntExtra("music", 0);
        bpm = getIntent().getIntExtra("bpm", 120);

        calculateTimingFromBPM();

        if (music != 0) {
            player = MediaPlayer.create(this, music);
            player.start();
        }

        // Responsive sizing after layout
        gameArea.post(() -> {

            tileWidth = gameArea.getWidth() / laneCount;

            tileHeight = Math.min(
                    (int) (tileWidth * 1.8f),
                    gameArea.getHeight() / 3
            );

            startTileSpawner();
        });
    }

    // 🎵 Convert BPM → timing
    private void calculateTimingFromBPM() {

        int beatMs = 60000 / bpm;

        // 🎵 Spawn every 2 beats (much playable)
        spawnInterval = beatMs * 2;

        // 🎮 Tiles fall over 6–8 beats
        fallDuration = beatMs * 7;

        // 🔒 Safety limits (VERY IMPORTANT)
        spawnInterval = Math.max(spawnInterval, 600);
        fallDuration = Math.max(fallDuration, 3500);
    }


    // 🔥 Fire tile chance
    private boolean shouldSpawnFireTile() {
        return random.nextInt(7) == 0; // ~14%
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
        }, spawnInterval);
    }

    private void spawnTile() {
        ImageView tile = new ImageView(this);
        tile.setScaleType(ImageView.ScaleType.FIT_XY);

        boolean isFireTile = shouldSpawnFireTile();

        if (isFireTile) {
            tile.setImageResource(fireTile);
        } else {
            tile.setImageResource(
                    normalTiles[random.nextInt(normalTiles.length)]
            );
        }

        boolean[] isHit = {false};

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(tileWidth, tileHeight);

        int laneIndex = random.nextInt(laneCount);
        params.leftMargin = laneIndex * tileWidth;
        tile.setLayoutParams(params);

        tile.setOnClickListener(v -> {
            if (isHit[0] || gameOver) return;
            isHit[0] = true;

            // 🔥 Fire tile = instant game over
            if (isFireTile) {
                triggerGameOver();
                return;
            }

            // ✅ Normal tile tap animation
            tile.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .alpha(0f)
                    .setDuration(120)
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

    private void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;

        handler.removeCallbacksAndMessages(null);

        if (player != null) {
            player.pause();
        }

        songLabel.setText("GAME OVER");
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
