package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class RemindersPage extends Fragment implements MedicationDialogFragment.MedicationDialogListener, AppointmentDialogFragment.AppointmentDialogListener {

    private ImageButton addMedicationButton;
    private ImageButton addAppointmentButton;
    private String selectedDate;
    private CalendarView calendarView;
    private boolean isButtonsVisible = false;
    private Set<String> savedDates = new HashSet<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminders_page, container, false);

        addMedicationButton = view.findViewById(R.id.addMedicationButton);
        addAppointmentButton = view.findViewById(R.id.addAppointmentButton);

        calendarView = view.findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Month is 0-indexed, so add 1
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String formattedMonth = String.format("%02d", currentMonth + 1);
        selectedDate = currentDayOfMonth + "-" + formattedMonth + "-" + currentYear;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Month is 0-indexed, so add 1 to it
                String formattedMonth = String.format("%02d", month + 1);
                selectedDate = dayOfMonth + "-" + formattedMonth + "-" + year;
            }
        });

        ImageButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonsVisibility();
            }
        });

        addMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedicationDialog();
            }
        });

        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppointmentDialog();
            }
        });

        return view;
    }

    private void toggleButtonsVisibility() {
        if (isButtonsVisible) {
            hideButtons();
        } else {
            showButtons();
        }
        isButtonsVisible = !isButtonsVisible;
    }

    private void showButtons() {
        TranslateAnimation translateMedication = new TranslateAnimation(-1000f, 0f, 0f, 0f);
        translateMedication.setDuration(500);
        addMedicationButton.setVisibility(View.VISIBLE);
        addMedicationButton.startAnimation(translateMedication);

        TranslateAnimation translateAppointment = new TranslateAnimation(1000f, 0f, 0f, 0f);
        translateAppointment.setDuration(500);
        addAppointmentButton.setVisibility(View.VISIBLE);
        addAppointmentButton.startAnimation(translateAppointment);
    }

    private void hideButtons() {
        TranslateAnimation translateMedication = new TranslateAnimation(0f, -1000f, 0f, 0f);
        translateMedication.setDuration(500);
        addMedicationButton.startAnimation(translateMedication);
        addMedicationButton.setVisibility(View.INVISIBLE);

        TranslateAnimation translateAppointment = new TranslateAnimation(0f, 1000f, 0f, 0f);
        translateAppointment.setDuration(500);
        addAppointmentButton.startAnimation(translateAppointment);
        addAppointmentButton.setVisibility(View.INVISIBLE);
    }

    private void showMedicationDialog() {
        MedicationDialogFragment dialogFragment = new MedicationDialogFragment();
        Bundle args = new Bundle();
        args.putString("date", selectedDate);
        dialogFragment.setArguments(args);
        dialogFragment.setTargetFragment(this, 0); // Set this fragment as the target
        dialogFragment.show(getParentFragmentManager(), "MedicationDialogFragment"); // Use getParentFragmentManager() instead of getChildFragmentManager()
    }
    private void showAppointmentDialog() {
        AppointmentDialogFragment dialogFragment = new AppointmentDialogFragment();
        Bundle args = new Bundle();
        args.putString("date", selectedDate);
        dialogFragment.setArguments(args);
        dialogFragment.setTargetFragment(this, 0); // Set this fragment as the target
        dialogFragment.show(getParentFragmentManager(), "AppointmentDialogFragment");
    }
    private void markSavedDates(CalendarView calendarView) {
        for (String date : savedDates) {
            String[] dateParts = date.split("-");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-indexed
            int year = Integer.parseInt(dateParts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            long milliTime = calendar.getTimeInMillis();

            calendarView.setDate(milliTime, true, false);
        }
    }
    @Override
    public void onMedicationSaved(String name, String time, String repeat, String duration) {
        SQLiteDatabase db = ((HomeMainActivity)requireActivity()).getProjectDB();
        String username = ((HomeMainActivity)requireActivity()).username;
        // SQL insert statement
        String sql = "INSERT INTO reminders (name, medicationName, time, day) VALUES (?, ?, ?, ?)";

        // Execute the SQL statement
        db.execSQL(sql, new Object[]{username, name, time, selectedDate});

        Toast.makeText(getContext(), "Medication Saved!", Toast.LENGTH_LONG).show();
        savedDates.add(selectedDate);
        markSavedDates(calendarView);
    }
    @Override
    public void onAppointmentSaved(String name, String specialization, String time) {
        SQLiteDatabase db = ((HomeMainActivity)requireActivity()).getProjectDB();
        String username = ((HomeMainActivity)requireActivity()).username;
        // SQL insert statement
        String sql = "INSERT INTO appointments (name, doctorName, specialization, time, day) VALUES (?, ?, ?, ?, ?)";

        // Execute the SQL statement
        db.execSQL(sql, new Object[]{username, name, specialization, time, selectedDate});

        Toast.makeText(getContext(), "Appointment Saved!", Toast.LENGTH_LONG).show();
        savedDates.add(selectedDate);
        markSavedDates(calendarView);
    }
}
