package com.austinbaird.liquorlog;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayDrink extends AppCompatActivity {

    //position of drink in appInfo.sharedDrinks
    int drinkPos = 0;
    AppInfo appInfo;
    //name of drink
    String name = "";
    //ingredients of drink
    ArrayList<Ingredient> ingredients;
    //info about each ingredient
    String qty = "";
    String measure = "";
    String ingredient = "";
    String msg = "";

    String total;
    //Drink being displayed
    DrinkRecipe displayedDrink;
    MediaPlayer mp1;
    MediaPlayer mp2;
    MediaPlayer mp3;
    MediaPlayer mp4;
    MediaPlayer mp5;

    private class ListElement {
        ListElement() {};

        ListElement(String tl) {
            textLabel = tl;
        }

        public String textLabel;
    }

    private ArrayList<ListElement> aList;

    //adapter for list of textLables, built from stings in ListElement above
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
        //list for ingredients to display
        aList = new ArrayList<ListElement>();
        //atach adapter to aList
        aa = new MyAdapter(this, R.layout.list_element_display, aList); // ArrayList of list elements
        ListView myListView = (ListView) findViewById(R.id.mobileViewDisplay);
        //set adapter for ListView displayed on screen
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();
        //set up souds for this activity
        mp1 = MediaPlayer.create(this, R.raw.zeldaselecting);
        mp2 = MediaPlayer.create(this, R.raw.backbutton);
        mp3 = MediaPlayer.create(this, R.raw.zeldaaddlibrarydrink);
        mp4 = MediaPlayer.create(this, R.raw.zeldacancel);
        mp5 = MediaPlayer.create(this, R.raw.zeldadelete1);
    }

    @Override
    protected void onResume() // MAY HAVE TO CHANGE TO ON RESUME
    {
        super.onResume();
        Bundle extras = getIntent().getExtras();

        //see if this activty was started from library activity
        if(extras.containsKey("fromLib") && extras.getBoolean("fromLib"))
        {
            //changed button to save drink if user came from library, rather than edit drink
            Button b = (Button)findViewById(R.id.butt_edit);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    saveDrink();
                }
            });
            b.setText("Add to Log");
            displayedDrink = appInfo.drinkFromLib;
        }

        else
        {
            //get drink that is being displayed from appInfo
            drinkPos = extras.getInt("drinkPosition");
            displayedDrink = appInfo.savedDrinks.get(drinkPos);
            View b = findViewById(R.id.butt_edit);
            b.setVisibility(View.VISIBLE);
        }

        //get views displayed on screen
        TextView tvName = (TextView) findViewById(R.id.textView1);
        TextView tvMsg = (TextView) findViewById(R.id.textView3);
        ImageView imageView = (ImageView) findViewById(R.id.drinkImage);


        name = displayedDrink.getName();
        msg = displayedDrink.getMsg();
        ingredients = displayedDrink.getIngredientList();

        //copy ingredients in the drink being displayed into the arraylist used to display ingredients
        aList.clear();
        for(int i = 0; i < ingredients.size(); i++)
        {
            qty = ingredients.get(i).getQty();
            measure = ingredients.get(i).getMeasure();
            ingredient = ingredients.get(i).getIngredient();
            total = qty + " " + measure + " " + ((qty + measure).equals("") ? "" : "of ") + ingredient;
            if(!qty.equals("") && measure.equals(""))
                total = qty + " " + ingredient;
            //add ingredient to list
            aList.add(new ListElement(total.trim()));
            //update screen
            aa.notifyDataSetChanged();
        }


        //set values to be displayed on screen in each view
        tvName.setText(name);
        tvMsg.setText(msg);
        imageView.setImageResource(displayedDrink.getImageID());


    }

    //goes to edit drink activity
    public void clickEdit(View V) {



        TextView tv1 = (TextView) findViewById(R.id.textView1);
        String text1 = tv1.getText().toString();


        TextView tv3 = (TextView) findViewById(R.id.textView3);





        DrinkRecipe displayedDrink = appInfo.savedDrinks.get(drinkPos);
        //appInfo.setIngredientsToEdit(displayedDrink.getIngredientList());
        appInfo.drinkToEdit = displayedDrink;

        // Go to first activity

        Intent intent = new Intent(this, EditDrinkActivity.class);

        //setFlag that drink being edited has already been created, rather than being a brand new drink
        intent.putExtra("alreadyCreated", true);
        //give position of drink in appInfo.savedDrinks
        intent.putExtra("drinkPosition", drinkPos);


        mp1.start();
        //go to edit activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //saves drink, used when display drink is accessed from library activity rather than main activity
    public void saveDrink()
    {

        //check if a drink exists in user's list
        if(isDuplicateName(displayedDrink.getName()))
        {
            duplicateDialog();
        }
        //else, add drink to user's list and increment download count of this drink to track popularity
        else
        {
            appInfo.addDrink(displayedDrink);
            mp3.start();

            incrementDownloads(displayedDrink);

        }
    }

    //checks if a drink in the user's list has the same name as this drink
    public boolean isDuplicateName(String name)
    {
        for(DrinkRecipe recipe : appInfo.savedDrinks)
        {
            if(name.equals(recipe.getName()))
                return true;
        }
        return false;
    }

    //alerts user that saving this drink to their list will mean they will have two drinks with
    //the same name
    public void duplicateDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayDrink.this);//.create();
        alertDialog.setTitle("Add Duplicate");
        mp5.start();

        alertDialog.setMessage("A drink with this name already exists in your log.\nAdd this drink anyway?(Existing drink will remain)")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if user is okay with dupliacte names, save drink and increment downloads to
                        //track popularity
                        appInfo.addDrink(displayedDrink);
                        mp3.start();

                        incrementDownloads(displayedDrink);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if user doesn't want duplicate, dismiss dialogue
                        mp4.start();
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        mp2.start();
        super.onBackPressed();
    }

    //increments the download count for this drink to track populatiry
    public void incrementDownloads(DrinkRecipe recipe)
    {
        //get this drink's unique id to increment its download count in the database
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("drinkId", recipe.uniqueId);

        }
        catch(Exception e)
        {

        }



        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://backendtest-165520.appspot.com/ndb_api/increment_download_count", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {

                        try
                        {


                            //when drink is added, return to library activity
                            onBackPressed();
                        }
                        catch(Exception e)
                        {




                            onBackPressed();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getBaseContext(), "Drink Not Added!", Toast.LENGTH_SHORT).show();


                onBackPressed();
            }
        });


        appInfo.queue.add(jsobj);


    }
}

