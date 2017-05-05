package com.austinbaird.liquorlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
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

import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.Spinner;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.austinbaird.liquorlog.R.id.editDrinkName;
import static com.austinbaird.liquorlog.R.id.messageEnter;

public class EditDrinkActivity extends AppCompatActivity
{

    RequestQueue queue;

    ArrayList<Ingredient> rowItems; //the row items that are displayed in the list
    ScrollerListAdapter adapter; //used to make viewable items on screem from data in rowItems

    Spinner qtySpinner;
    Spinner measureSpinner;
    EditText editName;

    EditText editIngredientName;

    String logTag = "tag";

    String drinkName;
    String msg;

    AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drink);
        appInfo = AppInfo.getInstance(this);

        drinkName = "";
        msg = "";


        rowItems = new ArrayList<>();
        //adapter = new ScrollerListAdapter(this, rowItems);
        adapter = new ScrollerListAdapter(this, R.layout.edit_list_element, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);


        qtySpinner = (Spinner) findViewById(R.id.qtySpinner);
        // Spinner click listener
        qtySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id)
            {

                // On selecting a spinner item
                String item = parentView.getItemAtPosition(pos).toString();

                // Showing selected spinner item
                Toast.makeText(parentView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();


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

        /*
        ArrayList<String> measures = new ArrayList<String>();
        measures.add("");
        measures.add("O");
        measures.add("ML");
        measures.add("Dash");
        measures.add("TSP");
        measures.add("Cup");
        measures.add("L");
        measures.add("Drop");
        measures.add("Pinch");
        ArrayAdapter<String> measureAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, measures);
        measureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measureSpinner.setAdapter(measureAdapter);*/



        editName = (EditText) findViewById(editDrinkName);

        editIngredientName = (EditText)findViewById(R.id.ingredientEdit);

        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    drinkName = Caption.getText().toString();
                }
            }
        });

        EditText editMsg = (EditText) findViewById(messageEnter);
        editMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    msg = Caption.getText().toString();
                    Log.d(logTag, "Changed Msg to" + msg);
                }
            }
        });

        //Button addBtn = (Button) findViewById(R.id.btnSaveDrink);

    }

    public void add(View v)
    {
        //ScrollerRowItem blank = new ScrollerRowItem(new IngredientList("","",""));
        String qty = (String)qtySpinner.getSelectedItem();
        String measure = (String)measureSpinner.getSelectedItem();
        String name = (String)editIngredientName.getText().toString();


        rowItems.add(new Ingredient(qty, measure, name));
        adapter.notifyDataSetChanged();
        //adapter.add();
    }

    public void saveDrink(View v)
    {
        //remove focus from edit texts so that text in them is saved
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.requestFocus();

        ArrayList<Ingredient> ingredients = new ArrayList<>();

        //Log.d(logTag, "Rowitems size: " + Integer.toString(rowItems.size()));
        for(Ingredient drink : rowItems)
        {
            Log.d(logTag, drink.getQty() + " " + drink.getIngredient());
            ingredients.add(drink);

        }
        //Log.d(logTag, "DrinkName: " + drinkName);
        //Log.d(logTag, "Message: " + msg);

        DrinkRecipe recipe = new DrinkRecipe(drinkName, ingredients, msg);
        appInfo.addDrink(recipe);

        addDrinkToNDB(null, recipe);

        EditText edv1 = (EditText) findViewById(editDrinkName);
        String text1 = edv1.getText().toString();
        appInfo.setColor1(text1);

        EditText edv2 = (EditText) findViewById(messageEnter);
        String text2 = edv2.getText().toString();
        appInfo.setColor2(text2);

        String toastMsg = "Drink Saved";
        Toast toast= Toast.makeText(getBaseContext() ,toastMsg,Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(EditDrinkActivity.this, MainActivity.class);
        intent.putExtra("drinkAdded", true);
        startActivity(intent);

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

    @Override
    protected void onResume() {
        EditText editName = (EditText) findViewById(editDrinkName);
        if (appInfo.sharedString1 != null) {
            editName.setText(appInfo.sharedString1);
        }

        EditText editMsg = (EditText) findViewById(messageEnter);
        if (appInfo.sharedString2 != null) {
            editMsg.setText(appInfo.sharedString2);
        }
        super.onResume();
    }
}