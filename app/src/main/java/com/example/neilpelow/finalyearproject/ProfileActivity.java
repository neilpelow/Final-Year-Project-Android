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
    TextView mTextView;
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

        mTextView = (TextView) findViewById(R.id.textView);

        try {
            mClient = new MobileServiceClient(
                    "https://c13481318.azurewebsites.net",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void insertDataToCloudDB() {
        final Event event = new Event();
        event.name = "Awesome item";
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

    public static void getEventInformation(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + profile.getId(),
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        //Log user information
                        String result = response.getRawResponse();
                        Log.d("Graph","Event info successfully collected");
                        Log.d("Graph",result);

                        try{
                            JSONObject resultsJSON = new JSONObject(result);
                            JSONObject eventsJSON = new JSONObject(resultsJSON.toString());
                            JSONArray eventsDataJSON = eventsJSON.getJSONArray("data");

                            for(int i = 0; i < eventsDataJSON.length(); i++) {
                                JSONObject eventObject = eventsDataJSON.getJSONObject(i);
                                String eventObjectItem1 = eventObject.getString("description");
                                Log.d("Event", eventObjectItem1);
                            }
                        } catch (Exception e){
                            //Opps
                            Log.d("JSON", "JSON exception");
                        }


                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "events");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
