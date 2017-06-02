package com.austinbaird.liquorlog;


import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity
{



    //alswilli  private static int SPLASH_TIME_OUT = 4000;

    Button popupMenuButton;


    ArrayList<DrinkRecipe> rowItems; //the row items that are displayed in the list
    CustomListAdapter adapter; //used to make viewable items on screem from data in rowItems

    String logTag = "tag";

    AppInfo appInfo;


    static final public String MYPREFS = "myprefs";

    //used when dealing with editing drinks that have already been created, rather than creating
    //new drink from scratch
    Boolean alreadyCreated;

    //pos of drink in appInfo.savedDrinks
    int drinkPos;

    //sounds for this activity
    MediaPlayer mp1;
    MediaPlayer mp2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //set up sounds
        mp1 = MediaPlayer.create(this, R.raw.zeldaselecting);
        mp2 = MediaPlayer.create(this, R.raw.backbuttonsuper);

        //initalize to false, only used as intermediate value when moving between activites
        alreadyCreated = false;

        popupMenuButton = (Button)findViewById(R.id.butt1);

        //set up popup when edit button is clicked to give options of what to do, i.e
        //create new drink, add form library, delete
        popupMenuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, popupMenuButton);
                popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
                mp1.start();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId()){
                            case R.id.item_option1:
                                String toastMsg1 = "Creating New Drink";
                                Toast.makeText(getApplicationContext(),toastMsg1,Toast.LENGTH_SHORT).show();
                                mp1.start();
                                goToEdit(null);
                                break;

                            case R.id.item_option2:
                                String toastMsg2 = "Adding from Library";
                                Toast.makeText(getApplicationContext(),toastMsg2,Toast.LENGTH_SHORT).show();
                                mp1.start();
                                goToLibrary(null);
                                break;

                            case R.id.item_option3:
                                String toastMsg3 = "Select a Drink to Delete";
                                Toast.makeText(getApplicationContext(),toastMsg3,Toast.LENGTH_SHORT).show();
                                break;
                        }
                        //Toast.makeText(MainActivity.this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }

                });

                popupMenu.show();

            }
        });

        //initalize list of drinks to dispay on screen
        rowItems = new ArrayList<>();


        //attach adapter to rowItems and then attach adapter to list view to display drinks
        adapter = new CustomListAdapter(this, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        //when an item is clicked, go to display activity for this drink
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //get clicked drink
                DrinkRecipe drink = (DrinkRecipe)listView.getItemAtPosition(position);
                String toastMsg = "Displaying " + drink.getName();


                Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
                toast.show();

                mp1.start();

                //go to display activity and put position of drink in appInfo.sharedDrinks in extras
                Intent intent = new Intent(MainActivity.this, DisplayDrink.class);
                intent.putExtra("drinkPosition", position);
                startActivity(intent);
            }
        });


        appInfo = AppInfo.getInstance(this);



        SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();


        //special actions for first time user opens app
        if (settings.getBoolean("first_time", true)) {

            Log.d(logTag, "First time");

            //TODO

            editor.putBoolean("first_time", false);
            editor.commit();
            saveDrinksAsJSON();
        }

        //load saved drinks from shared preferences
        loadDrinksFromPrefs();

    }




    @Override
    protected void onResume()
    {
        super.onResume();

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            //get position of drink in appInfo.sharedDrinks
            drinkPos = extras.getInt("drinkPosition");
            //get boolean if drink is new or being edited
            alreadyCreated = extras.getBoolean("alreadyCreated");
        }

        //update rowItems
        rowItems.clear();
        for (DrinkRecipe recipe : appInfo.savedDrinks)
        {
            rowItems.add(recipe);
        }

        //update screen
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onBackPressed() {
        mp2.start();
        super.onBackPressed();
    }



    @Override
    protected void onPause()
    {
        // save drinks in shared preferences when paused
        saveDrinksAsJSON();

        super.onPause();
    }


    //go to edit activity
    public void goToEdit(View v)
    {
        Intent intent = new Intent(MainActivity.this, EditDrinkActivity.class);
        //if we go to edit from main, we are creating a brand new drink, so appInfo.drinkToEdit
        //will be empty
        appInfo.drinkToEdit = new DrinkRecipe("", null, "");
        startActivity(intent);
    }

    //go to library
    public void goToLibrary(View v)
    {
        Intent intent = new Intent(MainActivity.this, LibraryActivity.class);

        startActivity(intent);
    }


    //save drinks to shared preferences as json string
    public void saveDrinksAsJSON()
    {

        JSONArray jArray = new JSONArray();
        for(DrinkRecipe savedRecipe : appInfo.savedDrinks)
        {
            try {

                jArray.put(savedRecipe.drinkAsJSON);
            }
            catch(Exception e)
            {
                //json key access didn't work
            }
        }


        SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("drinksAsJSON", jArray.toString());
        editor.commit();
    }



    public void deleteDrink(View v)
    {
        if(alreadyCreated)
        {
            appInfo.savedDrinks.remove(drinkPos);
        }



        saveDrinksAsJSON();


    }

    //load drinks from shared preferences
    public void loadDrinksFromPrefs()
    {
        //get preferences
        SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
        String drinksAsJSON = settings.getString("drinksAsJSON", null);

        //make sure there are drinks to load!
        if(drinksAsJSON == null)
        {
            Log.d(logTag, "No drinks!");
            return;
        }

        try
        {
            JSONArray jArray = new JSONArray(drinksAsJSON);

            //clear array list before adding elements to it
            appInfo.savedDrinks.clear();
            for(int i = 0; i < jArray.length(); i++)
            {
                //get current drink as JSON
                JSONObject jsonDrinkRecipe = jArray.getJSONObject(i);
                //get name
                String name = jsonDrinkRecipe.getString("name");

                //get each ingredient as JSON and convert to an Ingredient Object
                JSONArray jsonIngredients = jsonDrinkRecipe.getJSONArray("ingredients");
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for(int j = 0; j < jsonIngredients.length(); j++)
                {
                    JSONObject jsonIngredientComponents = jsonIngredients.getJSONObject(j);
                    String qty = jsonIngredientComponents.getString("qty");
                    String measure = jsonIngredientComponents.getString("measure");
                    String ingName = jsonIngredientComponents.getString("name");
                    //add Ingredient Object to ingredient list
                    ingredients.add(new Ingredient(qty, measure, ingName));
                }

                //get message and img id
                String msg = jsonDrinkRecipe.getString("msg");
                int img = jsonDrinkRecipe.getInt("img");
                //add DrinkRecipe to appInfo.savedDrinks
                appInfo.savedDrinks.add(new DrinkRecipe(name, ingredients, msg, img));

            }

        }
        catch(Exception e)
        {
            Log.d(logTag, "JSON loading failed");
        }

    }
}
