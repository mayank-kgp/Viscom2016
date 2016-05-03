package com.example.sanatkumarsaha.viscom2016;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        sp = getSharedPreferences("Check", Context.MODE_PRIVATE);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if(!sp.getBoolean("LogInStat",false)) {

                    Intent i = new Intent(SplashScreen.this, LogIn.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                }
                else
                    {
                        Intent i = new Intent(SplashScreen.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
