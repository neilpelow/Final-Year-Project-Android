package com.example.neilpelow.finalyearproject;

import org.json.JSONException;

/**
 * Created by neilpelow on 25/01/2017.
 */

public interface Callback {
    void onCompleted(Object data) throws JSONException;
}
