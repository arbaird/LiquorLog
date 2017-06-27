package com.austinbaird.liquorlog;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Button;
import android.content.Intent;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.ArrayList;
import android.view.View;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.austinbaird.liquorlog.R.id.editDrinkName;
import static com.austinbaird.liquorlog.R.id.imageViewDrink;
import static com.austinbaird.liquorlog.R.id.messageEnter;
import static com.austinbaird.liquorlog.R.id.textViewCharCount1;
import static com.austinbaird.liquorlog.R.id.textViewCharCount2;
import static com.austinbaird.liquorlog.R.id.textViewVharCount3;

/*
    Used to edit a drink and save to the user's list
 */
public class EditDrinkActivity extends AppCompatActivity
{
    //sounds for activity
    MediaPlayer mp1;
    MediaPlayer mp2;
    MediaPlayer mp3;
    MediaPlayer mp9;
    MediaPlayer mp7;
    MediaPlayer mp8;
    MediaPlayer mp10;

    //the row items that are displayed in the list
    ArrayList<Ingredient> rowItems;

    //used to make viewable items on screem from data in rowItems
    ScrollerListAdapter adapter;

    //fields that the user will fill in this activity
    Spinner qtySpinner;
    Spinner measureSpinner;
    Spinner fractionSpinner;
    EditText editName;
    EditText editMsg;
    EditText editIngredientName;

    //used for displaying char counts for text fields
    private TextView charCount;
    private TextView charCountIng;
    private TextView charCountMsg;

    String logTag = "tag";

    //info about drink being edited/created
    String drinkName;
    String msg;
    int image;

    AppInfo appInfo;

    //the drink's position in appInfo.sharedDrinks, if it is an already existing drink
    int drinkPos;

    //tracks if drink has already been created and is being edited or if it is a brand new one from scratch
    Boolean alreadyCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drink);
        appInfo = AppInfo.getInstance(this);

        //set up sounds
        mp1 = MediaPlayer.create(this, R.raw.zeldafanfare);
        mp10 = MediaPlayer.create(this, R.raw.backbutton);
        mp7 = MediaPlayer.create(this, R.raw.zeldadelete2);
        mp8 = MediaPlayer.create(this, R.raw.zeldacancel);
        mp9 = MediaPlayer.create(this, R.raw.cantsave);

        //attach sounds to play when buttons are clicked
        Button two = (Button)this.findViewById(R.id.btnAddIngredient);
        mp2 = MediaPlayer.create(this, R.raw.zeldamenuequip);
        mp3 = MediaPlayer.create(this, R.raw.zeldamenuno);
        two.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                String name = (String)editIngredientName.getText().toString();
                if(name.trim().equals(""))
                {
                    //if the name has not been inputted, don't add and display message
                    String toastMsg = "Specify ingredient name!";
                    Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
                    toast.show();
                    mp3.start();
                }
                else
                {
                    //else, add drink
                    String toastMsg = "Ingredient Added!";
                    Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
                    toast.show();
                    mp2.start();
                    add(null);
                }
            }
        });

        Button three = (Button)this.findViewById(R.id.EditImageButton);
        final MediaPlayer mp4 = MediaPlayer.create(this, R.raw.zeldaimageopen);
        three.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                mp4.start();
                editDrinkImage(null);
            }
        });

        Button four = (Button)this.findViewById(R.id.btnDeleteDrink);
        final MediaPlayer mp6 = MediaPlayer.create(this, R.raw.zeldadelete1);
        four.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                mp6.start();
                deleteDrink(null);
            }
        });


        //initalize values
        drinkName = "";
        msg = "";
        image = R.drawable.emptysmall;

        //initalize to false, may be updated to true in onResume
        alreadyCreated = false;

        //initalize array list and set adapter to this array list
        rowItems = new ArrayList<>();
        adapter = new ScrollerListAdapter(this, R.layout.edit_list_element, rowItems);

        //attach adapter to listview to display items on the screen
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        //get spinners on screen for convenience
        qtySpinner = (Spinner) findViewById(R.id.qtySpinner);
        fractionSpinner = (Spinner) findViewById(R.id.fractionSpinner);

        // Spinner Drop down elements, allow slection from nothing ("") to 10 for qty
        ArrayList<String> qtys = new ArrayList<String>();
        qtys.add("qty");
        qtys.add("");
        for(int i = 1; i < 11; i++)
        {
            qtys.add(Integer.toString(i));
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, qtys);

        // Drop down layout style - list view with delete button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        qtySpinner.setAdapter(dataAdapter);
        measureSpinner = (Spinner) findViewById(R.id.measureSpinner);

        //assign values to the textfield variables for convenience
        editName = (EditText) findViewById(editDrinkName);
        editMsg = (EditText)findViewById(messageEnter);
        editIngredientName = (EditText)findViewById(R.id.ingredientEdit);

        //track char count when entering text selections
        charCount = (TextView) findViewById(textViewCharCount1);
        charCountIng = (TextView) findViewById(textViewCharCount2);
        charCountMsg = (TextView) findViewById(textViewVharCount3);

        //set text watchers to count the number of characters that have been inputted
        final TextWatcher txwatcher1 = new TextWatcher()
        {
            //override necessary methods
            @Override
            public void afterTextChanged(Editable arg0)
            {
                if(editName.isFocused() && editName.getText().toString().trim().length() > 44)
                {
                    editName.setText(arg0.toString().substring(0, 44));
                    editName.setSelection(arg0.length()-1);
                    mp3.start();
                    Toast.makeText(EditDrinkActivity.this, "Maximum number of characters reached!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){}

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                if(editName.isFocused())
                {
                    charCount.setVisibility(View.VISIBLE);
                    charCount.setText("Chars = " + (String.valueOf(44 - arg0.length())));
                    if(arg0.length() > 34 && arg0.length() != 44) // Less than 10
                    {
                        charCount.setTextColor(Color.MAGENTA);
                    }
                    else if(arg0.length() == 44) //Equal to 0
                    {
                        charCount.setTextColor(Color.RED);
                    }
                    else // 10-44
                    {
                        charCount.setTextColor(Color.BLUE);
                    }
                }
                else if(editMsg.isFocused())
                {
                    charCount.setVisibility(View.VISIBLE);
                    charCount.setText("Chars = " + (String.valueOf(100 - arg0.length())));
                    if(arg0.length() > 90 && arg0.length() != 100) // Less than 10
                    {
                        charCount.setTextColor(Color.MAGENTA);
                    }
                    else if(arg0.length() == 100) //Equal to 0
                    {
                        charCount.setTextColor(Color.RED);
                    }
                    else // 10-44
                    {
                        charCount.setTextColor(Color.BLUE);
                    }
                }
            }
        };

        final TextWatcher txwatcher2 = new TextWatcher()
        {
            //override necessary methods
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before, int count)
            {
                if (editIngredientName.isFocused())
                {
                    charCountIng.setVisibility(View.VISIBLE);
                    charCountIng.setText("Chars = " + (String.valueOf(30 - arg0.length())));
                    if (arg0.length() > 25 && arg0.length() != 30) // Less than 5
                    {
                        charCountIng.setTextColor(Color.MAGENTA);
                    } else if (arg0.length() == 30) //Equal to 0
                    {
                        charCountIng.setTextColor(Color.RED);
                    } else // 5-60
                    {
                        charCountIng.setTextColor(Color.BLUE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable arg0)
            {
                if (editIngredientName.isFocused() && editIngredientName.getText().toString().trim().length() > 30)
                {
                    editIngredientName.setText(arg0.toString().substring(0, 30));
                    editIngredientName.setSelection(arg0.length() - 1);
                    mp3.start();
                    Toast.makeText(EditDrinkActivity.this, "Maximum number of characters reached!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final TextWatcher txwatcher3 = new TextWatcher()
        {
            //override necessary methods
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before, int count) {
                if (editMsg.isFocused()) {
                    charCountMsg.setVisibility(View.VISIBLE);
                    charCountMsg.setText("Chars = " + (String.valueOf(300 - arg0.length())));
                    if (arg0.length() > 200 && arg0.length() != 300) // Less than 100
                    {
                        charCountMsg.setTextColor(Color.MAGENTA);
                    } else if (arg0.length() == 300) //Equal to 0
                    {
                        charCountMsg.setTextColor(Color.RED);
                    } else // 200-300
                    {
                        charCountMsg.setTextColor(Color.BLUE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable arg0)
            {
                if (editMsg.isFocused() && editMsg.getText().toString().trim().length() > 300)
                {
                    editMsg.setText(arg0.toString().substring(0, 300));
                    editMsg.setSelection(arg0.length() - 1);
                    mp3.start();
                    Toast.makeText(EditDrinkActivity.this, "Maximum number of characters reached!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //assign textListeners to each text field
        editName.addTextChangedListener(txwatcher1);
        editMsg.addTextChangedListener(txwatcher3);
        editIngredientName.addTextChangedListener(txwatcher2);

        //save drink name if this edit text loses focus
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final EditText Caption = (EditText) v;
                    drinkName = Caption.getText().toString();
                }
            }
        });
    }

    //save inputted information if this activity loses focus
    @Override
    protected void onPause()
    {
        //create recipe and set appInfo.drinkToEdit
        msg = editMsg.getText().toString();
        drinkName = editName.getText().toString();

        ArrayList<Ingredient> ingredients = new ArrayList<>();

        for(Ingredient drink : rowItems)
        {
            ingredients.add(drink);
        }
        DrinkRecipe recipe = new DrinkRecipe(drinkName, ingredients, msg);
        recipe.setImageID(image);

        appInfo.drinkToEdit = recipe;
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        //set test fields and image
        editName.setText(appInfo.drinkToEdit.getName() );
        editMsg.setText(appInfo.drinkToEdit.getMsg());

        image = appInfo.drinkToEdit.getImageID();

        Bundle extras = getIntent().getExtras();

        //if there are extras, we assign to the boolean alreadyCreated to keep track of if this is a
        // brand new drink or an existingone that is being edited. We also get the position of the
        //drink in appInfo.sharedDrinks
        if(extras != null)
        {
            drinkPos = extras.getInt("drinkPosition");
            alreadyCreated = extras.getBoolean("alreadyCreated");
        }

        //if this is a drink that is an existing drink being edited, we get its ingredients and add
        //them to the screen
        if(appInfo.drinkToEdit.getIngredientList() != null)
        {
            rowItems.clear();
            for(Ingredient ingredient : appInfo.drinkToEdit.getIngredientList())
            {
                rowItems.add(new Ingredient(ingredient.getQty(), ingredient.getMeasure(), ingredient.getIngredient()));
            }
            adapter.notifyDataSetChanged();
        }

        //display selected image (this will be empty drink if no image has been selected)
        ImageView editImage = (ImageView) findViewById(imageViewDrink);
        editImage.setImageResource(image);

        super.onResume();
    }

    //go to edit drink image activity
    public void editDrinkImage(View v)
    {

        Intent intent = new Intent(EditDrinkActivity.this, EditDrinkImage.class);

        //keep track of if this drink is already created or not when returning from edit image activity
        intent.putExtra("alreadyCreated", alreadyCreated);

        //keep track of drink position in appInfo.sharedDrinks
        intent.putExtra("drinkPosition", drinkPos);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //save drink being edited in appInfo so entered info will remain after returning from edit
        //drink activity
        appInfo.drinkToEdit.setName(editName.getText().toString());
        appInfo.drinkToEdit.setMsg(editMsg.getText().toString());
        appInfo.drinkToEdit.setIngredientList(rowItems);

        startActivity(intent);
    }

    public void add(View v)
    {
        //get the entered name for the ingredient
        String name = editIngredientName.getText().toString();

        //make sure that a name for the ingredient has been entered, display warning Toast if not
        if(name.trim().equals(""))
        {
            String toastMsg = "Specify ingredient name!";
            Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //get entered qty and measure
        String qty = (String)qtySpinner.getSelectedItem();
        String measure = (String)measureSpinner.getSelectedItem();
        String fraction = (String)fractionSpinner.getSelectedItem();

        //if defaults were not changed, set values to blank strings
        if(qty.equals("qty"))
            qty = "";
        if(measure.equals("msr"))
            measure = "";

        //formatting, add space between qty and fraction if a fraction is specified
        if(!fraction.equals(""))
            qty += " " + fraction;

        //add new ingredient to rowItems and update screen
        rowItems.add(new Ingredient(qty, measure, name));
        adapter.notifyDataSetChanged();

        //reset selections
        qtySpinner.setSelection(0);
        fractionSpinner.setSelection(0);
        measureSpinner.setSelection(0);
        editIngredientName.setText("");

    }

    //save the drink to the user's list in main activity
    public void saveDrink(View v)
    {
        //get drink info
        msg = editMsg.getText().toString();
        drinkName = editName.getText().toString();

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for(Ingredient drink : rowItems)
        {
            ingredients.add(drink);
        }

        //make sure all entries have been filled before saving drink
        if(msg.isEmpty() || drinkName.isEmpty() || ingredients.size() == 0 || image == R.drawable.emptysmall)
        {
            mp9.start();
            Toast.makeText(EditDrinkActivity.this, "Drink Invalid! Please provide a Drink Name, Ingredient, Message/Procedure, and Drink Image", Toast.LENGTH_LONG).show();
            return;
        }
        //else, proceed to saving drink
        else
        {
            mp1.start();
            DrinkRecipe recipe;

            //if this is an already existing drink being edited, update the DrinkRecipe in the
            //specified position in appInfo.savedDrinks
            if(alreadyCreated)
            {
                recipe = appInfo.savedDrinks.get(drinkPos);
                recipe.setName(drinkName);
                recipe.setIngredientList(ingredients);
                recipe.setMsg(msg);
                recipe.setImageID(image);
            }
            //else, add this new drink to and of appInfo.savedDrinks
            else
            {
                recipe = new DrinkRecipe(drinkName, ingredients, msg);
                recipe.setImageID(image);
                appInfo.addDrink(recipe);
            }

            //save drink to database
            addDrinkToNDBDialogue(recipe);

            //clear appInfo.drinkToEdit
            appInfo.drinkToEdit = new DrinkRecipe("",null,"");

            saveDrinksAsJSON();
        }
    }

    //save drink recipe to database
    public void addDrinkToNDB(View v, DrinkRecipe recipe)
    {
        //create JSON representation of drink being added to database
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("name", recipe.getName());
            obj.put("msg", recipe.getMsg());
            obj.put("imgId", recipe.getImageID());

            JSONArray jArray = new JSONArray();
            for (Ingredient ingredient : recipe.getIngredientList())
            {
                jArray.put(ingredient.getJsonIngredient());
            }
            final String ing = jArray.toString();
            obj.put("ingredients", ing);
            obj.put("userCreated", recipe.userMade);
        }
        catch(Exception e)
        {
            Log.d(logTag, "Didn't create JSON properly");
        }

        //call backend method to add drink to database
        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://backendtest-165520.appspot.com/ndb_api/add_recipe_fancy", obj,/*new JSONObject(params),*/
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(logTag, "Received: " + response.toString());
                        try
                        {


                            Log.d(logTag,"Drink saved properly to database" );
                        }
                        catch(Exception e)
                        {
                            Log.d(logTag,"Drink not saved properly to database" );

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

    public void deleteDrink(View v)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditDrinkActivity.this);//.create();
        alertDialog.setTitle("Delete Prompt");

        //prompt user to make sure they want to delete this drink
        alertDialog.setMessage("Are you sure you want to delete this drink?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if this drink is already created, i.e not a brand new drink, make sure to remove
                        //it from appInfo.savedDrinks
                        if(alreadyCreated)
                        {
                            appInfo.savedDrinks.remove(drinkPos);
                        }

                        mp7.start();
                        Intent intent = new Intent(EditDrinkActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        //save that drink was deleted in shared preferences
                        saveDrinksAsJSON();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mp8.start();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //ask user if they want to add drink to database
    public void addDrinkToNDBDialogue(final DrinkRecipe recipe)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditDrinkActivity.this);
        alertDialog.setTitle("Drink Added to List!");

        //prompt user to confirm if they want to add drink to data base
        alertDialog.setMessage(("Drink saved to your list! Would you like to add this drink to the " +
                "Liquor Library for all user to be able to view? (You can add it later if you don't want to add it now!)"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //add to database if user allows this
                        addDrinkToNDB(null, recipe);

                        mp2.start();
                        Intent intent = new Intent(EditDrinkActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        saveDrinksAsJSON();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //return to main without adding to data base
                        Intent intent = new Intent(EditDrinkActivity.this, MainActivity.class);
                        mp8.start();
                        saveDrinksAsJSON();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        mp10.start();
        super.onBackPressed();
    }

    /*
    save drinks as json string in shared preferences
     */
    public void saveDrinksAsJSON()
    {
        JSONArray jArray = new JSONArray();
        for(DrinkRecipe savedRecipe : appInfo.savedDrinks)
        {
            try
            {
                jArray.put(savedRecipe.drinkAsJSON);
            }
            catch(Exception e)
            {
                //json key access didn't work
            }
        }
        SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("drinksAsJSON", jArray.toString());
        editor.commit();
    }

}