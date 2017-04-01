package com.example.neilpelow.finalyearproject;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.FacebookSdk;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, LocationListener {

    public ArrayList<Meetup> meetupList = new ArrayList<>();
    public Button logoutButton;

    private ListView mListView;
    private List<HashMap<String, String>> mMeetupMapList = new ArrayList<>();

    private DBHandler myDbHandler = new DBHandler(this);
    private LocationManager locationManager;

    private String idKey = "KEY_ID";
    private String descKey = "KEY_DESC";
    private String nameKey = "KEY_NAME";
    private String addressKey = "KEY_ADDRESS";
    private String startTimeKey = "KEY_STARTTIME";
    private String rsvpKey = "KEY_RSVP";

    static final Integer LOCATION = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Profile profile = Profile.getCurrentProfile();
        if(profile == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

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

        prepareMeetupList();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = getLocation(locationManager);
            if(myLocation != null) {
                double lat = myLocation.getLatitude();
                double lng = myLocation.getLongitude();
                String latitude = Double.toString(lat);
                String longitude = Double.toString(lng);
                //Hard Code for now
                String distance = "1000";
                String accessToken = GraphApi.getAccessToken();

                accessToken = accessToken.replace("{AccessToken token:", "");
                accessToken = accessToken.replace(" permissions:[public_profile, user_friends, user_events, user_status]}", "");


                sendRequest(latitude, longitude, distance, accessToken);

                //RE data creation
                AsyncT asyncT = new AsyncT();
                asyncT.execute();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareMeetupList() {
        meetupList = myDbHandler.getAllMeetups();
        Recommend rec = myDbHandler.getAllRecommendations();
        if(rec.id1 != null && rec.id2 != null && rec.id3 != null && rec.id4 != null && rec.id5 != null && rec.id6 != null) {

            ArrayList<Event> newEventList = new ArrayList<>();

            Event event1 = myDbHandler.getEventById(rec.id1);
            if(event1 != null) {
                newEventList.add(event1);
            }

            Event event2 = myDbHandler.getEventById(rec.id2);
            if(event2 != null) {
                newEventList.add(event2);
            }

            Event event3 = myDbHandler.getEventById(rec.id3);
            if(event3 != null) {
                newEventList.add(event3);
            }

            Event event4 = myDbHandler.getEventById(rec.id4);
            if(event4 != null) {
                newEventList.add(event4);
            }

            Event event5 = myDbHandler.getEventById(rec.id5);
            if(event5 != null) {
                newEventList.add(event5);
            }

            Event event6 = myDbHandler.getEventById(rec.id6);
            if(event6 != null) {
                newEventList.add(event6);
            }

            for (Event event: newEventList) {
                if(event.id != null){
                    Meetup meetup = new Meetup(event.id, event.description, event.name, event.address, event.startTime, event.rsvpStatus);
                    meetupList.add(meetup);
                }
            }
        }
        onLoaded(meetupList);
    }

    public Location getLocation(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        }
        String locationProvider = LocationManager.NETWORK_PROVIDER;

        return locationManager.getLastKnownLocation(locationProvider);
    }

    public void onLoaded(ArrayList<Meetup> meetupList) {

        for (Meetup meetup : meetupList) {

            HashMap<String, String> map = new HashMap<>();

            map.put(nameKey, meetup.getName());
            map.put(idKey, meetup.getId());
            map.put(startTimeKey, meetup.getStartTime());
            map.put(rsvpKey, meetup.getRsvpStatus());

            mMeetupMapList.add(map);
        }

        loadListView();
    }

    private void loadListView() {

        ListAdapter adapter = new SimpleAdapter(MainActivity.this, mMeetupMapList, R.layout.list_item,
                new String[] {nameKey},
                new int[] {R.id.name});

        mListView.setAdapter(adapter);
    }

    //Location based event search
    public void sendRequest(String latitude, String longitude, String distance, String accessToken) {
        AndroidNetworking.get("http://46.101.31.182:3000/events")
                .addQueryParameter("lat", latitude)
                .addQueryParameter("lng", longitude)
                .addQueryParameter("distance",distance)
                .addQueryParameter("accessToken", accessToken)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("Event", response.toString());
                        try {
                            JSONArray dataJSONArray = response.getJSONArray("events");

                            for(int i = 0; i < dataJSONArray.length(); i++){
                                JSONObject event = dataJSONArray.getJSONObject(i);

                                createEventObject(event);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("Event", error.toString());
                    }
                });
    }

    private void createEventObject (JSONObject event) {
        //Get event object values
        final Event myEvent = new Event();
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

            if(!event.isNull("address")) {
                myEvent.rsvpStatus = event.getString("address");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Always save Event to Db even if it is an old event which will not be shown in list view.
        //This is very much not okay anymore...
        saveEventToDb(myEvent);

        //Event access form within inner class so needs to be made final.
        final Event myFinalEvent = myEvent;
        getUserObjectsForEvent(myFinalEvent);
    }

    public void getUserObjectsForEvent(Event myFinalEvent) {
        LoadJSON j = new LoadJSON();
        final Event myOtherFinalEvent = myFinalEvent;
        j.getFriendsAttendingEvent( myFinalEvent, new Callback()  {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCompleted(Object data) throws JSONException {
                GraphResponse response = (GraphResponse) data;
                String stringResponse = response.getRawResponse();
                Log.d("Event", stringResponse);
                JSONObject json = new JSONObject(stringResponse);
                try {
                    JSONArray dataJSONArray = json.getJSONArray("data");

                    for(int i = 0; i < dataJSONArray.length(); i++){
                        JSONObject user = dataJSONArray.getJSONObject(i);

                        createUserObject(user, myOtherFinalEvent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createUserObject(JSONObject user, Event event) {
        User myUser = new User();
        try {
            if(!user.isNull("name")) {
                myUser.username = user.getString("name");
            }

            if(!user.isNull("id")) {
                myUser.userId = user.getString("id");
            }

            if(event.getId() != null) {
                myUser.eventId = event.getId();
                myUser.attending = "1";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveUserToDb(myUser);
        //Send JSON to Django server for RE here.
    }

    private void saveUserToDb(User user) {
        myDbHandler.addUser(user);
        Log.d("User", "User added to Db: " + user.username);
    }

    private void saveEventToDb(Event event) {
        myDbHandler.addEvent(event);
        Log.d("Event", "Event added to Db: " + event.name);
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Toast.makeText(this, mMeetupMapList.get(i).get(idKey),Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
        intent.putExtra("eventIdKey", mMeetupMapList.get(i).get(idKey));
        intent.putExtra("eventDescKey", mMeetupMapList.get(i).get(descKey));
        intent.putExtra("eventNameKey", mMeetupMapList.get(i).get(nameKey));
        intent.putExtra("eventAddressKey", mMeetupMapList.get(i).get(addressKey));
        intent.putExtra("eventStartTimeKey", mMeetupMapList.get(i).get(startTimeKey));
        intent.putExtra("eventRSVPKey", mMeetupMapList.get(i).get(rsvpKey));
        startActivity(intent);
    }

    //Location methods
    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Location Services disabled",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "Location Services enabled",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    //App drawer code. Auto generated.
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

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
        return myEvent;
    }

    /* Inner class to get response */
    private class AsyncT extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //HTTPClient myClient = new HTTPClient();
                //Delete all users not attending an event
                myDbHandler.dropAllNonAttendingUsers();
                //Add my profile user objects to the Db
                getMyUserInfo();
                //Retrieve all users from the db
                ArrayList<User> userArrayList = myDbHandler.getAllUsers();
                ArrayList<Event> eventArrayList = myDbHandler.getAllEvents();

                //Get array list of my user profile objects
                ArrayList<User> myProfileUserArrayList = myDbHandler.getMyProfileUserObjects();

                //Limit number of elements in list.
                if(userArrayList.size() > 500) {
                    userArrayList = new ArrayList<>(userArrayList.subList(1,550));
                }

                //Add all my profile users back in to the Array list that might have been removed in above step.
                //Duplicates will not be added to the final list.
                userArrayList.addAll(myProfileUserArrayList);
                Set<User> hs = new HashSet<>();
                hs.addAll(userArrayList);
                userArrayList.clear();
                userArrayList.addAll(hs);

                //Create users that are not attending each event. Db constraints will prevent errors due to a user attending multiple events.
                ArrayList<User> notAttendingList = createNotAttendingUsers(userArrayList, eventArrayList);
                userArrayList.addAll(notAttendingList);
                //Create JSON for each user. Both attending and not attending.
                JSONArray usersJSONArray = new JSONArray();

                for (User user:userArrayList) {
                    JSONObject jsonUser = new JSONObject();
                    jsonUser.put("userId", user.userId);
                    jsonUser.put("eventId", user.eventId);
                    jsonUser.put("attending", user.attending);
                    usersJSONArray.put(jsonUser);
                }

                JSONObject json = new JSONObject();
                json.put("data",usersJSONArray);
                String response = HTTPClient.POST(json);
                //Clean JSON string.
                String userRecommendations = removeBackslash(response);
                //Create JSON object for each user's recommendations.
                userRecommendations = userRecommendations.replace("\\\"","'");
                JSONObject userRecommendationsJSON = new JSONObject(userRecommendations.substring(1,userRecommendations.length()-1));
                //Parse JSON object for my user recommendations.
                getMyRecommendations(userRecommendationsJSON);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //":{\"userId\":
        public String removeBackslash(String response) {
            String userRecommendations = response.replace("\\","");
            return userRecommendations;
        }

        public void getMyRecommendations(JSONObject userRecJSON) {
            int i;
            User user = new User();
            final Profile profile = Profile.getCurrentProfile();

            //Delete Recommendations before new recommendations are saved.
            if(myDbHandler.getAllRecommendations() != null && userRecJSON != null){
                myDbHandler.dropAllRecommendations();
            }

            try {
                //Loop through JSON response until User info is found by matched userId.
                for(i = 0; i < 1000; i++) {
                    JSONObject myUserRecJSON = userRecJSON.getJSONObject(Integer.toString(i));
                    user.userId = myUserRecJSON.getString("userId");
                    if(user.userId.equals(profile.getId())) {
                        //Get recommendations and save to DB for display later.
                        Recommend rec = new Recommend();
                        rec.id1 = myUserRecJSON.getString("1");
                        rec.id2 = myUserRecJSON.getString("2");
                        rec.id3 = myUserRecJSON.getString("3");
                        rec.id4 = myUserRecJSON.getString("4");
                        rec.id5 = myUserRecJSON.getString("5");
                        rec.id6 = myUserRecJSON.getString("6");
                        myDbHandler.addRecommend(rec);
                        return;
                    }
                }
            } catch(JSONException e) {
                Log.d("RE", "Stuff happened. Don't care.");
                return;
            }
        }

        public ArrayList<User> createNotAttendingUsers(ArrayList<User> userList, ArrayList<Event> eventList) {
            ArrayList<User> newUserList = new ArrayList<>();
            for (User user : userList) {
                for (Event event : eventList) {
                    if (user.eventId != event.id) {
                        User newUser = new User();
                        newUser.eventId = event.id;
                        newUser.userId = user.userId;
                        newUser.username = user.username;
                        newUser.attending = "0";
                        newUserList.add(newUser);
                    }
                }
            }
            return newUserList;
        }

        /*
        Get user profile.
        Get each event the user is attending.
        Add all these events to the Db.
        Create User object for each event.
        Save these Users to the Db.
         */
        public void getMyUserInfo(){
            User user = new User();
            final Profile profile = Profile.getCurrentProfile();
            user.userId = profile.getId();
            LoadJSON j = new LoadJSON();
            j.getUserProfileEvents(user, new Callback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCompleted(Object data) throws JSONException {
                    GraphResponse response = (GraphResponse) data;
                    String stringResponse = response.getRawResponse();
                    JSONObject json = new JSONObject(stringResponse);
                    try {
                        //JSONArray to hold all my profile users
                        JSONArray dataJSONArray = json.getJSONArray("data");
                        for(int i = 0; i < dataJSONArray.length(); i++){
                            JSONObject event = dataJSONArray.getJSONObject(i);
                            Event myEvent = new Event();
                            myEvent = createEventList(event, myEvent);
                            User user = new User();
                            user.userId = profile.getId();
                            user.eventId = myEvent.id;
                            user.attending = "1";
                            saveUserToDb(user);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
