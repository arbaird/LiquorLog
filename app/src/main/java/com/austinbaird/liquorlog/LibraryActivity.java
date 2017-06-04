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


import android.app.ProgressDialog;


public class LibraryActivity extends AppCompatActivity
{

    //recycler views and their adapters/ arraylists displayed in this activity
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

    //sounds used in this activity
    MediaPlayer mp1;
    MediaPlayer mp2;
    MediaPlayer mp3;
    //MediaPlayer mp4;
    MediaPlayer mp5;
    MediaPlayer mp6;



    public String logTag = "Library";
    AppInfo appInfo;

    AlertDialog.Builder alertDialog;

    //keeps track of if this is the first time lobrary is loaded, only refresh user drinks if it is first time
    //so that drinks are only randomized when user clicks refresh
    boolean firstLoad;

    boolean toDisplay;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        appInfo = AppInfo.getInstance(this);


        firstLoad = true;
        toDisplay = false;
        //get sounds used in this activity
        mp1 = MediaPlayer.create(this, R.raw.librarysong);
        mp2 = MediaPlayer.create(this, R.raw.zeldaselecting);
        mp3 = MediaPlayer.create(this, R.raw.backbutton);
        mp6 = MediaPlayer.create(this, R.raw.zeldacancel);
        //mp4 = MediaPlayer.create(this, R.raw.zeldasearchopen); // Didn't know where to put this for proper function
        mp5 = MediaPlayer.create(this, R.raw.zeldasearchclick);






        searchView=(SearchView) findViewById(R.id.searchView);

        //when search view is clicked, hids refresh button
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) findViewById(R.id.refreshbutton);
                btn.setVisibility(View.GONE);
                Log.d(logTag, "searchCLick");
            }
        });




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
                 //necessary to override, boiler plate

                    return false;
                }
            });
        // Catch event on [x] button inside search view
        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
        //When pressed, collapse search view and make recycler views visible again, and hide search recycler view
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


    }

    @Override
    protected void onPause() {
        if(!toDisplay)
            mp1.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        mp1.stop();
        mp3.start();

        super.onBackPressed();


    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        //set sound effects
        mp1.start();
        mp1.setLooping(true);

        //load recipes
        Bundle extras = getIntent().getExtras();

        //see if this activty was started from library activity
        if(extras.containsKey("fromMain") && extras.getBoolean("fromMain") && firstLoad)
        {

            refreshDrinks(null);
            firstLoad = false;
        }
        else
        {
            loadRecipes(null, "get_library_recipes", libDrinks, libAdapter);
            loadRecipes(null, "get_popular_drinks", popDrinks, popAdapter);
        }

        //reset where we are going to play music only when in this activity and display activity
        toDisplay = false;
        super.onResume();
    }



    /*
    This function loads recipes into an arraylist given an api method
     */
    public void loadRecipes(View v, String apiMethod, final ArrayList<DrinkRecipe> drinkArrayList, final RecyclerView.Adapter adapter)
    {

        //display spinning progress bar while waiting for database response
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

                        try
                        {
                            //load received list into the given arraylist and update the screen
                            JSONArray receivedList = response.getJSONArray("results");
                            decodeJSON(receivedList, drinkArrayList, adapter);

                        } catch (Exception e) {
                            Log.d(logTag, ""+ e.getStackTrace());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Dismiss progress bar and let user know there was an issue connecting to server
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
            Log.d(logTag, "Problem putting query in POST");
        }

        //initialize seach recylcer view if it is null
        if (searchRecyclerView == null)
        {
            //init arraylist and find recylcer view by id
            searchDrinks = new ArrayList<>();
            searchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
            searchRecyclerView.setHasFixedSize(false);

            //set horizontal layout
            RecyclerView.LayoutManager userLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            searchRecyclerView.setLayoutManager(userLayoutManager);

            //attack adapter to search array list and set this adapter to the recycler view on screen
            searchAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, searchDrinks);
            searchRecyclerView.setAdapter(searchAdapter);
            //add touch listener that goes to display drink
            searchRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, userMadeRecyclerView, new drinkListener(searchDrinks)));
        }



        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://backendtest-165520.appspot.com/ndb_api/search", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(logTag, "Received: " + response.toString());
                        try
                        {
                            //hide all other recycler views and display the search results
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


                            //load search results into the search arraylist and notify adapter to
                            //update the scrren
                            JSONArray receivedList = response.getJSONArray("results");
                            decodeJSON(receivedList, searchDrinks, searchAdapter);

                        }
                        catch(Exception e)
                        {
                            Log.d(logTag, "Issue with search response" + e.getStackTrace());
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


    /*
    Create DrinkRecipe objects from the JSON array and load them into the array list and then have
    adapter notify to update the screen
     */
    public void decodeJSON(JSONArray jArray, ArrayList<DrinkRecipe> drinkArrayList, RecyclerView.Adapter adapter)
    {

        try
        {
            //clear current arraylist to copy new drinks into it
            drinkArrayList.clear();
            for(int i = 0; i < jArray.length(); i++)
            {
                JSONObject jsonDrinkRecipe = jArray.getJSONObject(i);
                //get name
                String name = jsonDrinkRecipe.getString("name");

                //get ingredient list
                JSONArray jsonIngredients = jsonDrinkRecipe.getJSONArray("ingredients");
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                //add each component of json ingredient list to DrinkRecipe ingredient list
                for(int j = 0; j < jsonIngredients.length(); j++)
                {
                    JSONObject jsonIngredientComponents = jsonIngredients.getJSONObject(j);
                    String qty = jsonIngredientComponents.getString("qty");
                    String measure = jsonIngredientComponents.getString("measure");
                    String ingName = jsonIngredientComponents.getString("name");
                    ingredients.add(new Ingredient(qty, measure, ingName));
                }

                //get img id
                int imgId = Integer.parseInt(jsonDrinkRecipe.getString("imgId"));
                //get unique id to keep track of drinks that are downloaded so we can track popularity
                String uniqueId = jsonDrinkRecipe.getString("uniqueid");
                //get message
                String msg = jsonDrinkRecipe.getString("msg");
                //get downloads
                int downloads = jsonDrinkRecipe.getInt("downloads");
                //add this drink to the array list
                drinkArrayList.add(new DrinkRecipe(name, ingredients, msg, imgId, uniqueId, downloads));
            }

        }
        catch(Exception e)
        {
            Log.d(logTag, "JSON loading failed");
        }

        adapter.notifyDataSetChanged();
    }

    public void refreshDrinks(View v)
    {
        //reload all vrecycler views
        loadRecipes(v, "get_user_recipes", databaseDrinks, userMadeAdapter);
        loadRecipes(v, "get_library_recipes", libDrinks, libAdapter);
        loadRecipes(v, "get_popular_drinks", popDrinks, popAdapter);
        Log.d(logTag, "refresh");
    }

    /*
    initalize all the recycler views
     */
    public void initRecycleViews()
    {
        //init array list
        databaseDrinks = new ArrayList<>();

        // get recycler view
        userMadeRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        userMadeRecyclerView.setHasFixedSize(false);

        // set recycler view to horizontal layout
        RecyclerView.LayoutManager userLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        userMadeRecyclerView.setLayoutManager(userLayoutManager);

        //attach array list to adapter and attach adapter to recycler view on screen
        userMadeAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, databaseDrinks);//databaseDrinks);
        userMadeRecyclerView.setAdapter(userMadeAdapter);

        //add on touch listener that goes to display drink when touched
        userMadeRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, userMadeRecyclerView, new drinkListener(databaseDrinks)));


        //init array list
        libDrinks = new ArrayList<>();

        // get recycler view
        libRecyclerView = (RecyclerView) findViewById(R.id.lib_recycler_view);
        libRecyclerView.setHasFixedSize(false);

        // set recycler view to horizontal layout
        RecyclerView.LayoutManager  libLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        libRecyclerView.setLayoutManager(libLayoutManager);

        //attach array list to adapter and attach adapter to recycler view on screen
        libAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, libDrinks);//databaseDrinks);
        libRecyclerView.setAdapter(libAdapter);

        //add on touch listener that goes to display drink when touched
        libRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, userMadeRecyclerView, new drinkListener(libDrinks)));

        //init array list
        popDrinks = new ArrayList<>();

        // get recycler view
        popRecyclerView = (RecyclerView) findViewById(R.id.pop_recycler_view);
        popRecyclerView.setHasFixedSize(false);

        // set recycler view to horizontal layout
        RecyclerView.LayoutManager  popLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popRecyclerView.setLayoutManager(popLayoutManager);

        //attach array list to adapter and attach adapter to recycler view on screen
        popAdapter = new HorizontalDrinkAdapter(LibraryActivity.this, popDrinks);//databaseDrinks);
        popRecyclerView.setAdapter(popAdapter);

        //add on touch listener that goes to display drink when touched
        popRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, popRecyclerView, new drinkListener(popDrinks)));

    }




    /*
    same on click listener used by every recycler view. On click, it goes to display drink activity
    for the drinks that has been clicked on
     */
    class drinkListener implements RecyclerItemClickListener.OnItemClickListener
    {
        ArrayList<DrinkRecipe> drinks;
        drinkListener(ArrayList<DrinkRecipe> drinks)
        {
            this.drinks = drinks;
        }

        @Override public void onItemClick(View view, int position)
        {
            //store drink to be displayed in appInfo
            appInfo.drinkFromLib = drinks.get(position);
            String drinkName = appInfo.drinkFromLib.getName().toString();
            //create intent to go to Diplay Drink Activity
            Intent intent = new Intent(LibraryActivity.this, DisplayDrink.class);
            //put extra that specifies that we are coming from lib activity
            intent.putExtra("fromLib", true);
            String toastMsg = "Displaying " + drinkName;
            Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
            toast.show();
            toDisplay = true;
            mp2.start();
            startActivity(intent);
        }
        @Override public void onItemLongClick(View v, int pos)
        {
            //nothing, bolierplate necessary
        }
    }




}
