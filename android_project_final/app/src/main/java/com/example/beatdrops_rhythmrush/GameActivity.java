package com.example.beatdrops_rhythmrush;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Receive the selected song from MusicSelection
        int music = getIntent().getIntExtra("music", 0);

        // Play the song
        if(music != 0) {
            player = MediaPlayer.create(this, music);
            player.start();
        }

        // Just for testing: show label
        TextView txt = findViewById(R.id.songLabel);
        txt.setText("Playing Selected Song...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
