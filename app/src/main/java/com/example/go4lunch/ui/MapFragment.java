package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends Fragment implements OnMapReadyCallback {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_map, container,
                false);

        ButterKnife.bind(this, view);

        /*mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
        // initialiser l'icone de la toolbar pour ce fragment
        mToolbar.setNavigationIcon(R.drawable.baseline_star);
        // définir l'action au clic sur le bouton de navigation de la toolbar
        mToolbar.setNavigationOnClickListener(v -> {
            //get parent activity
            DrawerLayout dl = ((CoreActivity)getActivity()).findViewById(R.id.coreDrawer);
            dl.open();
        });*/

        configureViewModel();


        Log.d("ONCREATEMAPFRAG", "long "+mDemoViewModel.getGpsLivedata().getValue());
        if(getArguments() != null && getArguments().size()>0) {
            longitudeStart = getArguments().getDouble(LONGITUDE_KEY);
            latitudeStart = getArguments().getDouble(LATITUDE_KEY);
        }


        mDemoViewModel.getGpsLivedata().observe(this, gps ->{
            Log.d("LOCATIOMapFragment", ""+gps);
            if (gps == null){
                Log.e("ONCREATECONFIG", "Error arg null");
            }else if(gps.getLatitude()!= null && gps.getLongitude() != null) {
                longitude = gps.getLongitude();
                latitude = gps.getLatitude();
                longitudeStart = longitude;
                latitudeStart = latitude;
                configureStartPosition();
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
        if(latitude!= null && longitude != null){
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
        configureStartPosition();
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
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
                Log.d("ONCAMERAMOVE", "Camera moving");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        LatLng poscamera = mGoogleMap.getCameraPosition().target;
                        mGoogleMap.addMarker(new MarkerOptions()
                                        .position(poscamera))
                                .setIcon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        latitude = poscamera.latitude;
                        longitude = poscamera.longitude;
                        Log.d("CAMERARUN", "Handler run");
                        //vv LIGNE COMMENTE POUR LIMITER LES APPELS A L API
                        //configureMarkers();
                    }
                }, 500);
            }
        });

    }

    public void configureMarkers(){
        if(latitude == null || longitude == null){
            return;
        }
        mDemoViewModel.getAllRestaurantsList(latitude,longitude).observe(this, restauList->{
            if (restauList != null){
                mRestaurants = restauList;
            }else {
                Log.e("RESTAU LIST", "ERROR NO RESTAURANTS IN LIST");
            }
        });
        mDemoViewModel.getAllRestaurantsWithLunch().observe(this, restau->{
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
}
