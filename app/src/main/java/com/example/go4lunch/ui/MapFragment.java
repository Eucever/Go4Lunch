package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends DialogFragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";

    private List<Restaurant> mRestaurants = new ArrayList<>();

    private final String LONGITUDE_KEY = "CLE_LONGITUDE";

    private final String LATITUDE_KEY = "CLE_LATITUDE";

    public Double longitude;

    public Double latitude;

    private DemoViewModel mDemoViewModel;

    public GoogleMap mGoogleMap;

    @BindView(R.id.mapView)
    MapView mapView;
    private FragmentManager mFragmentManager;
    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public void showDialog() {
        show(mFragmentManager, TAG);
    }

    public void display(FragmentManager fragmentManager) {
        mFragmentManager = checkNotNull(fragmentManager);
        showDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_LightDialog);

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

        configureViewModel();

        longitude = getArguments().getDouble(LONGITUDE_KEY);
        latitude = getArguments().getDouble(LATITUDE_KEY);



        mDemoViewModel.getGpsLivedata().observe(this, gps ->{
            if (gps == null){
                Log.e("ONCREATECONFIG", "Error arg null");
            }else if(gps.getLatitude()!= null && gps.getLongitude() != null) {
                longitude = gps.getLongitude();
                latitude = gps.getLatitude();
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        configureMarkers();

        // Inflate the layout for this fragment
        return view;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude)))
                .setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 10));
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
                        //configureMarkers()
;
                    }
                }, 500);
            }
        });

    }

    public void configureMarkers(){
        /*Call<ListRestaurant> listRestaurantCall = mDemoViewModel.getAllRestaurant(
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
                    List<Restaurant> list_Restaurants = new ArrayList<>();
                    for (int i = 0; i < listReponse.getResults().size(); i++) {
                        Log.i("Response in Fragment ", listReponse.getResults().get(i).getVicinity());
                        list_Restaurants.add(mDemoViewModel.resultToRestaurant(listReponse.getResults().get(i)));
                    }
                    mRestaurants = list_Restaurants;



                } else Log.i("Response", "Code " + response.code());
            }


            @Override
            public void onFailure(Call<ListRestaurant> call, Throwable t) {
                Log.i("error", "Call Failed");
            }

        });*/
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
