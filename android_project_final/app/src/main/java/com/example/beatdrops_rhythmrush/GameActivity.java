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

    private MediaPlayer player;
    private Handler handler;
    private Random random;

    private FrameLayout gameArea;
    private TextView songLabel;

    private int tileWidth;
    private int tileHeight;
    private final int laneCount = 4;

    private boolean gameOver = false;

    // 🎵 Tile images
    private final int[] tileImages = {
            R.drawable.dropblue,
            R.drawable.dropgreen,
            R.drawable.droppurple,
            R.drawable.dropyellow
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        hideSystemUI();

        handler = new Handler();
        random = new Random();

        songLabel = findViewById(R.id.songLabel);
        gameArea = findViewById(R.id.gameArea);

        int music = getIntent().getIntExtra("music", 0);
        if (music != 0) {
            player = MediaPlayer.create(this, music);
            player.start();
        }

        // Wait for layout → calculate responsive tile size
        gameArea.post(() -> {

            // Divide screen into lanes
            tileWidth = gameArea.getWidth() / laneCount;

            // Height based on width (perfect for phone + tablet)
            tileHeight = Math.min(
                    (int) (tileWidth * 1.8f),
                    gameArea.getHeight() / 3
            );

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
        ImageView tile = new ImageView(this);
        tile.setScaleType(ImageView.ScaleType.FIT_XY);

        // Random tile image
        tile.setImageResource(
                tileImages[random.nextInt(tileImages.length)]
        );

        boolean[] isHit = {false};

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(tileWidth, tileHeight);

        int laneIndex = random.nextInt(laneCount);
        params.leftMargin = laneIndex * tileWidth;
        tile.setLayoutParams(params);

        tile.setOnClickListener(v -> {
            if (isHit[0]) return;

            isHit[0] = true;

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
