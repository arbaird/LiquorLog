package com.austinbaird.liquorlog;


import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.util.Log;

public class MainActivity extends Activity
{


    ArrayList<RowItem> rowItems; //the row items that are displayed in the list
    CustomListAdapter adapter; //used to make viewable items on screem from data in rowItems

    String logTag = "tag";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rowItems = new ArrayList<>();
        //add values to test rowItems and adapter properly display to screen
        rowItems.add(new RowItem("Long Island", R.drawable.ic_launcher));
        rowItems.add(new RowItem("Manhattan", R.drawable.ic_launcher));
        rowItems.add(new RowItem("Black and Tan", R.drawable.ic_launcher));




        adapter = new CustomListAdapter(this, rowItems);
        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

    }

    public void addDrink(View v)
    {
        //add a row item
        adapter.add(new RowItem("Default", R.drawable.ic_launcher));
    }
}
