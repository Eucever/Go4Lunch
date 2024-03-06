package com.example.go4lunch.model;

import java.util.Date;
import java.util.Objects;

public class Lunch {
    private String date;

    private Workmate wMate;

    private Restaurant restaurant;

    //Constructor
    public Lunch(String date, Workmate wMate, Restaurant restaurant) {
        this.date = date;
        this.wMate = wMate;
        this.restaurant = restaurant;
    }

    //Getter
    public String getDate() {
        return date;
    }

    public Workmate getwMate() {
        return wMate;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    //Setter

    public void setDate(String date) {
        this.date = date;
    }

    public void setwMate(Workmate wMate) {
        this.wMate = wMate;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    //Equals & HashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lunch lunch = (Lunch) o;
        return Objects.equals(date, lunch.date) && Objects.equals(wMate, lunch.wMate) && Objects.equals(restaurant, lunch.restaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, wMate, restaurant);
    }
}
