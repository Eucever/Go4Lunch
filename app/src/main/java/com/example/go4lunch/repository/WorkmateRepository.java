package com.example.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class WorkmateRepository{

    private final MutableLiveData<ArrayList<Workmate>> workmatesList = new MutableLiveData<>();

    private static WorkmateRepository sWorkmateRepository;

    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static WorkmateRepository getInstance() {
        if (sWorkmateRepository == null) {
            sWorkmateRepository = new WorkmateRepository();
        }
        return sWorkmateRepository;
    }

    public static FirebaseUser getWorkmate() {
        return mAuth.getCurrentUser();
    }

    public Task<Void> signOut(Context context){
            return AuthUI.getInstance().signOut(context);
    }
    public static CollectionReference getWorkmatesCollection() {
        return db.collection("workmate");
    }

    private Workmate getFirebaseUserAsWorkmate() {
        Workmate workmate = new Workmate();
        FirebaseUser user = getWorkmate();
        if (user != null) {
            // UID specific to the provider
            String uid = user.getUid();

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String mail = user.getEmail();
            String photoUrl = null;
            if(user.getPhotoUrl()!=null){
                photoUrl = user.getPhotoUrl().toString();
            }
            workmate = new Workmate(uid, name, mail, photoUrl);
            return workmate;
        }else {
            return null;
        }

    }
    public void createOrUpdateWorkmate(){
        Workmate workmate = getFirebaseUserAsWorkmate();
        if (workmate != null){
            getWorkmatesCollection().document(workmate.getId()).set(workmate);
        }else {
            Log.d("CreateorUpdateWorkmate", "Workmate is null");
        }
    }

    public void createOrUpdateWorkmate(Boolean isNotificationActive){
        Workmate workmate = getFirebaseUserAsWorkmate();
        workmate.setNotificationActive(isNotificationActive);
        if (workmate != null){
            getWorkmatesCollection().document(workmate.getId()).set(workmate);
        }else {
            Log.d("CreateorUpdateWorkmate", "Workmate is null");
        }

    }

    public LiveData<ArrayList<Workmate>> getAllWorkmates(){
        getWorkmatesCollection()
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Workmate> workmates = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    workmates.add(document.toObject(Workmate.class));
                }
                workmatesList.postValue(workmates);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
                workmatesList.postValue(null);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handle error
                workmatesList.postValue(null);
            }
        });
        return workmatesList;
    }
    private static final String SUBCOLLECTION = "likedrestaurant";

    public void addLikedRestaurant(Restaurant restaurant){
        FirebaseUser workmate = getWorkmate();
        String uid = workmate.getUid();
        getWorkmatesCollection().document(uid).collection(SUBCOLLECTION).add(restaurant);

    }
    public void deleteLikedRestaurant(Restaurant restaurant){
        FirebaseUser workmate = getWorkmate();
        String uid = workmate.getUid();
        getWorkmatesCollection()
                .document(uid)
                .collection(SUBCOLLECTION)
                .whereEqualTo("name", restaurant.getName())
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

    public LiveData<Boolean> checkLikedRestaurant(Restaurant restaurant){
        FirebaseUser workmate = getWorkmate();
        String uid = workmate.getUid();
        MutableLiveData<Boolean> valueCheck = new MutableLiveData<>();
        getWorkmatesCollection()
                .document(uid)
                .collection(SUBCOLLECTION)
                .whereEqualTo("name", restaurant.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().size()>0){
                            valueCheck.postValue(true);
                        }else {
                            Log.d("CHECKLIKED", "restaurant not found");
                            valueCheck.postValue(false);
                        }
                    } else {
                        Log.e("Error", "Error getting documents: ", task.getException());
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

    public LiveData<Boolean> getNotificationActive(){
        FirebaseUser workmate = getWorkmate();
        String uid = workmate.getUid();

        MutableLiveData<Boolean> valueCheck = new MutableLiveData<>();
        getWorkmatesCollection()
                .whereEqualTo("id", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().size()>0){
                            valueCheck.postValue(task.getResult().toObjects(Workmate.class).get(0).getNotificationActive());
                        }else {
                            Log.d("CHECKLIKED", "false");
                            valueCheck.postValue(false);
                        }
                    } else {
                        Log.e("Error", "Error getting documents: ", task.getException());
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
}
