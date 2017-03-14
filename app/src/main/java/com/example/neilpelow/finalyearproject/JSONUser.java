package com.example.neilpelow.finalyearproject;

/**
 * Created by neilpelow on 13/03/2017.
 */

class JSONUser {
    public String userId;
    public String eventId;
    public boolean attending;

    public JSONUser() {

    }

    public JSONUser(String userId, String eventId, boolean attending) {
        this.userId = userId;
        this.eventId = eventId;
        this.attending = attending;
    }

    public String getUserId() {
        return userId;
    }
    public String getEventId() {
        return eventId;
    }
    public boolean getAttending() {
        return attending;
    }
}
