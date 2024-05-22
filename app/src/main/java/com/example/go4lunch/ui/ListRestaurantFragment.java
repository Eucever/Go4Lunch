package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewmodel.DemoViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListRestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListRestaurantFragment extends DialogFragment {

    private DemoViewModel mDemoViewModel;

    Double longitude;

    Double latitude;
    private final String LONGITUDE_KEY = "CLE_LONGITUDE";

    private final String LATITUDE_KEY = "CLE_LATITUDE";

    private List<RestaurantItem> mRestaurants;

    private static final String TAG = "ListRestaurantFragment";

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_list_restaurant, container,
                false);

        ButterKnife.bind(this, view);

        mRestaurants = new ArrayList<>();

        configureViewModel();

        configureListRestaurantsRecyclerView();

        longitude = getArguments().getDouble(LONGITUDE_KEY);
        latitude = getArguments().getDouble(LATITUDE_KEY);


        mDemoViewModel.getGpsLivedata().observe(this, arg ->{
            if (arg == null){
                Log.e("ONCREATECONFIG", "Error arg null");
            }else if(arg.getLatitude ()!= null && arg.getLongitude() !=null){
                    longitude = arg.getLongitude();
                    latitude = arg.getLatitude();
                    configureRestauList(arg.getLatitude(), arg.getLongitude());
                }
        });
        Log.d("TAG", "Call configure restau List "+ latitude +" " + longitude);
        configureRestauList(latitude,longitude);

        // Inflate the layout for this fragment
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        mDemoViewModel.refresh();

    }

    private void configureRestauList(Double latitude, Double longitude){
        /*Call<ListRestaurant> listRestaurantCall = mDemoViewModel.getAllRestaurant(
                "https://maps.googleapis.com/maps/api/place/",
                latitude+","+longitude,
                1000,
                "restaurant",
                BuildConfig.google_maps_api);

        Log.i("Call", listRestaurantCall.request().toString());
        ListRestaurantFragment fragment = this;
        listRestaurantCall.enqueue(new Callback<ListRestaurant>() {
            @Override
            public void onResponse(@NonNull Call<ListRestaurant> call, @NonNull
            Response<ListRestaurant> response) {

                if (response.isSuccessful()) {
                    Log.i("response", "found");
                    ListRestaurant listReponse = response.body();
                    List<RestaurantItem> list_Restaurants = new ArrayList<>();
                    for (int i = 0; i < listReponse.getResults().size(); i++) {
                        Log.i("Response in Fragment ", listReponse.getResults().get(i).getVicinity());
                        list_Restaurants.add(resultToRestaurantItem(listReponse.getResults().get(i), new LatLng(latitude, longitude)));

                    }
                    Collections.sort(list_Restaurants);
                    mRestaurants = list_Restaurants;
                    mDemoViewModel.getAllRestaurantsWithLunch().observe(fragment, restausWithLunch ->{
                        for (int i = 0; i < mRestaurants.size(); i++) {
                            int count = countRestauFrequency(restausWithLunch, mRestaurants.get(i));
                            mRestaurants.get(i).setNbParticipants(count);
                        }
                        listRestauAdapter.setmRestaurants(mRestaurants);


                    });
                } else Log.i("Response", "Code " + response.code());
            }


            @Override
            public void onFailure(Call<ListRestaurant> call, Throwable t) {
                Log.i("error", "Call Failed");
            }


        });*/
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
}