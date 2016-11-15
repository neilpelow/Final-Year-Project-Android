package com.example.neilpelow.finalyearproject;

import android.util.Log;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by neilpelow on 14/11/2016.
 */

public class User {
    boolean loggedIn = false;

    String name;

    public User setLoginToFalse(User mUser ) {
        mUser.loggedIn = false;
        return mUser;
    }

    public boolean UserLoggedIn() {
        return loggedIn = true;
    }

    public boolean isUserLoggedIn(User mUser) {
        if(mUser.loggedIn == true){
            return true;
        } else {
            return false;
        }
    }
}
