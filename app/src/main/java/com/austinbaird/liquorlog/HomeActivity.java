package com.austinbaird.liquorlog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3500;
    boolean firstTime = true;
    MediaPlayer mp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firstTime = false;
        mp1 = MediaPlayer.create(this, R.raw.beedleshop);
        mp1.start();
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

    @Override
    protected void onResume() {
        /*if(firstTime == true)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent homeIntent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }*/
        super.onResume();
    }
}
