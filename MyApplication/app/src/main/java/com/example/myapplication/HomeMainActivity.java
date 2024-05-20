package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeMainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton homeButton;
    private ImageButton remindersButton;
    private ImageButton symptomsButton;
    private String username;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main_activity);

        username = getIntent().getStringExtra("username");

        homeButton = findViewById(R.id.homeButton);
        remindersButton = findViewById(R.id.remindersButton);
        symptomsButton = findViewById(R.id.symptomsButton);

        // Set OnClickListener for navigation
        homeButton.setOnClickListener(this);
        remindersButton.setOnClickListener(this);
        symptomsButton.setOnClickListener(this);

        // Set OnTouchListener for button scaling effect
        homeButton.setOnTouchListener(new ScaleTouchListener());
        remindersButton.setOnTouchListener(new ScaleTouchListener());
        symptomsButton.setOnTouchListener(new ScaleTouchListener());

        // Initialize database
        db = openOrCreateDatabase("ProjectDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS reminders(name VARCHAR, reminder VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS appointments(name VARCHAR, appointment VARCHAR);");

        // Load initial fragment with username
        HomePage homePageFragment = new HomePage();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("nextMedication", getNextReminder("reminders"));
        bundle.putString("nextAppointment", getNextReminder("appointments"));
        homePageFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homePageFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("username", username);

        if (v.getId() == R.id.homeButton) {
            fragment = new HomePage();
            bundle.putString("nextMedication", getNextReminder("reminders"));
            bundle.putString("nextAppointment", getNextReminder("appointments"));
        } else if (v.getId() == R.id.remindersButton) {
            fragment = new RemindersPage();
            bundle.putString("nextMedication", getNextReminder("reminders"));
            bundle.putString("nextAppointment", getNextReminder("appointments"));
        } else if (v.getId() == R.id.symptomsButton) {
            fragment = new SymptomsPage();
            // Add other relevant data to the bundle if needed
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            navigateToFragment(fragment);
        }
    }

    public void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private String getNextReminder(String tableName) {
        Cursor cursor = db.rawQuery("SELECT " + tableName.substring(0, tableName.length() -1) + " FROM " + tableName + " LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String reminder = cursor.getString(0);
            cursor.close();
            return reminder;
        } else {
            cursor.close();
            return null;
        }
    }

    private class ScaleTouchListener implements View.OnTouchListener {

        private static final float SCALE_FACTOR = 0.9f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Scale down when pressed
                    v.setScaleX(SCALE_FACTOR);
                    v.setScaleY(SCALE_FACTOR);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Restore to original size when released or canceled
                    v.setScaleX(1f);
                    v.setScaleY(1f);
                    break;
            }
            return false;
        }
    }
}
