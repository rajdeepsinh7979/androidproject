package com.example.beatdrops_rhythmrush;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class musicselection extends AppCompatActivity {

    GridLayout songGrid;
    ArrayList<Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicselection);

        hideSystemUI();

        songGrid = findViewById(R.id.songGrid);

        // Add your songs here
        songs.add(new Song("Believer", R.raw.believer, 125));
        songs.add(new Song("Faded", R.raw.faded, 90));
        songs.add(new Song("Shape Of You", R.raw.shapeofyou, 96));
        songs.add(new Song("Thunder", R.raw.thunder, 168));

        loadSongs();
    }

    // 🔥 THIS is the real loadSongs
    private void loadSongs() {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Song song : songs) {

            View row = inflater.inflate(R.layout.song_item, songGrid, false);

            TextView songName = row.findViewById(R.id.songName);
            ImageView playBtn = row.findViewById(R.id.playBtn);

            songName.setText(song.name);   // ✅ correct

            playBtn.setOnClickListener(v -> {
                Intent i = new Intent(this, GameActivity.class);
                i.putExtra("music", song.musicRes);   // ✅ correct
                i.putExtra("bpm", song.bpm);          // ✅ correct
                startActivity(i);
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