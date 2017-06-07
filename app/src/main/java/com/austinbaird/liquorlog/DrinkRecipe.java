package com.austinbaird.liquorlog;

/**
 * Created by austinbaird on 5/3/17.
 */
import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

import java.util.ArrayList;

public class DrinkRecipe {
    public ArrayList<Ingredient> ingredientList;
    private String name;
    private String msg;
    private int imageID;
    public JSONObject drinkAsJSON;
    public boolean userMade;

    public String logTag = "";
    public String downloads = "0";
    public String uniqueId;

    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg, boolean userMade, int img) {
        this.name = name;
        this.ingredientList = ingredientList;
        this.msg = msg;
        this.imageID = img;
        drinkAsJSON = new JSONObject();
        this.userMade = userMade;
        setDrinksAsJSON();
    }


    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg) {
        this.name = name;
        if(ingredientList == null)
            ingredientList = new ArrayList<>();
        this.ingredientList = ingredientList;
        this.msg = msg;
        this.imageID = R.drawable.emptysmall;
        drinkAsJSON = new JSONObject();
        this.userMade = true;
        setDrinksAsJSON();
    }

    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg, int img) {
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

    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg, int img, String uniqueid, int downloads) {
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


    /*public String getQty() {return ingredientList.getQty();}
    public String getMeasure() {return ingredientList.getMeasure();}
    public String getIngredient() {return ingredientList.getIngredient();}*/

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }


    /*public void setQty(String qty) {ingredientList.setQty(qty);}
    public void setMeasure(String measure) {ingredientList.setMeasure(measure);}
    public void setIngredient(String ingredient) {ingredientList.setIngredient(ingredient);}*/

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

    public void setName(String name) {
        this.name = name;
        setDrinksAsJSON();
    }

    public void setMsg(String msg) {
        this.msg = msg;
        setDrinksAsJSON();
    }
    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public void setImageID(int imageID)
    {
        this.imageID = imageID;
        setDrinksAsJSON();
    }

    public void setIngredientList(ArrayList<Ingredient> ingredientList) {
        this.ingredientList.clear();
        for (Ingredient ingredient : ingredientList) {
            this.ingredientList.add(ingredient);
        }
        setDrinksAsJSON();
    }


    public void setDrinksAsJSON()
    {
        //JSONObject drinkRecipe = new JSONObject();

        try
        {
            JSONArray ingredientArray = new JSONArray();
            for (Ingredient ingredient : ingredientList)
            {
                ingredientArray.put(ingredient.getJsonIngredient());
                //jArray.put(new JSONArray(ingredient)); this would be ideal, but is unsupported before API 19 for android
            }
            drinkAsJSON.put("name", name);
            drinkAsJSON.put("ingredients", ingredientArray);
            drinkAsJSON.put("msg", msg);
            drinkAsJSON.put("userMade", userMade);
            drinkAsJSON.put("img", imageID);
            //jArray.put(drinkRecipe);
        }
        catch (Exception e) {
            Log.d(logTag, "Didn't set JSON properly in DrinkRecipe class" );
        }





    }
}
