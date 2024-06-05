package com.example.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.location.GPSStatus;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.ui.ListRestaurantFragment;
import com.example.go4lunch.ui.ListWorkmatesFragment;
import com.example.go4lunch.ui.MapFragment;
import com.example.go4lunch.ui.SettingsFragment;
import com.example.go4lunch.ui.YourLunchFragment;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        configureViewModel();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);




        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ButterKnife.bind(this);
        buttonMap.setOnClickListener(view -> {

            coordGps();

            Bundle bundle = new Bundle();
            bundle.putDouble(LONGITUDE_KEY, gpsStatus.getLongitude() );
            bundle.putDouble(LATITUDE_KEY, gpsStatus.getLatitude());
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            mapFragment.display(fm);

        });
        buttonWmate.setOnClickListener(view -> {

            ListWorkmatesFragment fragListWmate = new ListWorkmatesFragment();
            FragmentManager fm = getSupportFragmentManager();
            fragListWmate.display(fm);

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
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settingsFragment) {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentManager fm = getSupportFragmentManager();
            settingsFragment.display(fm);
            return true;
        }
        if( id == R.id.yourLunchFragment){
            YourLunchFragment yourLunchFragment = new YourLunchFragment();
            FragmentManager fm = getSupportFragmentManager();
            yourLunchFragment.display(fm);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}