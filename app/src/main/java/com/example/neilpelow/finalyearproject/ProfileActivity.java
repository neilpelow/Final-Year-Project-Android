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

import com.facebook.AccessToken;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/*
 * Created by neilpelow on 08/11/2016.
 */

//TODO: Get json for event venues and load into objects for storage in Db.

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView mListView;
    private List<HashMap<String, String>> mEventMapList = new ArrayList<>();
    public ArrayList<Event> eventList = new ArrayList<>();
    private DBHandler myDbHandler = new DBHandler(this);
    private String idKey = "KEY_ID";
    private String nameKey = "KEY_NAME";
    private String startTimeKey = "KEY_STARTTIME";
    private String rsvpKey = "KEY_RSVP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);

        AccessToken at =AccessToken.getCurrentAccessToken();

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
                        myEvent = createEventList(event, myEvent);
                        //If event is upcoming add to eventList.
                        if(checkIfUpcomingEvent(myEvent)) {
                            eventList.add(myEvent);
                        }
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

            map.put(nameKey, event.getName());
            map.put(idKey, event.getId());
            map.put(startTimeKey, event.getStartTime());
            map.put(rsvpKey, event.getRsvpStatus());

            mEventMapList.add(map);
        }

        loadListView();
    }

    //Method returns true if the start datetime of an event is after the current datetime.
    private boolean checkIfUpcomingEvent(Event event){
        String startTime = event.getStartTime();
        if(!(startTime == null)){

            //Get and format Event start time.
            startTime = startTime.replaceAll("[^\\d.]", "");
            startTime = removeTrailingChars(startTime);
            long numStartTime = Long.parseLong(startTime);
            
            //Get and format current time.
            Calendar c = Calendar.getInstance();
            DateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmm");
            String datetime = dateformat.format(c.getTime());
            long numDateTime = Long.parseLong(datetime);

            if(numStartTime > numDateTime){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    //Returns datetime string accurate to the nearest minute.
    public String removeTrailingChars(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length()-6);
        }
        return str;
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
                myEvent.startTime = event.getString("start_time");
            }

            if(!event.isNull("rsvpStatus")) {
                myEvent.rsvpStatus = event.getString("rsvpStatus");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Always save Event to Db even if it is an old event which will not be shown in list view.
        saveEventToDb(myEvent);
        return myEvent;
    }

    private void saveEventToDb(Event event) {
        myDbHandler.addEvent(event);
    }

    private void saveVenueToDb(Venue venue) {
        myDbHandler.addVenue(venue);
    }


    private void loadListView() {

        ListAdapter adapter = new SimpleAdapter(ProfileActivity.this, mEventMapList, R.layout.list_item,
                new String[] {nameKey},
                new int[] {R.id.name});

        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Toast.makeText(this, mEventMapList.get(i).get(idKey),Toast.LENGTH_LONG).show();


        Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
        intent.putExtra("eventIdKey", mEventMapList.get(i).get(idKey));
        intent.putExtra("eventNameKey", mEventMapList.get(i).get(nameKey));
        intent.putExtra("eventStartTimeKey", mEventMapList.get(i).get(startTimeKey));
        intent.putExtra("eventRSVPKey", mEventMapList.get(i).get(rsvpKey));
        startActivity(intent);
    }
}
