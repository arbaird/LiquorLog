package com.austinbaird.liquorlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.util.Log;

import android.widget.FrameLayout;
import android.widget.Toast;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

public class EditDrinkActivity extends AppCompatActivity
{

    RequestQueue queue;

    ArrayList<Ingredient> rowItems; //the row items that are displayed in the list
    ScrollerListAdapter adapter; //used to make viewable items on screem from data in rowItems

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
        adapter = new ScrollerListAdapter(this, rowItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);


        FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.button_footerview,null);
        //btnPostYourEnquiry = (Button) footerLayout.findViewById(R.id.btnAddIngredient);

        listView.addFooterView(footerLayout);

        EditText editName = (EditText) findViewById(R.id.editName);

        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    drinkName = Caption.getText().toString();
                }
            }
        });

        EditText editMsg = (EditText) findViewById(R.id.messageEnter);
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

        Button addBtn = (Button) findViewById(R.id.btnSaveDrink);

    }

    public void add(View v)
    {
        //ScrollerRowItem blank = new ScrollerRowItem(new IngredientList("","",""));
        adapter.add();
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
}
