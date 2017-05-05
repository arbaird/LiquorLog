package com.austinbaird.liquorlog;

/**
 * Created by austinbaird on 5/2/17.
 */

public class Ingredient
{
    public String[] components;
    public String qty;
    public String measure;
    public String ingredient;

    public Ingredient(String qty, String measure, String ingredient)
    {
        components = new String[3];
        components[0] = qty;
        this.qty = qty;
        components[1] = measure;
        this.measure = measure;
        components[2] = ingredient;
        this.ingredient = ingredient;
    }

    public String getQty() {return components[0];}
    public String getMeasure() {return components[1];}
    public String getIngredient() {return components[2];}

    public void setQty(String qty) {components[0] = qty; this.qty = qty;}
    public void setMeasure(String measure) {components[1] = measure;this.measure = measure;}
    public void setIngredient(String ingredient) {components[2] = ingredient; this.ingredient =ingredient;}
}
