package com.example.go4lunch.model.location;

import androidx.annotation.NonNull;

public class GPSStatus {


    private final Double longitude;

    private final Double latitude;

    private final Boolean hasGpsPermission;


    private final Boolean querying;

    public GPSStatus(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.hasGpsPermission = true;
        this.querying = false;
    }

    public GPSStatus(Boolean hasGpsPermission, Boolean querying) {
        this.hasGpsPermission = hasGpsPermission;
        this.querying = querying;
        this.longitude = null;
        this.latitude = null;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    @NonNull
    public Boolean getHasGpsPermission() {
        return hasGpsPermission;
    }

    @NonNull
    public Boolean getQuerying() {
        return querying;
    }

    @NonNull
    @Override
    public String toString() {
        return "GPSStatus{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", hasGpsPermission=" + hasGpsPermission +
                ", querying=" + querying +
                '}';
    }
}