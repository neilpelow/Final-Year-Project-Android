package com.example.neilpelow.finalyearproject;

/**
 * Created by neilpelow on 15/11/2016.
 */

public class Event {
    public int id;
    public String description;
    public String name;
    public String address;
    public String startTime;
    public String rsvpStatus;

    public Event() {;

    }

    public Event(int id, String description, String name, String address, String startTime, String rsvpStatus) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.address = address;
        this.startTime = startTime;
        this.rsvpStatus = rsvpStatus;
    }

    public int getId() {
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
