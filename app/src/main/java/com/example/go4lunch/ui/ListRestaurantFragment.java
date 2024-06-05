package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class ListRestaurantFragment extends Fragment {

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

        /*Toolbar mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
        // initialiser l'icone de la toolbar pour ce fragment
        mToolbar.setNavigationIcon(R.drawable.baseline_star);
        // définir l'action au clic sur le bouton de navigation de la toolbar
        mToolbar.setNavigationOnClickListener(v -> {
            //get parent activity
            DrawerLayout dl = ((CoreActivity)getActivity()).findViewById(R.id.coreDrawer);
            dl.open();
        });*/

        mRestaurants = new ArrayList<>();

        configureViewModel();

        configureListRestaurantsRecyclerView();


        if(getArguments() != null && getArguments().size()>0) {
            longitude = getArguments().getDouble(LONGITUDE_KEY);
            latitude = getArguments().getDouble(LATITUDE_KEY);
        }

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
}