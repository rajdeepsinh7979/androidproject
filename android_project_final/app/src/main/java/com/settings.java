package com.beatdrops.beatdrops_rhythmrush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class settings extends AppCompatActivity {
    private static final String PREFS_NAME = "BeatDropsPrefs";
    private static final String VIBRATION_KEY = "VIBRATION_ENABLED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        musicClickArea.setOnClickListener(v -> {
            Intent intent = new Intent(settings.this, songselection.class);
            startActivity(intent);

            // Optional: smooth transition
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        SwitchCompat switchVibration = findViewById(R.id.switchVibration);

// Load saved state
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean vibrationEnabled = prefs.getBoolean(VIBRATION_KEY, true); // default ON
        switchVibration.setChecked(vibrationEnabled);

// Save on toggle
        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(VIBRATION_KEY, isChecked).apply();
        });
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        SeekBar seekbarVolume = findViewById(R.id.seekbarVolume);

// Set max to device music stream max
        seekbarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

// Set current progress to device volume
        seekbarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

// Listen for changes
        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            progress,
                            0 // or AudioManager.FLAG_SHOW_UI for system volume popup
                    );
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        
    }
}
