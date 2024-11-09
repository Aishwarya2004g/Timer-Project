package com.example.timerapplication;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SoundSettingsActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);

        preferences = getSharedPreferences("sound_preferences", MODE_PRIVATE);

        findViewById(R.id.soundOption1).setOnClickListener(v -> selectSound("sound1", R.raw.sound1));
        findViewById(R.id.soundOption2).setOnClickListener(v -> selectSound("sound2", R.raw.sound2));
        findViewById(R.id.soundOption3).setOnClickListener(v -> selectSound("sound3", R.raw.sound3));
    }

    private void selectSound(String soundKey, int soundRes) {
        stopMediaPlayer();
        mediaPlayer = MediaPlayer.create(this, soundRes);
        mediaPlayer.start();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selected_sound", soundKey);
        editor.apply();
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMediaPlayer();
    }
}
