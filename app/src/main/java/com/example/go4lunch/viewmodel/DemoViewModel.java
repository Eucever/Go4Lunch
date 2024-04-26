package com.example.go4lunch.viewmodel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.location.Location;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.repository.LocationRepository;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.WorkmateRepository;

import java.util.ArrayList;

import retrofit2.Call;

public class DemoViewModel extends ViewModel {

    private LocationRepository mLocatRepo;

    private LunchRepository mLunchRepo;

    private RestaurantRepository mRestoRepo;

    private WorkmateRepository mWmateRepo;

    private final MediatorLiveData<GPSStatus> gpsStatusLiveData = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> hasGPSpermissionLiveData = new MutableLiveData<>();




    public DemoViewModel(LocationRepository locationRepository, LunchRepository lunchRepository, RestaurantRepository restaurantRepository, WorkmateRepository workmateRepository){
        mLocatRepo = locationRepository;
        LiveData<Location> locationLiveData = mLocatRepo.getLocationLiveData();
        gpsStatusLiveData.addSource(locationLiveData, location -> setStatus(location, hasGPSpermissionLiveData.getValue()));
        gpsStatusLiveData.addSource(hasGPSpermissionLiveData, hasGPSpermission -> setStatus(locationLiveData.getValue(), hasGPSpermission));
        mLunchRepo = lunchRepository;
        mRestoRepo = restaurantRepository;
        mWmateRepo = workmateRepository;
    }
    public DemoViewModel(LocationRepository locationRepository){
        mLocatRepo = locationRepository;
        LiveData<Location> locationLiveData = mLocatRepo.getLocationLiveData();
        gpsStatusLiveData.addSource(locationLiveData, location -> setStatus(location, hasGPSpermissionLiveData.getValue()));
        gpsStatusLiveData.addSource(hasGPSpermissionLiveData, hasGPSpermission -> setStatus(locationLiveData.getValue(), hasGPSpermission));
    }

    //GPSREPO

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

    //LUNCH REPO

    public void createLunch(Restaurant restaurantChosen, Workmate workmate){
        mLunchRepo.createLunch(restaurantChosen, workmate);
    }

    public void deleteLunch(Restaurant restaurant, String user_id){
        mLunchRepo.deleteLunch(restaurant, user_id);
    }

    public LiveData<Lunch> getTodayLunch(String id_workmate){
        return mLunchRepo.getTodayLunch(id_workmate);
    }


    public LiveData<ArrayList<Workmate>> getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(Restaurant restaurant){
        return mLunchRepo.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurant);
    }

    public LiveData<Boolean> checkIfCurrentWorkmateChoseThisRestaurantForLunch(Restaurant restaurant, String user_id){
        return mLunchRepo.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurant, user_id);
    }



    //RESTAU REPO

    public Call<ListRestaurant> getAllRestaurant(String url, String location, int radius, String type, String key){
        return mRestoRepo.getAllRestaurant(url, location, radius, type, key);
    }

    // WORKMATE REPO

    public LiveData<ArrayList<Workmate>> getAllWorkmates(){
        return mWmateRepo.getAllWorkmates();
    }

    public LiveData<Boolean> checkLikedRestaurant(Restaurant restaurant){
        return mWmateRepo.checkLikedRestaurant(restaurant);
    }

    public void deleteLikedRestaurant(Restaurant restaurant){
        mWmateRepo.deleteLikedRestaurant(restaurant);
    }

    public void addLikedRestaurant(Restaurant restaurant){
        mWmateRepo.addLikedRestaurant(restaurant);
    }

    public Workmate getFirebaseUserAsWorkmate(){
        return mWmateRepo.getFirebaseUserAsWorkmate();
    }



}
