package com.example.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.ui.RestaurantItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LunchRepository {

    private final MutableLiveData<Lunch> todayLunch = new MutableLiveData<>();



    private final MutableLiveData<ArrayList<Workmate>> workmatesRestaurant= new MutableLiveData<>();

    private final MutableLiveData<ArrayList<RestaurantItem>> restaurantsWithLunch = new MutableLiveData<>();

    private final MutableLiveData<ArrayList<Lunch>> lunchesLiveData = new MutableLiveData<>();
    private static LunchRepository sLunchRepository;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static LunchRepository getInstance() {
        if (sLunchRepository == null) {
            sLunchRepository = new LunchRepository();
        }
        return sLunchRepository;
    }

    public LunchRepository() {
    }

    private static String toDay() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return date;
    }

    public static CollectionReference getLunchCollection() {
        return db.collection("lunch");
    }

    public void createLunch(Restaurant restaurantChosen, Workmate workmate) {
        Lunch lunchAdd = new Lunch(toDay(), workmate, restaurantChosen);
        getLunchCollection()
                .whereEqualTo("wMate.id", workmate.getId())
                .whereEqualTo("date", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }else {
                        Log.d("Error", "Error getting documents: ", task.getException());

                    }
                    getLunchCollection().add(lunchAdd);
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle error
                        Log.e("ERROR", "Request Failed");
                    }
                });;

    }

    public LiveData<Lunch> getTodayLunch(String id_workmate) {
        getLunchCollection()
                .whereEqualTo("wMate.id", id_workmate)
                .whereEqualTo("date", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Lunch> lunches = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            lunches.add(document.toObject(Lunch.class));
                        }
                        if (lunches.isEmpty() || lunches.size()<0){
                            todayLunch.postValue(null);
                        }else {
                            todayLunch.postValue(lunches.get(0));
                        }

                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                        todayLunch.postValue(null);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle error
                        todayLunch.postValue(null);
                    }
                });
        return todayLunch;
    }
    public LiveData<ArrayList<Workmate>> getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(Restaurant restaurant){

        getLunchCollection()
                .whereEqualTo("restaurant.name", restaurant.getName())
                .whereEqualTo("date", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Workmate> workmates = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            workmates.add(document.toObject(Lunch.class).getwMate());
                        }
                        workmatesRestaurant.postValue(workmates);
                    } else {
                        Log.e("Error", "Error getting documents: ", task.getException());
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle error
                        workmatesRestaurant.postValue(null);
                    }
                });
        return workmatesRestaurant;}

    public LiveData<Boolean> checkIfCurrentWorkmateChoseThisRestaurantForLunch(Restaurant restaurant, String user_id){
        MutableLiveData<Boolean> valueCheck = new MutableLiveData<>();
        getLunchCollection()
                .whereEqualTo("restaurant.name", restaurant.getName())
                .whereEqualTo("wMate.id", user_id)
                .whereEqualTo("date", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().size()>0){
                            valueCheck.postValue(true);
                        }else {
                            valueCheck.postValue(false);
                        }
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                        valueCheck.postValue(false);
                    }

                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle error
                        Log.e("Error on request", "test");
                        valueCheck.postValue(false);
                    }
                });
                return valueCheck;
    }

    public void deleteLunch(Restaurant restaurant, String user_id){
        getLunchCollection()
                .whereEqualTo("restaurant.name", restaurant.getName())
                .whereEqualTo("wMate.id", user_id)
                .whereEqualTo("date", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                });
    }

    public LiveData<ArrayList<RestaurantItem>>  getAllRestaurantsWithLunch(){

        getLunchCollection()
                .whereEqualTo("date", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<RestaurantItem> restaurants = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            restaurants.add(RestaurantItem.restaurantToRestaurantItem(document.toObject(Lunch.class).getRestaurant()));
                        }
                        restaurantsWithLunch.postValue(restaurants);
                    } else {
                        Log.e("Error", "Error getting documents: ", task.getException());
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle error
                        restaurantsWithLunch.postValue(null);
                    }
                });
        return restaurantsWithLunch;}

    public LiveData<ArrayList<Lunch>> getAllLunch(){
        getLunchCollection()
                .whereEqualTo("date", toDay())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Lunch> lunches = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            lunches.add((document.toObject(Lunch.class)));
                        }
                        lunchesLiveData.postValue(lunches);
                    } else {
                        Log.e("Error", "Error getting documents: ", task.getException());
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle error
                        lunchesLiveData.postValue(null);
                    }
                });
        return lunchesLiveData;
    }
}

