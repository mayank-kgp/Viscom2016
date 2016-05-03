package com.example.sanatkumarsaha.viscom2016;

import android.app.Application;
import android.content.Context;

/**
 * Created by Sanat on 08-12-2015.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }

    public static MyApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return getsInstance().getApplicationContext();
    }

}
