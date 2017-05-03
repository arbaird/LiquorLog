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
import android.widget.Spinner;
import android.widget.EditText;
import android.view.View.OnFocusChangeListener;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


class ScrollerRowItem
{
    public IngredientList ingredientList;
    private String name;
    private String msg;

    public ScrollerRowItem(IngredientList ingredientList)
    {
        this.ingredientList = ingredientList;
    }


    public String getQty() {return ingredientList.getQty();}
    public String getMeasure() {return ingredientList.getMeasure();}
    public String getIngredient() {return ingredientList.getIngredient();}

    public void setQty(String qty) {ingredientList.setQty(qty);}
    public void setMeasure(String measure) {ingredientList.setMeasure(measure);}
    public void setIngredient(String ingredient) {ingredientList.setIngredient(ingredient);}




}


public class ScrollerListAdapter extends BaseAdapter
{

    private String logTag = "tag";
    Map<Integer, Integer> myMap;

    private final Activity context;
    private ArrayList<ScrollerRowItem> data;

    public ScrollerListAdapter(Activity context, ArrayList<ScrollerRowItem> data)
    {
        this.context=context;
        this.data = data;

        myMap = new HashMap<Integer, Integer>();
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
    public void add(ScrollerRowItem rItem)
    {
        data.add(rItem);
        notifyDataSetChanged();
    }

    public void add()
    {
        data.add(new ScrollerRowItem(new IngredientList("","","")));
        notifyDataSetChanged();
    }

    //used in getView to help convert code data items to View types that appear on screen
    class ViewHolder
    {
        Spinner qty;
        Spinner measurement;
        EditText ingredientName;
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
            row = inflater.inflate(R.layout.edit_list_element, parent, false);

            holder = new ViewHolder();
            holder.qty = (Spinner)row.findViewById(R.id.qtySpinner);
            holder.measurement = (Spinner)row.findViewById(R.id.measureSpinner);
            holder.ingredientName = (EditText)row.findViewById(R.id.ingredientEdit);

            row.setTag(holder);
        }
        else
        {
            //initialize holder to the values in the row's ViewHolder tag
            holder = (ViewHolder)row.getTag();
        }

        //Fill EditText with the value you have in data source
        holder.ingredientName.setText(data.get(position).ingredientList.ingredientComponents[2]);
        holder.ingredientName.setId(position);

        //we need to update adapter once we finish with editing
        holder.ingredientName.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    data.get(position).ingredientList.ingredientComponents[2] = Caption.getText().toString();
                }
            }
        });


        holder.qty.setPrompt(data.get(position).ingredientList.ingredientComponents[0]);
        //holder.qty.setId(position);

        //final String selected = (String)holder.qty.getSelectedItem().toString();
        if (myMap.containsKey(position)) {
            holder.qty.setSelection(myMap.get(position));
        }


        holder.qty.setOnItemSelectedListener(new OnItemSelectedListener() {
            int count=0;
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id)
            {

                ScrollerRowItem item = data.get(position);
                String selected = parentView.getItemAtPosition(pos).toString();

                myMap.put(position, pos);

                item.ingredientList.ingredientComponents[0] = selected;
                //Log.d(logTag, selected);
                //Log.d(logTag, "CLICK: " + data.get(position).ingredientList.ingredientComponents[0]);

            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                                                     // your code here
            }

        });

        Log.d(logTag, "View change from position: " + position + " " +  data.get(position).ingredientList.ingredientComponents[0]);
        /*holder.qty.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final int position = v.getId();
                    final Spinner Caption = (Spinner) v;
                    data.get(position).ingredientList.ingredientComponents[0] = Caption.getPrompt().toString();
                }
            }
        });*/

        //set xml view's data fields to data fields in row
        ScrollerRowItem rowItem = data.get(position);
        /*holder.qty.setPrompt("1");
        holder.measurement.setPrompt("-");
        holder.ingredientName.setText("ingredient");*/

        return row;
    }

}


