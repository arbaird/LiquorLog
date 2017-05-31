package com.austinbaird.liquorlog;

import java.io.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.content.Intent;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;

import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.austinbaird.liquorlog.R.id.editDrinkName;
import static com.austinbaird.liquorlog.R.id.imageViewDrink;
import static com.austinbaird.liquorlog.R.id.messageEnter;
import static com.austinbaird.liquorlog.R.id.textViewCharCount1;
import static com.austinbaird.liquorlog.R.id.textViewCharCount2;
import static com.austinbaird.liquorlog.R.id.textViewVharCount3;

public class EditDrinkActivity extends AppCompatActivity
{
    RequestQueue queue;

    MediaPlayer mp1;
    MediaPlayer mp3;
    MediaPlayer mp9;
    MediaPlayer mp7;
    MediaPlayer mp8;
    MediaPlayer mp10;

    ArrayList<Ingredient> rowItems; //the row items that are displayed in the list
    ScrollerListAdapter adapter; //used to make viewable items on screem from data in rowItems

    Spinner qtySpinner;
    Spinner measureSpinner;
    Spinner fractionSpinner;
    EditText editName;
    EditText editMsg;
    private TextView charCount;
    private TextView charCountIng;
    private TextView charCountMsg;

    EditText editIngredientName;

    String logTag = "tag";

    String drinkName;
    String msg;
    int image;

    AppInfo appInfo;

    int drinkPos;

    Boolean alreadyCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drink);
        appInfo = AppInfo.getInstance(this);

        mp1 = MediaPlayer.create(this, R.raw.zeldafanfare);
        mp10 = MediaPlayer.create(this, R.raw.backbutton);

        //Button one = (Button)this.findViewById(R.id.btnSaveDrink);
        //final MediaPlayer mp1 = MediaPlayer.create(this, R.raw.zeldafanfare);
        //one.setOnClickListener(new OnClickListener(){

            //public void onClick(View v) {
                //mp1.start();
                //saveDrink(null);
            //}
        //});

        mp9 = MediaPlayer.create(this, R.raw.cantsave);

        Button two = (Button)this.findViewById(R.id.btnAddIngredient);
        final MediaPlayer mp2 = MediaPlayer.create(this, R.raw.zeldamenuequip);
        mp3 = MediaPlayer.create(this, R.raw.zeldamenuno);
        two.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                String name = (String)editIngredientName.getText().toString();
                if(name.trim().equals(""))
                {
                    String toastMsg = "Specify ingredient name!";
                    Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
                    toast.show();
                    mp3.start();
                }
                else
                {
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

        mp7 = MediaPlayer.create(this, R.raw.zeldadelete2);
        mp8 = MediaPlayer.create(this, R.raw.zeldacancel);

        //mp = MediaPlayer.create(EditDrinkActivity.this, R.raw.zeldafanfare);

        //addPreDefined(); //uncomment to add library dirnks to data base. make sure to clear these first before re adding

        drinkName = "";
        msg = "";
        image = R.drawable.emptysmall;
        alreadyCreated = false;

        rowItems = new ArrayList<>();
        //adapter = new ScrollerListAdapter(this, rowItems);
        adapter = new ScrollerListAdapter(this, R.layout.edit_list_element, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        //FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.button_footerview,null);
        //btnPostYourEnquiry = (Button) footerLayout.findViewById(R.id.btnAddIngredient);
        //listView.addFooterView(footerLayout);



        final MediaPlayer mp5 = MediaPlayer.create(this, R.raw.zeldaselecting); // Didn't use because of weird glitch when starting activity

        qtySpinner = (Spinner) findViewById(R.id.qtySpinner);
        fractionSpinner = (Spinner) findViewById(R.id.fractionSpinner);
        // Spinner click listener
        qtySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id)
            {

                // On selecting a spinner item
                String item = parentView.getItemAtPosition(pos).toString();

                // Showing selected spinner item
                //Toast.makeText(parentView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                //MediaPlayer mp5 = MediaPlayer.create(EditDrinkActivity.this, R.raw.zeldaselecting);
                //mp5.start();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //nothing to do
            }

        });

        fractionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id)
            {

                // On selecting a spinner item
                String item = parentView.getItemAtPosition(pos).toString();

                // Showing selected spinner item
                //Toast.makeText(parentView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                //MediaPlayer mp5 = MediaPlayer.create(EditDrinkActivity.this, R.raw.zeldaselecting);
                //mp5.start();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //nothing to do
            }

        });

        // Spinner Drop down elements
        ArrayList<String> qtys = new ArrayList<String>();
        qtys.add("");
        for(int i = 1; i < 11; i++)
        {
            qtys.add(Integer.toString(i));
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, qtys);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        qtySpinner.setAdapter(dataAdapter);


        measureSpinner = (Spinner) findViewById(R.id.measureSpinner);
        measureSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id)
            {

                // On selecting a spinner item
                String item = parentView.getItemAtPosition(pos).toString();

                // Showing selected spinner item
                //Toast.makeText(parentView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                //MediaPlayer mp5 = MediaPlayer.create(EditDrinkActivity.this, R.raw.zeldaselecting);
                //mp5.start();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //nothing to do
            }

        });





        editName = (EditText) findViewById(editDrinkName);
        editMsg = (EditText)findViewById(messageEnter);
        editIngredientName = (EditText)findViewById(R.id.ingredientEdit);

        charCount = (TextView) findViewById(textViewCharCount1);
        charCountIng = (TextView) findViewById(textViewCharCount2);
        charCountMsg = (TextView) findViewById(textViewVharCount3);

        final TextWatcher txwatcher1 = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0)
            {
                if(editName.isFocused() && editName.getText().toString().trim().length() > 44){
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
                //charCount.setText(44 -   arg0.toString().length());
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
                //else if(etPassword.isFocused() && etPassword.getText().length() == 10)
                //Toast.makeText(MyActivity.this, "The password must be at most 10 characters!" , Toast.LENGTH_SHORT).show();
            }
        };

        final TextWatcher txwatcher2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before, int count) {
                if (editIngredientName.isFocused()) {
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
            public void afterTextChanged(Editable arg0) {
                if (editIngredientName.isFocused() && editIngredientName.getText().toString().trim().length() > 30) {
                    editIngredientName.setText(arg0.toString().substring(0, 30));
                    editIngredientName.setSelection(arg0.length() - 1);
                    mp3.start();
                    Toast.makeText(EditDrinkActivity.this, "Maximum number of characters reached!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final TextWatcher txwatcher3 = new TextWatcher() {
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
            public void afterTextChanged(Editable arg0) {
                if (editMsg.isFocused() && editMsg.getText().toString().trim().length() > 300) {
                    editMsg.setText(arg0.toString().substring(0, 300));
                    editMsg.setSelection(arg0.length() - 1);
                    mp3.start();
                    Toast.makeText(EditDrinkActivity.this, "Maximum number of characters reached!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        /*if(!(editName.isFocused()))
        {
            charCount.setVisibility(View.INVISIBLE);
        }*/

        editName.addTextChangedListener(txwatcher1);
        editMsg.addTextChangedListener(txwatcher3);
        editIngredientName.addTextChangedListener(txwatcher2);

        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    drinkName = Caption.getText().toString();
                }
            }
        });


        //Button addBtn = (Button) findViewById(R.id.btnSaveDrink);

    }

    @Override
    protected void onResume() {

        EditText editName = (EditText) findViewById(editDrinkName);

        editName.setText(appInfo.drinkToEdit.getName() ); //sharedString1);

        //TextView charCountName = (TextView) findViewById(textViewCharCount1);
        //charCountName.setText(namePass);

        EditText editMsg = (EditText) findViewById(messageEnter);

        editMsg.setText(appInfo.drinkToEdit.getMsg());

        image = appInfo.drinkToEdit.getImageID();

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            //image = extras.getInt("imageId");
            drinkPos = extras.getInt("drinkPosition");
            alreadyCreated = extras.getBoolean("alreadyCreated");
            Log.d(logTag, "editing already created?" + alreadyCreated);
        }
        if(appInfo.drinkToEdit.getIngredientList() != null)
        {
            //for(int i = 0; i < appInfo.ingredientsToEdit.size(); i++)
            rowItems.clear();
            for(Ingredient ingredient : appInfo.drinkToEdit.getIngredientList())
            {

                rowItems.add(new Ingredient(ingredient.getQty(), ingredient.getMeasure(), ingredient.getIngredient()));

            }



            adapter.notifyDataSetChanged();
        }

        ImageView editImage = (ImageView) findViewById(imageViewDrink);
        editImage.setImageResource(image);

        super.onResume();
    }

    public void editDrinkImage(View v)
    {
        String toastMsg = "Editing Drink Image";
        Toast toast= Toast.makeText(getBaseContext(),toastMsg,Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(EditDrinkActivity.this, EditDrinkImage.class);
        intent.putExtra("alreadyCreated", alreadyCreated);
        intent.putExtra("drinkPosition", drinkPos);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        EditText editMsg = (EditText)findViewById(messageEnter);

        appInfo.drinkToEdit.setName(editName.getText().toString());

        appInfo.drinkToEdit.setMsg(editMsg.getText().toString());

        appInfo.drinkToEdit.setIngredientList(rowItems);

        startActivity(intent);
    }

    public void add(View v)
    {
        //ScrollerRowItem blank = new ScrollerRowItem(new IngredientList("","",""));
        String name = (String)editIngredientName.getText().toString();
        if(name.trim().equals(""))
        {
            String toastMsg = "Specify ingredient name!";
            Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        String qty = (String)qtySpinner.getSelectedItem();
        String measure = (String)measureSpinner.getSelectedItem();
        String fraction = (String)fractionSpinner.getSelectedItem();


        Log.d(logTag, "qty " +  qty);
        Log.d(logTag, "msr " +  measure);
        Log.d(logTag, "name " +  name);

        if(!fraction.equals(""))
            qty += " " + fraction;

        rowItems.add(new Ingredient(qty, measure, name));
        adapter.notifyDataSetChanged();

        qtySpinner.setSelection(0);
        fractionSpinner.setSelection(0);
        measureSpinner.setSelection(0);

        editIngredientName.setText("");


        //adapter.add();
    }

    public void saveDrink(View v)
    {
        EditText editMsg = (EditText)findViewById(messageEnter);
        msg = editMsg.getText().toString();

        EditText editName = (EditText)findViewById(editDrinkName);
        drinkName = editName.getText().toString();

        //ImageView imageId = (ImageView) findViewById(imageViewDrink);
        //image;= imageId.getId();

        ArrayList<Ingredient> ingredients = new ArrayList<>();

        //Log.d(logTag, "Rowitems size: " + Integer.toString(rowItems.size()));
        for(Ingredient drink : rowItems)
        {
            Log.d(logTag, drink.getQty() + " " + drink.getMeasure() + " " + drink.getIngredient());
            ingredients.add(drink);
        }

        if(msg.isEmpty() || drinkName.isEmpty() || ingredients.size() == 0 || image == R.drawable.emptysmall)
        {
            //Toast.makeText(EditDrinkActivity.this, editName.getText().toString(), Toast.LENGTH_SHORT).show();
            mp9.start();
            Toast.makeText(EditDrinkActivity.this, "Drink Invalid! Please provide a Drink Name, Ingredient, Message/Procedure, and Drink Image", Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            mp1.start();
            DrinkRecipe recipe;
            if(alreadyCreated)
            {
                recipe = appInfo.savedDrinks.get(drinkPos);
                recipe.setName(drinkName);
                recipe.setIngredientList(ingredients);
                recipe.setMsg(msg);
                recipe.setImageID(image);
            }
            else
            {
                recipe = new DrinkRecipe(drinkName, ingredients, msg);
                recipe.setImageID(image);
                appInfo.addDrink(recipe);
            }

            Log.d(logTag, "From edit");
            for(DrinkRecipe d : appInfo.savedDrinks)
            {
                Log.d(logTag, d.getName());
            }

            addDrinkToNDB(null, recipe);

            EditText edv1 = (EditText) findViewById(editDrinkName);
            String text1 = edv1.getText().toString();
            appInfo.setColor1(text1);

            EditText edv2 = (EditText) findViewById(messageEnter);
            String text2 = edv2.getText().toString();
            appInfo.setColor2(text2);

            appInfo.drinkToEdit = new DrinkRecipe("",null,"");

            saveDrinksAsJSON();

            String toastMsg = "Drink Saved!";
            Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(EditDrinkActivity.this, MainActivity.class);
            //intent.putExtra("alreadyCreated", true); //setFlag that drink being edited has already been created
            //intent.putExtra("drinkPosition", mydrink);
            intent.putExtra("drinkAdded", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void addDrinkToNDB(View v, DrinkRecipe recipe)
    {

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
                //jArray.put(new JSONArray(ingredient)); this would be ideal, but is unsupported before API 19 for android
            }
            final String ing = jArray.toString();
            obj.put("ingredients", ing);
            obj.put("userCreated", recipe.userMade);

        }
        catch(Exception e)
        {

        }


        JsonObjectRequest jsobj = new JsonObjectRequest(
                "https://backendtest-165520.appspot.com/ndb_api/add_recipe_fancy", obj,/*new JSONObject(params),*/
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(logTag, "Received: " + response.toString());
                        try
                        {

                            String responseString = response.getString("result");
                            Log.d(logTag,responseString );
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

    public void deleteDrink(View v)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditDrinkActivity.this);//.create();
        alertDialog.setTitle("Delete Prompt");


        alertDialog.setMessage("Are you sure you want to delete this drink?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(alreadyCreated)
                        {
                            appInfo.savedDrinks.remove(drinkPos);
                        }

                        mp7.start();
                        Intent intent = new Intent(EditDrinkActivity.this, MainActivity.class);
                        appInfo.sharedString1 = null;
                        appInfo.sharedString2 = null;
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        saveDrinksAsJSON();

                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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

    @Override
    public void onBackPressed()
    {
        appInfo.sharedString1 = null;
        appInfo.sharedString2 = null;
        mp10.start();
        super.onBackPressed();
    }

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
        //Log.d(logTag, "After saving, json array is: " + jArray.toString());

        SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("drinksAsJSON", jArray.toString());
        editor.commit();
    }

    public void addPreDefined()
    {
        ArrayList<DrinkRecipe> recipes = new ArrayList<>();

        try
        {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getAssets().open("Drinks.txt")));
            String line;
            int lineNum = 1; //used for error reporting

            while ((line = br.readLine()) != null)
            {
                try
                {

                    String drinkName = line;
                    ArrayList<Ingredient> ingredients = new ArrayList<>();
                    String ings;
                    ings = br.readLine();
                    while(!ings.equals("-"))
                    {
                        lineNum++;
                        Map<String, String> ingInfo = parseFileLine(ings);
                        ingredients.add(new Ingredient(ingInfo.get("qty"), ingInfo.get("measure"), ingInfo.get("ingredient")));
                        ings = br.readLine();
                    }
                    int id = Integer.parseInt(br.readLine());
                    String msg = br.readLine();

                    recipes.add(new DrinkRecipe(drinkName, ingredients, msg, false, appInfo.drinksIcon[id]));
                    lineNum++;
                    br.readLine(); // clear newline

                }
                catch(Exception ex)
                {
                    System.err.println("Invalid item format in file at line " +
                            lineNum);
                }
                lineNum++;

            }
        }
        catch(FileNotFoundException ex)
        {
            Log.d(logTag, "File not found");
        }
        catch(Exception e)
        {
            Log.d(logTag, "Fucked up");
        }
        for(DrinkRecipe recipe : recipes)
        {
            Log.d(logTag, recipe.drinkAsJSON.toString());
            addDrinkToNDB(null, recipe);
        }


    }

    Map<String,String> parseFileLine(String line)
    {
        Map<String, String> lineInfo = new HashMap<>(); //= new String[];
        String[] tokens = line.split(" ");
        if(tokens[0].equals("/")) //this means no qty or measure is specified
        {
            lineInfo.put("qty", "");
            lineInfo.put("measure", "");
            String name = "";
            for(int i = 1; i < tokens.length; i++)
            {
                name += tokens[i] + " ";
            }
            name = name.trim();
            lineInfo.put("ingredient", name);
        }
        else if(tokens[1].equals("/")) //this means no measure is specified
        {
            lineInfo.put("qty", tokens[0]);
            lineInfo.put("measure", "");
            String name = "";
            for(int i = 2; i < tokens.length; i++)
            {
                name += tokens[i] + " ";
            }
            name = name.trim();
            lineInfo.put("ingredient", name);
        }
        else
        {
            lineInfo.put("qty", tokens[0]);
            lineInfo.put("measure", tokens[1]);
            String name = "";
            for(int i = 2; i < tokens.length; i++)
            {
                name += tokens[i] + " ";
            }
            name = name.trim();
            lineInfo.put("ingredient", name);
        }
        return lineInfo;

    }
}