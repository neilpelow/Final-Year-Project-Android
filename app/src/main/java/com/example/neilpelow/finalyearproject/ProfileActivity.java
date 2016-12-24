package com.example.neilpelow.finalyearproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/*
 * Created by neilpelow on 08/11/2016.
 */

public class ProfileActivity extends AppCompatActivity {
    MobileServiceClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("Profile","Profile activity loaded");
        if(Profile.getCurrentProfile() != null) {
            getProfileInformation();
            getEventInformation();
            Log.d("Profile","Profile and event info loaded");
        }
        else {
            Log.d("Profile","Profile info not loaded");
        }


        try {
            mClient = new MobileServiceClient(
                    "https://c13481318.azurewebsites.net",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void getEventInformation(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + profile.getId(),
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        //Log user event information.
                        try{
                            JSONObject resultsJSON = response.getJSONObject();
                            JSONObject eventsJSON = resultsJSON.getJSONObject("events");
                            JSONArray dataJSON = eventsJSON.getJSONArray("data");

                            //Use list here to instantiate the correct number of event objects.
                            for(int i = 0; i < dataJSON.length(); i++) {
                                JSONObject eventObject = dataJSON.getJSONObject(i);
                                Event event = new Event();
                                //Get each attribute of JSON event object and pass into Event object.
                                //Add error checking to this later.
                                event.id = eventObject.getString("id");
                                event.description = eventObject.getString("description");

                                //Display in list view.


                                //Insert event to Db.
                                insertDataToCloudDB(event);


                            }
                        } catch (Exception e){
                            //Opps
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "events");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void insertDataToCloudDB(Event event) {
        mClient.getTable(Event.class).insert(event, new TableOperationCallback<Event>() {
        public void onCompleted(Event entity, Exception exception, ServiceFilterResponse response) {
            if (exception == null) {
                // Insert succeeded
                Log.d("Db", "onCompleted: Insert completed");
            } else {
                // Insert failed
                Log.d("Db", "onCompleted: Insert failed");
            }
        }
    });
}
}
