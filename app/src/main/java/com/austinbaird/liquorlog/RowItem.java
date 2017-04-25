package com.austinbaird.liquorlog;

/**
 * Created by austinbaird on 4/24/17.
 * Holds basic info that is displayed in the list. Possibly change to a struct, since all we
 * use this for right now is to hold data members
 */

public class RowItem
{
    private String drinkName;
    private int imageID;

    public RowItem(String drinkName, int imageID)
    {
        this.drinkName = drinkName;
        this.imageID = imageID;
    }

    public String getName()
    {
        return drinkName;
    }

    public int getImageID()
    {
        return imageID;
    }

    public void setName(String drinkName)
    {
        this.drinkName = drinkName;
    }

    public void setImageID(int imageID)
    {
        this.imageID = imageID;
    }

}
