package com.example.neilpelow.finalyearproject;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;

import static android.support.customtabs.CustomTabsIntent.KEY_ID;
import static com.example.neilpelow.finalyearproject.R.id.button;

public class CreateActivity extends Activity {

    private TextView eventId;
    private EditText editTextName;
    private EditText editTextStartTime;
    private EditText editTextRSVP;

    private Button createButton;
    private Button cancelButton;

    private DBHandler myDbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        myDbHandler = new DBHandler(this);

        eventId = (TextView) findViewById(R.id.eventIdTextView);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextStartTime = (EditText) findViewById(R.id.editTextStartTime);
        //editTextRSVP = (EditText) findViewById(R.id.editTextRSVP);

        String eventIdString = "eventIdKey";
        String eventDescString = "eventDescKey";
        String eventNameString = "eventNameKey";
        String eventAddressString = "eventAddressKey";
        String eventStartTimeString = "eventStartTimeKey";
        String eventRSVPString = "eventRSVPKey";

        eventId.setText(unpackExtra(eventIdString));
        editTextName.setText(unpackExtra(eventNameString));
        editTextStartTime.setText(unpackExtra(eventStartTimeString));
        //editTextRSVP.setText(unpackExtra(eventRSVPString));


        //Get fields from selected Event in previous Activity (Create Meet Up).
        Event event = new Event();
        event.id = unpackExtra(eventIdString);
        event.description = unpackExtra(eventDescString);
        event.name = unpackExtra(eventNameString);
        event.address = unpackExtra(eventAddressString);
        event.startTime = unpackExtra(eventStartTimeString);
        event.rsvpStatus = unpackExtra(eventRSVPString);

        //Create Meet up and populate with fields from Event
        Meetup meetup = new Meetup();
        meetup = populateMeetUp(event, meetup);
        //Store Meet up in local Db.
        final Meetup meetupFinal = meetup;

        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Store newly created Meet Up in DB.
                SQLiteDatabase  db = myDbHandler.getWritableDatabase();
                myDbHandler.addMeetup(meetupFinal);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public Meetup populateMeetUp(Event event, Meetup meetup) {
        meetup.id = event.getId();
        meetup.description = event.getDescription();
        meetup.name = event.getName();
        meetup.address = event.getAddress();
        meetup.startTime = event.getStartTime();
        meetup.rsvpStatus = event.getRsvpStatus();
        return meetup;
    }

    private String unpackExtra(String textViewString) {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            textViewString = null;
        } else {
            textViewString = extras.getString(textViewString);
        }
        return textViewString;
    }
}
