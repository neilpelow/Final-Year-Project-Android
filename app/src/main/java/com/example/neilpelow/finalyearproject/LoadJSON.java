package com.example.neilpelow.finalyearproject; /**
 * Created by neilpelow on 23/01/2017.
 */


import android.os.AsyncTask;
import android.os.Bundle;


import com.example.neilpelow.finalyearproject.Event;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
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

import static java.lang.System.in;

public class LoadJSON {

    public void loadJSON(final Callback callback) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();

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
