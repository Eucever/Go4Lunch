package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.place.Result;
import com.example.go4lunch.viewmodel.DemoViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListRestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListRestaurantFragment extends DialogFragment {

    private DemoViewModel mDemoViewModel;

    private final String LONGITUDE_KEY = "CLE_LONGITUDE";

    private final String LATITUDE_KEY = "CLE_LATITUDE";

    private List<Restaurant> mRestaurants = new ArrayList<>();

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

        configureViewModel();

        configureListRestaurantsRecyclerView();

        Double longitude = getArguments().getDouble(LONGITUDE_KEY);
        Double latitude = getArguments().getDouble(LATITUDE_KEY);


        configureRestauList(latitude,longitude);



        // Inflate the layout for this fragment
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void configureRestauList(Double latitude, Double longitude){
        Call<ListRestaurant> listRestaurantCall = mDemoViewModel.getAllRestaurant(
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
                        list_Restaurants.add(resultToRestaurant(listReponse.getResults().get(i)));
                        mRestaurants = list_Restaurants;
                    }
                listRestauAdapter.setmRestaurants(mRestaurants);

                } else Log.i("Response", "Code " + response.code());
            }

            @Override
            public void onFailure(Call<ListRestaurant> call, Throwable t) {
                Log.i("error", "Call Failed");
            }


        });

    }
    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
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

    private void configureListRestaurantsRecyclerView(){
        listRestauAdapter = new ListRestaurantFragmentAdapter();
        restaurantsRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        restaurantsRecycler.setAdapter(listRestauAdapter);


    }
}