package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;

public class AccountManagerActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView emailTextView;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_manager_activity);

        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        emailTextView = findViewById(R.id.emailTextView);

        db = openOrCreateDatabase("ProjectDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS credentials(name VARCHAR,email VARCHAR, password VARCHAR);");

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        usernameTextView.setText(username);

        String password = getPasswordFromDatabase(username);
        passwordTextView.setText(password);

        String email = getEmailFromDatabase(username);
        emailTextView.setText(email);
    }
    @SuppressLint("Range")
    private String getPasswordFromDatabase(String username) {
        String password = null;
        Cursor cursor = db.rawQuery("SELECT password FROM credentials WHERE name=?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex("password"));
            cursor.close();
        }
        return password;
    }
    @SuppressLint("Range")
    private String getEmailFromDatabase(String username) {
        String email = null;
        Cursor cursor = db.rawQuery("SELECT email FROM credentials WHERE name=?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex("email"));
            cursor.close();
        }
        return email;
    }
}
