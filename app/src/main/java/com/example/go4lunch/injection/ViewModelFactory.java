package com.example.go4lunch.injection;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.repository.LocationRepository;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.WorkmateRepository;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.gms.location.LocationServices;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final LunchRepository mLunchRepo;

    private final RestaurantRepository mRestoRepo;

    private final WorkmateRepository mWmateRepo;

    public ViewModelFactory(LunchRepository lunchRepository, RestaurantRepository restaurantRepository, WorkmateRepository workmateRepository){
        mLunchRepo = lunchRepository;
        mRestoRepo = restaurantRepository;
        mWmateRepo = workmateRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if(modelClass.isAssignableFrom(DemoViewModel.class)) {
            Application application = MainApplication.getApplication();
            Log.d("ViewModelFactory", "create: " + application);
            LocationRepository loc = new LocationRepository(
                    LocationServices.getFusedLocationProviderClient(
                            application
                    )
            );

            return (T) new DemoViewModel(loc, mLunchRepo, mRestoRepo, mWmateRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
