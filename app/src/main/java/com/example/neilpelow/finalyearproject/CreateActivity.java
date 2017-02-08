package com.example.neilpelow.finalyearproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import static android.support.customtabs.CustomTabsIntent.KEY_ID;

public class CreateActivity extends Activity {

    private TextView eventId;
    private EditText editTextName;
    private EditText editTextStartTime;
    private EditText editTextRSVP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        eventId = (TextView) findViewById(R.id.eventIdTextView);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextStartTime = (EditText) findViewById(R.id.editTextStartTime);
        //editTextRSVP = (EditText) findViewById(R.id.editTextRSVP);

        String eventIdString = "eventIdKey";
        String eventNameString = "eventNameKey";
        String eventStartTimeString = "eventStartTimeKey";
        //String eventRSVPString = "eventRSVPKey";

        eventId.setText(unpackExtra(eventIdString));
        editTextName.setText(unpackExtra(eventNameString));
        editTextStartTime.setText(unpackExtra(eventStartTimeString));
        //editTextRSVP.setText(unpackExtra(eventRSVPString));

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
