package com.example.neilpelow.finalyearproject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;

/**
 * Created by neilpelow on 26/10/2016.
 */

public class GraphApi {

    public static void getUserInformation(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + profile.getId(),
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        //Log user information
                        Log.d("Graph","Profile info successfully collected");
                        Log.d("Graph",response.getRawResponse());
                        Log.d("Graph",response.getRequest().toString());
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,events");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static void getFriendList(AccessToken accessToken) {
        new GraphRequest(
                accessToken,
                "/{friend-list-id}",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //Handle result.
                        String myResponse = response.toString();
                        //Log.d("GraphApi", response.toString());
                    }
                }
        ).executeAsync();
    }
}
