package com.example.myapplication;

import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class ChangePasswordDialogFragment extends DialogFragment {

    public interface ChangePasswordDialogListener {
        void onPasswordChanged(String username, String newPassword);
    }

    public ChangePasswordDialogListener listener;
    private EditText oldPassword;
    private EditText oldPasswordConfirm;
    private EditText newPassword;
    private Button saveNewPasswordButton;
    private String password;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.password_change_popout, container, false);

        // Retrieve password from arguments
        Bundle args = getArguments();
        if (args != null) {
            password = args.getString("password");
            username = args.getString("username");
        }

        // Initialize views
        oldPassword = view.findViewById(R.id.oldPassword);
        oldPasswordConfirm = view.findViewById(R.id.oldPasswordConfirm);
        newPassword = view.findViewById(R.id.newPassword);
        saveNewPasswordButton = view.findViewById(R.id.saveButton);

        saveNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewPassword();
            }
        });

        return view;
    }

    private void setNewPassword() {
        String oldPasswordText = oldPassword.getText().toString();
        String oldPasswordConfirmText = oldPasswordConfirm.getText().toString();
        String newPasswordText = newPassword.getText().toString();


        if (!oldPasswordText.equals(password)) {
            Toast.makeText(getContext(), "Incorrect old password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!oldPasswordConfirmText.equals(oldPasswordText)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPasswordText)) {
            Toast.makeText(getContext(), "Please enter a new password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPasswordText.length() < 8) {
            Toast.makeText(requireContext(), "New password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) {
            listener.onPasswordChanged(username, newPasswordText);
            Toast.makeText(requireContext(),"Password changed successfully", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChangePasswordDialogListener) {
            listener = (ChangePasswordDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ChangePasswordDialogListener");
        }
    }
}
