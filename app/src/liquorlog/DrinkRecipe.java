package com.austinbaird.liquorlog;

/**
 * Created by austinbaird on 5/3/17.
 */
import java.util.ArrayList;

public class DrinkRecipe
{
    public ArrayList<Ingredient> ingredientList;
    private String name;
    private String msg;
    private int imageID;

    public DrinkRecipe(String drinkName, int imageID)
    {
        this.name = drinkName;
        this.imageID = imageID;
    }

    public DrinkRecipe(ArrayList<Ingredient> ingredientList)
    {
        this.ingredientList = ingredientList;
    }

    public DrinkRecipe(String name, ArrayList<Ingredient> ingredientList, String msg)
    {
        this.name = name;
        this.ingredientList = ingredientList;
        this.msg = msg;
        this.imageID = R.drawable.ic_launcher;
    }


    /*public String getQty() {return ingredientList.getQty();}
    public String getMeasure() {return ingredientList.getMeasure();}
    public String getIngredient() {return ingredientList.getIngredient();}*/

    public ArrayList<Ingredient> getIngredientList() {return ingredientList;}


    /*public void setQty(String qty) {ingredientList.setQty(qty);}
    public void setMeasure(String measure) {ingredientList.setMeasure(measure);}
    public void setIngredient(String ingredient) {ingredientList.setIngredient(ingredient);}*/

    public String getName() {return this.name;}
    public int getImageID()
    {
        return imageID;
    }
    public String getMsg() {return msg;}

    public void setName(String name) {this.name = name;}
    public void setMsg(String msg) {this.msg = msg;}
    public void setImageID(int imageID)
    {
        this.imageID = imageID;
    }




}
