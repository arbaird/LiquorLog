package com.austinbaird.liquorlog;


import android.content.Intent;
import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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

    ArrayList<DrinkRecipe> rowItems; //the row items that are displayed in the list
    CustomListAdapter adapter; //used to make viewable items on screem from data in rowItems

    String logTag = "tag";

    AppInfo appInfo;

    Boolean fromPause = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        rowItems = new ArrayList<>();
        //add values to test rowItems and adapter properly display to screen
        rowItems.add(new DrinkRecipe("Long Island", R.drawable.ic_launcher));
        rowItems.add(new DrinkRecipe("Manhattan", R.drawable.ic_launcher));
        rowItems.add(new DrinkRecipe("Black and Tan", R.drawable.ic_launcher));




        adapter = new CustomListAdapter(this, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        appInfo = AppInfo.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DrinkRecipe drink = (DrinkRecipe)listView.getItemAtPosition(position);
                //String url = newsInfo.getURL();
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
                /*Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
                intent.putExtra("URL", url);
                startActivity(intent);*/
            }
        });

    }

    @Override
    protected void onResume()
    {

        Log.d(logTag, "fromPause is " + fromPause);
        if(!fromPause)
        {
            for (DrinkRecipe recipe : appInfo.savedDrinks)
            {
                rowItems.add(recipe);
            }

        }

        adapter.notifyDataSetChanged();
        //Log.d(logTag, "rowItems size before add: " + rowItems.size());


        Bundle extras = getIntent().getExtras();
        /*if(extras != null)
        {
            if(extras.getBoolean("drinkAdded") && !fromPause)
            {

                //rowItems.add(appInfo.savedDrinks.get(appInfo.savedDrinks.size()-1)); //add last recipe in appInfo, since this will be the newest element
                //adapter.notifyDataSetChanged();
                addDrinkToNDB(null, appInfo.savedDrinks.get(appInfo.savedDrinks.size()-1));
                //Log.d(logTag, "rowItems size after add: " + rowItems.size());
                fromPause = false;


            }

        }*/
        super.onResume();


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


        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        Log.d(logTag, "onRestart()");


        super.onRestart();
    }



    public void addDrinkToNDB(View v, DrinkRecipe recipe)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", recipe.getName());
        params.put("msg", recipe.getMsg());

        JSONArray jArray = new JSONArray();
        for(Ingredient ingredient : recipe.getIngredientList())
        {
            JSONArray ingredientComponents = new JSONArray();
            ingredientComponents.put(ingredient.getQty());
            ingredientComponents.put(ingredient.getMeasure());
            ingredientComponents.put(ingredient.getIngredient());
            jArray.put(ingredientComponents);
            //jArray.put(new JSONArray(ingredient)); this would be ideal, but is unsupported before API 19 for android
        }



        final String ing = jArray.toString();
        Log.d(logTag, ing);

        params.put("ingredients", ing);
        //need to use JSON array so we store array properly in JSON format, i.e with "" around each element in list


        //copy pasted from hw3, changed url
        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://backendtest-165520.appspot.com/ndb_api/add_recipe", new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(logTag, "Received: " + response.toString());
                        try
                        {

                            String responseString = response.getString("result");
                            //detailView.setText(responseString);
                        }
                        catch(Exception e)
                        {
                            //mapped value was not a string, didn't exist, etc
                            Log.d(logTag, "Couldn't get mapped value for key 'result' ");
                            //detailView.setText("Couldn't get mapped value for key 'result' ");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //no error respsonse handling
            }
        });


        appInfo.queue.add(jsobj);

        //calling loadRecipes directly from add recipes to check in the console that recipes
        //are being added properly. A lot of repeats will be printed, I've been testing with the
        //same recipes via android, local host, app engine, etc.


        //add an actual row item on screen
        //adapter.add(new DrinkRecipe("Default", R.drawable.ic_launcher));
    }
    /*
    adds a default drink to the visible list on screen and a hardcoded drink recipe to the database.
    We'll adapt this later to add a user inputed drink recipe to the listview and database
     */
    public void addDrink(View v)
    {
        //add a row item

        //sample list of ingredients to add. In final version, these will be extracted from
        //a view that the user enters their input into
        //String[] ingredients = {"rum", "tequilla", "whiskey"};
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Mind Eraser");
        params.put("msg", "drinkIt");

        JSONArray jArray = new JSONArray();
        JSONArray ingredients = new JSONArray();
        ingredients.put("1");
        ingredients.put("cup");
        ingredients.put("tequilla");

        JSONArray ingredients2 = new JSONArray();
        ingredients2.put("3");
        ingredients2.put("dashes");
        ingredients2.put("rum");

        //jArray.put("rum");
        //jArray.put("tequilla");
        //jArray.put("whiskey");

        jArray.put(ingredients);
        jArray.put(ingredients2);


        final String ing = jArray.toString();
        Log.d(logTag, ing);

        params.put("ingredients", ing);
        //need to use JSON array so we store array properly in JSON format, i.e with "" around each element in list


        //copy pasted from hw3, changed url
        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://backendtest-165520.appspot.com/ndb_api/add_recipe", new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(logTag, "Received: " + response.toString());
                        try
                        {

                            String responseString = response.getString("result");
                            //detailView.setText(responseString);
                        }
                        catch(Exception e)
                        {
                            //mapped value was not a string, didn't exist, etc
                            Log.d(logTag, "Couldn't get mapped value for key 'result' ");
                            //detailView.setText("Couldn't get mapped value for key 'result' ");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //no error respsonse handling
            }
        });


        appInfo.queue.add(jsobj);

        //calling loadRecipes directly from add recipes to check in the console that recipes
        //are being added properly. A lot of repeats will be printed, I've been testing with the
        //same recipes via android, local host, app engine, etc.
        loadRecipes(v);

        //add an actual row item on screen
        adapter.add(new DrinkRecipe("Default", R.drawable.ic_launcher));
    }

    public void goToEdit(View v)
    {
        Intent intent = new Intent(MainActivity.this, EditDrinkActivity.class);
        Log.d(logTag, "rowItems size before going to new activity: " + rowItems.size());
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
}
