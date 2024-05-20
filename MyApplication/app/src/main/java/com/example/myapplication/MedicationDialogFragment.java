package com.example.myapplication;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MedicationDialogFragment extends DialogFragment {

    private String selectedDay = "day";
    private TextView selectedDayInCalendar;
    private EditText medicationNameEditText;
    private EditText medicationTimeEditText;
    private EditText medicationRepeatEditText;
    private EditText medicationDurationEditText;
    private Button medicationSaveButton;

    private int selectedHour, selectedMinute;
    private boolean[] selectedDays = new boolean[7];
    private String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private List<Integer> selectedDayIndices = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.medication_popup_layout, container, false);

        selectedDayInCalendar = view.findViewById(R.id.selectedDay);
        medicationNameEditText = view.findViewById(R.id.medicationNameEditText);
        medicationTimeEditText = view.findViewById(R.id.medicationTimeEditText);
        medicationRepeatEditText = view.findViewById(R.id.medicationRepeatEditText);
        medicationDurationEditText = view.findViewById(R.id.medicationDurationEditText);
        medicationSaveButton = view.findViewById(R.id.saveButton);

        selectedDay = getArguments().getString("date");
        selectedDayInCalendar.setText(selectedDay);

        medicationTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        medicationRepeatEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeekdayPickerDialog();
            }
        });

        medicationSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicationReminder();
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
                medicationTimeEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void showWeekdayPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Days of the Week")
                .setMultiChoiceItems(daysOfWeek, selectedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            selectedDayIndices.add(indexSelected);
                        } else if (selectedDayIndices.contains(indexSelected)) {
                            selectedDayIndices.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StringBuilder selectedDaysStr = new StringBuilder();
                        for (int i = 0; i < selectedDays.length; i++) {
                            if (selectedDays[i]) {
                                if (!TextUtils.isEmpty(selectedDaysStr)) {
                                    selectedDaysStr.append(", ");
                                }
                                selectedDaysStr.append(daysOfWeek[i]);
                            }
                        }
                        medicationRepeatEditText.setText(selectedDaysStr.toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveMedicationReminder() {
        String name = medicationNameEditText.getText().toString();
        String time = medicationTimeEditText.getText().toString();
        String repeat = medicationRepeatEditText.getText().toString();
        String duration = medicationDurationEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(time) || TextUtils.isEmpty(repeat) || TextUtils.isEmpty(duration)) {
            Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        dismiss();
    }
}
