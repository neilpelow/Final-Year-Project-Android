package com.example.neilpelow.finalyearproject;

/**
 * Created by neilpelow on 08/02/2017.
 */

public class Venue {
    public String id;
    public String name;
    public String longitude;
    public String latitude;

    public Venue() {

    }

    public Venue(String id, String name, String longitude, String latitude) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId(){ return id; }

    public String getName(){ return name; }

    public String getLongitude(){ return longitude; }

    public String getLatitude(){ return latitude; }
}
