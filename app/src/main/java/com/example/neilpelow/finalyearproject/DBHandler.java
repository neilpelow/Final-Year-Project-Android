package com.example.neilpelow.finalyearproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by neilpelow on 22/12/2016.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "eventsDb";

    private static final String TABLE_EVENTS = "events";

    //Events Table column names
    private static final String KEY_ID = "id";
    private static final String KEY_DESC = "description";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDR = "address";
    private static final String KEY_START = "startTime";
    private static final String KEY_RSVP = "rsvpStatus";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_EVENTS);
        // Creating tables again
        onCreate(db);
    }

    //Inserting Event data into Db
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

}