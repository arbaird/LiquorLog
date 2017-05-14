package com.austinbaird.liquorlog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;


public class LibraryActivity extends AppCompatActivity
{

    public ArrayList<DrinkRecipe> databaseDrinks;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    //ArrayList<String> alName;
    //ArrayList<Integer> alImage;

    public String logTag = "Library";
    AppInfo appInfo;
    CustomListAdapter adapter; //used to make viewable items on screem from data in rowItems

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(logTag, "FUCKING LIBRARY");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        appInfo = AppInfo.getInstance(this);


        databaseDrinks = new ArrayList<>();


        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(false);

        // The number of Columns
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, databaseDrinks);//databaseDrinks);
        mRecyclerView.setAdapter(mAdapter);


        /*mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d(logTag, "You clicked " + position);
                        appInfo.drinkFromLib = databaseDrinks.get(position);
                        Intent intent = new Intent(LibraryActivity.this, DisplayDrink.class);
                        intent.putExtra("fromLib", true);
                        startActivity(intent);
                    }
                    @Override public void onItemLongClick(View v, int pos)
                    {
                        //nothing
                    }
                }));*/
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d(logTag, "You clicked " + position);
                        appInfo.drinkFromLib = databaseDrinks.get(position);
                        Intent intent = new Intent(LibraryActivity.this, DisplayDrink.class);
                        intent.putExtra("fromLib", true);
                        startActivity(intent);
                    }
                    @Override public void onItemLongClick(View v, int pos)
                    {
                        //nothing
                    }
                }));

        loadRecipes(null);

    }
    @Override
    protected void onResume()
    {


        super.onResume();




        Log.d(logTag, "PLEEEAAASSSEEEE");






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
                            decodeJSON(receivedList);

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

    public void decodeJSON(JSONArray jArray)
    {

        try
        {
            //JSONArray jArray = new JSONArray(drinksAsJSON);
            Log.d(logTag, jArray.toString());

            databaseDrinks.clear();
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
                databaseDrinks.add(new DrinkRecipe(name, ingredients, msg));
            }

        }
        catch(Exception e)
        {
            Log.d(logTag, "JSON loading failed");
        }

        mAdapter.notifyDataSetChanged();
    }
}
