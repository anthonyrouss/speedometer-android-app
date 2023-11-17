package com.example.speedometerapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.speedometerapp.R;
import com.example.speedometerapp.controller.DatabaseController;
import com.example.speedometerapp.enums.SelectableSearchOption;
import com.example.speedometerapp.model.SpeedViolation;

import java.util.ArrayList;
import java.util.List;

public class ListViolationsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ListView violationList;
    private Spinner spinner;
    private DatabaseController databaseController;
    private ArrayAdapter<SpeedViolation> violationItemsAdapter;
    private SelectableSearchOption selectedSearchOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_violations);

        databaseController = new DatabaseController(this);

        // Init the searchOptions list
        List<SelectableSearchOption> searchOptions = new ArrayList<>();
        for (SelectableSearchOption option : SelectableSearchOption.values()) {
            // Fetch the strings from the xml based on the options resource IDs
            option.setName(getResources().getString(option.getResourceId()));
            searchOptions.add(option);
        }

        // Setup the spinner
        ArrayAdapter<SelectableSearchOption> selectableItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, searchOptions);
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(selectableItemsAdapter);
        selectedSearchOption = (SelectableSearchOption) spinner.getSelectedItem();
        spinner.setOnItemSelectedListener(this);

        // Setup the list
        violationItemsAdapter = initViolationItemsAdapter();
        violationList = findViewById(R.id.violationList);
        violationList.setAdapter(violationItemsAdapter);

        // Set click listener to show point on map on user's click
        violationList.setOnItemClickListener(((adapterView, view, i, l) -> showPointOnMap((SpeedViolation) adapterView.getItemAtPosition(i))));

    }

    private ArrayAdapter<SpeedViolation> initViolationItemsAdapter() {
        // Fetch the speed violations
        List<SpeedViolation> items = databaseController.selectViolations(selectedSearchOption);
        return new ArrayAdapter<SpeedViolation>(this, android.R.layout.simple_list_item_1, items);
    }

    private void updateList() {
        List<SpeedViolation> items = databaseController.selectViolations(selectedSearchOption);
        violationItemsAdapter.clear();
        violationItemsAdapter.addAll(items);
    }

    private void showPointOnMap(SpeedViolation speedViolation) {
        // geo:latitude,longitude?z=zoom
        String uri = String.format("google.streetview:cbll=%f,%f?z=22", speedViolation.getLatitude(), speedViolation.getLongitude());
        Uri gmmIntentUri = Uri.parse(uri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedSearchOption = (SelectableSearchOption) adapterView.getSelectedItem();
        updateList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        System.out.println("Nothing selected");
    }
}