package nl.pharmit.foodapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class PollActivity extends AppCompatActivity {
    String pollID, username, groupID;
    Spinner firstChoiceSpinner, secondChoiceSpinner;
    List<FoodItem> foodChoices = new ArrayList<FoodItem>();
    SharedPreferences sharedPreferences;
    ArrayAdapter<FoodItem> dataAdapter;
    RequestQueue requestQueue;

    class onItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private boolean firstChoice;

        //boolean indicates if it is firstChoice or not
        public onItemSelectedListener(boolean firstChoice) {
            this.firstChoice = firstChoice;
        }

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            FoodItem selectedChoice =  (FoodItem) parent.getItemAtPosition(pos);
//            Log.d("Test", "selected choice: " + selectedChoice.toString());
            updateChoice(selectedChoice, this.firstChoice);

            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        this.username = sharedPreferences.getString(getResources().getString(R.string.USERNAME), null);
        this.groupID = sharedPreferences.getString(getResources().getString(R.string.GROUPID), null);

        firstChoiceSpinner = (Spinner) findViewById(R.id.spinnerFirst);
//        retrieveChoice(true);

        secondChoiceSpinner = (Spinner) findViewById(R.id.spinnerSecond);

        RequestManager.getInstance(this).getActivePoll(this.groupID, new CustomListener<String>()
        {
            @Override
            public void getResult(String result)
            {
                if (!result.isEmpty())
                {
                    PollActivity.this.pollID = result;
                    RequestManager.getInstance(PollActivity.this).getAllPollFood(PollActivity.this.pollID, new CustomListener<JSONArray>()
                    {
                        @Override
                        public void getResult(JSONArray result) throws JSONException {
                            if (!(result == null))
                            {
                                for (int i=0;i<result.length();i++){
                                    boolean lastItem = false;
                                    if (i == result.length() - 1) {
//                                            Toast.makeText(PollActivity.this, "test", Toast.LENGTH_LONG).show();
                                        lastItem = true;
                                    }
                                    JSONObject foodOption = null;
                                    foodOption = result.getJSONObject(i);
                                    final String foodID = foodOption.getString(getResources().getString(R.string.FOODID));
                                    final boolean lastItemBool = lastItem;
                                    RequestManager.getInstance(PollActivity.this).getFood(foodID, new CustomListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject object) throws JSONException {
                                            String foodName = object.getString(getResources().getString(R.string.FOODNAME));
                                            foodChoices.add(new FoodItem(foodID, foodName));
                                            if (lastItemBool) {
                                                dataAdapter = new ArrayAdapter<FoodItem>(PollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodChoices);
                                                firstChoiceSpinner.setAdapter(dataAdapter);
                                                retrieveChoice(true);
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });


                }
            }
        });

    }

    private void retrieveChoice(final boolean firstChoice) {
        final String paramUsername = this.username;
        final String paramPID = this.pollID;
        int fileName;
        if (firstChoice) {
            fileName = R.string.getFirstChoice;
        } else {
            fileName = R.string.getSecondChoice;
        }
        Log.d("Test", "username: " + paramUsername);
        Log.d("Test", "pollID: " + paramPID);
        Log.d("Test", "fileName" + String.valueOf(getResources().getString(fileName)));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(fileName) ,
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
                                String foodID = jObj.getString(getResources().getString(R.string.FOODID));
                                Log.d("Test", "fisrt choice: ");
                                if (firstChoice) {
                                    int spinnerPosition = dataAdapter.getPosition(new FoodItem(foodID, foodName));
//                                    firstChoiceSpinner.setSelection(spinnerPosition);
                                    firstChoiceSpinner.setOnItemSelectedListener(new onItemSelectedListener(true));
                                }
//
//                                foodChoices.add(new FoodItem(paramFID, foodName));
                            } else {
                                Log.d("Test", "error");
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
                params.put(getResources().getString(R.string.USERNAME), paramUsername);
                params.put(getResources().getString(R.string.POLLID), paramPID);
                return params;
            }
        };

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateChoice(FoodItem choice, boolean firstChoice) {
        final String paramFID = choice.getFoodID();
        final String paramUsername = this.username;
        final String paramGID = this.groupID;
        int fileName;
        if (firstChoice) {
            fileName = R.string.updateFirstChoice;
        } else {
            fileName = R.string.updateSecondChoice;
        }
        Log.d("Test", getResources().getString(fileName));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(fileName) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                Log.d("Test", "test2");
                                Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
//                                String foodName = jObj.getString(getResources().getString(R.string.FOODNAME));
//                                foodChoices.add(new FoodItem(paramFID, foodName));
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
                params.put(getResources().getString(R.string.USERNAME), paramUsername);
                params.put(getResources().getString(R.string.FOODID), paramFID);
                params.put(getResources().getString(R.string.GROUPID), paramGID);
                return params;
            }
        };

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
