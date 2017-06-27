package com.austinbaird.liquorlog;

import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

import java.util.ArrayList;

/*
    Holds information about a drink recipe. Also has method to get json version of a drink recipe
    for easy access when loading and saving drinks to shared preferences
 */
public class DrinkRecipe
{
    //list of ingredients
    public ArrayList<Ingredient> ingredientList;

    private String name;
    private String msg;

    private int imageID;

    //the drink as a JSON object
    public JSONObject drinkAsJSON;

    //if the drink is user made or obtained from pre defined library
    public boolean userMade;

    public String logTag = "";

    //amount of times this drink has been downloaded from database
    public String downloads = "0";

    //unique id representing this drink in the database
    public String uniqueId;

    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg)
    {
        this.name = name;
        if(ingredientList == null)
            ingredientList = new ArrayList<>();
        this.ingredientList = ingredientList;
        this.msg = msg;
        this.imageID = R.drawable.emptysmall;
        drinkAsJSON = new JSONObject();
        this.userMade = true;

        //set the JSON representation of this drink
        setDrinksAsJSON();
    }

    //overloaded constructor
    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg, int img)
    {
        this.name = name;
        if(ingredientList == null)
            ingredientList = new ArrayList<>();
        this.ingredientList = ingredientList;
        this.msg = msg;
        drinkAsJSON = new JSONObject();
        this.userMade = true;
        this.imageID = img;
        setDrinksAsJSON();
    }

    //overloaded constructor
    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg, int img, String uniqueid, int downloads)
    {
        this.name = name;
        if(ingredientList == null)
            ingredientList = new ArrayList<>();
        this.ingredientList = ingredientList;
        this.msg = msg;
        drinkAsJSON = new JSONObject();
        this.userMade = true;
        this.imageID = img;
        this.uniqueId = uniqueid;
        this.downloads = "" + downloads;
        setDrinksAsJSON();
    }

    //getters and setters
    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public String getName() {
        return this.name;
    }

    public int getImageID() {
        return imageID;
    }

    public String getMsg() {
        return msg;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setName(String name)
    {
        this.name = name;
        setDrinksAsJSON();
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
        setDrinksAsJSON();
    }


    public void setImageID(int imageID)
    {
        this.imageID = imageID;
        setDrinksAsJSON();
    }

    //set list of ingredients
    public void setIngredientList(ArrayList<Ingredient> ingredientList)
    {
        this.ingredientList.clear();
        for (Ingredient ingredient : ingredientList)
        {
            this.ingredientList.add(ingredient);
        }
        setDrinksAsJSON();
    }

    //save a JSON representation of this drink
    public void setDrinksAsJSON()
    {
        try
        {
            JSONArray ingredientArray = new JSONArray();
            for (Ingredient ingredient : ingredientList)
            {
                ingredientArray.put(ingredient.getJsonIngredient());
            }
            drinkAsJSON.put("name", name);
            drinkAsJSON.put("ingredients", ingredientArray);
            drinkAsJSON.put("msg", msg);
            drinkAsJSON.put("userMade", userMade);
            drinkAsJSON.put("img", imageID);

        }
        catch (Exception e)
        {
            Log.d(logTag, "Didn't set JSON properly in DrinkRecipe class" );
        }
    }
}
