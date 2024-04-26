package com.example.go4lunch.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private String address;
    private Boolean openingHours;

    private Double rating;
    private String image;

    //private String phoneNumber;
    //private String website;
    private List<String> types;

    //Constructor
    public Restaurant() {
    }

    public Restaurant(String id, String name, String address, Boolean openingHours, Double rating, String image, List<String> types) {
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


    //Equals & Hash

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(address, that.address) && Objects.equals(openingHours, that.openingHours) && Objects.equals(rating, that.rating) && Objects.equals(image, that.image) && Objects.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, openingHours, rating, image, types);
    }
}
