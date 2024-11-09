package com.example.timerapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TimerHistoryActivity extends AppCompatActivity {
    private ListView historyListView;
    private SQLiteDatabase database;
    private ArrayList<String> historyList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_history);

        historyListView = findViewById(R.id.historyListView);
        historyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        database = openOrCreateDatabase("QuickTimerDB", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS TimerHistory(duration TEXT, endTime TEXT);");

        loadHistory();
    }

    private void loadHistory() {
        Cursor cursor = database.rawQuery("SELECT * FROM TimerHistory", null);

        if (cursor.moveToFirst()) {
            do {
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
                historyList.add("Duration: " + duration + ", Ended at: " + endTime);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}
