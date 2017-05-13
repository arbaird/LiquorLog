package com.austinbaird.liquorlog;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

    Button contextMenuButton;

    ArrayList<DrinkRecipe> rowItems; //the row items that are displayed in the list
    CustomListAdapter adapter; //used to make viewable items on screem from data in rowItems

    String logTag = "tag";

    AppInfo appInfo;

    static final public String MYPREFS = "myprefs";

    Boolean fromPause = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        Log.d(logTag, "OnCreate()");


        contextMenuButton = (Button)findViewById(R.id.butt1);
        registerForContextMenu(contextMenuButton);

        rowItems = new ArrayList<>();

        adapter = new CustomListAdapter(this, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DrinkRecipe drink = (DrinkRecipe)listView.getItemAtPosition(position);
                String toastMsg = "Going to " + drink.getName();
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

                Intent intent = new Intent(MainActivity.this, DisplayDrink.class);
                intent.putExtra("drinkPosition", position);
                startActivity(intent);
            }
        });


        appInfo = AppInfo.getInstance(this);



        SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();

        //editor.putString("drinksAsJSON", null);
        //editor.commit();
        //editor.putBoolean("first_time", true);
        //editor.commit();

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

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.main_menu,menu);
    }*/

    /*private void addOnClickListener()
    {
        contextMenuButton.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.showContextMenu();
            }
        })
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);

    }*/

    /*@Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_option1:
                String toastMsg1 = "Creating new drink";
                Toast.makeText(getApplicationContext(),toastMsg1,Toast.LENGTH_SHORT).show();
                //addDrink(null);
                goToEdit(null);
                break;

            case R.id.item_option2:
                String toastMsg2 = "Adding from Library ";
                Toast.makeText(getApplicationContext(),toastMsg2,Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_option1:
                Toast.makeText(getApplicationContext(),item.toString(),Toast.LENGTH_SHORT).show();
                break;

            case R.id.item_option2:
                Toast.makeText(getApplicationContext(),item.toString(),Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/


    @Override
    protected void onResume()
    {


        super.onResume();



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
    protected void onStart()
    {

        Log.d(logTag, "OnStart()");


        super.onStart();
    }

    @Override
    protected void onPause()
    {
        fromPause = true;
        Log.d(logTag, "We paused main");
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
        appInfo.sharedString1 = null;
        appInfo.sharedString2 = null;
        startActivity(intent);
    }
    /*
    This function prints every recipe in the cloud datastore to the console.
     */
    public void loadRecipes(View v)
    {
        String url = "https://backendtest-165520.appspot.com/ndb_api/get_recipes";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(logTag, "Received: " + response.toString());
                        // Ok, let's disassemble a bit the json object.
                        try {
                            JSONArray receivedList = response.getJSONArray("results");
                            String allTogether = "(";
                            for (int i = 0; i < receivedList.length(); i++) {
                                JSONObject obj = receivedList.getJSONObject(i);
                                Log.d(logTag, "got " + obj.get("ingredients"));
                            }
                            allTogether += ")";

                        } catch (Exception e) {
                            Log.d(logTag, "Aaauuugh, received bad json: " + e.getStackTrace());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d(logTag, error.toString());
                    }
                });

        // In some cases, we don't want to cache the request.
        // jsObjRequest.setShouldCache(false);

        appInfo.queue.add(jsObjRequest);
    }

    public void saveDrinksAsJSON()
    {

        JSONArray jArray = new JSONArray();
        for(DrinkRecipe savedRecipe : appInfo.savedDrinks)
        {
            try {
                JSONObject drinkRecipe = new JSONObject();
                drinkRecipe.put("name", savedRecipe.getName());

                JSONArray ingredientArray = new JSONArray();
                for (Ingredient ingredient : savedRecipe.getIngredientList()) {
                    try {
                        JSONObject ingredientComponents = new JSONObject();
                        ingredientComponents.put("qty", ingredient.getQty());
                        ingredientComponents.put("measure", ingredient.getMeasure());
                        ingredientComponents.put("name", ingredient.getIngredient());
                        ingredientArray.put(ingredientComponents);
                    } catch (Exception e) {

                    }
                    //jArray.put(new JSONArray(ingredient)); this would be ideal, but is unsupported before API 19 for android
                }
                drinkRecipe.put("ingredients",ingredientArray);
                drinkRecipe.put("msg", savedRecipe.getMsg());
                jArray.put(drinkRecipe);
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
                appInfo.savedDrinks.add(new DrinkRecipe(name, ingredients, msg));
            }

        }
        catch(Exception e)
        {
            Log.d(logTag, "JSON loading failed");
        }

    }
}
