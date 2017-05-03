package com.austinbaird.liquorlog;

/**
 * Created by austinbaird on 5/2/17.
 */

public class IngredientList
{
    public String[] ingredientComponents;
    public String qty;
    public String measure;
    public String ingredient;

    public IngredientList(String qty, String measure, String ingredient)
    {
        ingredientComponents = new String[3];
        ingredientComponents[0] = qty; this.qty = qty;
        ingredientComponents[1] = measure; this.measure = measure;
        ingredientComponents[2] = ingredient; this.ingredient = ingredient;
    }

    public String getQty() {return ingredientComponents[0];}
    public String getMeasure() {return ingredientComponents[1];}
    public String getIngredient() {return ingredientComponents[2];}

    public void setQty(String qty) {ingredientComponents[0] = qty; this.qty = qty;}
    public void setMeasure(String measure) {ingredientComponents[1] = measure;this.measure = measure;}
    public void setIngredient(String ingredient) {ingredientComponents[2] = ingredient; this.ingredient =ingredient;}
}
