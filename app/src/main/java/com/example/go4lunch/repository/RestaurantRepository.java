package com.example.go4lunch.repository;

import com.example.go4lunch.data.RetrofitMapsApi;
import com.example.go4lunch.place.ListRestaurant;

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

        //utiliser le retrofit builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //appeler le retrofit.create avc interface retrofitmapsapi
        //faire un appel sur api.getallrestaurant
        //retourner le call api.getallrestaurant+
        return  retrofit.create(RetrofitMapsApi.class)
                .getAllRestaurants(location, radius, type, key);


    }
}
