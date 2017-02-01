package com.example.neilpelow.finalyearproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.customtabs.CustomTabsIntent.KEY_ID;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.internal.FacebookRequestErrorClassification.KEY_NAME;
import static java.util.Objects.isNull;

/*
 * Created by neilpelow on 08/11/2016.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView mListView;
    private List<HashMap<String, String>> mEventMapList = new ArrayList<>();
    public ArrayList<Event> eventList = new ArrayList<>();
    private DBHandler myDbHandler = new DBHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);

        LoadJSON j = new LoadJSON();
        j.loadJSON(new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCompleted(Object data) throws JSONException {
                GraphResponse response = (GraphResponse) data;
                String stringResponse = response.getRawResponse();
                JSONObject json = new JSONObject(stringResponse);
                try {
                    JSONObject eventJSONObject = json.getJSONObject("events");
                    JSONArray dataJSONArray = eventJSONObject.getJSONArray("data");

                    //List<Event> eventList = new ArrayList<Event>();

                    for(int i = 0; i < dataJSONArray.length(); i++){
                        JSONObject event = dataJSONArray.getJSONObject(i);
                        Event myEvent = new Event();
                        createEventList(event, myEvent);
                        eventList.add(myEvent);
                    }
                    onLoaded(eventList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void onLoaded(ArrayList<Event> eventList) {

        for (Event event : eventList) {

            HashMap<String, String> map = new HashMap<>();

            map.put(KEY_NAME, event.getName());
            map.put(KEY_ID, event.getId());

            mEventMapList.add(map);
        }

        loadListView();
    }

    private Event createEventList (JSONObject event, Event myEvent) {
            //Get event object values
        try {
            if(!event.isNull("name")) {
                myEvent.name = event.getString("name");
            }

            if(!event.isNull("id")) {
                myEvent.id = event.getString("id");
            }

            if(!event.isNull("description")) {
                myEvent.description = event.getString("description");
            }

            if(!event.isNull("start_time")) {
                myEvent.startTime = event.getString("startTime");
            }

            if(!event.isNull("rsvpStatus")) {
                myEvent.rsvpStatus = event.getString("rsvpStatus");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveEventToDb(myEvent);
        return myEvent;
    }

    private void saveEventToDb(Event event) {
        myDbHandler.addEvent(event);
    }


    private void loadListView() {

        ListAdapter adapter = new SimpleAdapter(ProfileActivity.this, mEventMapList, R.layout.list_item,
                new String[] {KEY_NAME},
                new int[] {R.id.name});

        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Toast.makeText(this, mEventMapList.get(i).get(KEY_ID),Toast.LENGTH_LONG).show();


        Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
        intent.putExtra("eventIdKey", mEventMapList.get(i).get(KEY_ID));
        intent.putExtra("eventNameKey", mEventMapList.get(i).get(KEY_NAME));
        startActivity(intent);
    }
}
