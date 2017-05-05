package com.austinbaird.liquorlog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DisplayDrink extends AppCompatActivity {

    int mydrink = 0;
    AppInfo appInfo;
    String name = "";
    ArrayList<Ingredient> ingredients;
    String qty = "";
    String measure = "";
    String ingredient = "";
    String msg = "";
    String total;

    private class ListElement {
        ListElement() {};

        ListElement(String tl) {
            textLabel = tl;
        }

        public String textLabel;
    }

    private ArrayList<ListElement> aList;

    private class MyAdapter extends ArrayAdapter<ListElement> {

        int resource;
        Context context;

        public MyAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            final ListElement w = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater vi = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi.inflate(resource, newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            // Fills in the view.
            TextView tv = (TextView) newView.findViewById(R.id.itemText);
            tv.setText(w.textLabel);

            return newView;
        }
    }

    private MyAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_drink);
        appInfo = AppInfo.getInstance(this);
        aList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.list_element_display, aList); // ArrayList of list elements
        ListView myListView = (ListView) findViewById(R.id.mobileViewDisplay);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();
    }

    @Override
    protected void onResume() // MAY HAVE TO CHANGE TO ONRESUME
    {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            finish(); // No idea what else to do
        } else {
            mydrink = extras.getInt("drinkPosition");
        }

        TextView tv1 = (TextView) findViewById(R.id.textView1);
        //TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
       // TextView tv5 = (TextView) findViewById(R.id.textView5);
        //TextView tv6 = (TextView) findViewById(R.id.textView6);
        //TextView tv4 = (TextView) findViewById(R.id.textView3); // THIS SHOULD BE WHATEVER THE VIEW IS FOR IMAGES
        if (appInfo.savedDrinks.get(mydrink) != null) {
            DrinkRecipe displayedDrink = appInfo.savedDrinks.get(mydrink);
            name = displayedDrink.getName();
            ingredients = displayedDrink.getIngredientList();

            for(int i = 0; i < ingredients.size(); i++)
            {
                qty = ingredients.get(i).getQty();
                measure = ingredients.get(i).getMeasure();
                ingredient = ingredients.get(i).getIngredient();
                total = qty + " " + measure + " of " + ingredient;
                aList.add(new ListElement(total));
                aa.notifyDataSetChanged();

                /*if(tv2.getText() != null) // doesnt work
                {
                    tv2.setText(qty + " " + measure + " of " + ingredient);
                }
                else if(tv5.getText() == null) // doesnt work
                {
                    tv5.setText(qty + " " + measure + " of " + ingredient);
                }*/
            }

            msg = displayedDrink.getMsg();
            //int imageID = displayedDrink.getImageID();
            tv1.setText(name);
            //tv5.setText("Ingredients: ");
            //tv2.setText(qty + " " + measure + " of " + ingredient);
            //tv6.setText("Directions: ");
            tv3.setText(msg);
            //tv4.setText(imageID) //THIS SHOULD BE DIFFERENT
        }
    }

    public void clickEdit(View V) {

        /*TextView tv1 = (TextView) findViewById(R.id.textView1);
        String text1 = tv1.getText().toString();
        //appInfo.setColor1(text1);
        //appInfo.addDrink(mydrink); = text1;*/

        TextView tv1 = (TextView) findViewById(R.id.textView1);
        String text1 = tv1.getText().toString();
        //appInfo.setColor2(text2);
        appInfo.sharedString1 = text1;

        TextView tv3 = (TextView) findViewById(R.id.textView3);
        String text3 = tv3.getText().toString();
        //appInfo.setColor1(text1);
        appInfo.sharedString2 = text3;
/*
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        String text3 = tv2.getText().toString();
        //appInfo.setColor1(text1);
        appInfo.sharedString2 = text2;

        TextView tv2 = (TextView) findViewById(R.id.textView2);
        String text3 = tv2.getText().toString();
        //appInfo.setColor1(text1);
        appInfo.sharedString2 = text2;*/

        // Go to first activity
        Intent intent = new Intent(this, EditDrinkActivity.class);
        //intent.putExtra("name", name);
        //intent.putExtra("qty", qty);
        //intent.putExtra("measure", measure);
        //intent.putExtra("ingredient", ingredient);
        //intent.putExtra("msg", msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //May neee to change this location to other activities instead
        startActivity(intent);
    }
}

