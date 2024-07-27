package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.CoreActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    AutocompleteSupportFragment acsf;
    PlacesClient placeClient;

    private List<Restaurant> mRestaurants = new ArrayList<>();

    private final String LONGITUDE_KEY = "CLE_LONGITUDE";

    private final String LATITUDE_KEY = "CLE_LATITUDE";

    public Double longitude;

    public Double latitude;

    public Double longitudeStart;

    public Double latitudeStart;

    private DemoViewModel mDemoViewModel;

    public GoogleMap mGoogleMap;

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.myLocationButton)
    FloatingActionButton myLocatbtn;

    private FragmentManager mFragmentManager;
    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }


    public void display(FragmentManager fragmentManager) {
        mFragmentManager = checkNotNull(fragmentManager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
    }

    @Override
    public void onStart(){
        super.onStart();

        acsf.getView().setVisibility(View.INVISIBLE);

        Toolbar mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
        mToolbar.setTitle("Map Restaurant");
        // initialise toolbar icon
        mToolbar.setNavigationIcon(R.drawable.setting_icon);
        // Defines navigation on click listener
        mToolbar.setNavigationOnClickListener(v -> {
            //get parent activity
            DrawerLayout dl = ((CoreActivity)getActivity()).findViewById(R.id.coreDrawer);
            dl.open();
        });

        mToolbar.setOnClickListener(v ->{
            acsf.getView().setVisibility(View.VISIBLE);
            mapView.setVisibility(View.INVISIBLE);
        });

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_map, container,
                false);

        ButterKnife.bind(this, view);

        acsf= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.acf_map);

        configureACSF();

        configureViewModel();


        Log.d("MapRestaurantFragment", "long "+mDemoViewModel.getGpsLivedata().getValue());
        if(getArguments() != null && getArguments().size()>0) {
            longitudeStart = getArguments().getDouble(LONGITUDE_KEY);
            latitudeStart = getArguments().getDouble(LATITUDE_KEY);
        }


        mDemoViewModel.getGpsLivedata().observe(this, gps ->{
            Log.d("MapRestaurantFragment", ""+gps);
            if (gps == null){
                Log.e("MapRestaurantFragment", "Error arg null");
            }else if(gps.getLatitude()!= null && gps.getLongitude() != null) {
                longitude = gps.getLongitude();
                latitude = gps.getLatitude();
                if(longitudeStart ==null){
                    longitudeStart = longitude;
                    latitudeStart = latitude;
                    configureStartPosition();
                }
                configureMarkers();
            }
        });

        if(latitude != null && longitude != null){
            configureMarkers();

        }
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        myLocatbtn.setOnClickListener(v -> {
            configureStartPosition();
        } );



        // Inflate the layout for this fragment
        return view;


    }

    private void configureStartPosition(){
        if(latitudeStart!= null && longitudeStart != null && mGoogleMap != null){
            Log.d("MapRestaurantFragment", "Configure start position");
            mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitudeStart,longitudeStart)))
                    .setIcon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeStart,longitudeStart), 10));

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        Log.d("MapRestaurantFragment", "On Map Ready");
        configureStartPosition();
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("MapRestaurantFragment", "On MarkerClick");
                String markerName = (String) marker.getTag();
                for (int i = 0; i< mRestaurants.size(); i++){
                    if (mRestaurants.get(i).getId().equals(markerName)){
                        RestaurantDetailActivity.navigate(MapFragment.this.getContext(), mRestaurants.get(i));
                    }
                }

                return false;
            }
        });
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mGoogleMap.clear();
                Log.d("MapRestaurantFragment", "Camera moving");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.d("MapRestaurantFragment", "RUNNIN");
                        LatLng poscamera = mGoogleMap.getCameraPosition().target;
                        mGoogleMap.addMarker(new MarkerOptions()
                                        .position(poscamera))
                                .setIcon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        latitude = poscamera.latitude;
                        longitude = poscamera.longitude;
                        Log.d("MapRestaurantFragment", "Handler run");
                        //vv LIGNE COMMENTE POUR LIMITER LES APPELS A L API
                        configureMarkers();
                    }
                }, 50);
            }
        });

    }

    public void configureMarkers(){
        if(latitude == null || longitude == null){
            return;
        }
        mDemoViewModel.getAllRestaurantsList(latitude,longitude).observe(this, restauList->{
            Log.i("MapRestaurantFragment", "Get ALL restaurants Notified");
            if (restauList != null){
                mRestaurants = restauList;
                mDemoViewModel.getAllRestaurantsWithLunch().observe(this, restau->{
                    Log.i("MapRestaurantFragment", "Get ALL restauWithlunch Notified");
                    for (int i = 0; i< mRestaurants.size(); i++){
                        mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(mRestaurants.get(i).getLocation().getLat(),
                                                mRestaurants.get(i).getLocation().getLng())))
                                .setTag(mRestaurants.get(i).getId());

                        for(int j= 0; j<restau.size(); j++){
                            if(mRestaurants.get(i).getId().equals(restau.get(j).getId())) {
                                mGoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(mRestaurants.get(i).getLocation().getLat(),
                                                        mRestaurants.get(i).getLocation().getLng()))
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                                        .setTag(mRestaurants.get(i).getId());
                            }
                        }


                    }
                });
            }else {
                Log.e("RESTAU LIST", "ERROR NO RESTAURANTS IN LIST");
            }

        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mDemoViewModel.refresh();
        configureMarkers();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void configureACSF() {
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), BuildConfig.google_maps_api);
        }
        placeClient = Places.createClient(getContext());
        acsf.setCountries("FR");
        acsf.setHint("Search a restaurant");
        acsf.setPlaceFields(Arrays.asList(Place.Field.ADDRESS,Place.Field.NAME));
        acsf.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // get lat lon
                LatLng latLng = place.getLatLng();
                mapView.setVisibility(View.VISIBLE);
                acsf.getView().setVisibility(View.INVISIBLE);
                // get place name
                String name = place.getName();
                String address = place.getAddress();
                if(latLng == null){
                    latLng = getLocationFromAddress(address);
                }
                Log.i("MapRestaurantFragment", "onPlaceSelect success : " + name + " " + latLng + " " + address);

                if (latLng==null){
                    return;
                }
                mGoogleMap.clear();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                Log.i("MapRestaurantFragment", "Marker Configured");

            }
            @Override
            public void onError(@NonNull Status status) {
                Log.e("MapRestaurantFragment", "onPlaceSelected error : " + status.getStatusMessage());
            }
        });

    }
    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(strAddress, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e("MapRestaurantFragment", "Geocoder failed: " + e.getMessage());
        }

        return latLng;
    }
}
