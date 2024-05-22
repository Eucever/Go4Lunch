package com.example.go4lunch.viewmodel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainApplication;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.place.Result;
import com.example.go4lunch.repository.LocationRepository;
import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.WorkmateRepository;
import com.example.go4lunch.ui.RestaurantItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public LiveData<ArrayList<RestaurantItem>> getAllRestaurantsWithLunch(){
        return mLunchRepo.getAllRestaurantsWithLunch();
    }



    //RESTAU REPO

    public Call<ListRestaurant> getAllRestaurant(String url, String location, int radius, String type, String key){
        return mRestoRepo.getAllRestaurant(url, location, radius, type, key);
    }
    public double getDistanceFromPosition(LatLng posGPS, com.example.go4lunch.place.Location location){
        return SphericalUtil.computeDistanceBetween(posGPS, new LatLng(location.getLat(), location.getLng()));
    }

    public Restaurant resultToRestaurant(Result result){
        if (result != null && result.getOpeningHours() != null){
            return new Restaurant(result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    result.getOpeningHours().getOpenNow(),
                    result.getRating(),
                    result.getIcon(),
                    result.getTypes(),
                    result.getGeometry().getLocation());

        }if (result != null && result.getOpeningHours() == null) {
            return new Restaurant(result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    false,
                    result.getRating(),
                    result.getIcon(),
                    result.getTypes(),
                    result.getGeometry().getLocation());

        }else {
            return null;
        }

    }
    public LiveData<List<RestaurantItem>> getAllRestaurantsItemList(double latitude, double longitude){
        MutableLiveData<List<RestaurantItem>> liveDataRestaurantsItem = new MutableLiveData<>();

        Call<ListRestaurant> listRestaurantCall = getAllRestaurant(
                "https://maps.googleapis.com/maps/api/place/",
                latitude+","+longitude,
                1000,
                "restaurant",
                BuildConfig.google_maps_api);

        Log.i("Call", listRestaurantCall.request().toString());
        listRestaurantCall.enqueue(new Callback<ListRestaurant>() {
            @Override
            public void onResponse(@NonNull Call<ListRestaurant> call, @NonNull
            Response<ListRestaurant> response) {

                if (response.isSuccessful()) {
                    Log.i("response", "found");
                    ListRestaurant listReponse = response.body();
                    List<RestaurantItem> restaurantsItemList = new ArrayList<>();
                    for (int i = 0; i < listReponse.getResults().size(); i++) {
                        Log.i("Response in Fragment ", listReponse.getResults().get(i).getVicinity());
                        restaurantsItemList.add(resultToRestaurantItem(listReponse.getResults().get(i), new LatLng(latitude,longitude)));

                    }
                    Collections.sort(restaurantsItemList);
                    liveDataRestaurantsItem.postValue(restaurantsItemList);

                } else Log.i("Response", "Code " + response.code());
            }


            @Override
            public void onFailure(Call<ListRestaurant> call, Throwable t) {
                Log.i("error", "Call Failed");
            }


        });
        return liveDataRestaurantsItem;
    }

    public RestaurantItem resultToRestaurantItem(Result result, LatLng posGps){
        if (result != null && result.getOpeningHours() != null){
            return new RestaurantItem(result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    result.getOpeningHours().getOpenNow(),
                    result.getRating(),
                    result.getIcon(),
                    result.getTypes(),
                    getDistanceFromPosition(posGps, result.getGeometry().getLocation()));

        }if (result != null && result.getOpeningHours() == null) {
            return new RestaurantItem(result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    false,
                    result.getRating(),
                    result.getIcon(),
                    result.getTypes(),
                    getDistanceFromPosition(posGps, result.getGeometry().getLocation()));

        }else {
            return null;
        }

    }

    public LiveData<List<Restaurant>> getAllRestaurantsList(double latitude, double longitude){
        MutableLiveData<List<Restaurant>> liveDataRestaurants = new MutableLiveData<>();

        Call<ListRestaurant> listRestaurantCall = getAllRestaurant(
                "https://maps.googleapis.com/maps/api/place/",
                latitude+","+longitude,
                1000,
                "restaurant",
                BuildConfig.google_maps_api);

        Log.i("Call", listRestaurantCall.request().toString());
        listRestaurantCall.enqueue(new Callback<ListRestaurant>() {
            @Override
            public void onResponse(@NonNull Call<ListRestaurant> call, @NonNull
            Response<ListRestaurant> response) {

                if (response.isSuccessful()) {
                    Log.i("response", "found");
                    ListRestaurant listReponse = response.body();
                    List<Restaurant> restaurantsList = new ArrayList<>();
                    for (int i = 0; i < listReponse.getResults().size(); i++) {
                        Log.i("Response in Fragment ", listReponse.getResults().get(i).getVicinity());
                        restaurantsList.add(resultToRestaurant(listReponse.getResults().get(i)));

                    }
                    liveDataRestaurants.postValue(restaurantsList);

                } else Log.i("Response", "Code " + response.code());
            }


            @Override
            public void onFailure(Call<ListRestaurant> call, Throwable t) {
                Log.i("error", "Call Failed");
            }


        });
        return liveDataRestaurants;
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

    public LiveData<Boolean> getNotificationActive(){
        return mWmateRepo.getNotificationActive();
    }

    public void createOrUpdateWorkmate(Boolean isNotificationActive){
        mWmateRepo.createOrUpdateWorkmate(isNotificationActive);
    }





}
