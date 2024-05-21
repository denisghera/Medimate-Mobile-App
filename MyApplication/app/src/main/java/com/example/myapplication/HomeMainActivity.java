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
    public String username;
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
        db.execSQL("DROP TABLE reminders");
        db.execSQL("DROP TABLE appointments");
        db.execSQL("CREATE TABLE IF NOT EXISTS reminders(name VARCHAR, medicationName VARCHAR, time VARCHAR, day VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS appointments(name VARCHAR, doctorName VARCHAR, specialization VARCHAR, time VARCHAR, day VARCHAR);");

        // Load initial fragment with username
        HomePage homePageFragment = new HomePage();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("nextMedication", getNextReminder());
        bundle.putString("nextAppointment", getNextAppointment());
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
            bundle.putString("nextMedication", getNextReminder());
            bundle.putString("nextAppointment", getNextAppointment());
        } else if (v.getId() == R.id.remindersButton) {
            fragment = new RemindersPage();
            bundle.putString("nextMedication", getNextReminder());
            bundle.putString("nextAppointment", getNextAppointment());
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
    private String getNextReminder() {
        Cursor cursor = db.rawQuery("SELECT medicationName, time FROM reminders ORDER BY time ASC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String medicationName = cursor.getString(0);
            String time = cursor.getString(1);
            cursor.close();
            return medicationName + " " + time;
        } else {
            cursor.close();
            return null;
        }
    }
    private String getNextAppointment() {
        Cursor cursor = db.rawQuery("SELECT doctorName, time FROM appointments ORDER BY time ASC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String doctorName = cursor.getString(0);
            String time = cursor.getString(1);
            cursor.close();
            return doctorName + " " + time;
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

    public SQLiteDatabase getProjectDB() { return db; }
}
