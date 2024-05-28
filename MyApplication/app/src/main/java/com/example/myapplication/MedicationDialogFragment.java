package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

    public interface MedicationDialogListener {
        void onMedicationSaved(String name, String time, String repeat, String duration);
    }

    private MedicationDialogListener listener;

    private String selectedDay = "day";
    private TextView selectedDayInCalendar;
    private EditText medicationNameEditText;
    private EditText medicationTimeEditText;
    private EditText medicationRepeatEditText;
    private EditText medicationDurationEditText;
    private Button medicationSaveButton;
    SQLiteDatabase db;

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

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(time)) {
            Toast.makeText(requireContext(), "Please enter name and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the listener to send the data back to the parent fragment
        if (listener != null) {
            listener.onMedicationSaved(name, time, repeat, duration);
            scheduleAlarm(name, time, repeat, duration);
        }
        dismiss();
    }
    private void scheduleAlarm(String name, String time, String repeat, String duration) {
        // Parse time string to get hour and minute
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Parse selected day string to get day, month, and year
        String[] dayParts = selectedDay.split("-");
        int year = Integer.parseInt(dayParts[2]);
        int month = Integer.parseInt(dayParts[1]) - 1; // Month is 0-based in Calendar class
        int day = Integer.parseInt(dayParts[0]);

        // Get instance of Calendar and set the alarm time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0); // Set seconds to zero for precise timing

        // Generate a unique requestCode
        int requestCode = generateRequestCode(name, time);

        // Schedule alarm using AlarmManager
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("name", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE); // Specify FLAG_IMMUTABLE
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(requireContext(), "Medication reminder scheduled", Toast.LENGTH_SHORT).show();
    }
    private int generateRequestCode(String name, String time) {
        // Combine medication name and time to generate a unique integer code
        String uniqueString = name + time;
        return uniqueString.hashCode();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Attach the listener
            listener = (MedicationDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement MedicationDialogListener");
        }
    }
}
