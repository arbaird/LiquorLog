package com.austinbaird.liquorlog;

import android.util.Log;

import org.json.JSONObject;

/**
 * A class that holds info about each individual ingredient
 */

public class Ingredient
{
    public String[] components;
    public String qty;
    public String measure;
    public String ingredient;
    public JSONObject jsonIngredientComponents;
    public String logTag = "";

    public Ingredient(String qty, String measure, String ingredient)
    {

        this.qty = qty;

        this.measure = measure;

        this.ingredient = ingredient;

        //convenient to have JSON representation of this class for quick exchanges with database
        try {
            jsonIngredientComponents = new JSONObject();
            jsonIngredientComponents.put("qty", qty);
            jsonIngredientComponents.put("measure", measure);
            jsonIngredientComponents.put("name", ingredient);
        }
        catch(Exception e)
        {
            Log.d(logTag, "Didn't set JSON properly in Ingredient class" );
        }
    }

    public String getQty() {return qty;}
    public String getMeasure() {return measure;}
    public String getIngredient() {return ingredient;}

    public void setQty(String qty) {
        this.qty = qty;
        try {
            jsonIngredientComponents.put("qty", qty);
        }
        catch(Exception e)
        {

        }
    }
    public void setMeasure(String measure)
    {
        this.measure = measure;
        try {
            jsonIngredientComponents.put("measure", measure);
        }
        catch(Exception e)
        {

        }
    }
    public void setIngredient(String ingredient)
    {
        this.ingredient =ingredient;
        try {
            jsonIngredientComponents.put("name", ingredient);
        }
        catch(Exception e)
        {
            Log.d(logTag, "Didn't set JSON properly in Ingredient class" );
        }
    }

    public JSONObject getJsonIngredient()
    {
        return jsonIngredientComponents;
    }


}
