package com.codepathgroup5.wanttoknow;

import android.app.Application;
import android.util.Log;

import com.codepathgroup5.models.ChatMessagePO;
import com.codepathgroup5.models.MessagePO;
import com.codepathgroup5.models.PersonalListPO;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;


public class WantToKnowApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Connecting with facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Parse Models
        ParseObject.registerSubclass(PersonalListPO.class);
        ParseObject.registerSubclass(MessagePO.class);
        ParseObject.registerSubclass(ChatMessagePO.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("reiko") // should correspond to APP_ID env variable
                .clientKey("wannaKnow")  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .enableLocalDataStore()
                .server("https://wanttoknow.herokuapp.com/parse").build());

        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicWriteAccess(true);
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        /*Test of Parse
        ParseObject testObject = new ParseObject("ChatMessagePO");
        testObject.put("body", "testing");
        testObject.put("user_id","rOhvwX30rS");
        testObject.put("id_request","JPoGb68l2s");

        testObject.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    public static CallbackManager getCallBackManager() {
        return CallbackManager.Factory.create();
    }

    @Override
    public void onTerminate() {
        Log.d(WantToKnowApplication.class.getName(),"Parse User Log out");
        ParseUser.logOut();
        //FacebookClient.newInstance().stopAccessTokenTracking();
    }
}