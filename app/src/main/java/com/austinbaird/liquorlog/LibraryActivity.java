package com.austinbaird.liquorlog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.ImageView;

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
    RecyclerView userMadeRecyclerView;
    RecyclerView.Adapter userMadeAdapter;

    public ArrayList<DrinkRecipe> searchDrinks;
    RecyclerView searchRecyclerView;
    RecyclerView.Adapter searchAdapter;

    public ArrayList<DrinkRecipe> libDrinks;
    RecyclerView libRecyclerView;
    RecyclerView.Adapter libAdapter;

    SearchView searchView;


    public String logTag = "Library";
    AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_library);
            appInfo = AppInfo.getInstance(this);

            //Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(myToolbar);

            searchView=(SearchView) findViewById(R.id.searchView);
            //searchView.setQueryHint("Drink Name or Ingredient");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                    search(query, null, null);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();

                    return false;
                }
            });
         // Catch event on [x] button inside search view
             int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
                ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
                // Set on click listener
                closeButton.setOnClickListener(new View.OnClickListener() {
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
            });



        initRecycleViews();

        Log.d(logTag, userMadeAdapter.toString());
        Log.d(logTag, libAdapter.toString());


        loadRecipes(null, /*"get_userMade_recipes"*/"get_recipes_fancy", databaseDrinks, userMadeAdapter);
        loadRecipes(null, "get_library_recipes", libDrinks, libAdapter);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }



    /*
    This function prints every recipe in the cloud datastore to the console.
     */
    public void loadRecipes(View v, String apiMethod, final ArrayList<DrinkRecipe> drinkArrayList, final RecyclerView.Adapter adapter)
    {

        String url = "https://backendtest-165520.appspot.com/ndb_api/" + apiMethod;//get_userMade_recipes";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(logTag, "Received: " + response.toString());
                        // Ok, let's disassemble a bit the json object.
                        try {
                            JSONArray receivedList = response.getJSONArray("results");
                            decodeJSON(receivedList, drinkArrayList, adapter);

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

    public void search(String q, final ArrayList<DrinkRecipe> drinkArrayList, final RecyclerView.Adapter adapter)
    {

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("q",q);

        }
        catch(Exception e)
        {

        }

        if (searchRecyclerView == null)
        {
            searchDrinks = new ArrayList<>();
            searchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
            searchRecyclerView.setHasFixedSize(false);

            RecyclerView.LayoutManager userLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            searchRecyclerView.setLayoutManager(userLayoutManager);

            searchAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, searchDrinks);//databaseDrinks);
            searchRecyclerView.setAdapter(searchAdapter);
            searchRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, userMadeRecyclerView, new drinkListener(searchDrinks)));
        }



        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://backendtest-165520.appspot.com/ndb_api/search", obj,/*new JSONObject(params),*/
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(logTag, "Received: " + response.toString());
                        try
                        {
                            userMadeRecyclerView.setVisibility(View.GONE);
                            libRecyclerView.setVisibility(View.GONE);
                            searchRecyclerView.setVisibility(View.VISIBLE);

                            String responseString = response.getString("results");
                            Log.d(logTag,responseString );

                            JSONArray receivedList = response.getJSONArray("results");
                            decodeJSON(receivedList, searchDrinks, searchAdapter);

                        }
                        catch(Exception e)
                        {
                            //mapped value was not a string, didn't exist, etc
                            Log.d(logTag, "Couldn't get mapped value for key 'results' ");
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


    }



    public void decodeJSON(JSONArray jArray, ArrayList<DrinkRecipe> drinkArrayList, RecyclerView.Adapter adapter)
    {

        try
        {
            //JSONArray jArray = new JSONArray(drinksAsJSON);
            Log.d(logTag, jArray.toString());
            drinkArrayList.clear();
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

                int imgId = Integer.parseInt(jsonDrinkRecipe.getString("imgId"));
                String msg = jsonDrinkRecipe.getString("msg");
                drinkArrayList.add(new DrinkRecipe(name, ingredients, msg, imgId));
            }

        }
        catch(Exception e)
        {
            Log.d(logTag, "JSON loading failed");
        }

        adapter.notifyDataSetChanged();
    }

    public void initRecycleViews()
    {
        databaseDrinks = new ArrayList<>();

        // Calling the RecyclerView
        userMadeRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        userMadeRecyclerView.setHasFixedSize(false);

        // The number of Columns
        RecyclerView.LayoutManager userLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        userMadeRecyclerView.setLayoutManager(userLayoutManager);

        userMadeAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, databaseDrinks);//databaseDrinks);
        userMadeRecyclerView.setAdapter(userMadeAdapter);


        userMadeRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, userMadeRecyclerView, new drinkListener(databaseDrinks)));



        libDrinks = new ArrayList<>();

        libRecyclerView = (RecyclerView) findViewById(R.id.lib_recycler_view);
        libRecyclerView.setHasFixedSize(false);

        // The number of Columns
        RecyclerView.LayoutManager  libLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        libRecyclerView.setLayoutManager(libLayoutManager);

        libAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, libDrinks);//databaseDrinks);
        libRecyclerView.setAdapter(libAdapter);


        libRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, userMadeRecyclerView, new drinkListener(libDrinks)));
    }





    class drinkListener implements RecyclerItemClickListener.OnItemClickListener
    {
        ArrayList<DrinkRecipe> drinks;
        drinkListener(ArrayList<DrinkRecipe> drinks)
        {
            this.drinks = drinks;
        }

        @Override public void onItemClick(View view, int position) {
            //Log.d(logTag, "You clicked " + position);
            appInfo.drinkFromLib = drinks.get(position);
            Intent intent = new Intent(LibraryActivity.this, DisplayDrink.class);
            intent.putExtra("fromLib", true);
            startActivity(intent);
        }
        @Override public void onItemLongClick(View v, int pos)
        {
            //nothing
        }
    }




}
