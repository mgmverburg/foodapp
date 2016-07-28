package nl.pharmit.foodapp;

import android.app.TimePickerDialog;
import android.content.Context;
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

public class CreatePollActivity extends AppCompatActivity implements CustomListener<String>{
    TextView dinnerTime, deadline;
    ListView listFoodChoices;
    Button addFood;
    List<FoodItem> foodChoices = new ArrayList<FoodItem>();
    RequestQueue requestQueue;
    Spinner spinner;
    ArrayAdapter<FoodItem> dataAdapter;
    SharedPreferences sharedPreferences;
    String pollID, groupID;
    private List<FoodItem> data;
    int requestCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        this.groupID = sharedPreferences.getString(getResources().getString(R.string.GROUPID), null);


        requestQueue = Volley.newRequestQueue(this);

        dinnerTime = (TextView) findViewById(R.id.timePickerDinner);
        deadline = (TextView) findViewById(R.id.timePickerDeadline);
        listFoodChoices = (ListView) findViewById(R.id.listViewPollChoices);
        spinner = (Spinner) findViewById(R.id.spinner);
        addFood = (Button) findViewById(R.id.addFoodButton);

        RequestManager.getInstance(this).getActivePoll(this.groupID, new CustomListener<String>()
        {
            @Override
            public void getResult(String result)
            {
                if (!result.isEmpty())
                {
                    CreatePollActivity.this.pollID = result;
                    enableButtons();
                    retrievePollFoodOptions();
                }
            }
        });
        retrieveAllFoodOptions();
    }

    private void createNewPoll() {
        final String paramGID = this.groupID;
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
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

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
                        dinnerTime.setText(pad(selectedHour) + ":" + pad(selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select the time of dinner");
                mTimePicker.show();
            }
        });

        deadline.setOnClickListener(new View.OnClickListener() {
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
                        deadline.setText(pad(selectedHour) + ":" + pad(selectedMinute));
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

    private void retrieveAllFoodOptions() {
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
                                        boolean lastItem = false;
                                        if (i == foodOptions.length() - 1) {
//                                            Toast.makeText(PollActivity.this, "test", Toast.LENGTH_LONG).show();
                                            lastItem = true;
                                        }
                                        JSONObject foodOption = foodOptions.getJSONObject(i);
                                        String foodName = foodOption.getString(getResources().getString(R.string.FOODNAME));
                                        String foodID = foodOption.getString(getResources().getString(R.string.FOODID));
                                        foodChoices.add(new FoodItem(foodID, foodName));
                                    }
                                }
                                dataAdapter = new ArrayAdapter<FoodItem>(CreatePollActivity.this, android.R.layout.simple_spinner_dropdown_item, foodChoices);
// Apply the adapter to the spinner
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                                        FoodItem selectedChoice =  (FoodItem) adapterView.getItemAtPosition(pos);
                                        addOption(selectedChoice);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                                spinner.setAdapter(dataAdapter);
                                addFood.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        spinner.performClick();
                                    }
                                });

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

    private void addOption(FoodItem food) {
        final String paramFID = food.getFoodID();
        final String paramPID = this.pollID;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL)
                + getResources().getString(R.string.addFoodChoice) ,
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

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateFoodTypes(JSONArray types) throws JSONException {
        data = new ArrayList<FoodItem>();
        final int totalNumberRequests = types.length();
        requestCount = 0;
        for (int i = 0; i < types.length(); i++) {
            final JSONObject type = types.getJSONObject(i);
//            final boolean lastItem = (i == types.length() - 1);
            RequestManager.getInstance(this).getFood(type.getString(getResources().getString(R.string.FOODID)), new CustomListener<JSONObject>()
            {
                @Override
                public void getResult(JSONObject result) throws JSONException {
                    if (!(result == null)) {
                        requestCount++;
                        Log.d("Test", result.getString(getResources().getString(R.string.FOODNAME)));
                        data.add(new FoodItem(result.getString(getResources().getString(R.string.FOODID)),
                                result.getString(getResources().getString(R.string.FOODNAME))));
                        if (requestCount == totalNumberRequests) {
                            Log.d("Test", "last item processed");
                            CreatePollFoodArrayAdapter adapter = new CreatePollFoodArrayAdapter(CreatePollActivity.this, data, CreatePollActivity.this.pollID);
//                            ArrayAdapter<FoodItem> adapter = new ArrayAdapter<FoodItem>(CreatePollActivity.this, android.R.layout.simple_spinner_item, data);
                            listFoodChoices.setAdapter(adapter);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void getResult(String result) {
        retrievePollFoodOptions();
    }

}
