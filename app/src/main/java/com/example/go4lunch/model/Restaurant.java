package com.example.go4lunch.model;

import java.util.Objects;

public class Restaurant {

    private String id;
    private String name;
    private String address;
    private String openingHours;

    private Double rating;
    private String image;
    private String phoneNumber;
    private String website;
    private String type;

    //Constructor
    public Restaurant() {
    }

    public Restaurant(String id, String name, String address, String openingHours, Double rating, String image, String phoneNumber, String website, String type) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.rating = rating;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.type = type;
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

    public String getOpeningHours() {
        return openingHours;
    }

    public Double getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getType() {
        return type;
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

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setType(String type) {
        this.type = type;
    }


    //Equals & Hash

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(address, that.address) && Objects.equals(openingHours, that.openingHours) && Objects.equals(rating, that.rating) && Objects.equals(image, that.image) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(website, that.website) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, openingHours, rating, image, phoneNumber, website, type);
    }
}
