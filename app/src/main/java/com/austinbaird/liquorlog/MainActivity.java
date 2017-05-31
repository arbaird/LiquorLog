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
    RequestQueue queue;

    //alswilli  private static int SPLASH_TIME_OUT = 4000;

    Button popupMenuButton;
    Button contextMenuButton;

    ArrayList<DrinkRecipe> rowItems; //the row items that are displayed in the list
    CustomListAdapter adapter; //used to make viewable items on screem from data in rowItems

    String logTag = "tag";

    AppInfo appInfo;

    static final public String MYPREFS = "myprefs";

    Boolean fromPause = false;

    Boolean alreadyCreated;

    int drinkPos;

    MediaPlayer mp1;
    MediaPlayer mp2;

    Boolean switchToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        /*new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);*/

        Log.d(logTag, "OnCreate()");

        //registerForContextMenu(contextMenuButton);
        //contextMenuButton = (Button)findViewById(R.id.butt1);
        //registerForContextMenu(contextMenuButton);

        mp1 = MediaPlayer.create(this, R.raw.zeldaselecting);
        mp2 = MediaPlayer.create(this, R.raw.backbuttonsuper);

        alreadyCreated = false;

        popupMenuButton = (Button)findViewById(R.id.butt1);

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
                                //addDrink(null);
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


        rowItems = new ArrayList<>();

            /*closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    if(searchRecyclerView != null)
                    {

                        searchDrinks.clear();
                        searchAdapter.notifyDataSetChanged();
                        searchRecyclerView.setVisibility(View.GONE);

                        userMadeRecyclerView.setVisibility(View.VISIBLE);
                        libRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            });*/

        adapter = new CustomListAdapter(this, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DrinkRecipe drink = (DrinkRecipe)listView.getItemAtPosition(position);
                String toastMsg = "Displaying " + drink.getName();
                try
                {
                    Log.d(logTag, "First ingredient is: " + drink.getIngredientList().get(0).getIngredient());
                }
                catch(Exception e)
                {
                    Log.d(logTag, "No ingredients defined for " + drink.getName());
                }

                Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
                toast.show();

                mp1.start();

                Intent intent = new Intent(MainActivity.this, DisplayDrink.class);
                intent.putExtra("drinkPosition", position);
                startActivity(intent);
            }
        });

        /*adapter = new CustomListAdapter(this, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        Button delete = (Button) newView.findViewById(R.id.itemButton);
        delete.setText("Delete");

        // Sets a listener for the button, and a tag for the button as well.
        delete.setTag(new Integer(position));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reacts to a button press.
                // Gets the integer tag of the button.
                String s = v.getTag().toString();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, s, duration);
                toast.show();
                // Let's remove the list item.
                int i = Integer.parseInt(s);
                data.remove(i);//TODO i = position
                notifyDataSetChanged();
            }
        });*/


        /*if(switchToDelete != null)
        {
            searchDrinks.clear();
            searchAdapter.notifyDataSetChanged();
            searchRecyclerView.setVisibility(View.GONE);

            userMadeRecyclerView.setVisibility(View.VISIBLE);
            libRecyclerView.setVisibility(View.VISIBLE);
        }*/

        appInfo = AppInfo.getInstance(this);



        SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();

        /*editor.putString("drinksAsJSON", null);
        editor.commit();
        editor.putBoolean("first_time", true);
        editor.commit();*/

        if (settings.getBoolean("first_time", true)) {
            //the app is being launched for first time, do something
            Log.d(logTag, "First time");


            ArrayList<Ingredient> ingredients1 = new ArrayList();
            ingredients1.add(new Ingredient("3", "dashes", "rum"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));
            ingredients1.add(new Ingredient("9", "quarts", "beer"));


            ArrayList<Ingredient> ingredients2 = new ArrayList();
            ingredients2.add(new Ingredient("1", "pint", "whiskey"));

            ArrayList<Ingredient> ingredients3 = new ArrayList();
            ingredients3.add(new Ingredient("2", "oz", "tequila"));

            appInfo.addDrink(new DrinkRecipe("Long Island", ingredients1, "Stir some shit"));
            appInfo.addDrink(new DrinkRecipe("Manhattan", ingredients2, "Pour some shit"));
            appInfo.addDrink(new DrinkRecipe("Black and Tan", ingredients3, "Bake some shit hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"));



            editor.putBoolean("first_time", false);
            editor.commit();
            saveDrinksAsJSON();
        }

        loadDrinksFromPrefs();

    }




    @Override
    protected void onResume()
    {
        super.onResume();

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            //image = extras.getInt("imageId");
            drinkPos = extras.getInt("drinkPosition");
            alreadyCreated = extras.getBoolean("alreadyCreated");
            Log.d(logTag, "editing already created?" + alreadyCreated);
        }

        rowItems.clear();
        for (DrinkRecipe recipe : appInfo.savedDrinks)
        {
            rowItems.add(recipe);
        }



        //rowItems = new ArrayList<DrinkRecipe>(appInfo.savedDrinks);
        //rowItems = appInfo.savedDrinks;


        adapter.notifyDataSetChanged();
        //Log.d(logTag, "rowItems size before add: " + rowItems.size());

    }

    @Override
    public void onBackPressed() {
        mp2.start();
        super.onBackPressed();
    }

    @Override
    protected void onStart()
    {

        Log.d(logTag, "OnStart()");


        super.onStart();
    }

    @Override
    protected void onPause()
    {
        //fromPause = true;
        //Log.d(logTag, "We paused main");
        saveDrinksAsJSON();

        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        Log.d(logTag, "onRestart()");


        super.onRestart();
    }


    public void goToEdit(View v)
    {
        Intent intent = new Intent(MainActivity.this, EditDrinkActivity.class);
        //Log.d(logTag, "rowItems size before going to new activity: " + rowItems.size());
        appInfo.drinkToEdit = new DrinkRecipe("", null, "");
        startActivity(intent);
    }

    public void goToLibrary(View v)
    {
        Intent intent = new Intent(MainActivity.this, LibraryActivity.class);

        //Log.d(logTag, "rowItems size before going to new activity: " + rowItems.size());
        //appInfo.sharedString1 = null;
        //appInfo.sharedString2 = null;
        startActivity(intent);
    }


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
        //Log.d(logTag, "After saving, json array is: " + jArray.toString());

        SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("drinksAsJSON", jArray.toString());
        editor.commit();
    }

    public void deleteListView(View v)
    {

    }

    public void deleteDrink(View v)
    {
        if(alreadyCreated)
        {
            appInfo.savedDrinks.remove(drinkPos);
        }

        //Intent intent = new Intent(EditDrinkActivity.this, MainActivity.class);
        appInfo.sharedString1 = null;
        appInfo.sharedString2 = null;

        saveDrinksAsJSON();

        //startActivity(intent);
    }

    public void loadDrinksFromPrefs()
    {

        SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
        String drinksAsJSON = settings.getString("drinksAsJSON", null);
        if(drinksAsJSON == null)
        {
            Log.d(logTag, "No drinks!");
            return;
        }
        try
        {
            JSONArray jArray = new JSONArray(drinksAsJSON);
            Log.d(logTag, jArray.toString());

            appInfo.savedDrinks.clear();
            for(int i = 0; i < jArray.length(); i++)//JSONArray drinkRecipe : jArray)
            {
                JSONObject jsonDrinkRecipe = jArray.getJSONObject(i);
                String name = jsonDrinkRecipe.getString("name"); //name of drink is first element in each list

                JSONArray jsonIngredients = jsonDrinkRecipe.getJSONArray("ingredients");
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for(int j = 0; j < jsonIngredients.length(); j++)
                {
                    JSONObject jsonIngredientComponents = jsonIngredients.getJSONObject(j);
                    String qty = jsonIngredientComponents.getString("qty");
                    String measure = jsonIngredientComponents.getString("measure");
                    String ingName = jsonIngredientComponents.getString("name");
                    ingredients.add(new Ingredient(qty, measure, ingName));
                }

                String msg = jsonDrinkRecipe.getString("msg");
                Boolean userMade = jsonDrinkRecipe.getBoolean("userMade");
                int img = jsonDrinkRecipe.getInt("img");
                appInfo.savedDrinks.add(new DrinkRecipe(name, ingredients, msg, img));

            }

        }
        catch(Exception e)
        {
            Log.d(logTag, "JSON loading failed");
        }

    }
}
