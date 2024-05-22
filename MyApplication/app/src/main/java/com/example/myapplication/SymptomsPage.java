package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.List;

public class SymptomsPage extends Fragment {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private List<DiseaseItem> dataList;
    private RecyclerViewAdapter adapter;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    public SymptomsPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.symptoms_page, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerView);
        constraintLayout = view.findViewById(R.id.constraintLayout);

        // Initialize data list with sample data
        dataList = new ArrayList<>();
        dataList.add(new DiseaseItem("Influenza", "A viral infection that attacks your respiratory system.", "Fever, cough, sore throat"));
        dataList.add(new DiseaseItem("Diabetes", "A group of diseases that result in too much sugar in the blood.", "Increased thirst, frequent urination"));
        dataList.add(new DiseaseItem("Hypertension", "A condition in which the force of the blood against the artery walls is too high.", "Often none, but can include headaches, shortness of breath"));

        adapter = new RecyclerViewAdapter(getContext(), dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        // Add a TextWatcher to update the list as the user types with debouncing
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        performSearch();
                    }
                };
                searchHandler.postDelayed(searchRunnable, 300); // 300ms delay
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void performSearch() {
        String keyword = searchEditText.getText().toString().trim().toLowerCase();
        List<DiseaseItem> filteredList = new ArrayList<>();

        for (DiseaseItem item : dataList) {
            if (item.getCommonSymptoms().toLowerCase().contains(keyword)) {
                filteredList.add(item);
            }
        }

        adapter.updateData(filteredList);

        // Show RecyclerView and update constraints
        TransitionManager.beginDelayedTransition(constraintLayout);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.searchEditText, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.applyTo(constraintLayout);
    }
}
