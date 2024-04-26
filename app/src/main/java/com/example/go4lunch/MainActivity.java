package com.example.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.place.Result;
import com.example.go4lunch.repository.LocationRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.ui.ListRestaurantFragment;
import com.example.go4lunch.ui.ListWorkmatesFragment;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String LONGITUDE_KEY = "CLE_LONGITUDE";

    private final String LATITUDE_KEY = "CLE_LATITUDE";

    private RestaurantRepository restaurantRepository = new RestaurantRepository();

    private DemoViewModel mDemoViewModel;

    private GPSStatus gpsStatus;

    public Restaurant restaurantTosend = new Restaurant();

    @BindView(R.id.buttonMap)
    public Button buttonMap;

    @BindView(R.id.buttonWorkmate)
    public Button buttonWmate;

    @BindView(R.id.buttonGPS)
    public Button buttonGPS;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        configureViewModel();


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ButterKnife.bind(this);
        buttonMap.setOnClickListener(view -> {
            Call<ListRestaurant> listRestaurantCall = restaurantRepository.getAllRestaurant(
                    "https://maps.googleapis.com/maps/api/place/",
                    "37.4226047,-122.0851341",
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
                        List<Restaurant> list_Restaurants = new ArrayList<>();
                        for (int i = 0; i < listReponse.getResults().size(); i++) {
                            Log.i("Response", listReponse.getResults().get(i).getVicinity());
                            list_Restaurants.add(resultToRestaurant(listReponse.getResults().get(i)));

                        }
                        restaurantTosend = resultToRestaurant(listReponse.getResults().get(0));



                        Bundle bundle = new Bundle();
                        bundle.putSerializable("CLE_LIST_RESTOS", (Serializable) list_Restaurants);
                        ListRestaurantFragment fragListrestau = new ListRestaurantFragment();
                        fragListrestau.setArguments(bundle);
                        FragmentManager fm = getSupportFragmentManager();
                        fragListrestau.display(fm);

                    } else Log.i("Response", "Code " + response.code());
                }

                @Override
                public void onFailure(Call<ListRestaurant> call, Throwable t) {
                    Log.i("error", "Call Failed");
                }

            });


            /*LunchRepository lunchRepository = LunchRepository.getInstance();

            Restaurant resto1 = new Restaurant("1", "Le resto 1", "Adress 1", "8h00", 4.5, "http", "06655464", "Site", "Pizza");
            Restaurant resto2 = new Restaurant("2", "Le resto 2", "Adress 2", "8h30", 2.5, "http", "06452512", "Site", "Burger");

            Workmate workmate1 = new Workmate("5", "Nom1", "efgzf@ag", true, "htr");
            Workmate workmate2 = new Workmate("6", "Nom2", "efg5z@ag", true, "htr");
            Workmate workmate3 = new Workmate("7", "Nom3", "efguljyhlz@ag", true, "htr");


            lunchRepository.createLunch(resto1, workmate1);
            lunchRepository.createLunch(resto1, workmate2);
            lunchRepository.createLunch(resto2, workmate3);

            lunchRepository.getTodayLunch("5").observe(this, arg -> {
                if (arg == null) {
                    Log.e("TODAYLUNCH", "error arg Lunch null");
                } else {
                    Log.i("TODAYLUNCH", "TodayLunch " + arg.getRestaurant().getName());
                }
            });
            lunchRepository.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(resto1).observe(this, arg -> {
                if (arg == null) {
                    Log.e("WORKMATECHOSERESTO", "error arg null");
                } else {
                    for (int i = 0; i < arg.size(); i++) {
                        Log.i("WORKMATECHOSERESTO", "workmates " + arg.get(i).getName());

                    }
                }
            });
            lunchRepository.checkIfCurrentWorkmateChoseThisRestaurantForLunch(resto1, "5").observe(this, arg -> {
                Log.d("WORKMATECHECK1", "value : " + arg);
                if (arg == null) {
                    Log.e("WORKMATECHECK1", "error arg null");
                } else if (arg) {
                    Log.i("WORKMATECHECK1", "workmate " + workmate1.getId() + " has chosen the restaurant " + resto1.getName());
                } else {
                    Log.i("WORKMATECHECK1", "workmate " + workmate1.getId() + " has not chosen the restaurant " + resto1.getName());
                }
            });
            lunchRepository.checkIfCurrentWorkmateChoseThisRestaurantForLunch(resto2, "5").observe(this, arg1 -> {
                Log.d("WORKMATECHECK2", "value : " + arg1);
                if (arg1 == null) {
                    Log.e("WORKMATECHECK2", "error arg null");
                } else if (arg1) {
                    Log.i("WORKMATECHECK2", "workmate " + workmate1.getId() + " has chosen the restaurant " + resto2.getName());
                } else {
                    Log.i("WORKMATECHECK2", "workmate " + workmate1.getId() + " has not chosen the restaurant " + resto2.getName());
                }
            });

            lunchRepository.deleteLunch(resto1, "5");
            lunchRepository.deleteLunch(resto1, "6");
            lunchRepository.deleteLunch(resto2, "7");*/

        });
        buttonWmate.setOnClickListener(view -> {

            ListWorkmatesFragment fragListWmate = new ListWorkmatesFragment();
            FragmentManager fm = getSupportFragmentManager();
            fragListWmate.display(fm);

            /*WorkmateRepository wMateRepo = WorkmateRepository.getInstance();

            mAuth.signInWithEmailAndPassword("user1@gmail.com", "password").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SIGNUSER", "signInWithEmail:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SIGNUSER", "signInWithEmail:failure", task.getException());
                    }
                }
            });

            FirebaseUser user1 = getWorkmate();
            if (user1 == null) {
                Log.d("SIGNUSER", "No user is signed in");
            } else {
                Log.d("SIGNUSER", "User is signed in");
            }
            wMateRepo.createOrUpdateWorkmate();

            Restaurant resto1 = new Restaurant("1", "Le resto 1", "Adress 1", true, 4.5, "http", null);
            Restaurant resto2 = new Restaurant("2", "Le resto 2", "Adress 2", true, 2.5, "http", null);


            wMateRepo.getAllWorkmates().observe(this, arg -> {
                if (arg == null) {
                    Log.e("WORKMATESLIST", "error arg null");
                } else if (arg.size() == 0) {
                    Log.d("WORKMATESLIST", "No workmates in the collection");
                } else {
                    for (int i = 0; i < arg.size(); i++) {
                        Log.i("WORKMATESLIST", "workmates " + arg.get(i).getMail());

                    }
                }
            });
            //wMateRepo.addLikedRestaurant(resto1);
            wMateRepo.checkLikedRestaurant(resto1).observe(this, arg -> {
                if (arg == null) {
                    Log.e("WORKMATERESTOCHECK", "error arg null");
                } else if (arg) {
                    Log.i("WORKMATERESTOCHECK", "The restaurant " + resto1.getName() + " is liked");
                } else {
                    Log.i("WORKMATERESTOCHECK", "The restaurant " + resto1.getName() + " is not liked");
                }
            });

            wMateRepo.checkLikedRestaurant(resto2).observe(this, arg -> {
                if (arg == null) {
                    Log.e("WORKMATERESTOCHECK", "error arg null");
                } else if (arg) {
                    Log.i("WORKMATERESTOCHECK", "The restaurant " + resto2.getName() + " is liked");
                } else {
                    Log.i("WORKMATERESTOCHECK", "The restaurant " + resto2.getName() + " is not liked");
                }
            });
            wMateRepo.deleteLikedRestaurant(resto1);
            wMateRepo.getNotificationActive().observe(this, arg -> {
                if (arg == null) {
                    Log.e("WORKMATENOTIFCHECK", "error arg null");
                } else if (arg) {
                    Log.i("WORKMATENOTIFCHECK", "The user has notification active");
                } else {
                    Log.i("WORKMATENOTIFCHECK", "The user doesn't have notification active");
                }
            });
            wMateRepo.signOut(this);*/
        });
        buttonGPS.setOnClickListener(view -> {

            coordGps();

            Bundle bundle = new Bundle();
            bundle.putDouble(LONGITUDE_KEY, gpsStatus.getLongitude() );
            bundle.putDouble(LATITUDE_KEY, gpsStatus.getLatitude());
            ListRestaurantFragment fragListRestau = new ListRestaurantFragment();
            fragListRestau.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            fragListRestau.display(fm);


            //testGps();

        });
    }

    private void coordGps(){


        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                0
        );

        mDemoViewModel.getGpsLivedata().observe(this, arg ->{
            if (arg == null){
                Log.e("DEMOVMTEST","Error arg null");
            }else {
                Log.d("DEMOVMTEST","longitude :" + arg.getLongitude());

                Log.d("DEMOVMTEST","latitude :" + arg.getLatitude());


                setGPSStatus(arg.getLongitude(), arg.getLatitude());
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        if(mDemoViewModel != null){
            mDemoViewModel.refresh();
        }
    }

    private void setGPSStatus(Double longitude, Double latitude){
        gpsStatus = new GPSStatus(longitude, latitude);
    };

    private void configureViewModel(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDemoViewModel = new DemoViewModel(new LocationRepository(fusedLocationClient));
    }

    public Restaurant resultToRestaurant(Result result){
        if (result != null && result.getOpeningHours() != null){
            return new Restaurant(result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    result.getOpeningHours().getOpenNow(),
                    result.getRating(),
                    result.getIcon(),
                    result.getTypes());

        }if (result != null && result.getOpeningHours() == null) {
            return new Restaurant(result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    false,
                    result.getRating(),
                    result.getIcon(),
                    result.getTypes());

        }else {
            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}