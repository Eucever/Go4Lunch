package com.example.go4lunch.ui;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;

import java.util.Objects;

public class WorkmateItem {
    private String id;
    private String name;
    private String mail;

    private Boolean isNotificationActive;

    private Restaurant restaurantForLunch;

    private String avatar;

    //Constructor

    public WorkmateItem(String id, String name, String mail, Boolean isNotificationActive,String avatar) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.isNotificationActive = isNotificationActive;
        this.avatar = avatar;
        this.restaurantForLunch = null;
    }
    public WorkmateItem(String id, String name, String mail,String avatar) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.avatar = avatar;
        this.isNotificationActive = false;
        this.restaurantForLunch = null;
    }
    public WorkmateItem(){

    }

    //Getter

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public Boolean getNotificationActive() {
        return isNotificationActive;
    }

    public String getAvatar() {
        return avatar;
    }

    public Restaurant getRestaurantForLunch() {
        return restaurantForLunch;
    }

    //Setter

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setNotificationActive(Boolean notificationActive) {
        isNotificationActive = notificationActive;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRestaurantForLunch(Restaurant restaurant){this.restaurantForLunch = restaurant;}

    public static WorkmateItem workmateToWorkmateItem(Workmate workmate){
        WorkmateItem wMateItem =new WorkmateItem();
        if (workmate != null) {
            wMateItem = new WorkmateItem(
                    workmate.getId(),
                    workmate.getName(),
                    workmate.getMail(),
                    workmate.getAvatar()
            );

        }
        return wMateItem;
    }


    //Equals & Hash

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateItem workmateItem = (WorkmateItem) o;
        return Objects.equals(id, workmateItem.id) && Objects.equals(name, workmateItem.name) && Objects.equals(mail, workmateItem.mail) && Objects.equals(isNotificationActive, workmateItem.isNotificationActive) && Objects.equals(avatar, workmateItem.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, mail, isNotificationActive, avatar);
    }
}
