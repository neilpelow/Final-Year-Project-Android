package com.example.neilpelow.finalyearproject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
                        //try {
                        //    parseJSONArray(response);
                        //} catch (JSONException e) {
                        //    e.printStackTrace();
                        //}
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,events");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static void parseJSONArray(GraphResponse response) throws JSONException {
        String str = response.getRawResponse();
        JSONArray jsonArray = new JSONArray(str);

        for(int i =0; i < jsonArray.length(); i ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String desc = jsonObject.getString("description");
            String endTime = jsonObject.getString("end_time");
            String name = jsonObject.getString("name");
            String place = jsonObject.getString("place");
            String id = jsonObject.getString("id");
            String rsvp = jsonObject.getString("rsvp_status");
        }
    }
}
