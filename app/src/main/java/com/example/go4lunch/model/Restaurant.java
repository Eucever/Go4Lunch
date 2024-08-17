package com.example.go4lunch.model;

import com.example.go4lunch.place.Location;
import com.example.go4lunch.place.Photo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Restaurant implements Serializable {

    private String place_id;
    private String name;
    private String address;
    private Boolean openingHours;
    private Double rating;
    private String image;

    private Location location;

    private String phoneNumber;



    private List<Photo> photos;

    private String website;
    private List<String> types;

    //Constructor
    public Restaurant() {
    }

    public Restaurant(String place_id, String name, String address, Boolean openingHours, Double rating,String phoneNumber, String website, String image, List<String> types) {
        this.place_id = place_id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.rating = rating;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.types = types;
    }

    public Restaurant(String place_id, String name, String address, Boolean openingHours, Double rating, String image, List<String> types) {
        this.place_id = place_id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.rating = rating;
        this.image = image;
        this.types = types;
    }


    public Restaurant(String place_id, String name, String address, Boolean openingHours, Double rating, String image, List<String> types, Location location) {
        this.place_id = place_id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.rating = rating;
        this.image = image;
        this.types = types;
        this.location = location;
    }

    //Getter

    public String getId() {
        return place_id;
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

    public Location getLocation(){
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public List<Photo> getPhotos() {
        return photos;
    }




    //Setter

    public void setId(String place_id) {
        this.place_id = place_id;
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

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }


    public void setType(List<String> type) {
        this.types = types;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setLocation(Location location){
        this.location = location;
    }


    //Equals & Hash

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(place_id, that.place_id) && Objects.equals(name, that.name) && Objects.equals(address, that.address) && Objects.equals(openingHours, that.openingHours) && Objects.equals(rating, that.rating) && Objects.equals(image, that.image) && Objects.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(place_id, name, address, openingHours, rating, image, types);
    }
}
