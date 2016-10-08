package ru.example.sic.my_ads;


import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;

public class App extends Application {

    private String ApplicationID = "CPE6ZlvDNZsQeHhLOzoni6WechLXLRlQGDhcYQQO";
    private String ClientKey = "WUN4r5JUXXvyn2ZGn1oZsSZ3tERbE0WQIcNtLyLZ";

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(ApplicationID)
                .clientKey(ClientKey)
                .server("https://ads9000.herokuapp.com/parse")
                .build());
    }
}
