package com.austinbaird.liquorlog;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import android.util.Log;

public class AppInfo
{

    private static AppInfo instance = null;

    public static String logTag;

    protected AppInfo()
    {
        // Exists only to defeat instantiation.
    }



    private Context my_context;

    // Here are some values we want to keep global.

    //the list of saved drinks for the user
    public ArrayList<DrinkRecipe> savedDrinks;

    //drink to be displayed when going from library activity to display activity
    public DrinkRecipe drinkFromLib;

    //handles all Volley requests.
    public RequestQueue queue;

    //drink to edit when going from display drink to edit drink acitivty.
    public DrinkRecipe drinkToEdit;

    //ingredients to edit when going from display drink to edit drink acitivty. Easier to extract
    //separate from DrinkRecipe since these ingredients are used for a custom list adapter in both
    //activities
    public ArrayList<Ingredient> ingredientsToEdit;

    public static AppInfo getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new AppInfo();
            instance.my_context = context;
            instance.savedDrinks = new ArrayList<>();

            instance.ingredientsToEdit = new ArrayList<>();
            instance.drinkToEdit = null;
            instance.queue = Volley.newRequestQueue(context);
        }
        return instance;
    }



    public void addDrink(DrinkRecipe drink)
    {
        instance.savedDrinks.add(drink);
    }

}
