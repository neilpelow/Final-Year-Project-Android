package com.example.neilpelow.finalyearproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static com.example.neilpelow.finalyearproject.Event.createEventList;


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

        meetupList = myDbHandler.getAllMeetups();
        onLoaded(meetupList);

        //Get params for
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

            accessToken = accessToken.substring(19,234);

            sendRequest(latitude, longitude, distance, accessToken);

            AsyncT asyncT = new AsyncT();
            asyncT.execute();
        }
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


    public Location getLocation(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        }
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Or use LocationManager.NETWORK_PROVIDER

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Always save Event to Db even if it is an old event which will not be shown in list view.
        //This is very much not okay anymore...
        saveEventToDb(myEvent);
        //Get list of users attending each event.
        //------------------------------------------ DON'T DO THIS ON THE MAIN THREAD YOU STUPID MORON!!!! ------------------------------------------//
        //GraphApi.getFriendsAttendingEvent(AccessToken.getCurrentAccessToken(), myEvent);

        //Event access form within inner class so needs to be made final.
        final Event myFinalEvent = myEvent;
        getUserObject(myFinalEvent);
    }

    public void getUserObject(Event myFinalEvent) {
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

    /* Inner class to get response */
    private class AsyncT extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            //HttpPost httppost = new HttpPost("<YOUR_SERVICE_URL>");

            try {

                ArrayList<User> userArrayList = myDbHandler.getAllUsers();
                ArrayList<Event> eventArrayList = myDbHandler.getAllEvents();

                ArrayList<JSONUser> jsonUserList = createJSONUsers(userArrayList, eventArrayList);

                JSONArray userJSONArray = new JSONArray();

                for (JSONUser jsonUser: jsonUserList) {
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("name", jsonUser.userId);
                    jsonobj.put("id", jsonUser.eventId);
                    jsonobj.put("attending", jsonUser.attending);
                    userJSONArray.put(jsonobj);
                }

                Log.d("JSONArray", userJSONArray.toString());

                /*
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("req", jsonobj.toString()));

                Log.e("mainToPost", "mainToPost" + nameValuePairs.toString());

                // Use UrlEncodedFormEntity to send in proper format which we need
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                */


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public ArrayList<JSONUser> createJSONUsers(ArrayList<User> userList, ArrayList<Event> eventList){
            ArrayList<JSONUser> jsonUsersList = new ArrayList<>();
            for (Event event: eventList) {
                for (User user: userList) {
                    JSONUser jsonUser = new JSONUser();
                    jsonUser.eventId = event.getId();
                    jsonUser.userId = user.getUserId();
                    if(setAttending(jsonUser) == true) {
                        jsonUser.attending = true;
                    } else {
                        jsonUser.attending = false;
                    }
                    jsonUsersList.add(jsonUser);
                }
            }
            return jsonUsersList;
        }

        public boolean setAttending(JSONUser jsonUser) {
            if(myDbHandler.isUserAttendingEvent(jsonUser.userId, jsonUser.eventId) != null) {
                return true; //User is attending.
            } else {
                return false; //User is not attending.
            }
        }
    }
}
