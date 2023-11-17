package com.example.speedometerapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.speedometerapp.R;
import com.example.speedometerapp.controller.DatabaseController;
import com.example.speedometerapp.model.MyTextToSpeech;
import com.example.speedometerapp.model.SpeedViolation;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String SPEED_LIMIT_KEY = "speed_limit";

    private MyTextToSpeech myTextToSpeech;
    private SharedPreferences sharedPreferences;
    private DatabaseController databaseController;
    private LocationManager locationManager;
    private ConstraintLayout constraintLayout;
    private Drawable defaultActivityBackground;
    private boolean speedometerRunning, limitExceeded, hasSpoken = false;
    private float speedLimit;
    private Location currentLocation;
    private Button startBtn, updateSpeedLimitBtn, historyBtn;

    private TextView speedLabel, speedLimitValueLabel;
    private EditText speedLimitField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.main_activity);
        defaultActivityBackground = constraintLayout.getBackground();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speedLimit = sharedPreferences.getFloat(SPEED_LIMIT_KEY, 0f);
        databaseController = new DatabaseController(this);

        startBtn = findViewById(R.id.startBtn);
        updateSpeedLimitBtn = findViewById(R.id.updateSpeedLimitBtn);
        historyBtn = findViewById(R.id.historyBtn);

        speedLabel = findViewById(R.id.speedLabel);
        speedLimitValueLabel = findViewById(R.id.speedLimitValueLabel);

        speedLimitField = findViewById(R.id.speedLimitField);

        // Check for shared preferences
        speedLimitValueLabel.setText(String.valueOf(speedLimit));

        myTextToSpeech = new MyTextToSpeech(this);

        startBtn.setOnClickListener(view -> startSpeedometer());
        updateSpeedLimitBtn.setOnClickListener(view -> updateSpeedLimit());
        historyBtn.setOnClickListener(view -> showViolations());

        updateSpeedometerButton();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the request was granted or denied
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted on req permission result
                startSpeedometer();
            } else {
                // Permission not granted on req permission result
                Toast.makeText(this, R.string.location_permission_result_not_granted, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
        speedLabel.setText(String.format("%.2f", location.getSpeed()));

        // Check for violation
        checkForSpeedViolation();
    }

    private void checkForSpeedViolation() {
        if (currentLocation.getSpeed() > speedLimit) {
            speedLimitExceeded();
        } else if (limitExceeded) {
            // If the speed is not over the limit and the limit was exceeded, clear the warning
            clearWarning();
        }
    }
    private void startSpeedometer() {
        if (!speedometerRunning) {
            // Start the speedometer
            // Check if the location permission is given
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // If not, request for it and return
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
            // The location permission is granted. Start requesting for location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, this);

        } else {
            // Stop the speedometer. Remove location updates
            stopSpeedometer();
        }
        // Change the state of the speedometer
        speedometerRunning = !speedometerRunning;
        updateSpeedometerButton();

    }

    private void stopSpeedometer() {
        // Set speed label back to default
        speedLabel.setText(R.string.init_speed);
        // Remove location updates
        locationManager.removeUpdates(this);
        clearWarning();
        // System.out.println("Speedometer stopped successfully");
    }

    private void updateSpeedometerButton() {
        if (speedometerRunning) {
            startBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_red_light));
            startBtn.setText(R.string.speedometer_stop_btn);
        } else {
            startBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_green_light));
            startBtn.setText(R.string.speedometer_start_btn);
        }
    }

    private void updateSpeedLimit() {
        try {
            float newSpeedLimit = Float.parseFloat(speedLimitField.getText().toString());
            setSpeedLimit(newSpeedLimit);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, R.string.enter_valid_number, Toast.LENGTH_SHORT).show();
        }

    }

    private void setSpeedLimit(float speedLimit) {

        // Save to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(SPEED_LIMIT_KEY, speedLimit);
        editor.commit();
        Toast.makeText(this, R.string.speed_limit_saved, Toast.LENGTH_SHORT).show();

        this.speedLimit = speedLimit;
        speedLimitValueLabel.setText(String.valueOf(speedLimit));
    }

    private void speedLimitExceeded() {
        if(!limitExceeded) {
            limitExceeded = true;
            // Insert the violation to the database
            databaseController.insertViolation(new SpeedViolation(currentLocation.getLongitude(), currentLocation.getLatitude(), currentLocation.getSpeed(), this.speedLimit, new Timestamp(System.currentTimeMillis())));
        }
        showWarning();
    }

    private void showWarning() {
        if (!hasSpoken && myTextToSpeech != null) {
            myTextToSpeech.speak(getResources().getString(R.string.warning_message_long));
            Toast.makeText(this, R.string.warning_message_short, Toast.LENGTH_LONG).show();
            hasSpoken = true;
        }
        constraintLayout.setBackgroundResource(R.color.danger);
    }

    private void clearWarning() {
        limitExceeded = false;
        hasSpoken = false;
        constraintLayout.setBackground(defaultActivityBackground);
    }

    private void showViolations() {
        Intent intent = new Intent(this, ListViolationsActivity.class);
        startActivity(intent);
    }

    // TODO: Implement onPause() etc

}