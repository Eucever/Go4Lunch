package com.example.go4lunch;

import static org.junit.Assert.assertEquals;

import com.example.go4lunch.place.ListRestaurant;
import com.example.go4lunch.repository.RestaurantRepository;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RetroFitTest {

    private final String MAPS_API_KEY = BuildConfig.google_maps_api;

    private final String MAPS_RESTAURANT_TYPE = "restaurant";

    private final String MAPS_URL ="https://maps.googleapis.com/maps/api/place/";

    private final int EXPECTED_RESTAURANT_COUNT = 5;

    @Test
    public void checkRestaurantNearBySynchronously() throws IOException {

        // initialize the repository
        RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();

        // get all the restaurants from Rennes city with a radius of 1000
        Call<ListRestaurant> restaurantsCall = restaurantRepository.getAllRestaurant(MAPS_URL, "48.1159843,-1.7296427", 1000, MAPS_RESTAURANT_TYPE, MAPS_API_KEY);

        Response<ListRestaurant> restaurantsRes = restaurantsCall.execute();

        ListRestaurant restaurants = restaurantsRes.body();

        assertEquals(EXPECTED_RESTAURANT_COUNT, restaurants.getResults().size());
    }

    @Test
    public void checkRestaurantNearByAsynchronously() throws InterruptedException {

        // create an atomic integer to store the number of restaurants found
        AtomicInteger i = new AtomicInteger(0);

        // create a count down latch, we want to wait for the response before continuing the test
        CountDownLatch latch = new CountDownLatch(1);

        // create a thread to make the request to the API, as we are in a test we can't use the main thread
        Thread thread = new Thread(() -> {

            // initialize the repository
            RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();

            // get all the restaurants from Rennes city with a radius of 1000
            Call<ListRestaurant> restaurantsCall = restaurantRepository.getAllRestaurant(MAPS_URL, "48.1159843,-1.7296427", 1000, MAPS_RESTAURANT_TYPE, MAPS_API_KEY);

            // create a callback to handle the response
            final Callback<ListRestaurant> callback = new Callback<ListRestaurant>() {

                // if the request is successful
                @Override
                public void onResponse(Call<ListRestaurant> call, Response<ListRestaurant> response) {
                    // Get the ListRestaurant object from the response.
                    ListRestaurant listRestaurant = response.body();

                    // get the number of restaurants found
                    Integer restaurantCount = listRestaurant.getResults().size();

                    // store the number of restaurants found in the atomic integer
                    synchronized (i) {
                        i.set(restaurantCount);
                    }

                    // free the latch to continue the test
                    latch.countDown();
                }

                // if the request failed
                @Override
                public void onFailure(Call<ListRestaurant> call, Throwable t) {
                    // free the latch to continue the test
                    latch.countDown();
                }
            };

            // process the callback asynchronously
            restaurantsCall.enqueue(callback);

        });

        // start the second thread
        thread.start();

        // wait for the response
        latch.await();

        // get the number of restaurants found
        int value = i.get();

        // check if the number of restaurants found is correct
        assertEquals(EXPECTED_RESTAURANT_COUNT,value);
    }


}