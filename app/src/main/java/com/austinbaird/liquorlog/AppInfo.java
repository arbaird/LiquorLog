package com.austinbaird.liquorlog;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by luca on 18/1/2016.
 */
public class AppInfo {

    private static AppInfo instance = null;
    private static final String COLOR_NAME = "color2";




    protected AppInfo() {
        // Exists only to defeat instantiation.
    }

    // Here are some values we want to keep global.
    public ArrayList<DrinkRecipe> savedDrinks;

    private Context my_context;
    public RequestQueue queue;

    public static AppInfo getInstance(Context context) {
        if(instance == null) {
            instance = new AppInfo();
            instance.savedDrinks = new ArrayList<>();
            instance.queue = Volley.newRequestQueue(context);

        }
        return instance;
    }

    public void addDrink(DrinkRecipe drink) {
        instance.savedDrinks.add(drink);
        /*SharedPreferences settings = my_context.getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(COLOR_NAME, c);
        editor.commit();*/
    }

}
