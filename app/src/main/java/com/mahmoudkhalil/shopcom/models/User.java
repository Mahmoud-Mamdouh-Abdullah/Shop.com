package com.mahmoudkhalil.shopcom.models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String ID;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String photoUri;
    public List<String> cartList = new ArrayList<>();
    public List<String> wishList = new ArrayList<>();

    public User() {
    }



    public User(String ID, String fullName, String email, String password, String phone, String gender) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
