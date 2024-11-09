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

public class TimerActivity extends AppCompatActivity {
    private TextView timerDisplay;
    private EditText inputHours, inputMinutes, inputSeconds;
    private Button startButton, pauseButton, resetButton;
    private CountDownTimer timer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 0;
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        preferences = getSharedPreferences("QuickTimerPrefs", MODE_PRIVATE);
        int soundId = preferences.getInt("selectedSound", R.raw.sound1); // default sound

        timerDisplay = findViewById(R.id.timerDisplay);
        inputHours = findViewById(R.id.inputHours);
        inputMinutes = findViewById(R.id.inputMinutes);
        inputSeconds = findViewById(R.id.inputSeconds);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);

        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        if (!isRunning) {
            int hours = Integer.parseInt(inputHours.getText().toString());
            int minutes = Integer.parseInt(inputMinutes.getText().toString());
            int seconds = Integer.parseInt(inputSeconds.getText().toString());
            timeLeftInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;

            timer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {
                    mediaPlayer = MediaPlayer.create(TimerActivity.this, R.raw.notification_sound); // Use selected sound
                    mediaPlayer.start();
                    Toast.makeText(TimerActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                    isRunning = false;
                }
            };
            timer.start();
            isRunning = true;
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            timer.cancel();
            isRunning = false;
        }
    }

    private void resetTimer() {
        if (isRunning) {
            timer.cancel();
        }
        timeLeftInMillis = 0;
        updateTimerDisplay();
        isRunning = false;
    }

    private void updateTimerDisplay() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerDisplay.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }
}
