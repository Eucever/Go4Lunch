package com.example.go4lunch.repository;

import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;


public class LocationRepository {

    private static final int LOCATION_REQUEST_INTERVAL_MS = 20_000;

    private static final float SMALLEST_DISPLACEMENT_METER = 50;

    @NonNull
    private final FusedLocationProviderClient fusedClient;

    @NonNull
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>(null);


    private LocationCallback locationCallback;


    public LocationRepository(@NonNull FusedLocationProviderClient fusedClient){
        this.fusedClient = fusedClient;
    }

    public LiveData<Location> getLocationLiveData(){
        return locationMutableLiveData;
    }


    private void armLocationCallback(){
        if(locationCallback == null){

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    locationMutableLiveData.setValue(location);
                }
            };
        }
    }

    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public void startLocatingRequest(){
        armLocationCallback();
        Log.d("LOCATIOREPO", "Start");

        fusedClient.removeLocationUpdates(locationCallback);
        fusedClient.requestLocationUpdates(
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setSmallestDisplacement(SMALLEST_DISPLACEMENT_METER)
                        .setInterval(LOCATION_REQUEST_INTERVAL_MS),
                locationCallback,
                Looper.getMainLooper()

        );
    }

    public void stopLocationCallback(){
        Log.d("LOCATIOREPO", "Stop");
        if (locationCallback != null){
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }




}
