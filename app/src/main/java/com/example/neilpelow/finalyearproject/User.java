package com.example.neilpelow.finalyearproject;

import android.util.Log;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by neilpelow on 14/11/2016.
 */

public class User {
    public String userId;
    public String username;
    public String DOB;  //may change to "age" depending on format given by FB.
    //Db table for user will include FK for event ID that the user has attended. One-many relationship.
}
