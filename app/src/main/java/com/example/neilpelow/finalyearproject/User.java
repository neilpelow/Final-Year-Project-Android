package com.example.neilpelow.finalyearproject;

import android.util.Log;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by neilpelow on 14/11/2016.
 */

public class User {
    public String userId;
    public String username;
    public String eventId;

    public User() {

    }

    public User(String userId, String username, String eventId) {
        this.userId = userId;
        this.username = username;
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return username;
    }

    public String getEventId() {
        return eventId;
    }
}