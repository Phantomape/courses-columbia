package com.eathere.cc.eathere.model;


public class Restaurant {
    public String id;
    public double overallRating;
    public String address;
    public double latitude;
    public double longitude;
    public String picUrl;

    public Restaurant(String id) {
        this.id = id;
    }
}
