package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.CoreActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListRestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListRestaurantFragment extends Fragment {

    AutocompleteSupportFragment acsf;
    PlacesClient placeClient;

    private DemoViewModel mDemoViewModel;

    Double longitude;

    Double latitude;
    private final String LONGITUDE_KEY = "CLE_LONGITUDE";

    private final String LATITUDE_KEY = "CLE_LATITUDE";

    private List<RestaurantItem> mRestaurants;

    private FragmentManager mFragmentManager;

    @BindView(R.id.ListRestauRecycler)
    RecyclerView restaurantsRecycler;

    private ListRestaurantFragmentAdapter listRestauAdapter;

    public ListRestaurantFragment() {
        // Required empty public constructor
    }

    public static ListRestaurantFragment newInstance() {
        ListRestaurantFragment fragment = new ListRestaurantFragment();
        return fragment;
    }


    public void display(FragmentManager fragmentManager) {
        mFragmentManager = checkNotNull(fragmentManager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_list_restaurant, container,
                false);

        ButterKnife.bind(this, view);

        acsf= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.acf_list); // ou acf_map dans le cas du MapFragment !




        configureACSF();

        mRestaurants = new ArrayList<>();

        //Initialising ViewModel
        configureViewModel();

        configureListRestaurantsRecyclerView();


        if(getArguments() != null && getArguments().size()>0) {
            longitude = getArguments().getDouble(LONGITUDE_KEY);
            latitude = getArguments().getDouble(LATITUDE_KEY);
        }


        //Acquires GPS positions to configure the restaurant list if the positions aren't null
        mDemoViewModel.getGpsLivedata().observe(this, arg ->{
            if (arg == null){
                Log.e("ONCREATECONFIG", "Error arg null");
            }else if(arg.getLatitude ()!= null && arg.getLongitude() !=null){
                    longitude = arg.getLongitude();
                    latitude = arg.getLatitude();
                    configureRestauList(arg.getLatitude(), arg.getLongitude());
                }
        });


        /*Log.d("TAG", "Call configure restau List "+ latitude +" " + longitude);
        configureRestauList(latitude,longitude);*/

        // Inflate the layout for this fragment
        return view;


    }

    @Override
    public void onStart(){
        super.onStart();

        acsf.getView().setVisibility(View.INVISIBLE);

        Toolbar mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
        mToolbar.setTitle("List Restaurant");
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
            restaurantsRecycler.setVisibility(View.INVISIBLE);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshing GPS location
        mDemoViewModel.refresh();

    }

    private void configureRestauList(Double latitude, Double longitude){
        if(latitude == null || longitude == null){
            return;
        }
        mDemoViewModel.getAllRestaurantsItemList(latitude, longitude).observe(this, restausItemList->{
            if (restausItemList != null){
                mRestaurants = restausItemList;
                mDemoViewModel.getAllRestaurantsWithLunch().observe(this, restausWithLunch -> {
                    for (int i = 0; i < mRestaurants.size(); i++) {
                        int count = countRestauFrequency(restausWithLunch, mRestaurants.get(i));
                        mRestaurants.get(i).setNbParticipants(count);
                    }
                    listRestauAdapter.setmRestaurants(mRestaurants);
                });
            }else {
                Log.e("ERRORLIST", "ERROR NO RESTAURANTS IN LIST");
            }
        });





    }
    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
    }
    public int countRestauFrequency(List<RestaurantItem> restaurantItems, RestaurantItem restaurantItem){
        int occurence = 0;
        for(int i = 0; i<restaurantItems.size(); i++){
            if(restaurantItems.get(i).getId().equals(restaurantItem.getId())){
                occurence++;
            }
        }
        return occurence;
    }

    private void configureListRestaurantsRecyclerView(){
        listRestauAdapter = new ListRestaurantFragmentAdapter();
        restaurantsRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        restaurantsRecycler.setAdapter(listRestauAdapter);
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
                restaurantsRecycler.setVisibility(View.VISIBLE);
                acsf.getView().setVisibility(View.INVISIBLE);
                // get place name
                String name = place.getName();
                String address = place.getAddress();
                if(latLng == null){
                    latLng = getLocationFromAddress(address);
                }
                Log.i("ListRestaurantFragment", "onPlaceSelect success : " + name + " " + latLng + " " + address);

                if (latLng==null){
                    return;
                }
                configureRestauList(latLng.latitude, latLng.longitude);

            }
            @Override
            public void onError(@NonNull Status status) {
                Log.e("ListRestaurantFragment", "onPlaceSelected error : " + status.getStatusMessage());
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
            Log.e("ListRestaurantFragment", "Geocoder failed: " + e.getMessage());
        }

        return latLng;
    }
}