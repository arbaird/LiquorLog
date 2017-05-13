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
    public String logTag = "";

    public DrinkRecipe(String drinkName, int imageID) {
        this.name = drinkName;
        this.imageID = imageID;
        setDrinksAsJSON();
    }

    public DrinkRecipe(ArrayList<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
        setDrinksAsJSON();
    }

    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg) {
        this.name = name;
        this.ingredientList = ingredientList;
        this.msg = msg;
        this.imageID = R.drawable.ic_launcher;
        drinkAsJSON = new JSONObject();
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

    public void setName(String name) {
        this.name = name;
        setDrinksAsJSON();
    }

    public void setMsg(String msg) {
        this.msg = msg;
        setDrinksAsJSON();
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
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
            //jArray.put(drinkRecipe);
        }
        catch (Exception e) {
            Log.d(logTag, "Didn't set JSON properly in DrinkRecipe class" );
        }





    }
}
