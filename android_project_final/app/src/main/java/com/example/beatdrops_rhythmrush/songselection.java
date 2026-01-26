package com.beatdrops.beatdrops_rhythmrush;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class songselection extends AppCompatActivity {

    GridLayout songGrid;
    TextView bestScoreText;
    ArrayList<Song> songs = new ArrayList<>();
    private static final String PREFS_NAME = "BeatDropsPrefs";
    private static final String HIGH_SCORE_KEY = "HIGH_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songselection);

        hideSystemUI();

        songGrid = findViewById(R.id.songGrid);
        bestScoreText = findViewById(R.id.bestScoreText); // ✅ FIX

        // ✅ Load High Score
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int highScore = prefs.getInt(HIGH_SCORE_KEY, 0);
        bestScoreText.setText(String.valueOf(highScore));

        // Add your songs here
        songs.add(new Song("Believer", R.raw.believer, 125));
        songs.add(new Song("Faded", R.raw.faded, 90));
        songs.add(new Song("Shape Of You", R.raw.shapeofyou, 96));
        songs.add(new Song("Thunder", R.raw.thunder, 168));
        songs.add(new Song("Test Song (UI Only)", 0, 110));

        loadSongs();
    }
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int highScore = prefs.getInt(HIGH_SCORE_KEY, 0);
        bestScoreText.setText(String.valueOf(highScore));
    }


    @SuppressLint("ClickableViewAccessibility")
    private void loadSongs() {
        LayoutInflater inflater = LayoutInflater.from(this);

        // ✅ Load animations ONCE
        Animation pressAnim = AnimationUtils.loadAnimation(this, R.anim.button_press);
        Animation releaseAnim = AnimationUtils.loadAnimation(this, R.anim.button_release);

        for (Song song : songs) {

            View row = inflater.inflate(R.layout.song_item, songGrid, false);

            TextView songName = row.findViewById(R.id.songName);
            ImageView playBtn = row.findViewById(R.id.playBtn);

            songName.setText(song.name);

            playBtn.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(pressAnim);
                        return true;

                    case MotionEvent.ACTION_UP:
                        v.startAnimation(releaseAnim);

                        v.performClick(); // ✅ FIXES WARNING

                        Intent i = new Intent(songselection.this, game.class);
                        i.putExtra("music", song.musicRes);
                        i.putExtra("bpm", song.bpm);
                        startActivity(i);
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(releaseAnim);
                        return true;
                }
                return false;
            });

            songGrid.addView(row);
        }
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
}