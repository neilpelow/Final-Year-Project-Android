package com.example.neilpelow.finalyearproject; /**
 * Created by neilpelow on 23/01/2017.
 */


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import com.example.neilpelow.finalyearproject.Event;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.System.in;

public class LoadJSON {

    private ProfileTracker mProfileTracker;

    public void loadJSON(final Callback callback) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();

        if(Profile.getCurrentProfile() == null) {
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    Log.v("facebook - profile", profile2.getFirstName());
                    mProfileTracker.stopTracking();
                    final AccessToken accessToken1 = accessToken;

                    profile = profile2;

                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            accessToken1,
                            "/" + profile.getId(),
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response){
                                    try {
                                        callback.onCompleted(response);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "events");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            };
            // no need to call startTracking() on mProfileTracker
            // because it is called by its constructor, internally.
        }
        else {
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    "/" + profile.getId(),
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response){
                            try {
                                callback.onCompleted(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            Bundle parameters = new Bundle();
            parameters.putString("fields", "events");
            request.setParameters(parameters);
            request.executeAsync();
        }


    }
}
