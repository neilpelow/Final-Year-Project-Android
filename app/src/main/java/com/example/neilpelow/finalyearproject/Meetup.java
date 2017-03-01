package com.example.neilpelow.finalyearproject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by neilpelow on 22/02/2017.
 */

public class Meetup {
    public String id;
    public String description;
    public String name;
    public String address;
    public String startTime;
    public String rsvpStatus;

    public Meetup() {

    }

    public Meetup(String id, String description, String name, String address, String startTime, String rsvpStatus) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.address = address;
        this.startTime = startTime;
        this.rsvpStatus = rsvpStatus;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getRsvpStatus() {
        return rsvpStatus;
    }
}

