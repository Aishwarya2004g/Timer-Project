package com.example.timerapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class TimerHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private TimerDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_history);
        //  back navigation
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> onBackPressed());


        listView = findViewById(R.id.historyListView);
        dbHelper = new TimerDatabaseHelper(this);

        Cursor cursor = dbHelper.getAllTimers();
        if (cursor != null && cursor.getCount() > 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[]{"duration", "end_time"},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0
            );
            listView.setAdapter(adapter);
        }
    }
}
