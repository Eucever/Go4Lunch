package com.example.go4lunch.model;

import java.util.Objects;

public class Workmate {
    private String id;
    private String name;
    private String mail;

    private Boolean isNotificationActive;

    private String avatar;

    //Constructor

    public Workmate(String id, String name, String mail, Boolean isNotificationActive,String avatar) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.isNotificationActive = isNotificationActive;
        this.avatar = avatar;
    }
    public Workmate(String id, String name, String mail,String avatar) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.avatar = avatar;
        this.isNotificationActive = false;
    }
    public Workmate(){

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


    //Equals & Hash

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workmate workmate = (Workmate) o;
        return Objects.equals(id, workmate.id) && Objects.equals(name, workmate.name) && Objects.equals(mail, workmate.mail) && Objects.equals(isNotificationActive, workmate.isNotificationActive) && Objects.equals(avatar, workmate.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, mail, isNotificationActive, avatar);
    }
}
