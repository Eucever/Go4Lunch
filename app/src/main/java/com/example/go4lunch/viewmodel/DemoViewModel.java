package com.example.go4lunch.viewmodel;

import android.location.Location;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.repository.LocationRepository;

public class DemoViewModel extends ViewModel {

    private final LocationRepository mLocatRepo;

    private final MediatorLiveData<GPSStatus> gpsStatusLiveData = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> hasGPSpermissionLiveData = new MutableLiveData<>();




    public DemoViewModel(LocationRepository locationRepository){
        mLocatRepo = locationRepository;
        LiveData<Location> locationLiveData = mLocatRepo.getLocationLiveData();
        gpsStatusLiveData.addSource(locationLiveData, location -> setStatus(location, hasGPSpermissionLiveData.getValue()));
        gpsStatusLiveData.addSource(hasGPSpermissionLiveData, hasGPSpermission -> setStatus(locationLiveData.getValue(), hasGPSpermission));
    }

    private void setStatus(Location location, Boolean hasGPSpermission) {
        if(location== null){
            if(hasGPSpermission==null || !hasGPSpermission){
                gpsStatusLiveData.setValue(new GPSStatus(false,false));

            }else {
                gpsStatusLiveData.setValue(new GPSStatus(true,true));
            }
        }else {
            gpsStatusLiveData.setValue(new GPSStatus(location.getLongitude(),location.getLatitude()));
        }
    }

    public LiveData<GPSStatus> getGpsLivedata(){
        return gpsStatusLiveData;
    }

    public void refresh(){
        boolean hasGpsPermission = ContextCompat.checkSelfPermission(
                MainApplication.getApplication(), ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED;
        hasGPSpermissionLiveData.setValue(hasGpsPermission);
        if(hasGpsPermission){
            mLocatRepo.startLocatingRequest();
        }else {
            mLocatRepo.stopLocationCallback();
        }
    }



}
