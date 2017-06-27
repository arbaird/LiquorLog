package com.austinbaird.liquorlog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
    screen to display when app is first opened
 */
public class HomeActivity extends AppCompatActivity
{
    //how long to display this screen
    private static int SPLASH_TIME_OUT = 3350;

    MediaPlayer mp1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mp1 = MediaPlayer.create(this, R.raw.beedleshop);
        mp1.start();

        //pauses the intent to go to main activity so the xml for home activty can be displayed
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mp1.stop();
                Intent homeIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
