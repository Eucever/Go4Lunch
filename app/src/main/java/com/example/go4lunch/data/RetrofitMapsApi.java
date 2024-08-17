package com.example.go4lunch.data;

import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.place.ResultDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitMapsApi {
    @GET("nearbysearch/json")
    Call<ListRestaurant> getAllRestaurants(
            @Query("location") String location,
            @Query("radius") Integer radius,
            @Query("type") String type,
            @Query("key") String key
    );

    @GET("details/json")
    Call<ResultDetails> getRestaurantDetails(
            @Query("key") String key,
            @Query("place_id") String placeId);
}
