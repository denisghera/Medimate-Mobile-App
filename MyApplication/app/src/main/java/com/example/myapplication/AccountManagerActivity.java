package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountManagerActivity extends AppCompatActivity implements ChangePasswordDialogFragment.ChangePasswordDialogListener {
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView userIconImageView;
    private ImageButton changeIconFab;
    private Button backButton;
    private Button changePasswordButton;
    SQLiteDatabase db;
    private String password;

    private final int[] icons = {R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_manager_activity);

        usernameTextView = findViewById(R.id.usernameTextView);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        emailTextView = findViewById(R.id.emailTextView);
        userIconImageView = findViewById(R.id.userIconImageView);
        changeIconFab = findViewById(R.id.changeIconFab);

        db = openOrCreateDatabase("ProjectDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS credentials(name VARCHAR, email VARCHAR, password VARCHAR, icon INTEGER);");

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        usernameTextView.setText(username);

        password = getPasswordFromDatabase(username);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordDialogFragment dialogFragment = new ChangePasswordDialogFragment();
                Bundle args = new Bundle();
                args.putString("password", password);
                args.putString("username", username);
                dialogFragment.setArguments(args);
                dialogFragment.listener = new ChangePasswordDialogFragment.ChangePasswordDialogListener() {
                    @Override
                    public void onPasswordChanged(String username, String newPassword) {
                        db.execSQL("UPDATE credentials SET password=? WHERE name=?", new Object[]{newPassword,username});

                    }
                };
                dialogFragment.show(getSupportFragmentManager(), "ChangePasswordDialogFragment");
            }
        });

        String email = getEmailFromDatabase(username);
        emailTextView.setText(email);

        int icon = getIconFromDatabase(username);
        userIconImageView.setImageResource(icon != 0 ? icon : R.drawable.user1);

        changeIconFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIconSelectorDialog(username);
            }
        });
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountManagerActivity.this, HomeMainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
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

    @SuppressLint("Range")
    private int getIconFromDatabase(String username) {
        int icon = 0;
        Cursor cursor = db.rawQuery("SELECT icon FROM credentials WHERE name=?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            icon = cursor.getInt(cursor.getColumnIndex("icon"));
            cursor.close();
        }
        return icon;
    }

    private void showIconSelectorDialog(final String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Icon");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.icon_selector_dialog, null);
        builder.setView(dialogView);

        GridView gridView = dialogView.findViewById(R.id.iconGridView);
        gridView.setAdapter(new IconAdapter(this, icons));

        AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedIcon = icons[position];
            userIconImageView.setImageResource(selectedIcon);
            saveIconToDatabase(username, selectedIcon);
            dialog.dismiss();
        });

        dialog.show();
    }
    private void saveIconToDatabase(String username, int icon) {
        db.execSQL("UPDATE credentials SET icon=? WHERE name=?", new Object[]{icon, username});
    }
    @Override
    public void onPasswordChanged(String username, String newPassword) {
        db.execSQL("UPDATE credentials SET password=? WHERE name=?", new Object[]{newPassword,username});
        password = newPassword;
    }
}
