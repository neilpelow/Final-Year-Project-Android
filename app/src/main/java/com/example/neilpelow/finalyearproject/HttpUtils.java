package com.example.neilpelow.finalyearproject;

import android.location.Location;

import com.facebook.AccessToken;
import com.loopj.android.http.*;

/**
 * Created by neilpelow on 01/03/2017.
 */

public class HttpUtils {
    private final String BASE_URL = "http://Neils-MacBook-Pro.local:3000/events?";
    private String relativeUrl;


    private AsyncHttpClient client = new AsyncHttpClient();


    public String getBaseUrl(){
        String baseUrl;

        baseUrl = BASE_URL;
        return baseUrl;
    }
}

//localhost:3000/events?lat=53.337415&lng=-6.267563&distance=1000&accessToken=EAABwZAXd0Og8BAGKG8hmjJ5FJy9l4AXs6yyFY0r6GfdEVZBzSG6m4xTTJZCZB1pFjMuUcWOfjlRGb6vFI1cojwm3dD1Wo7oGV9BjphvunHOyXIMyAZBjZADeFMZCcZCZBZATApN4RZBVkC7A4pKCTUYH1FKwvAnW7ZAZAxwgmgidwyJZAX4ZBZCbNELb3EnjdMpsYhL9Ob5oPk0FZAIzLBbMd6hKofdvp0GOhHH0yVMIZD