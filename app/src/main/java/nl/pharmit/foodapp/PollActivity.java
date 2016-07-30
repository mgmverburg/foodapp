package nl.pharmit.foodapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollActivity extends AppCompatActivity {
    String pollID, username, groupID;
    boolean isAdmin;
    Spinner firstChoiceSpinner, secondChoiceSpinner;
    List<FoodItem> foodChoicesFirst = new ArrayList<FoodItem>();
    List<FoodItem> foodChoicesSecond = new ArrayList<FoodItem>();
    SharedPreferences sharedPreferences;
    CustomSpinnerAdapter firstChoiceDataAdapter, secondChoiceDataAdapter;
    RequestQueue requestQueue;
    int requestCount;
    FoodItem noselection, oldFirstChoice, oldSecondChoice;
    ToggleButton nopreference;
    ToggleButton notjoining;
    TextView timer;
    boolean firstRetrieval;
    ToggleButton polltab2;
    ToggleButton grouptab2;
    ToggleButton polltab3;
    ToggleButton grouptab3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        this.username = sharedPreferences.getString(getResources().getString(R.string.USERNAME), null);
        this.groupID = sharedPreferences.getString(getResources().getString(R.string.GROUPID), null);
        this.isAdmin = sharedPreferences.getBoolean(getResources().getString(R.string.ISADMIN), false);

        noselection = new FoodItem("0", "Select choice");
//        foodChoices.add(noselection);

        RequestManager.getInstance(this).getActivePoll(this.groupID, new CustomListener<String>()
        {
            @Override
            public void getResult(String pollID)
            {
                PollActivity.this.pollID = pollID;
                if (isAdmin) {
                    initializeAdminView();
                } else {
                    if (!pollID.isEmpty())
                    {
                        initializePollVoting(pollID);
                    } else {
                        setContentView(R.layout.activity_poll_admin);
                        FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
                        menu.setEnabled(false);
                        menu.setVisibility(View.INVISIBLE);
                        TextView message = (TextView) findViewById(R.id.activePollMessage);
                        if (groupID != null && !groupID.isEmpty() ) {
                            message.setText("There is currently no poll active. Wait for the admin to create one.");
                        } else {
                            message.setText("You are not yet part of a group, so you will not see any polls.");
                        }
                        polltab3 = (ToggleButton) findViewById(R.id.polltab2);
                        grouptab3 =(ToggleButton) findViewById(R.id.grouptab2);

                        polltab3.setChecked(true);
                        polltab3.setEnabled(false);
                        grouptab3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(PollActivity.this, GroupPage.class));
                                polltab3.setChecked(false);
                            }
                        });
                    }
                }



            }
        });

    }

    private void initializeAdminView() {
        setContentView(R.layout.activity_poll_admin);

        polltab2 = (ToggleButton) findViewById(R.id.polltab2);
        grouptab2 = (ToggleButton) findViewById(R.id.grouptab2);

        polltab2.setChecked(true);
        polltab2.setEnabled(false);
        grouptab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollActivity.this, GroupPage.class));
                polltab2.setChecked(false);
            }
        });
        //@TODO: show poll vote amounts

        FloatingActionButton foodTypeButton = (FloatingActionButton) findViewById(R.id.foodType);
        foodTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                startActivity(new Intent(PollActivity.this, FoodTypeActivity.class));
            }
        });

        FloatingActionButton favoritePollsButton = (FloatingActionButton) findViewById(R.id.favoritePolls);
        favoritePollsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Toast.makeText(PollActivity.this, getResources().getString(R.string.error_unsupported),  Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton createPoll = (FloatingActionButton) findViewById(R.id.createPoll);
        createPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                if (pollID.isEmpty()) {
                    startActivity(new Intent(PollActivity.this, CreatePollActivity.class));
                } else {
                    Toast.makeText(PollActivity.this, getResources().getString(R.string.poll_already_active),  Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView message = (TextView) findViewById(R.id.activePollMessage);
        if (pollID.isEmpty()) {

            message.setText("There is currently no poll active");
        } else {
            message.setText("There is currently a poll active");
        }
    }

    private void initializePollVoting(String pollID) {
        setContentView(R.layout.activity_poll_user);
        nopreference = (ToggleButton) findViewById(R.id.nopreference);
        notjoining = (ToggleButton) findViewById(R.id.notjoining);
        timer = (TextView) findViewById(R.id.countdown);
        polltab3 = (ToggleButton) findViewById(R.id.toggleButton);
        grouptab3 =(ToggleButton) findViewById(R.id.toggleButton2);

        polltab3.setChecked(true);
        polltab3.setEnabled(false);
        grouptab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollActivity.this, GroupPage.class));
                polltab3.setChecked(false);
            }
        });

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
                                    firstChoiceDataAdapter = new CustomSpinnerAdapter
                                            (PollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodChoicesFirst);
                                    secondChoiceDataAdapter = new CustomSpinnerAdapter
                                            (PollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodChoicesSecond);
                                    firstChoiceSpinner.setAdapter(firstChoiceDataAdapter);
                                    secondChoiceSpinner.setAdapter(secondChoiceDataAdapter);
                                    firstChoiceDataAdapter.insert(noselection,0);
                                    secondChoiceDataAdapter.insert(noselection,0);
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
            if (spinnerInitializedCount < spinnerCount)
            {
                spinnerInitializedCount++;
            }
            else {

            FoodItem selectedChoice = (FoodItem) parent.getItemAtPosition(pos);
//            if (!selectedChoice.equals(noselection)) {
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

//                                    firstChoiceDataAdapter.remove(noselection);
                                    int spinnerPosition = firstChoiceDataAdapter.getPosition(choice);

                                    firstChoiceSpinner.setSelection(spinnerPosition);
                                } else {

//                                    secondChoiceDataAdapter.remove(noselection);
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
//            firstChoiceDataAdapter.remove(noselection);
//            firstChoiceDataAdapter.insert(noselection, 0);
            int spinnerPosition = firstChoiceDataAdapter.getPosition(noselection);
            firstChoiceSpinner.setSelection(spinnerPosition);
        } else {
//            secondChoiceDataAdapter.remove(noselection);
//            secondChoiceDataAdapter.insert(noselection, 0);
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
//        boolean allowUpdate = false;
//        if (oldSecondChoice != null || oldFirstChoice != null) {
//            allowUpdate = true;
//        }

        if (firstChoice) {
//            oldFirstChoice = choice;
//            secondChoiceSpinner.getSelectedItem()
//            if (oldSecondChoice != null && oldSecondChoice.equals(choice)) {
            FoodItem currentSecondChoice = secondChoiceDataAdapter.getItem(secondChoiceSpinner.getSelectedItemPosition());
            if (currentSecondChoice != null && currentSecondChoice.equals(choice)) {
                removeChoice(!firstChoice);
            }
            fileName = R.string.updateFirstChoice;
        } else {
            FoodItem currentFirstChoice = firstChoiceDataAdapter.getItem(firstChoiceSpinner.getSelectedItemPosition());
            if (currentFirstChoice != null && currentFirstChoice.equals(choice)) {
                removeChoice(!firstChoice);
            }
            fileName = R.string.updateSecondChoice;
        }
//        if (allowUpdate) {
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
//        }
    }
}
