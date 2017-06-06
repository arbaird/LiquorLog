package com.austinbaird.liquorlog;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;

import android.view.MenuItem;


import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;



import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;





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
                                mp1.start();
                                deleteDrink(null);
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

        //editor.putBoolean("first_time", true);
        //editor.commit();

        //special actions for first time user opens app
        if (settings.getBoolean("first_time", true)) {

            //display message to welcome user to LiquorList!
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Welcome!");
            alertDialog.setMessage("Welcome to LiquorList! To get started, press 'Edit' below" +
                    " to start creating your own drinks or load drinks from the library!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    });

            alertDialog.show();

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
        TextView hintText = (TextView) findViewById(R.id.textViewHint);
        if(rowItems.size() == 0)
            hintText.setVisibility(View.VISIBLE);
        else
            hintText.setVisibility(View.INVISIBLE);

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
        intent.putExtra("fromMain", true);

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

        adapter.addDeleteButtons();


        popupMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                cancel();
            }
        });
        popupMenuButton.setText("Done");

    }

    public void cancel()
    {
        //get rid of delete buttons
        adapter.removeDeleteButtons();
        popupMenuButton.setText("Edit");

        //set the click listener back to its original onclick action
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
                                mp1.start();
                                deleteDrink(null);
                                break;
                        }
                        //Toast.makeText(MainActivity.this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }

                });

                popupMenu.show();

            }
        });

        //check to see if the size of the list became 0 and show message if it has
        TextView hintText = (TextView) findViewById(R.id.textViewHint);
        if(rowItems.size() == 0)
            hintText.setVisibility(View.VISIBLE);
        else
            hintText.setVisibility(View.INVISIBLE);


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
