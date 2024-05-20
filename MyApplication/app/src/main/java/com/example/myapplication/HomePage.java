package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class HomePage extends Fragment {

    private TextView welcomeTextView;
    private TextView nextMedicationTextView;
    private TextView nextAppointmentTextView;
    private TextView bloodPressureTextView;
    private TextView heartRateTextView;

    public HomePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page, container, false);
        welcomeTextView = view.findViewById(R.id.homeText);
        nextMedicationTextView = view.findViewById(R.id.nextMedicationText);
        nextAppointmentTextView = view.findViewById(R.id.nextAppointmentText);
        bloodPressureTextView = view.findViewById(R.id.bloodPressureText);
        heartRateTextView = view.findViewById(R.id.heartRateText);

        // Assume you have methods to get heart rate and blood pressure data
        // For example:
        int heartRate = getHeartRateData(); // Replace with your method to get heart rate
        int bloodPressure = getBloodPressureData(); // Replace with your method to get blood pressure

        // Set the text for heart rate and blood pressure TextViews
        heartRateTextView.setText("Heart Rate: " + heartRate + " bpm");
        bloodPressureTextView.setText("Blood Pressure: " + bloodPressure + " mmHg");

        // Retrieve other data from arguments
        if (getArguments() != null) {
            String username = getArguments().getString("username");
            String nextMedication = getArguments().getString("nextMedication");
            String nextAppointment = getArguments().getString("nextAppointment");

            if (username != null) {
                welcomeTextView.setText("Welcome to Medimate, " + username);
            }

            if (nextMedication != null) {
                nextMedicationTextView.setText(nextMedication);
            } else {
                nextMedicationTextView.setText("Zzz, looks like nothing is scheduled");
            }

            if (nextAppointment != null) {
                nextAppointmentTextView.setText(nextAppointment);
            } else {
                nextAppointmentTextView.setText("Zzz, looks like nothing is scheduled");
            }
        }

        return view;
    }

    // Example method to get heart rate data (replace with your implementation)
    private int getHeartRateData() {
        // Replace this with your logic to fetch heart rate from smartwatch
        return 75; // Example heart rate value
    }

    // Example method to get blood pressure data (replace with your implementation)
    private int getBloodPressureData() {
        // Replace this with your logic to fetch blood pressure from smartwatch
        return 120; // Example systolic blood pressure value
    }
}
