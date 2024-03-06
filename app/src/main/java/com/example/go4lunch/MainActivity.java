package com.example.go4lunch;

import android.os.Bundle;

import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.repository.RestaurantRepository;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.go4lunch.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;


import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RestaurantRepository restaurantRepository = new RestaurantRepository();

    @BindView(R.id.buttonMap)
    public Button buttonMap;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Call<ListRestaurant> listRestaurantCall = restaurantRepository.getAllRestaurant(
                    "https://maps.googleapis.com/maps/api/place/",
                    "37.4226047,-122.0851341",
                    1000,
                    "restaurant",
                    "AIzaSyAuy4-qNWGilytsFTzoTISpJV-7o3gAEQc");

            Log.i ("Call", listRestaurantCall.request().toString());

            listRestaurantCall.enqueue(new Callback<ListRestaurant>() {
                @Override
                public void onResponse(@NonNull Call<ListRestaurant> call, @NonNull
                Response<ListRestaurant> response) {

                    if (response.isSuccessful()) {
                        Log.i("response", "found");
                        ListRestaurant listRestaurant = response.body();
                        for (int i=0; i < listRestaurant.getResults().size(); i++)
                        {
                            Log.i("Response", listRestaurant.getResults().get(i).getName());
                        }



                    }else Log.i("Response", "Code "+response.code());
                }

                @Override
                public void onFailure(Call<ListRestaurant> call, Throwable t) {
                Log.i("error", "Call Failed");
                }

        });
    });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}