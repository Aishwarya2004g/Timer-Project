package com.example.timerapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {
    private TextView tvTimerDisplay;
    private EditText etHours, etMinutes, etSeconds;
    private Button buttonStart, buttonPause, buttonReset, buttonSoundSettings, buttonTimerHistory;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning;
    private long timeLeftInMillis;
    private TimerDatabaseHelper dbHelper;
    private String selectedSound = "sound1"; // Default sound
    private String originalDuration;  // Store the original duration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        etHours = findViewById(R.id.etHours);
        etMinutes = findViewById(R.id.etMinutes);
        etSeconds = findViewById(R.id.etSeconds);
        buttonStart = findViewById(R.id.buttonStart);
        buttonPause = findViewById(R.id.buttonPause);
        buttonReset = findViewById(R.id.buttonReset);
        buttonSoundSettings = findViewById(R.id.buttonSoundSettings);
        buttonTimerHistory = findViewById(R.id.buttonTimerHistory);

        dbHelper = new TimerDatabaseHelper(this);

        buttonStart.setOnClickListener(v -> startTimer());
        buttonPause.setOnClickListener(v -> pauseTimer());
        buttonReset.setOnClickListener(v -> resetTimer());
        buttonSoundSettings.setOnClickListener(v -> startActivity(new Intent(TimerActivity.this, SoundSettingsActivity.class)));
        buttonTimerHistory.setOnClickListener(v -> startActivity(new Intent(TimerActivity.this, TimerHistoryActivity.class)));
    }

    private void startTimer() {
        String hoursText = etHours.getText().toString();
        String minutesText = etMinutes.getText().toString();
        String secondsText = etSeconds.getText().toString();

        int hours = hoursText.isEmpty() ? 0 : Integer.parseInt(hoursText);
        int minutes = minutesText.isEmpty() ? 0 : Integer.parseInt(minutesText);
        int seconds = secondsText.isEmpty() ? 0 : Integer.parseInt(secondsText);

        // Calculate total time in milliseconds
        long timeInMillis = (hours * 3600 * 1000) + (minutes * 60 * 1000) + (seconds * 1000);
        timeLeftInMillis = timeInMillis;

        // Save the original duration
        originalDuration = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        if (timeInMillis > 0) {
            countDownTimer = new CountDownTimer(timeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {
                    playSoundAndNotify();
                    saveTimerToHistory(); // Save the original duration
                }
            }.start();

            isTimerRunning = true;
            buttonStart.setEnabled(false);
            buttonPause.setEnabled(true);
        }
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
            buttonStart.setEnabled(true);
            buttonPause.setEnabled(false);
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timeLeftInMillis = 0;
        updateTimerDisplay();
        isTimerRunning = false;
        buttonStart.setEnabled(true);
        buttonPause.setEnabled(false);
    }

    private void updateTimerDisplay() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        tvTimerDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void playSoundAndNotify() {
        SharedPreferences prefs = getSharedPreferences("sound_preferences", MODE_PRIVATE);
        selectedSound = prefs.getString("selected_sound", "sound1");

        int soundResourceId = getSoundResourceId(selectedSound);
        MediaPlayer.create(TimerActivity.this, soundResourceId).start();
        Toast.makeText(TimerActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
    }

    private int getSoundResourceId(String soundName) {
        switch (soundName) {
            case "sound2":
                return R.raw.sound2;
            case "sound3":
                return R.raw.sound3;
            default:
                return R.raw.sound1;
        }
    }

    private void saveTimerToHistory() {
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        dbHelper.saveTimerHistory(originalDuration, endTime); // Use original duration
    }
}
