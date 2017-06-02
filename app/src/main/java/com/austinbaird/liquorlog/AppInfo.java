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

    //used for addPreDefined in Edit drink activity
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


    public static String logTag;

    protected AppInfo() {
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

    public static AppInfo getInstance(Context context) {
        if(instance == null) {
            instance = new AppInfo();
            instance.my_context = context;
            instance.savedDrinks = new ArrayList<>();

            instance.ingredientsToEdit = new ArrayList<>();
            instance.drinkToEdit = null;
            instance.queue = Volley.newRequestQueue(context);
            SharedPreferences settings = context.getSharedPreferences(MainActivity.MYPREFS, 0);

            Log.d(logTag, "First AppInfo!");


        }
        return instance;
    }



    public void addDrink(DrinkRecipe drink) {
        instance.savedDrinks.add(drink);

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
