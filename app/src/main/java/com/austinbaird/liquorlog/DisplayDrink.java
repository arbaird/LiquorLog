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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;

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
    DrinkRecipe displayedDrink;

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
        //DrinkRecipe displayedDrink;
        if(extras.containsKey("fromLib") && extras.getBoolean("fromLib"))
        {
            Button b = (Button)findViewById(R.id.butt_edit);
            //b.setVisibility(View.GONE);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    saveDrink();
                }
            });
            b.setText("Add to Log");
            displayedDrink = appInfo.drinkFromLib;
        }

        else {
            mydrink = extras.getInt("drinkPosition");
            displayedDrink = appInfo.savedDrinks.get(mydrink);
            View b = findViewById(R.id.butt_edit);
            b.setVisibility(View.VISIBLE);
        }

        TextView tv1 = (TextView) findViewById(R.id.textView1);
        //TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textView3);

        ImageView imageView = (ImageView) findViewById(R.id.drinkImage);

       // TextView tv5 = (TextView) findViewById(R.id.textView5);
        //TextView tv6 = (TextView) findViewById(R.id.textView6);
        //TextView tv4 = (TextView) findViewById(R.id.textView3); // THIS SHOULD BE WHATEVER THE VIEW IS FOR IMAGES
        //if (appInfo.savedDrinks.get(mydrink) != null) {
          //  DrinkRecipe displayedDrink = appInfo.savedDrinks.get(mydrink);
            name = displayedDrink.getName();
            ingredients = displayedDrink.getIngredientList();

            aList.clear();
            for(int i = 0; i < ingredients.size(); i++)
            {
                qty = ingredients.get(i).getQty();
                measure = ingredients.get(i).getMeasure();
                ingredient = ingredients.get(i).getIngredient();
                total = qty + " " + measure + " " + ((qty + measure).equals("") ? "" : "of ") + ingredient;
                aList.add(new ListElement(total.trim()));
                aa.notifyDataSetChanged();

            }

            msg = displayedDrink.getMsg();
            //int imageID = displayedDrink.getImageID();
            tv1.setText(name);
            //tv5.setText("Ingredients: ");
            //tv2.setText(qty + " " + measure + " of " + ingredient);
            //tv6.setText("Directions: ");
            tv3.setText(msg);

            imageView.setImageResource(displayedDrink.getImageID());
            //tv4.setText(imageID) //THIS SHOULD BE DIFFERENT

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

        DrinkRecipe displayedDrink = appInfo.savedDrinks.get(mydrink);
        //appInfo.setIngredientsToEdit(displayedDrink.getIngredientList());
        appInfo.drinkToEdit = displayedDrink;

        // Go to first activity

        Intent intent = new Intent(this, EditDrinkActivity.class);

        intent.putExtra("alreadyCreated", true); //setFlag that drink being edited has already been created
        intent.putExtra("drinkPosition", mydrink);
        //intent.putExtra("name", name);
        //intent.putExtra("qty", qty);
        //intent.putExtra("measure", measure);
        //intent.putExtra("ingredient", ingredient);
        //intent.putExtra("msg", msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //May neee to change this location to other activities instead
        startActivity(intent);
    }

    public void saveDrink()
    {

        if(isDuplicateName(displayedDrink.getName()))
        {
            duplicateDialog();
        }
        else
        {
            appInfo.addDrink(displayedDrink);

            Intent intent = new Intent(DisplayDrink.this, LibraryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public boolean isDuplicateName(String name)
    {
        for(DrinkRecipe recipe : appInfo.savedDrinks)
        {
            if(name.equals(recipe.getName()))
                return true;
        }
        return false;
    }

    public void duplicateDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayDrink.this);//.create();
        alertDialog.setTitle("Add Duplicate");


        alertDialog.setMessage("A drink with this name already exists in your log.\nAdd this drink anyway?(Existing drink will remain)")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appInfo.addDrink(displayedDrink);

                        Intent intent = new Intent(DisplayDrink.this, LibraryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }


}

