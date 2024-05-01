package com.example.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.viewmodel.DemoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RestaurantDetailActivity extends AppCompatActivity {

    public Restaurant restaurantSent;

    //private LunchRepository lunchRepository;

    //private WorkmateRepository workmateRepository;

    public ImageView avatarRestau;

    public TextView titleRestau;

    public RatingBar restauRatingStars;

    public TextView restauDesc;

    public FloatingActionButton chooseRestauBtn;

    public ImageButton likeRestaubtn;

    public RecyclerView workmateRecycler;

    private WorkmateRecyclerAdapter restauAdapter;


    private final static String CLE_RESTAU = "CLE_RESTAU";
    private DemoViewModel mDemoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        avatarRestau = findViewById(R.id.avatarRestau);
        titleRestau = findViewById(R.id.titleRestau);
        restauRatingStars = findViewById(R.id.restauRatingStars);
        restauDesc = findViewById(R.id.restauDesc);
        workmateRecycler = findViewById(R.id.workmatesRecycler);
        likeRestaubtn = findViewById(R.id.likeRestauButton);
        chooseRestauBtn = findViewById(R.id.chooseRestauBtn);

        /*lunchRepository = LunchRepository.getInstance();

        workmateRepository = WorkmateRepository.getInstance();*/

        restauAdapter = new WorkmateRecyclerAdapter();

        restaurantSent = (Restaurant) getIntent().getSerializableExtra(CLE_RESTAU);


        configureViewModel();

        configureWorkmatesRecyclerView();

        configureRestaurant();

        configureFavorite();

        configureChoosenRestaurantWorkmate();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mDemoViewModel != null){
            mDemoViewModel.refresh();
        }
    }

    private void configureRestaurant(){
        titleRestau.setText(restaurantSent.getName());
        restauDesc.setText(restaurantSent.getAddress());
        restauRatingStars.setRating(restaurantSent.getRating().floatValue());

        Glide.with(this).load(restaurantSent.getImage()).into(avatarRestau);
    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);

    }

    public static void navigate(Context context, Restaurant restaurant) {
        Intent restIntent = new Intent(context, RestaurantDetailActivity.class);
        restIntent.putExtra(CLE_RESTAU,restaurant);
        ActivityCompat.startActivity(context, restIntent,null);
    }

    private void configureWorkmatesRecyclerView(){
        workmateRecycler.setLayoutManager(new LinearLayoutManager(this));
        workmateRecycler.setAdapter(restauAdapter);
        mDemoViewModel.getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(restaurantSent).observe(this, arg -> {
            if (arg == null) {
                Log.e("RECYCLERCONFIG", "Error arg Lunch null");
            } else {
                restauAdapter.setmWorkmates(arg);

            }
        });

    }

    private void configureFavorite(){
        mDemoViewModel.checkLikedRestaurant(restaurantSent).observe(this, arg -> {
            if(arg == null){
                Log.e("LIKEBUTTONCONFIG", "Error arg null");
            }else if (arg){
                likeRestaubtn.setImageResource(R.drawable.baseline_star);
            }else {
                likeRestaubtn.setImageResource(R.drawable.baseline_star_border);
            }
        });

        likeRestaubtn.setOnClickListener(view -> {
            mDemoViewModel.checkLikedRestaurant(restaurantSent).observe(this, arg -> {
                if(arg == null){
                    Log.e("LIKEBUTTONCLICK", "Error arg null");
                }else if (arg){
                    mDemoViewModel.deleteLikedRestaurant(restaurantSent);
                    likeRestaubtn.setImageResource(R.drawable.baseline_star_border);
                }else {
                    mDemoViewModel.addLikedRestaurant(restaurantSent);
                    likeRestaubtn.setImageResource(R.drawable.baseline_star);
                }
            });

        });
    }

    private void configureChoosenRestaurantWorkmate(){
       Workmate workmate = mDemoViewModel.getFirebaseUserAsWorkmate();
        mDemoViewModel.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurantSent, workmate.getId()).observe(this, arg ->{
           if (arg == null){
               Log.e("CHOOSEBUTTONCONFIG", "Error arg null");
           }else if (arg){
               chooseRestauBtn.setImageResource(R.drawable.sharp_clear_24);
           }else {
               chooseRestauBtn.setImageResource(R.drawable.baseline_check);
           }
       });

       chooseRestauBtn.setOnClickListener(view -> {
           mDemoViewModel.checkIfCurrentWorkmateChoseThisRestaurantForLunch(restaurantSent, workmate.getId()).observe(this, arg ->{
               if (arg == null){
                   Log.e("CHOOSEBUTTONCONFIG", "Error arg null");
               }else if (arg){
                   mDemoViewModel.deleteLunch(restaurantSent, workmate.getId());
                   chooseRestauBtn.setImageResource(R.drawable.baseline_check);
               }else {
                   mDemoViewModel.createLunch(restaurantSent, workmate);
                   chooseRestauBtn.setImageResource(R.drawable.sharp_clear_24);
               }
           });
       });
    }



}