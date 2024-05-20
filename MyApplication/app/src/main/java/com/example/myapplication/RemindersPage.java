package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RemindersPage extends Fragment {

    private ImageButton addMedicationButton;
    private ImageButton addAppointmentButton;
    private String selectedDate;
    private boolean isButtonsVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminders_page, container, false);

        addMedicationButton = view.findViewById(R.id.addMedicationButton);
        addAppointmentButton = view.findViewById(R.id.addAppointmentButton);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Month is 0-indexed, so add 1 to it
                selectedDate = dayOfMonth + " - " + (month + 1) + " - " + year;
            }
        });

        ImageButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonsVisibility();
            }
        });

        // Set click listeners for medication and appointment buttons
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
        // Slide animation to move buttons from off-screen to their positions
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
        // Slide animation to move buttons from their positions to off-screen
        TranslateAnimation translateMedication = new TranslateAnimation(0f, -1000f, 0f, 0f);
        translateMedication.setDuration(500);
        addMedicationButton.startAnimation(translateMedication);
        addMedicationButton.setVisibility(View.INVISIBLE);

        TranslateAnimation translateAppointment = new TranslateAnimation(0f, 1000f, 0f, 0f);
        translateAppointment.setDuration(500);
        addAppointmentButton.startAnimation(translateAppointment);
        addAppointmentButton.setVisibility(View.INVISIBLE);
    }

    // Method to show medication dialog fragment
    private void showMedicationDialog() {
        MedicationDialogFragment dialogFragment = new MedicationDialogFragment();
        Bundle args = new Bundle();
        args.putString("date", selectedDate);
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), "MedicationDialogFragment");
    }

    // Method to show appointment dialog fragment
    private void showAppointmentDialog() {
        AppointmentDialogFragment dialogFragment = new AppointmentDialogFragment();
        Bundle args = new Bundle();
        args.putString("date", selectedDate);
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), "AppointmentDialogFragment");
    }
}

