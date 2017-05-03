package com.austinbaird.liquorlog;

/**
 * Created by austinbaird on 5/3/17.
 */

public class DrinkRecipe
{

    private IngredientList ingredients;
    private String name;
    private String msg;

    public DrinkRecipe(String name, IngredientList ingredients, String msg)
    {
        this.name = name;
        this.ingredients = ingredients;
        this.msg = msg;
    }


}
