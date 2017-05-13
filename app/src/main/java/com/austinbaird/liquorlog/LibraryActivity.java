package com.austinbaird.liquorlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;



public class LibraryActivity extends AppCompatActivity
{

    public String logTag = "";
    AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        appInfo = AppInfo.getInstance(this);
        Log.d(logTag, "FUCKING LIBRARY");
        loadRecipes(null);

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
