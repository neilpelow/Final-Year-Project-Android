package com.example.neilpelow.finalyearproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.facebook.Profile;

import java.util.ArrayList;

/**
 * Created by neilpelow on 22/12/2016.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 26;

    private static final String DATABASE_NAME = "eventsDb";

    private static final String TABLE_EVENTS = "events";

    private static final String TABLE_MEETUPS = "meetups";

    private static final String TABLE_VENUES = "venues";

    private static final String TABLE_USERS = "users";

    private static final String TABLE_RECOMMEND = "recommend";

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
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EVENTID = "eventId";
    private static final String KEY_ATTENDING = "attending";

    //Recommend Table column names
    private static final String KEY_ID1 = "id1";
    private static final String KEY_ID2 = "id2";
    private static final String KEY_ID3 = "id3";
    private static final String KEY_ID4 = "id4";
    private static final String KEY_ID5 = "id5";
    private static final String KEY_ID6 = "id6";

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
                + KEY_USERID + " INTEGER, "
                + KEY_USERNAME + " TEXT, "
                + KEY_EVENTID + " INTEGER, "
                + KEY_ATTENDING + " TEXT, "
                + " FOREIGN KEY(" + KEY_EVENTID + ") REFERENCES " + TABLE_EVENTS + "(" + KEY_ID + "), "
                + "UNIQUE(" + KEY_USERID + ", " + KEY_EVENTID + ")"
                + ")";
        try {
            db.execSQL("PRAGMA foreign_keys=ON");
            db.execSQL(CREATE_USERS_TABLE);
        } catch (SQLiteException e) {
            Log.d("DB", "Users table already exists");
        }

        String CREATE_RECOMMEND_TABLE = "CREATE TABLE " + TABLE_RECOMMEND
                + "("
                + KEY_ID1 + " INTEGER, "
                + KEY_ID2 + " INTEGER, "
                + KEY_ID3 + " INTEGER, "
                + KEY_ID4 + " INTEGER, "
                + KEY_ID5 + " INTEGER, "
                + KEY_ID6 + " INTEGER "
                + ")";
        try {
            db.execSQL(CREATE_RECOMMEND_TABLE);
        } catch (SQLiteException e) {
            Log.d("DB", "Recommend table already exists");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //Not sure if I really want to do this...
        //Turns out I do want to. Good Good :)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUES + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETUPS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOMMEND + ";");
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

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID,user.getUserId());
        values.put(KEY_USERNAME, user.getName());
        values.put(KEY_EVENTID, user.getEventId());
        values.put(KEY_ATTENDING, user.getAttending());
        // Inserting row
        try {
            db.insert(TABLE_USERS,null, values);
            String log = "Id: " + user.getUserId() + " Name: " + user.getName();
            Log.d("DB", log);
            db.close();
        } catch(Exception e){
            Log.d("BG", "All is well.");
        }

    }

    public void addRecommend(Recommend rec) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID1,rec.getId1());
        values.put(KEY_ID2,rec.getId2());
        values.put(KEY_ID3,rec.getId3());
        values.put(KEY_ID4,rec.getId4());
        values.put(KEY_ID5,rec.getId5());
        values.put(KEY_ID6,rec.getId6());
        try {
            db.insert(TABLE_RECOMMEND,null, values);
            db.close();
        } catch(Exception e){
            Log.d("BG", "All is well.");
        }
    }


    //------------------------------------SELECT STATEMENTS-------------------------------------//
    public ArrayList<Event> getAllEvents() {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        // Get the instance of the database
        SQLiteDatabase db = this.getReadableDatabase();
        //get the cursor you're going to use
        Cursor cursor = db.rawQuery(selectQuery, null);

        //this is optional - if you want to return one object
        //you don't need a list
        ArrayList<Event> eventList = new ArrayList<Event>();

        //you should always use the try catch statement incase
        //something goes wrong when trying to read the data
        try
        {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    //the .getString(int x) method of the cursor returns the column
                    //of the table your query returned
                    Event event = new Event(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5)
                    );
                    // Adding contact to list
                    eventList.add(event);
                } while (cursor.moveToNext());
            }
        }
        catch (SQLiteException e)
        {
            Log.d("SQL Error", e.getMessage());
            return null;
        }
        finally
        {
            //release all your resources
            cursor.close();
            db.close();
        }
        return eventList;
    }

    public ArrayList<Meetup> getAllMeetups() {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MEETUPS;
        // Get the instance of the database
        SQLiteDatabase db = this.getWritableDatabase();
        //get the cursor you're going to use
        Cursor cursor = db.rawQuery(selectQuery, null);

        //this is optional - if you want to return one object
        //you don't need a list
        ArrayList<Meetup> meetupList = new ArrayList<Meetup>();

        //you should always use the try catch statement incase
        //something goes wrong when trying to read the data
        try
        {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    //the .getString(int x) method of the cursor returns the column
                    //of the table your query returned
                    Meetup meetup= new Meetup(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5)
                            );
                    // Adding contact to list
                    meetupList.add(meetup);
                } while (cursor.moveToNext());
            }
        }
        catch (SQLiteException e)
        {
            Log.d("SQL Error", e.getMessage());
            return null;
        }
        finally
        {
            //release all your resources
            cursor.close();
            db.close();
        }
        return meetupList;
    }
    public Event getEventById(String id) {
        // Select All Query
        String query =
                "SELECT * FROM "
                        + TABLE_EVENTS
                        + " WHERE id = "
                        + id
                        + ";";
        // Get the instance of the database
        SQLiteDatabase db = this.getWritableDatabase();
        //get the cursor you're going to use
        Cursor cursor = db.rawQuery(query, null);
        Event event = new Event();
        try {
            if(cursor.moveToFirst()){
                event = new Event(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
            }
        } catch (SQLiteException e) {
            Log.d("SQL Error", e.getMessage());
            cursor.close();
            db.close();
            return null;
        }
        //release all your resources
        cursor.close();
        db.close();
        return event;
    }

    public Meetup getAllMeetupsById(String id) {
        // Select All Query
        String query =
                "SELECT * FROM "
                        + TABLE_MEETUPS
                        + " WHERE id = "
                        + id
                        + ";";
        // Get the instance of the database
        SQLiteDatabase db = this.getWritableDatabase();
        //get the cursor you're going to use
        Cursor cursor = db.rawQuery(query, null);

        //this is optional - if you want to return one object
        //you don't need a list
        ArrayList<Meetup> meetupList = new ArrayList<Meetup>();

        Meetup meetup = new Meetup();

        //you should always use the try catch statement incase
        //something goes wrong when trying to read the data
        try
        {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    //the .getString(int x) method of the cursor returns the column
                    //of the table your query returned
                    meetup= new Meetup(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5)
                    );
                    // Adding contact to list
                } while (cursor.moveToNext());
            }
        }
        catch (SQLiteException e)
        {
            Log.d("SQL Error", e.getMessage());
            return null;
        }
        finally
        {
            //release all your resources
            cursor.close();
            db.close();
        }
        return meetup;
    }

    public ArrayList<User> getMyProfileUserObjects() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<User> userList = new ArrayList<>();
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        String query =
                "SELECT * FROM "
                        + TABLE_USERS
                        + " WHERE id = "
                        + id
                        + ";";
        Cursor cursor = db.rawQuery(query, null);
        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    //the .getString(int x) method of the cursor returns the column
                    //of the table your query returned
                    User user= new User(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    );
                    // Adding contact to list
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.d("SQL Error", e.getMessage());
            return null;
        }
        //release all your resources
        cursor.close();
        db.close();
        return userList;
    }

    public ArrayList<User> getAllUsers() {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        // Get the instance of the database
        SQLiteDatabase db = this.getWritableDatabase();
        //get the cursor you're going to use
        Cursor cursor = db.rawQuery(selectQuery, null);

        //this is optional - if you want to return one object
        //you don't need a list
        ArrayList<User> userList = new ArrayList<>();

        //you should always use the try catch statement incase
        //something goes wrong when trying to read the data
        try
        {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    //the .getString(int x) method of the cursor returns the column
                    //of the table your query returned
                    User user= new User(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    );
                    // Adding contact to list
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        }
        catch (SQLiteException e)
        {
            Log.d("SQL Error", e.getMessage());
            return null;
        }
        finally
        {
            //release all your resources
            cursor.close();
            db.close();
        }
        return userList;
    }

    public Recommend getAllRecommendations() {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RECOMMEND;
        // Get the instance of the database
        SQLiteDatabase db = this.getWritableDatabase();
        //get the cursor you're going to use
        Cursor cursor = db.rawQuery(selectQuery, null);

        Recommend rec = new Recommend();

        //you should always use the try catch statement incase
        //something goes wrong when trying to read the data
        try
        {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    //the .getString(int x) method of the cursor returns the column
                    //of the table your query returned
                    rec.id1 = cursor.getString(0);
                    rec.id2 = cursor.getString(1);
                    rec.id3 = cursor.getString(2);
                    rec.id4 = cursor.getString(3);
                    rec.id5 = cursor.getString(4);
                    rec.id6 = cursor.getString(5);
                } while (cursor.moveToNext());
            }
        }
        catch (SQLiteException e)
        {
            Log.d("SQL Error", e.getMessage());
            return null;
        }
        finally
        {
            //release all your resources
            cursor.close();
            db.close();
        }
        return rec;
    }

    public void dropAllNonAttendingUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query =
                "DELETE FROM "
                + TABLE_USERS
                + " WHERE attending = 0;";
        db.execSQL(query);
        db.close();
    }

    public void dropAllRecommendations() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String query =
                    "DELETE FROM "
                            + TABLE_RECOMMEND
                            + ";";
            db.execSQL(query);
        } catch(SQLiteException e) {
            Log.d("DB", "No recommendations to delete");
        } finally {
            db.close();
        }
    }
}