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


/*
Custom List adapter for dsiplaying the drink image and name in a vertical listver. Used in main activity
 */

public class CustomListAdapter extends BaseAdapter {



    private final Activity context;
    private ArrayList<DrinkRecipe> data;

    public CustomListAdapter(Activity context, ArrayList<DrinkRecipe> data)
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
    public void add(DrinkRecipe rItem)
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



        DrinkRecipe rowItem = data.get(position);
        holder.name.setText(rowItem.getName());
        holder.imgIcon.setImageResource(rowItem.getImageID());

        return row;
    }

}
