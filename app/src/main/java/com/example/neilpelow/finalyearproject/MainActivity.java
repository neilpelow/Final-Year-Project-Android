package com.example.neilpelow.finalyearproject;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks {

    public ArrayList<Event> eventList = new ArrayList<>();
    public Button logoutButton;
    public Location mLastLocation;

    private ListView mListView;
    private List<HashMap<String, String>> mEventMapList = new ArrayList<>();


    private DBHandler myDbHandler = new DBHandler(this);

    /**
     * ATTENTION: This "addApi(AppIndex.API)" was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */,
                    (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
            .addApi(Drive.API)
            .addScope(Drive.SCOPE_FILE)
            .addApi(AppIndex.API).build();



    private String idKey = "KEY_ID";
    private String descKey = "KEY_DESC";
    private String nameKey = "KEY_NAME";
    private String addressKey = "KEY_ADDRESS";
    private String startTimeKey = "KEY_STARTTIME";
    private String rsvpKey = "KEY_RSVP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateMeetUpActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        logoutButton = (Button) findViewById(R.id.logoutButton);

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

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    //Build request URL with params.
    String latitude = getLatitude(mLastLocation);
    String longitude = getLongitude(mLastLocation);
    //String distance = MainActivity.getDistance();

    String latitudeUrl = "lat=" + latitude +"&";
    String longitudeUrl = "lat=" + longitude + "&";
    //String distanceUrl =  "distance" + distance +"&";
    String accessTokenUrl = "accessToken=" + AccessToken.getCurrentAccessToken();

    public String getLatitude(Location location) {
        String latitude = String.valueOf(location.getLatitude());
        return latitude;
    }

    public String getLongitude(Location location) {
        String longitude = String.valueOf(location.getLongitude());
        return longitude;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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

        ListAdapter adapter = new SimpleAdapter(MainActivity.this, mEventMapList, R.layout.list_item,
                new String[] {nameKey},
                new int[] {R.id.name});

        mListView.setAdapter(adapter);
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Toast.makeText(this, mEventMapList.get(i).get(idKey),Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
        intent.putExtra("eventIdKey", mEventMapList.get(i).get(idKey));
        intent.putExtra("eventDescKey", mEventMapList.get(i).get(descKey));
        intent.putExtra("eventNameKey", mEventMapList.get(i).get(nameKey));
        intent.putExtra("eventAddressKey", mEventMapList.get(i).get(addressKey));
        intent.putExtra("eventStartTimeKey", mEventMapList.get(i).get(startTimeKey));
        intent.putExtra("eventRSVPKey", mEventMapList.get(i).get(rsvpKey));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutButton) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }
}
