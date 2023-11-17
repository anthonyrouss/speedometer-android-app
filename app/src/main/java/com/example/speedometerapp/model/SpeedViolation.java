package com.example.speedometerapp.model;

import java.sql.Timestamp;

public class SpeedViolation {
    private double longitude;
    private double latitude;
    private double speed;
    private double speedLimit;
    private Timestamp timestamp;

    public SpeedViolation(double longitude, double latitude, double speed, double speedLimit, Timestamp timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.speedLimit = speedLimit;
        this.timestamp = timestamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Long: %f,\tLat: %f\nSpeed: %.2f,\tSpeed Limit: %.2f\nTimestamp: %s\n", longitude, latitude, speed, speedLimit, timestamp);
    }
}
