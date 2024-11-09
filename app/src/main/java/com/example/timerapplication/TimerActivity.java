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

        // Check if any of the input fields are empty
        if (hoursText.isEmpty() || minutesText.isEmpty() || secondsText.isEmpty()) {
            Toast.makeText(TimerActivity.this, "Please fill all input fields (hours, minutes, and seconds).", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the inputs have more than two digits
        if (hoursText.length() > 2 || minutesText.length() > 2 || secondsText.length() > 2) {
            Toast.makeText(TimerActivity.this, "Please enter valid time (up to 2 digits for hours, minutes, and seconds).", Toast.LENGTH_SHORT).show();
            return;
        }

        int hours = Integer.parseInt(hoursText);
        int minutes = Integer.parseInt(minutesText);
        int seconds = Integer.parseInt(secondsText);

        // Validate the input range for hours, minutes, and seconds
        if (hours > 23 || minutes > 59 || seconds > 59) {
            Toast.makeText(TimerActivity.this, "Invalid time input. Please enter valid hours (0-23), minutes (0-59), and seconds (0-59).", Toast.LENGTH_SHORT).show();
            return;
        }

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
