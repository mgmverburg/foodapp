package nl.pharmit.foodapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String pollID;
    Spinner firstChoiceSpinner, secondChoiceSpinner;
    List<FoodItem> foodChoices = new ArrayList<FoodItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        firstChoiceSpinner = (Spinner) findViewById(R.id.spinnerFirst);
        secondChoiceSpinner = (Spinner) findViewById(R.id.spinnerSecond);

        getActivePoll();

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void getActivePoll() {
        SharedPreferences sharedpreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        final String paramGID = sharedpreferences.getString(getResources().getString(R.string.GROUPID), null);
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.getActivePoll) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                PollActivity.this.pollID = jObj.getString(getResources().getString(R.string.POLLID));
                                getAllPollFood();
//
                            } else {
                                Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.GROUPID), paramGID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }



    private void getAllPollFood() {
        final String paramPID = this.pollID;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.getFoodOptions) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONArray foodOptions = jObj.getJSONArray(getResources().getString(R.string.FOODITEMS));

                                if (foodOptions != null) {
                                    for (int i=0;i<foodOptions.length();i++){
                                        boolean lastItem = false;
                                        if (i == foodOptions.length() - 1) {
                                            lastItem = true;
                                        }
                                        JSONObject foodOption = foodOptions.getJSONObject(i);
                                        getFood(foodOption.getString("FID"), lastItem);
                                    }
                                }

//
                            } else {
                                Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.POLLID), paramPID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getFood(String foodID, boolean lastItemParam) {
        final String paramFID = foodID;
        final boolean lastItem = lastItemParam;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.getFood) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                String foodName = jObj.getString(getResources().getString(R.string.FOODNAME));
                                foodChoices.add(new FoodItem(paramFID, foodName));
                                if (lastItem) {
                                    ArrayAdapter<FoodItem> dataAdapter = new ArrayAdapter<FoodItem>(PollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodChoices);
// Apply the adapter to the spinner
                                    firstChoiceSpinner.setAdapter(dataAdapter);
                                }
                            } else {
                                Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.FOODID), paramFID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
