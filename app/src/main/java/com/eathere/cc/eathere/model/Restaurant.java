package com.eathere.cc.eathere.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Restaurant {
    public static final String EMPTY_STRING = "";
    public static final double EMPTY_DOUBLE = -1;

    public String rid;
    public String rname;
    public double overallRating;
    public String address;
    public double latitude;
    public double longitude;
    public String picUrl;

    public Restaurant(String rid, String rname, double overallRating, String address, double latitude, double longitude, String picUrl) {
        this.rid = rid;
        this.rname = rname;
        this.overallRating = overallRating;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.picUrl = picUrl;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> restaurant = new HashMap<String, Object>();
        restaurant.put("rid", rid);
        restaurant.put("rname", rname);
        restaurant.put("overall_rating", overallRating);
        restaurant.put("address", address);
        restaurant.put("latitude", latitude);
        restaurant.put("longitude", longitude);
        restaurant.put("pic_url", "http://random.com"); // TODO: FIX
        return restaurant;
    }

    public static Restaurant fromJSONObject(JSONObject restaurantJson) throws JSONException {
        String rid = restaurantJson.getString("rid");
        String rname = restaurantJson.getString("name");
        double overallRating = restaurantJson.optDouble("overall_rating", EMPTY_DOUBLE);
        String address = restaurantJson.optString("address", EMPTY_STRING);
        double latitude = restaurantJson.optDouble("lat", EMPTY_DOUBLE);
        double longitude = restaurantJson.optDouble("lng", EMPTY_DOUBLE);
        String picUrl = restaurantJson.optString("pic_url", EMPTY_STRING);
        return new Restaurant(rid, rname, overallRating, address, latitude, longitude, picUrl);
    }

}