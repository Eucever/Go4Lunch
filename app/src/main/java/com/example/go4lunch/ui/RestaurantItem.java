package com.example.go4lunch.ui;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.place.Location;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RestaurantItem implements Serializable, Comparable<RestaurantItem> {
    private String id;
    private String name;
    private String address;
    private Boolean openingHours;
    private Double rating;
    private String image;

    private Location location;

    private double distance;

    private int nbParticipants;

    //private String phoneNumber;
    //private String website;
    private List<String> types;

    //Constructor
    public RestaurantItem() {
    }
    public RestaurantItem(String id, String name, String address, Boolean openingHours, Double rating, String image, List<String> types) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.rating = rating;
        this.image = image;
        /*this.phoneNumber = phoneNumber;
        this.website = website;*/
        this.types = types;
    }

    public RestaurantItem(String id, String name, String address, Boolean openingHours, Double rating, String image, List<String> types,double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.rating = rating;
        this.image = image;
        this.types = types;
        this.distance = distance;
    }

    //Getter

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getOpeningHours() {
        return openingHours;
    }

    public Double getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }

    public List<String> getTypes() {
        return types;
    }

    public double getDistance(){return distance;}


    public int getNbParticipants(){return nbParticipants;}


    //Setter

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOpeningHours(Boolean openingHours) {
        this.openingHours = openingHours;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setType(List<String> type) {
        this.types = types;
    }

    public void setDistance(double distance){this.distance = distance;}

    public void setNbParticipants(int nbParticipants){this.nbParticipants = nbParticipants;}


    //Equals & Hash

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantItem that = (RestaurantItem) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(address, that.address) && Objects.equals(openingHours, that.openingHours) && Objects.equals(rating, that.rating) && Objects.equals(image, that.image) && Objects.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, openingHours, rating, image, types);
    }

    public static RestaurantItem restaurantToRestaurantItem(Restaurant restau){
        RestaurantItem restauItem =new RestaurantItem();
        if (restau != null && restau.getOpeningHours() != null) {
            restauItem = new RestaurantItem(restau.getId(),
                    restau.getName(),
                    restau.getAddress(),
                    restau.getOpeningHours(),
                    restau.getRating(),
                    restau.getImage(),
                    restau.getTypes());
        }else if (restau != null && restau.getOpeningHours() == null){
            restauItem = new RestaurantItem(restau.getId(),
                    restau.getName(),
                    restau.getAddress(),
                    false,
                    restau.getRating(),
                    restau.getImage(),
                    restau.getTypes());
        }
        return restauItem;
    }

    @Override
    public int compareTo(RestaurantItem restaurantItem) {
        double d1= this.distance;
        double d2= restaurantItem.getDistance();
        if( d1 < d2 ){
            return -1;
        }else if( d1 > d2 ){
            return 1;
        }
        return 0;
    }
}
