package com.example.myapplication;

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

public class AppointmentDialogFragment extends DialogFragment {

    public interface AppointmentDialogListener {
        void onAppointmentSaved(String name, String specialization, String time);
    }

    public AppointmentDialogListener listener;
    private String selectedDay;
    private TextView selectedDayTextView;
    private EditText appointmentDoctorNameEditText;
    private EditText appointmentSpecializationEditText;
    private EditText appointmentTimeEditText;
    private Button appointmentSaveButton;

    private int selectedHour, selectedMinute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appointment_popup_layout, container, false);

        selectedDayTextView = view.findViewById(R.id.selectedDay);
        appointmentDoctorNameEditText = view.findViewById(R.id.appointmentDoctorNameEditText);
        appointmentSpecializationEditText = view.findViewById(R.id.appointmentSpecializationNameEditText);
        appointmentTimeEditText = view.findViewById(R.id.appointmentTimePicker);
        appointmentSaveButton = view.findViewById(R.id.saveButton);

        selectedDay = getArguments().getString("date");
        selectedDayTextView.setText(selectedDay);

        appointmentTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        appointmentSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentReminder();
            }
        });

        return view;
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                appointmentTimeEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveAppointmentReminder() {
        String doctorName = appointmentDoctorNameEditText.getText().toString();
        String specialization = appointmentSpecializationEditText.getText().toString();
        String time = appointmentTimeEditText.getText().toString();

        if (TextUtils.isEmpty(doctorName) || TextUtils.isEmpty(specialization) || TextUtils.isEmpty(time)) {
            Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) {
            listener.onAppointmentSaved(doctorName, specialization, time);
        }
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Attach the listener
            listener = (AppointmentDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement AppointmentDialogListener");
        }
    }
}