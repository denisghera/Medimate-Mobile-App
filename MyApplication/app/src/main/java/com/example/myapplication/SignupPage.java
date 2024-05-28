package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.SignupPageBinding;

public class SignupPage extends Fragment {

    private SignupPageBinding binding;
    SQLiteDatabase db;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = SignupPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = ((MainActivity) requireActivity()).getProjectDB();

        // Find views
        EditText passwordEditText = view.findViewById(R.id.edittext_password);
        ImageButton togglePasswordButton = view.findViewById(R.id.button_toggle_password_visibility);
        EditText confirmPasswordEditText = view.findViewById(R.id.edittext_password_check);
        ImageButton toggleConfirmPasswordButton = view.findViewById(R.id.button_toggle_password_check_visibility);

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

        toggleConfirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmPasswordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    toggleConfirmPasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    toggleConfirmPasswordButton.setImageResource(R.drawable.baseline_visibility_24);
                }

                confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
            }
        });

        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void signUp() {
        String name = binding.edittextName.getText().toString().trim();
        String email = binding.edittextEmail.getText().toString().trim();
        String password = binding.edittextPassword.getText().toString();
        String confirmPassword = binding.edittextPasswordCheck.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(requireContext(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "Signup successful", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(SignupPage.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);

        // Parameterized query to insert data
        String insertQuery = "INSERT INTO credentials (name, email, password) VALUES (?, ?, ?)";

        // Execute the insert query with parameters
        db.execSQL(insertQuery, new Object[]{name, email, password});
    }
}
