package com.example.timerapplication;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SoundSettingsActivity extends AppCompatActivity {
    private RadioGroup soundOptionsGroup;
    private Button previewButton, saveButton;
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);

        preferences = getSharedPreferences("QuickTimerPrefs", MODE_PRIVATE);

        soundOptionsGroup = findViewById(R.id.soundOptionsGroup);
        previewButton = findViewById(R.id.previewButton);
        saveButton = findViewById(R.id.saveButton);

        previewButton.setOnClickListener(v -> previewSelectedSound());
        saveButton.setOnClickListener(v -> saveSelectedSound());
    }

    private void previewSelectedSound() {
        int selectedSoundId = getSelectedSoundId();

        // Stop any currently playing sound before previewing new one
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, selectedSoundId);
        mediaPlayer.start();
    }

    private void saveSelectedSound() {
        int selectedSoundId = getSelectedSoundId();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selectedSound", selectedSoundId);
        editor.apply();

        Toast.makeText(this, "Sound selection saved", Toast.LENGTH_SHORT).show();
    }

    private int getSelectedSoundId() {
        int selectedRadioId = soundOptionsGroup.getCheckedRadioButtonId();

        if (selectedRadioId == R.id.soundOption1) {
            return R.raw.sound1;
        } else if (selectedRadioId == R.id.soundOption2) {
            return R.raw.sound2;
        } else {
            return R.raw.sound3;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
