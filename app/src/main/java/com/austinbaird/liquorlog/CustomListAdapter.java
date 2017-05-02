package com.austinbaird.liquorlog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import android.util.Log;



class RowItem
{
    private String drinkName;
    private int imageID;
    private String message;
    private ArrayList<String> ingredients;

    public RowItem(String drinkName, int imageID)
    {
        this.drinkName = drinkName;
        this.imageID = imageID;
    }

    public RowItem(String drinkName, int imageID, String message, ArrayList<String> ingredients)
    {
        this.drinkName = drinkName;
        this.imageID = imageID;
        this.message = message;
        this.ingredients = ingredients;
    }

    public String getName()
    {
        return drinkName;
    }

    public String getMessage()
    {
        return message;
    }

    public ArrayList<String> getIngredients()
    {
        return ingredients;
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

    public void setMessage(String message) {this.message= message;}

    public void setIngredients(ArrayList<String> ingredients)
    {
        this.ingredients = ingredients;
    }

    public void addIngredient(String ingredient)
    {
        this.ingredients.add(ingredient);
    }

}


public class CustomListAdapter extends BaseAdapter {

    private String logTag = "tag";

    private final Activity context;
    private ArrayList<RowItem> data;

    public CustomListAdapter(Activity context, ArrayList<RowItem> data)
    {
        this.context=context;
        this.data = data;
    }

    //the following mehtods must be implemented since this extends an abstract class
    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return data.indexOf(getItem(position));
    }

    //add another row item
    public void add(RowItem rItem)
    {
        data.add(rItem);
        notifyDataSetChanged();
    }

    //used in getView to help convert code data items to View types that appear on screen
    class ViewHolder
    {
        ImageView imgIcon;
        TextView name;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View row = convertView;
        ViewHolder holder = null;

        if(row == null)
        {
            //get xml info for the row and assign the appropriate view ids to the holder
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.activity_listview, parent, false);

            holder = new ViewHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.drink_image);
            holder.name = (TextView)row.findViewById(R.id.label);

            row.setTag(holder);
        }
        else
        {
            //initialize holder to the values in the row's ViewHolder tag
            holder = (ViewHolder)row.getTag();
        }

        //set xml view's data fields to data fields in row
        RowItem rowItem = data.get(position);
        holder.name.setText(rowItem.getName());
        holder.imgIcon.setImageResource(rowItem.getImageID());

        return row;
    }

}
