package com.example.supermarket;

import android.net.Uri;

public class Users {

    private String name;
    private String email;
    private Uri photoUrl;

    public Users(String name, String email, Uri photoUrl, String phone) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phone = phone;
    }

    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
