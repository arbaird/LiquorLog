package com.austinbaird.liquorlog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;

import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import android.app.ProgressDialog;


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

    public ArrayList<DrinkRecipe> popDrinks;
    RecyclerView popRecyclerView;
    RecyclerView.Adapter popAdapter;

    SearchView searchView;

    MediaPlayer mp1;
    MediaPlayer mp2;
    MediaPlayer mp3;
    //MediaPlayer mp4;
    MediaPlayer mp5;
    MediaPlayer mp6;

    boolean enteringLibrary;

    public String logTag = "Library";
    AppInfo appInfo;

    AlertDialog.Builder alertDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_library);
            appInfo = AppInfo.getInstance(this);

            mp1 = MediaPlayer.create(this, R.raw.librarysong);
            mp2 = MediaPlayer.create(this, R.raw.zeldaselecting);
            mp3 = MediaPlayer.create(this, R.raw.backbutton);
            mp6 = MediaPlayer.create(this, R.raw.zeldacancel);
            //mp4 = MediaPlayer.create(this, R.raw.zeldasearchopen); // Didn't know where to put this for proper function
            mp5 = MediaPlayer.create(this, R.raw.zeldasearchclick);

            //mp1.start();
            //mp1.setLooping(true);

            //Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(myToolbar);

        //mp1.setLooping(true);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(myToolbar);




        searchView=(SearchView) findViewById(R.id.searchView);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) findViewById(R.id.refreshbutton);
                btn.setVisibility(View.GONE);
                Log.d(logTag, "searchCLick");
            }
        });

            /*searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button btn = (Button) findViewById(R.id.refreshbutton);
                    btn.setVisibility(View.GONE);
                }
            });*/
            //searchView.setQueryHint("Drink Name or Ingredient");


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(getBaseContext(), "Displaying results for phrase '" + query + "' in Library", Toast.LENGTH_LONG).show();
                    mp5.start();
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
                    mp6.start();

                    if(searchRecyclerView != null)
                    {

                        searchDrinks.clear();
                        searchAdapter.notifyDataSetChanged();
                        searchRecyclerView.setVisibility(View.GONE);

                        userMadeRecyclerView.setVisibility(View.VISIBLE);
                        libRecyclerView.setVisibility(View.VISIBLE);
                        popRecyclerView.setVisibility(View.VISIBLE);

                        TextView userText = (TextView)findViewById(R.id.textViewUserDrinks);
                        userText.setVisibility(View.VISIBLE);
                        TextView libText = (TextView)findViewById(R.id.textViewLibraryDrinks);
                        libText.setVisibility(View.VISIBLE);
                        TextView popText = (TextView)findViewById(R.id.textViewPopularDrinks);
                        popText.setVisibility(View.VISIBLE);

                        Button btn = (Button) findViewById(R.id.refreshbutton);
                        btn.setVisibility(View.VISIBLE);

                    }
                    Button btn = (Button) findViewById(R.id.refreshbutton);
                    btn.setVisibility(View.VISIBLE);
                }
            });



        initRecycleViews();

        Log.d(logTag, userMadeAdapter.toString());
        Log.d(logTag, libAdapter.toString());


        loadRecipes(null, /*"get_userMade_recipes"*/"get_recipes_fancy", databaseDrinks, userMadeAdapter);
        loadRecipes(null, "get_library_recipes", libDrinks, libAdapter);
        loadRecipes(null, "get_popular_drinks", popDrinks, popAdapter);
    }

    @Override
    protected void onPause() {
        //mp1.stop();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        mp1.stop();
        mp3.start();

        //enteringLibrary = false;
        super.onBackPressed();


    }

    @Override
    protected void onStart() {

        //if(enteringLibrary == true)
        //{
            //mp1.start();
            //mp1.setLooping(true);
           // enteringLibrary = true;
        //}
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        //if(mp1.isPlaying())
        //{
            //do nothing
        //}
        //else
        //{
            //mp1.start();
            //mp1.setLooping(true);
            //enteringLibrary = true;
        //}
        mp1.start();
        mp1.setLooping(true);
        Log.d(logTag, "LIB ON RESUME");
        loadRecipes(null, "get_recipes_fancy", databaseDrinks, userMadeAdapter);
        loadRecipes(null, "get_library_recipes", libDrinks, libAdapter);
        loadRecipes(null, "get_popular_drinks", popDrinks, popAdapter);
        super.onResume();
    }



    /*
    This function prints every recipe in the cloud datastore to the console.
     */
    public void loadRecipes(View v, String apiMethod, final ArrayList<DrinkRecipe> drinkArrayList, final RecyclerView.Adapter adapter)
    {

        final ProgressDialog progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Loading");
        progress.setMessage("Loading recipes...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        String url = "https://backendtest-165520.appspot.com/ndb_api/" + apiMethod;//get_userMade_recipes";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progress.dismiss();
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
                        progress.dismiss();

                        if (alertDialog == null)
                        {
                            alertDialog = new AlertDialog.Builder(LibraryActivity.this);
                            alertDialog.setTitle("No Connection");
                            alertDialog.setMessage("Sorry, there was an issue connecting to server.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.dismiss();
                                            alertDialog = null;
                                        }
                                    });


                            alertDialog.show();
                        }

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
                            popRecyclerView.setVisibility(View.GONE);

                            TextView userText = (TextView)findViewById(R.id.textViewUserDrinks);
                            userText.setVisibility(View.GONE);
                            TextView libText = (TextView)findViewById(R.id.textViewLibraryDrinks);
                            libText.setVisibility(View.GONE);
                            TextView popText = (TextView)findViewById(R.id.textViewPopularDrinks);
                            popText.setVisibility(View.GONE);


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
                String uniqueId = jsonDrinkRecipe.getString("uniqueid");
                String msg = jsonDrinkRecipe.getString("msg");
                //Log.d(logTag, name + " has unique id " + uniqueId);
                int downloads = jsonDrinkRecipe.getInt("downloads");
                drinkArrayList.add(new DrinkRecipe(name, ingredients, msg, imgId, uniqueId, downloads));
            }

        }
        catch(Exception e)
        {
            Log.d(logTag, "JSON loading failed");
        }

        adapter.notifyDataSetChanged();
    }

    public void refreshUserDrinks(View v)
    {
        loadRecipes(v, "get_recipes_fancy", databaseDrinks, userMadeAdapter);
        loadRecipes(v, "get_library_recipes", libDrinks, libAdapter);
        loadRecipes(v, "get_popular_drinks", popDrinks, popAdapter);
        Log.d(logTag, "refresh");
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


        popDrinks = new ArrayList<>();

        popRecyclerView = (RecyclerView) findViewById(R.id.pop_recycler_view);
        popRecyclerView.setHasFixedSize(false);

        // The number of Columns
        RecyclerView.LayoutManager  popLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popRecyclerView.setLayoutManager(popLayoutManager);

        popAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, popDrinks);//databaseDrinks);
        popRecyclerView.setAdapter(popAdapter);


        popRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, popRecyclerView, new drinkListener(popDrinks)));

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
            String drinkName = appInfo.drinkFromLib.getName().toString();
            Intent intent = new Intent(LibraryActivity.this, DisplayDrink.class);
            intent.putExtra("fromLib", true);
            String toastMsg = "Displaying " + drinkName;
            Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
            toast.show();
            mp2.start();
            startActivity(intent);
        }
        @Override public void onItemLongClick(View v, int pos)
        {
            //nothing
        }
    }




}
