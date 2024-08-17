package com.example.go4lunch.repository;

import com.example.go4lunch.data.RetrofitMapsApi;
import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.place.ResultDetails;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;
    public RestaurantRepository(){
    }
    public static RestaurantRepository getInstance(){
        if (instance==null){
            instance = new RestaurantRepository();
        }
        return instance;
    }

    public Call<ListRestaurant> getAllRestaurant(String url, String location, int radius, String type, String key){

        //use the retrofit builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //call the retrofit.create with the interface retrofitmapsapi
        //call the api.getallrestaurant
        //return the call api.getallrestaurant
        return  retrofit.create(RetrofitMapsApi.class)
                .getAllRestaurants(location, radius, type, key);


    }

    public Call<ResultDetails> getRestaurantDetail(String url, String key, String place_id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitMapsApi.class)
                .getRestaurantDetails(key, place_id);
    }


}
