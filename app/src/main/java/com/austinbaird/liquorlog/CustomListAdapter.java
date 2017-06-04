package com.austinbaird.liquorlog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;


/*
Custom List adapter for dsiplaying the drink image and name in a vertical listver. Used in main activity
 */

public class CustomListAdapter extends BaseAdapter {

    MediaPlayer mp6;
    MediaPlayer mp7;
    MediaPlayer mp8;


    private final Activity context;
    private ArrayList<DrinkRecipe> data;
    boolean deleteMode;
    AppInfo appInfo;

    public CustomListAdapter(Activity context, ArrayList<DrinkRecipe> data)
    {
        this.context=context;
        this.data = data;
        this.deleteMode = false;
        appInfo = AppInfo.getInstance(this.context);

        mp6 = MediaPlayer.create(this.context, R.raw.zeldadelete1);
        mp7 = MediaPlayer.create(this.context, R.raw.zeldadelete2);
        mp8 = MediaPlayer.create(this.context, R.raw.zeldacancel);
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

    public void addDeleteButtons()
    {
        deleteMode = true;
        notifyDataSetChanged();
    }

    public void removeDeleteButtons()
    {
        deleteMode = false;
        notifyDataSetChanged();
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
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


        Button b = (Button) row.findViewById(R.id.deleteButton);
        //b.setText("Delete");

        // Sets a listener for the button ot delete rowItem when pressed. Adapted from ListView example
        //provided by the professor
        b.setTag(new Integer(position+1));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reacts to a button press.
                // Gets the integer tag of the button.

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);//.create();
                alertDialog.setTitle("Delete Prompt");

                //prompt user to make sure they want to delete this drink
                alertDialog.setMessage("Are you sure you want to delete this drink?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //play delete sound
                                mp7.start();
                                //remove the drink form appInfo and update the screen
                                appInfo.savedDrinks.remove(position);
                                data.remove(position);
                                notifyDataSetChanged();

                                //update shared preferences
                                saveDrinksAsJSON();


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //play dismiss sound
                                mp8.start();
                                dialog.dismiss();
                            }
                        });
                //play warning sound
                mp6.start();
                alertDialog.show();




            }
        });

        if(deleteMode)
            b.setVisibility(View.VISIBLE);
        else
            b.setVisibility(View.GONE);


        //set xml view's data fields to data fields in row



        DrinkRecipe rowItem = data.get(position);
        holder.name.setText(rowItem.getName());
        holder.imgIcon.setImageResource(rowItem.getImageID());

        return row;
    }


    public void saveDrinksAsJSON()
    {

        JSONArray jArray = new JSONArray();
        for(DrinkRecipe savedRecipe : appInfo.savedDrinks)
        {
            try
            {
                jArray.put(savedRecipe.drinkAsJSON);
            }
            catch(Exception e)
            {
                //json key access didn't work
            }
        }


        SharedPreferences settings = context.getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("drinksAsJSON", jArray.toString());
        editor.commit();
    }


}
