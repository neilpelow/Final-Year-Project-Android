package com.example.neilpelow.finalyearproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import static android.support.customtabs.CustomTabsIntent.KEY_ID;

public class CreateActivity extends Activity {

    private TextView eventId;
    private TextView eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        eventId = (TextView) findViewById(R.id.eventIdTextView);
        eventName = (TextView) findViewById(R.id.eventNameTextView);
        String eventIdString = "eventIdKey";
        String eventNameString = "eventNameKey";

        eventId.setText(unpackExtra(eventIdString));
        eventName.setText(unpackExtra(eventNameString));
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
