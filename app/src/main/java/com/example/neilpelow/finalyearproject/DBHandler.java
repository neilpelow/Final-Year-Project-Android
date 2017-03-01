package com.example.neilpelow.finalyearproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by neilpelow on 22/12/2016.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "eventsDb";

    private static final String TABLE_EVENTS = "events";

    private static final String TABLE_MEETUPS = "meetups";

    private static final String TABLE_VENUES = "venues";

    private static final String TABLE_USERS = "users";

    //Events Table column names
    private static final String KEY_ID = "id";
    private static final String KEY_DESC = "description";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDR = "address";
    private static final String KEY_START = "startTime";
    private static final String KEY_RSVP = "rsvpStatus";

    //Venues Table column names
    private static final String KEY_VENUEID = "id";
    private static final String KEY_VENUENAME = "name";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";

    //Users Table column names
    private static final String KEY_USERID = "id";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //------------------------------------CREATE STATEMENTS-------------------------------------//

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY " + " UNIQUE, "
                + KEY_DESC + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_ADDR + " TEXT, "
                + KEY_START + " TEXT, "
                + KEY_RSVP + " TEXT "
                + ")";
        try {
            db.execSQL(CREATE_EVENTS_TABLE);
        } catch (SQLiteException e) {
            Log.d("DB", "Events table already exists");
        }


        String CREATE_MEETUP_TABLE = "CREATE TABLE " + TABLE_MEETUPS
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY " + " UNIQUE, "
                + KEY_DESC + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_ADDR + " TEXT, "
                + KEY_START + " TEXT, "
                + KEY_RSVP + " TEXT "
                + ")";
        try {
            db.execSQL(CREATE_MEETUP_TABLE);
        } catch (SQLiteException e) {
            Log.d("DB", "Meet Ups table already exists");
        }

        String CREATE_VENUES_TABLE = "CREATE TABLE " + TABLE_VENUES
                + "("
                + KEY_VENUEID + " INTEGER PRIMARY KEY " + " UNIQUE, "
                + KEY_VENUENAME + " TEXT, "
                + KEY_LONGITUDE + " TEXT, "
                + KEY_LATITUDE + " TEXT "
                + ")";
        try {
            db.execSQL(CREATE_VENUES_TABLE);
        } catch (SQLiteException e) {
            Log.d("DB", "Venues table already exists");
        }

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS
                + "("
                + KEY_USERID + " INTEGER PRIMARY KEY " + " UNIQUE, "
                + ")";
        try {
            db.execSQL(CREATE_USERS_TABLE);
        } catch (SQLiteException e) {
            Log.d("DB", "Users table already exists");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //Not sure if I really want to do this...
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS + TABLE_MEETUPS + TABLE_VENUES + TABLE_USERS + ";");
        // Creating tables again
        onCreate(db);
    }

    //------------------------------------INSERT STATEMENTS-------------------------------------//

    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, event.getId());              //Event ID primary key
        values.put(KEY_DESC, event.getDescription());   //Event description
        values.put(KEY_NAME, event.getName());          //Event name
        values.put(KEY_ADDR, event.getAddress());       //Event address
        values.put(KEY_START, event.getStartTime());    //Event start time
        values.put(KEY_RSVP, event.getRsvpStatus());    //Event RSVP status
        // Inserting row
        db.insert(TABLE_EVENTS, null, values);
        String log = "Id: " + event.getId() + " ,Name: " + event.getName();
        Log.d("DB",log);
        db.close();
    }

    public void addMeetup(Meetup meetup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, meetup.getId());
        values.put(KEY_DESC, meetup.getDescription());
        values.put(KEY_NAME, meetup.getName());
        values.put(KEY_ADDR, meetup.getAddress());
        values.put(KEY_START, meetup.getStartTime());
        values.put(KEY_RSVP, meetup.getRsvpStatus());
        // Inserting row
        db.insert(TABLE_MEETUPS, null, values);
        String log = "Id: " + meetup.getId() + " ,Name: " + meetup.getName();
        Log.d("DB Meet Up",log);
        db.close();
    }

    public void addVenue(Venue venue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VENUEID,venue.getId());
        values.put(KEY_VENUENAME, venue.getName());
        values.put(KEY_LONGITUDE, venue.getLongitude());
        values.put(KEY_LATITUDE, venue.getLatitude());
        // Inserting row
        db.insert(TABLE_VENUES,null, values);
        String log = "Id: " + venue.getId() + " Name: " + venue.getName();
        Log.d("DB", log);
        db.close();
    }


    //------------------------------------SELECT STATEMENTS-------------------------------------//

    public Event retrieveAllEvents(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Event event = new Event();
        String query = "SELECT * FROM TABLE_EVENTS WHERE " +  " EQUALS "
                + id
                + ";";
        db.execSQL(query);


        return event;
    }

    public Venue retrieveAllVenues(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Venue venue = new Venue();
        String query = "SELECT * FROM TABLE_VENUES WHERE " +  " EQUALS "
                + id
                + ";";
        db.execSQL(query);


        return venue;
    }

}