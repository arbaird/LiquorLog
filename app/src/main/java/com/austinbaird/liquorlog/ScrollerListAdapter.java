package com.austinbaird.liquorlog;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import android.util.Log;
import android.widget.Spinner;
import android.widget.EditText;
import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;


import android.view.View.OnFocusChangeListener;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/*class ScrollerRowItem
{
    public IngredientList ingredientList;
    private String name;
    private String msg;
    public ScrollerRowItem(IngredientList ingredientList)
    {
        this.ingredientList = ingredientList;
    }
    public ScrollerRowItem(String name, IngredientList ingredientList, String msg)
    {
        this.name = name;
        this.ingredientList = ingredientList;
        this.msg = msg;
    }
    public String getQty() {return ingredientList.getQty();}
    public String getMeasure() {return ingredientList.getMeasure();}
    public String getIngredient() {return ingredientList.getIngredient();}
    public IngredientList getIngredientList() {return ingredientList;}
    public void setQty(String qty) {ingredientList.setQty(qty);}
    public void setMeasure(String measure) {ingredientList.setMeasure(measure);}
    public void setIngredient(String ingredient) {ingredientList.setIngredient(ingredient);}
    public void setName(String name) {this.name = name;}
    public void setMsg(String msg) {this.msg = msg;}
}*/


class ScrollerListAdapter extends ArrayAdapter<Ingredient> {

    int resource;
    Context context;
    String logTag ="";
    ArrayList<Ingredient> data;
    MediaPlayer mp1;

    public ScrollerListAdapter(Context _context, int _resource, ArrayList<Ingredient> items) {
        super(_context, _resource, items);
        resource = _resource;
        context = _context;
        this.data = items;
        mp1 = MediaPlayer.create(context, R.raw.zeldadeleteing);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout newView;

        Ingredient w = getItem(position);

        // Inflate a new view if necessary.
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            LayoutInflater vi = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi.inflate(resource,  newView, true);
        } else {
            newView = (LinearLayout) convertView;
        }

        // Fills in the view.
        TextView tv = (TextView) newView.findViewById(R.id.itemText);
        Button b = (Button) newView.findViewById(R.id.itemButton);
        tv.setText((w.getQty() + " " + w.getMeasure() + " " + w.getIngredient().trim()));
        b.setText("Delete");

        // Sets a listener for the button, and a tag for the button as well.
        b.setTag(new Integer(position+1));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reacts to a button press.
                // Gets the integer tag of the button.
                String s = v.getTag().toString();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Ingredient " + s + " removed", duration);
                toast.show();
                mp1.start();
                // Let's remove the list item.
                int i = Integer.parseInt(s);
                i = i-1;
                data.remove(i);//TODO i = position (ALEX fixed this)
                notifyDataSetChanged();
            }
        });

        // Set a listener for the whole list item.
        newView.setTag(w.getQty() + " " + w.getMeasure() + " " + w.getIngredient());
        newView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = v.getTag().toString();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, s, duration);
                toast.show();
            }
        });

        return newView;
    }
}



