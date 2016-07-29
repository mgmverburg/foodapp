package nl.pharmit.foodapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    List<FoodItem> foodChoicesFirst = new ArrayList<FoodItem>();
    List<FoodItem> foodChoicesSecond = new ArrayList<FoodItem>();
    SharedPreferences sharedPreferences;
    ArrayAdapter<FoodItem> firstChoiceDataAdapter, secondChoiceDataAdapter;
    RequestQueue requestQueue;
    int requestCount;
    FoodItem noselection, oldFirstChoice, oldSecondChoice;
    ToggleButton nopreference;
    ToggleButton notjoining;
    boolean firstRetrieval;

    class onItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private boolean firstChoice;
        private int spinnerInitializedCount = 0, spinnerCount = 0;

        //boolean indicates if it is firstChoice or not
        public onItemSelectedListener(boolean firstChoice) {
            this.firstChoice = firstChoice;
            this.spinnerCount = 1;
        }

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
//            if (spinnerInitializedCount < spinnerCount)
//            {
//                spinnerInitializedCount++;
//            }
//            else {


                FoodItem selectedChoice = (FoodItem) parent.getItemAtPosition(pos);
            if (!selectedChoice.equals(noselection)) {
                notjoining.setChecked(false);
                nopreference.setChecked(false);
//            Log.d("Test", "selected choice: " + selectedChoice.toString());
                    updateChoice(selectedChoice, this.firstChoice);
                }


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
        nopreference = (ToggleButton) findViewById(R.id.nopreference);
        notjoining = (ToggleButton) findViewById(R.id.notjoining);

        nopreference.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notjoining.setChecked(false);
                    nopreference.setEnabled(false);
                }
                else {
                    nopreference.setEnabled(true);

                }
            }
        });

        notjoining.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    nopreference.setChecked(false);
                    notjoining.setEnabled(false);
                }
                else {
                    notjoining.setEnabled(true);
                }
            }
        });


        firstChoiceSpinner = (Spinner) findViewById(R.id.spinnerFirst);
//        retrieveChoice(true);

        secondChoiceSpinner = (Spinner) findViewById(R.id.spinnerSecond);

        noselection = new FoodItem("0", "Select choice");
//        foodChoices.add(noselection);

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
                                final int totalNumberRequests = result.length();
                                requestCount = 0;
                                for (int i=0;i<result.length();i++){
                                    JSONObject foodOption = null;
                                    foodOption = result.getJSONObject(i);
                                    final String foodID = foodOption.getString(getResources().getString(R.string.FOODID));
                                    RequestManager.getInstance(PollActivity.this).getFood(foodID, new CustomListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject object) throws JSONException {
                                            requestCount++;
                                            String foodName = object.getString(getResources().getString(R.string.FOODNAME));
                                            foodChoicesFirst.add(new FoodItem(foodID, foodName));
                                            foodChoicesSecond.add(new FoodItem(foodID, foodName));
                                            if (requestCount == totalNumberRequests) {
                                                firstChoiceDataAdapter = new ArrayAdapter<FoodItem>
                                                        (PollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodChoicesFirst);
                                                secondChoiceDataAdapter = new ArrayAdapter<FoodItem>
                                                        (PollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodChoicesSecond);
                                                firstChoiceSpinner.setAdapter(firstChoiceDataAdapter);
                                                secondChoiceSpinner.setAdapter(secondChoiceDataAdapter);
                                                firstRetrieval = true; //sets boolean to true so that we can set the listeners the first time we
                                                //retrieve the choices
                                                requestCount = 0;
                                                retrieveChoice(true);
                                                retrieveChoice(false);
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(fileName) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (firstRetrieval) {
                                requestCount++;
                            }

                            if (!isError) {
//                                String foodName = jObj.getString(getResources().getString(R.string.FOODNAME));
                                String foodID = jObj.getString(getResources().getString(R.string.FOODID));
//                                Log.d("Test", "fisrt choice: ");
                                FoodItem choice =  new FoodItem(foodID, "");

                                if (firstChoice) {

                                    firstChoiceDataAdapter.remove(noselection);
                                    int spinnerPosition = firstChoiceDataAdapter.getPosition(choice);

                                    firstChoiceSpinner.setSelection(spinnerPosition);
                                } else {

                                    secondChoiceDataAdapter.remove(noselection);
                                    int spinnerPosition = secondChoiceDataAdapter.getPosition(choice);
                                    secondChoiceSpinner.setSelection(spinnerPosition);
                                }
//
//                                foodChoices.add(new FoodItem(paramFID, foodName));
                            } else {
                                resetSpinnerSelection(firstChoice);
//                                Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                            if (requestCount == 2) {
                                firstChoiceSpinner.setOnItemSelectedListener(new onItemSelectedListener(true));
                                secondChoiceSpinner.setOnItemSelectedListener(new onItemSelectedListener(false));
                                firstRetrieval = false;
                                requestCount++;
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

    private void resetSpinnerSelection(boolean firstChoice) {
        if (firstChoice) {
            firstChoiceDataAdapter.remove(noselection);
            firstChoiceDataAdapter.insert(noselection, 0);
            int spinnerPosition = firstChoiceDataAdapter.getPosition(noselection);
            firstChoiceSpinner.setSelection(spinnerPosition);
        } else {
            secondChoiceDataAdapter.remove(noselection);
            secondChoiceDataAdapter.insert(noselection, 0);
            int spinnerPosition = secondChoiceDataAdapter.getPosition(noselection);
            secondChoiceSpinner.setSelection(spinnerPosition);
        }
    }

    private void removeChoice(final boolean firstChoice) {
        final String paramUsername = this.username;
        final String paramPID = this.pollID;
        int fileName;
        if (firstChoice) {
            fileName = R.string.removeFirstChoice;
        } else {
            fileName = R.string.removeSecondChoice;
        }
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
//                                Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
//                                String foodName = jObj.getString(getResources().getString(R.string.FOODNAME));
//                                foodChoices.add(new FoodItem(paramFID, foodName));
                                retrieveChoice(firstChoice);
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
                params.put(getResources().getString(R.string.POLLID), paramPID);
                return params;
            }
        };

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateChoice(FoodItem choice, final boolean firstChoice) {
        final String paramFID = choice.getFoodID();
        final String paramUsername = this.username;
        final String paramPID = this.pollID;
        int fileName;
        boolean allowUpdate = false;
        if (oldSecondChoice != null || oldFirstChoice != null) {
            allowUpdate = true;
        }

        if (firstChoice) {
            oldFirstChoice = choice;
//            secondChoiceSpinner.getSelectedItem()
            if (oldSecondChoice != null && oldSecondChoice.equals(choice)) {
                removeChoice(!firstChoice);
            }
            fileName = R.string.updateFirstChoice;
        } else {
            oldSecondChoice = choice;
            if (oldFirstChoice != null && oldFirstChoice.equals(choice)) {
                removeChoice(!firstChoice);
            }
            fileName = R.string.updateSecondChoice;
        }
        if (allowUpdate) {
            Log.d("Test", getResources().getString(fileName));
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(fileName),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jObj = null;
                            Boolean isError = false;
                            try {
                                jObj = new JSONObject(response);
                                isError = jObj.getBoolean("isError");
                                if (!isError) {
                                    Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
//                                String foodName = jObj.getString(getResources().getString(R.string.FOODNAME));
//                                foodChoices.add(new FoodItem(paramFID, foodName));
                                    retrieveChoice(firstChoice);
                                } else {
//                                Toast.makeText(PollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
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
                    params.put(getResources().getString(R.string.POLLID), paramPID);
                    return params;
                }
            };

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }
}
