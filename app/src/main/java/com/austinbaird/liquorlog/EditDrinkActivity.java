package com.austinbaird.liquorlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import android.view.View;
import android.util.Log;

import android.widget.FrameLayout;

public class EditDrinkActivity extends AppCompatActivity
{

    RequestQueue queue;

    ArrayList<ScrollerRowItem> rowItems; //the row items that are displayed in the list
    ScrollerListAdapter adapter; //used to make viewable items on screem from data in rowItems

    String logTag = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drink);


        rowItems = new ArrayList<>();
        adapter = new ScrollerListAdapter(this, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);


        FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.button_footerview,null);
        //btnPostYourEnquiry = (Button) footerLayout.findViewById(R.id.btnAddIngredient);

        listView.addFooterView(footerLayout);
    }

    public void add(View v)
    {
        //ScrollerRowItem blank = new ScrollerRowItem(new IngredientList("","",""));
        adapter.add();
    }

    public void saveDrink(View v)
    {
        Log.d(logTag, "Rowitems size: " + Integer.toString(rowItems.size()));
        for(ScrollerRowItem drink : rowItems)
        {
            Log.d(logTag, drink.getQty() + " " + drink.getIngredient());

        }

    }
}
