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


    int drinksIcon[] = { R.drawable.bluesmall, R.drawable.blueicesmall, R.drawable.blueumbrellasmall, R.drawable.bluefruitsmall,
            R.drawable.blueiceumbrellasmall, R.drawable.blueicefruitsmall, R.drawable.blueumbrellafruitsmall, R.drawable.blueiceumbrellafruitsmall,
            R.drawable.darkbrownsmall, R.drawable.darkbrownicesmall, R.drawable.darkbrownumbrellasmall, R.drawable.darkbrownfruitsmall,
            R.drawable.darkbrowniceumbrellasmall, R.drawable.darkbrownicefruitsmall, R.drawable.darkbrownumbrellafruitsmall, R.drawable.darkbrowniceumbrellafruitsmall,
            R.drawable.goldsmall, R.drawable.goldicesmall, R.drawable.goldumbrellasmall, R.drawable.goldfruitsmall,
            R.drawable.goldiceumbrellasmall, R.drawable.goldicefruitsmall, R.drawable.goldumbrellafruitsmall, R.drawable.goldiceumbrellafruitsmall,
            R.drawable.greensmall, R.drawable.greenicesmall, R.drawable.greenumbrellasmall, R.drawable.greenfruitsmall,
            R.drawable.greeniceumbrellasmall, R.drawable.greenicefruitsmall, R.drawable.greenumbrellafruitsmall, R.drawable.greeniceumbrellafruitsmall,
            R.drawable.redsmall, R.drawable.redicesmall, R.drawable.redumbrellasmall, R.drawable.redfruitsmall,
            R.drawable.rediceumbrellasmall, R.drawable.redicefruitsmall, R.drawable.redumbrellafruitsmall, R.drawable.rediceumbrellafruitsmall};


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

    public DrinkRecipe drinkFromLib;

    public RequestQueue queue;

    public DrinkRecipe drinkToEdit;
    public ArrayList<Ingredient> ingredientsToEdit;

    public static AppInfo getInstance(Context context) {
        if(instance == null) {
            instance = new AppInfo();
            instance.my_context = context;
            instance.savedDrinks = new ArrayList<>();

            instance.ingredientsToEdit = new ArrayList<>();
            instance.drinkToEdit = null;
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

    public void clearDrinkToEdit()
    {
        instance.drinkToEdit = new DrinkRecipe("", null, "");
    }

}
