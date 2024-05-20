package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.LoginPageBinding;

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends Fragment {

    SQLiteDatabase db;
    private LoginPageBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = LoginPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                db = ((MainActivity) requireActivity()).getProjectDB();

                String username = binding.edittextUsername.getText().toString().trim();
                String password = binding.edittextPassword.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, String> credentialsMap = new HashMap<>();

                Cursor cursor = db.rawQuery("SELECT * FROM credentials", null);
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String passwordFromDB = cursor.getString(cursor.getColumnIndex("password"));
                        credentialsMap.put(name, passwordFromDB);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                if (credentialsMap.containsKey(username)) {
                    String passwordFromMap = credentialsMap.get(username);
                    if (password.equals(passwordFromMap)) {
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireContext(), HomeMainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Username not found", Toast.LENGTH_SHORT).show();
                }
            }

        });

        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginPage.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        // Find views
        EditText passwordEditText = view.findViewById(R.id.edittext_password);
        ImageButton togglePasswordButton = view.findViewById(R.id.button_toggle_password_visibility);

        // Set click listener for toggle password button
        togglePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordButton.setImageResource(R.drawable.baseline_visibility_24);
                }

                // Move cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}