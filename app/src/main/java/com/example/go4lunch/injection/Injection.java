package com.example.go4lunch.injection;

import com.example.go4lunch.repository.LunchRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.WorkmateRepository;

public class Injection {

    private static LunchRepository provideLunchRepo(){
        LunchRepository lunchRepository = LunchRepository.getInstance();
        return lunchRepository;
    }

    private static RestaurantRepository provideRestoRepo(){
        RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();
        return restaurantRepository;
    }

    private static WorkmateRepository provideWmateRepo(){
        WorkmateRepository wMateRepo = WorkmateRepository.getInstance();
        return wMateRepo;
    }
    public static ViewModelFactory provideViewModelFactory(){
        LunchRepository lunchRepository = provideLunchRepo();
        RestaurantRepository restaurantRepository = provideRestoRepo();
        WorkmateRepository workmateRepository = provideWmateRepo();

        return new ViewModelFactory(lunchRepository, restaurantRepository, workmateRepository);
    }

}
