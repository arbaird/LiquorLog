package com.austinbaird.liquorlog;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import android.util.Log;
/**
 * Created by luca on 18/1/2016.
 */
public class AppInfo {

    private static AppInfo instance = null;
    private static final String COLOR_NAME1 = "color1";
    private static final String COLOR_NAME2 = "color2";

    public static String logTag;

    protected AppInfo() {
        // Exists only to defeat instantiation.
    }

    public String sharedString1;
    public String sharedString2;

    private Context my_context;

    // Here are some values we want to keep global.
    public ArrayList<DrinkRecipe> savedDrinks;

    public RequestQueue queue;

    public ArrayList<Ingredient> ingredientsToEdit;

    public static AppInfo getInstance(Context context) {
        if(instance == null) {
            instance = new AppInfo();
            instance.my_context = context;
            instance.savedDrinks = new ArrayList<>();

            instance.ingredientsToEdit = new ArrayList<>();
            instance.queue = Volley.newRequestQueue(context);
            SharedPreferences settings = context.getSharedPreferences(MainActivity.MYPREFS, 0);
            instance.sharedString1 = settings.getString(COLOR_NAME1, null);
            instance.sharedString2 = settings.getString(COLOR_NAME2, null);
            Log.d(logTag, "First AppInfo!");


        }
        return instance;
    }

    public void setColor1(String c) {
        instance.sharedString1 = c;
        SharedPreferences settings = my_context.getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(COLOR_NAME1, c);
        editor.commit();
    }

    public void setColor2(String c) {
        instance.sharedString2 = c;
        SharedPreferences settings = my_context.getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(COLOR_NAME2, c);
        editor.commit();
    }

    public void addDrink(DrinkRecipe drink) {
        instance.savedDrinks.add(drink);
        /*SharedPreferences settings = my_context.getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(COLOR_NAME, c);
        editor.commit();*/
    }

    public void setIngredientsToEdit(ArrayList<Ingredient> ingredients)
    {
        instance.ingredientsToEdit.clear();
        for(Ingredient ingredient : ingredients)
            instance.ingredientsToEdit.add(ingredient);
    }

}
