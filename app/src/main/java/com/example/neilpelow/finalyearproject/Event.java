package com.example.neilpelow.finalyearproject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by neilpelow on 15/11/2016.
 */

public class Event {
    public String id;
    public String description;
    public String name;
    public String address;
    public String startTime;
    public String rsvpStatus;

    public Event() {

    }

    public Event(String id, String description, String name, String address, String startTime, String rsvpStatus) {
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

    public static Event createEventList (JSONObject event, Event myEvent) {
        //Get event object values
        try {
            if(!event.isNull("name")) {
                myEvent.name = event.getString("name");
            }

            if(!event.isNull("id")) {
                myEvent.id = event.getString("id");
            }

            if(!event.isNull("description")) {
                myEvent.description = event.getString("description");
            }

            if(!event.isNull("start_time")) {
                myEvent.startTime = event.getString("start_time");
            }

            if(!event.isNull("rsvpStatus")) {
                myEvent.rsvpStatus = event.getString("rsvpStatus");
            }

            if(!event.isNull("address")) {
                myEvent.address = event.getString("address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myEvent;
    }
}
