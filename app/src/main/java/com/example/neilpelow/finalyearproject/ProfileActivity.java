package com.example.neilpelow.finalyearproject;

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

import static com.facebook.internal.FacebookRequestErrorClassification.KEY_NAME;
import static java.util.Objects.isNull;

/*
 * Created by neilpelow on 08/11/2016.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView mListView;
    private List<HashMap<String, String>> mEventMapList = new ArrayList<>();

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
                //TODO: Parse response String into event list to be passed to onLoaded method.
                try {
                    JSONObject eventJSONObject = json.getJSONObject("events");
                    JSONArray dataJSONArray = eventJSONObject.getJSONArray("data");

                    List<Event> eventList = new ArrayList<Event>();

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



    public void onLoaded(List<Event> eventList) {

        for (Event event : eventList) {

            HashMap<String, String> map = new HashMap<>();

            map.put(KEY_NAME, event.getName());

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
                myEvent.id = event.getInt("id");
            }

            if(!event.isNull("description")) {
                myEvent.description = event.getString("description");
            }

            if(!event.isNull("address")) {
                myEvent.address = event.getString("address");
            }

            if(!event.isNull("startTime")) {
                myEvent.startTime = event.getString("startTime");
            }

            if(!event.isNull("rsvpStatus")) {
                myEvent.rsvpStatus = event.getString("rsvpStatus");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myEvent;
    }


    private void loadListView() {

        ListAdapter adapter = new SimpleAdapter(ProfileActivity.this, mEventMapList, R.layout.list_item,
                new String[] {KEY_NAME},
                new int[] {R.id.name});

        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Toast.makeText(this, mEventMapList.get(i).get(KEY_NAME),Toast.LENGTH_LONG).show();
    }
}
