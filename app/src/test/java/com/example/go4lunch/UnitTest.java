package com.example.go4lunch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UnitTest {
    private Restaurant restauInit;

    private Workmate workmateInit;

    private Lunch lunchInit;


    private List<String> types = new ArrayList<>();



    @Before
    public void setup(){
        types.add("Pizza");
        restauInit = new Restaurant("IFDSGE","Restau name" ,"Restau Address", true, 5.0, "httplsdfe", types);
        workmateInit = new Workmate("RG45D64SD", "Bob", "bob@mail.com", "http://efsefs");
        lunchInit = new Lunch("06-21-2022", workmateInit, restauInit);
    }


    @Test
    public void testRestaurant(){
        Restaurant restauCorrect = new Restaurant("IFDSGE","Restau name" ,"Restau Address", true, 5.0, "httplsdfe", types);
        Restaurant restauIncorrect = new Restaurant("88888","Restau name" ,"Restau Address", true, 5.0, "httplsdfe", types);

        assertTrue(restauCorrect.equals(restauInit));
        assertFalse(restauIncorrect.equals(restauInit));
    }

    @Test
    public void testWorkmate(){
        Workmate workmateCorrect = new Workmate("RG45D64SD", "Bob", "bob@mail.com", "http://efsefs");
        Workmate workmateIncorrect = new Workmate("gre4g5488","Greg" ,"greg@mail.com", "null");

        assertTrue(workmateCorrect.equals(workmateInit));
        assertFalse(workmateIncorrect.equals(workmateInit));
    }

    @Test
    public void testLunch(){
        Lunch lunchCorrect = new Lunch("06-21-2022", workmateInit, restauInit);
        Lunch lunchIncorrect = new Lunch("06-28-2022", workmateInit, restauInit);

        assertTrue(lunchCorrect.equals(lunchInit));
        assertFalse(lunchIncorrect.equals(lunchInit));
    }



}
