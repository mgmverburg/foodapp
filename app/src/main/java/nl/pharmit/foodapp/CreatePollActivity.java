package nl.pharmit.foodapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

public class CreatePollActivity extends AppCompatActivity implements CustomListener<String> {
    TextView dinnerTime, deadlineTime;
    ListView listFoodChoices;
    Button addFood, startPoll;
    List<FoodItem> foodTypes = new ArrayList<FoodItem>();
    RequestQueue requestQueue;
    Spinner spinner;
    ArrayAdapter<FoodItem> dataAdapter;
    SharedPreferences sharedPreferences;
    String pollID, groupID;
    CreatePollFoodArrayAdapter pollOptionsAdapter;
    private List<FoodItem> data;
    int requestCount, spinnerCount = 0 , spinnerInitializedCount = 0;//this counts how many Gallery's are on the UI
    String deadlineTimeHour, deadlineTimeMinute, dinnerTimeHour, dinnerTimeMinute;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        this.groupID = sharedPreferences.getString(getResources().getString(R.string.GROUPID), null);


        requestQueue = Volley.newRequestQueue(this);

        dinnerTime = (TextView) findViewById(R.id.timePickerDinner);
        deadlineTime = (TextView) findViewById(R.id.timePickerDeadline);
        listFoodChoices = (ListView) findViewById(R.id.listViewPollChoices);
        spinner = (Spinner) findViewById(R.id.spinner);
        addFood = (Button) findViewById(R.id.addFoodButton);
        startPoll = (Button) findViewById(R.id.startPollButton);
        spinnerCount=1;

        RequestManager.getInstance(this).getActivePoll(this.groupID, new CustomListener<String>()
        {
            @Override
            public void getResult(String result)
            {
                if (!result.isEmpty())
                {
                    CreatePollActivity.this.pollID = result;
                    enableButtons();
                    //needs to be replaced with possible loading from saved polls options
//                    retrievePollFoodOptions();
                }
            }
        });

        data = new ArrayList<FoodItem>();
        pollOptionsAdapter = new CreatePollFoodArrayAdapter(CreatePollActivity.this,CreatePollActivity.this, data, CreatePollActivity.this.pollID);
        listFoodChoices.setAdapter(pollOptionsAdapter);
        RequestManager.getInstance(this).retrieveAllFoodTypes(new CustomListener<List<FoodItem>>()
        {
            @Override
            public void getResult(List<FoodItem> result)
            {
                if (!result.isEmpty())
                {
                    foodTypes.addAll(result);
                    lastInitialization();
                    //needs to be replaced with possible loading from saved polls options
//                    retrievePollFoodOptions();
                }
            }
        });

    }

    private void createNewPoll() {
        final String paramGID = this.groupID;
        final String paramDeadlineHour = this.deadlineTimeHour;
        final String paramDeadlineMinute = this.deadlineTimeMinute;
        final String paramDinnerHour = this.dinnerTimeHour;
        final String paramDinnerMinute = this.dinnerTimeMinute;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL)
                + getResources().getString(R.string.createNewPoll) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                String pollID = jObj.getString(getResources().getString(R.string.POLLID));
//                                postPollFood(pollID);
                                startActivity(new Intent(CreatePollActivity.this, PollActivity.class));
                            } else {
                                Toast.makeText(CreatePollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreatePollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.GROUPID), paramGID);
                params.put(getResources().getString(R.string.DEADLINETIMEHOUR), paramDeadlineHour);
                params.put(getResources().getString(R.string.DEADLINETIMEMINUTE), paramDeadlineMinute);
                params.put(getResources().getString(R.string.DINNERTIMEHOUR), paramDinnerHour);
                params.put(getResources().getString(R.string.DINNERTIMEMINUTE), paramDinnerMinute);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

//    private void postPollFood(String pollID) {
//
//        for (int i = 0; i < dataAdapter.)
/*
    StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL)
            + getResources().getString(R.string.addFoodChoice),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jObj = null;
                    Boolean isError = false;
                    try {
                        jObj = new JSONObject(response);
                        isError = jObj.getBoolean("isError");
                        if (!isError) {
                            Toast.makeText(CreatePollActivity.this, jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
                            retrievePollFoodOptions();
//                                String foodName = jObj.getString(getResources().getString(R.string.FOODNAME));
//                                foodChoices.add(new FoodItem(paramFID, foodName));
                        } else {
                            Toast.makeText(CreatePollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CreatePollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put(getResources().getString(R.string.FOODID), paramFID);
            params.put(getResources().getString(R.string.POLLID), paramPID);
            return params;
        }
    };
    requestQueue.add(stringRequest);*/
//    }

    private void retrievePollFoodOptions() {
        RequestManager.getInstance(CreatePollActivity.this).getAllPollFood(CreatePollActivity.this.pollID,
                new CustomListener<JSONArray>()
                {
                    @Override
                    public void getResult(JSONArray result) throws JSONException {
                        if (!(result == null))
                        {
                            updateFoodTypes(result);
                        }
                    }
                });
    }

    private void enableButtons() {
        dinnerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreatePollActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //pad adds 0's that are usually removed like 04:07 becomes 4:7, now it fixes that
                        CreatePollActivity.this.dinnerTimeHour = Integer.toString(selectedHour);
                        CreatePollActivity.this.dinnerTimeMinute = Integer.toString(selectedMinute);
                        dinnerTime.setText(pad(selectedHour) + ":" + pad(selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select the time of dinner");
                mTimePicker.show();
            }
        });

        deadlineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreatePollActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //pad adds 0's that are usually removed like 04:07 becomes 4:7, now it fixes that
                        CreatePollActivity.this.deadlineTimeHour = Integer.toString(selectedHour);
                        CreatePollActivity.this.deadlineTimeMinute = Integer.toString(selectedMinute);
                        deadlineTime.setText(pad(selectedHour) + ":" + pad(selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select the time when the poll closes");
                mTimePicker.show();
            }
        });
    }

    public String pad(int input) {
        String str = "";
        if (input >= 10) {
            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);
        }
        return str;
    }

    private void retrieveAllFoodTypes() {
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL)
                + getResources().getString(R.string.getAllFood) ,
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
                                    for (int i=0;i < foodOptions.length();i++){
                                        JSONObject foodOption = foodOptions.getJSONObject(i);
                                        String foodName = foodOption.getString(getResources().getString(R.string.FOODNAME));
                                        String foodID = foodOption.getString(getResources().getString(R.string.FOODID));
                                        foodTypes.add(new FoodItem(foodID, foodName));
                                    }
                                }

                                lastInitialization();


                            } else {
                                Toast.makeText(CreatePollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreatePollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }


    private void updateFoodTypes(JSONArray types) throws JSONException {
        data = new ArrayList<FoodItem>();
        final int totalNumberRequests = types.length();
        requestCount = 0;
        for (int i = 0; i < types.length(); i++) {
            final JSONObject type = types.getJSONObject(i);
//            final boolean lastItem = (i == types.length() - 1);
            Log.d("Test", "food type: " + type.getString(getResources().getString(R.string.FOODID)));

            final String foodID = type.getString(getResources().getString(R.string.FOODID));
            RequestManager.getInstance(this).getFood(foodID, new CustomListener<JSONObject>()
            {
                @Override
                public void getResult(JSONObject result) throws JSONException {
                    if (!(result == null)) {
                        requestCount++;
                        String foodName = result.getString(getResources().getString(R.string.FOODNAME));
                        Log.d("Test", foodName);
                        data.add(new FoodItem(foodID, foodName));
                        if (requestCount == totalNumberRequests) {
                            Log.d("Test", "last item processed");
                            CreatePollFoodArrayAdapter adapter = new CreatePollFoodArrayAdapter(CreatePollActivity.this,CreatePollActivity.this, data, CreatePollActivity.this.pollID);
//                            ArrayAdapter<FoodItem> adapter = new ArrayAdapter<FoodItem>(CreatePollActivity.this, android.R.layout.simple_spinner_item, data);
                            listFoodChoices.setAdapter(adapter);
                        }
                    }
                }
            });
        }
    }

    private void lastInitialization() {
        dataAdapter = new ArrayAdapter<FoodItem>(CreatePollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodTypes);
// Apply the adapter to the spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (spinnerInitializedCount < spinnerCount)
                {
                    spinnerInitializedCount++;
                }
                else {
                    FoodItem selectedChoice = (FoodItem) adapterView.getItemAtPosition(pos);
                    if (data.contains(selectedChoice)) {
                        Toast.makeText(CreatePollActivity.this, "The food you are trying to add was already added to the poll", Toast.LENGTH_LONG).show();
                    } else {
                        data.add(selectedChoice);
                        pollOptionsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(CreatePollActivity.this, "The food you are trying to add was already added to the poll", Toast.LENGTH_LONG).show();
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        startPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deadlineTime.getText().equals(CreatePollActivity.this.getResources().getString(R.string.emptyTime)) ||
                        dinnerTime.getText().equals(CreatePollActivity.this.getResources().getString(R.string.emptyTime))) {
                    if (deadlineTime.getText().equals(CreatePollActivity.this.getResources().getString(R.string.emptyTime))) {
                        deadlineTime.setError("Deadline time is required!");
                    }
                    if (dinnerTime.getText().equals(CreatePollActivity.this.getResources().getString(R.string.emptyTime))) {
                        dinnerTime.setError("Dinner time is required!");
                    }
//                    Toast.makeText(CreatePollActivity.this, CreatePollActivity.this.getResources().getString(R.string.error_field_deadline_required), Toast.LENGTH_LONG).show();
                } else {
                    CreatePollActivity.this.createNewPoll();
                }


            }
        });
    }

    @Override
    public void getResult(String string) {
        Log.d("Test", "test");
        retrievePollFoodOptions();
    }

}
