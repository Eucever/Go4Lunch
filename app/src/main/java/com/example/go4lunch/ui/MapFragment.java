package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends DialogFragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";

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

        longitude = getArguments().getDouble(LONGITUDE_KEY);
        latitude = getArguments().getDouble(LATITUDE_KEY);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 10));
    }

    @Override
    public void onResume() {
        super.onResume();
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
