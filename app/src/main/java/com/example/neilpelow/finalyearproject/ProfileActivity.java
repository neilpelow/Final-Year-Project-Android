package com.example.neilpelow.finalyearproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;

/**
 * Created by neilpelow on 08/11/2016.
 */

public class ProfileActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("Profile","Profile activity loaded");
        if(Profile.getCurrentProfile() != null) {
            getProfileInformation();
            Log.d("Profile","Profile info loaded");
        }
        else {
            Log.d("Profile","Profile info not loaded");
        }
    }

    public static void getProfileInformation(){
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

}
