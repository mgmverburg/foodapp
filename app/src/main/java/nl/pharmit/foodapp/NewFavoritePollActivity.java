package nl.pharmit.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

/**
 * Created by s157218 on 31-7-2016.
 */
public class NewFavoritePollActivity extends AppCompatActivity implements CustomListener<FoodItem> {
    TextView textPollChoices;
    ListView listFoodChoices;
    Button addFood, savePoll;
    List<FoodItem> allFoodTypes = new ArrayList<FoodItem>();
    RequestQueue requestQueue;
    Spinner spinner;
    EditText pollName;
    CustomSpinnerAdapter dataAdapter;
    CreatePollFoodArrayAdapter pollOptionsAdapter;
    String existingName;
    boolean creatingNew;
    private List<FoodItem> foodOptions;
    int requestCount,totalRequestCount,spinnerCount = 0 , spinnerInitializedCount = 0;//this counts how many Gallery's are on the UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_favorite_poll);

        requestQueue = Volley.newRequestQueue(this);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            existingName = b.getString("name");
            creatingNew = false;
        } else {
            creatingNew = true;
        }

        listFoodChoices = (ListView) findViewById(R.id.listViewPollChoices);
        spinner = (Spinner) findViewById(R.id.spinner);
        addFood = (Button) findViewById(R.id.addFoodButton);
        savePoll = (Button) findViewById(R.id.savePollButton);
        pollName = (EditText) findViewById(R.id.namePollFav);
        textPollChoices = (TextView) findViewById(R.id.textViewPollChoices);

        if (!creatingNew) {
            pollName.setEnabled(false);
            pollName.setClickable(false);
            pollName.setText(existingName);
        }

        spinnerCount = 1;

        foodOptions = new ArrayList<FoodItem>();
        pollOptionsAdapter = new CreatePollFoodArrayAdapter(NewFavoritePollActivity.this, NewFavoritePollActivity.this, foodOptions);
        listFoodChoices.setAdapter(pollOptionsAdapter);
        RequestManager.getInstance(this).retrieveAllFoodTypes(new CustomListener<List<FoodItem>>()
        {
            @Override
            public void getResult(List<FoodItem> result)
            {
                if (!result.isEmpty())
                {
                    allFoodTypes.addAll(result);
                    if (!creatingNew) {
                        getFavoritePoll(existingName);
                    } else {
                        lastInitialization();
                    }
                    //needs to be replaced with possible loading from saved polls options
//                    retrievePollFoodOptions();
                }
            }
        });
    }


    private void lastInitialization() {
        dataAdapter = new CustomSpinnerAdapter(NewFavoritePollActivity.this, android.R.layout.simple_spinner_dropdown_item, allFoodTypes);
// Apply the adapter to the spinner
        dataAdapter.insert(new FoodItem("", ""),0);
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


                    if (foodOptions.contains(selectedChoice) && creatingNew) {
                        Toast.makeText(NewFavoritePollActivity.this, "The food you are trying to add was already added to the poll", Toast.LENGTH_LONG).show();
                    } else if (!foodOptions.contains(selectedChoice) && creatingNew) {
                        pollOptionsAdapter.add(selectedChoice);
                    } else {
                        NewFavoritePollActivity.this.postFavoritePollFood(existingName, selectedChoice.getFoodID());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(NewFavoritePollActivity.this, "The food you are trying to add was already added to the poll", Toast.LENGTH_LONG).show();
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        savePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pollName.getText().toString().isEmpty() ||
                        pollOptionsAdapter.getCount() < 2) {
                    if (pollName.getText().toString().isEmpty()) {
                        pollName.setError("Deadline time is required!");
                    } else {
                        pollName.setError(null);
                    }
                    if (pollOptionsAdapter.getCount() < 2) {
                        textPollChoices.setError("At least 2 food type options must be added!");
                    } else {
                        textPollChoices.setError(null);
                    }
//                    Toast.makeText(CreatePollActivity.this, CreatePollActivity.this.getResources().getString(R.string.error_field_deadline_required), Toast.LENGTH_LONG).show();
                } else {
                    NewFavoritePollActivity.this.savePoll();
                }


            }
        });
    }

    private void getFavoritePoll(String name) {
        final String paramFavoriteName = name;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL)
                + getResources().getString(R.string.getFavoritePoll) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONArray foodOptionsArray = jObj.getJSONArray(getResources().getString(R.string.FOODITEMS));
                                final int totalNumberRequests = foodOptionsArray.length();
                                requestCount = 0;
//                                final List<FoodItem> foodChoices = new ArrayList<FoodItem>();
                                for (int i=0;i<foodOptionsArray.length();i++) {
                                    JSONObject foodOption = null;
                                    foodOption = foodOptionsArray.getJSONObject(i);
                                    final String foodID = foodOption.getString(getResources().getString(R.string.FOODID));
                                    RequestManager.getInstance(NewFavoritePollActivity.this).getFood(foodID, new CustomListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject object) throws JSONException {
                                            requestCount++;
                                            String foodName = object.getString(getResources().getString(R.string.FOODNAME));
                                            pollOptionsAdapter.add(new FoodItem(foodID, foodName));
                                            if (requestCount == totalNumberRequests) {
                                                lastInitialization();
                                            }
                                        }
                                    });

                                }
                            } else {
                                Toast.makeText(NewFavoritePollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewFavoritePollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.FAVORITENAME), paramFavoriteName);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void savePoll() {

        if (creatingNew) {
            final String paramFavoriteName = pollName.getText().toString();
            //making HTTP request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL)
                    + getResources().getString(R.string.saveFavoritePoll),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jObj = null;
                            Boolean isError = false;
                            try {
                                jObj = new JSONObject(response);
                                isError = jObj.getBoolean("isError");
                                if (!isError) {
                                    postAllFavoritePollFood(paramFavoriteName);
                                } else {
                                    Toast.makeText(NewFavoritePollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(NewFavoritePollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(getResources().getString(R.string.FAVORITENAME), paramFavoriteName);
                    return params;
                }

            };
            requestQueue.add(stringRequest);

        } else {
            startActivity(new Intent(NewFavoritePollActivity.this, FavoritePollActivity.class));
        }

    }

    private void postAllFavoritePollFood(String name) {
        final String paramFavoriteName = name;
        requestCount = 0;
        totalRequestCount = pollOptionsAdapter.getCount();
        for (int i = 0; i < pollOptionsAdapter.getCount(); i++) {
            FoodItem option = pollOptionsAdapter.getItem(i);
            String foodID = option.getFoodID();
            postFavoritePollFood(paramFavoriteName, foodID);

        }
    }



    private void postFavoritePollFood(String name, String foodID) {
        final String paramFavoriteName = name;
        final String paramFID = foodID;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL)
                + getResources().getString(R.string.addFavoritePollFoodChoice),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                requestCount++;
                                if (requestCount == totalRequestCount && creatingNew) {
                                    startActivity(new Intent(NewFavoritePollActivity.this, FavoritePollActivity.class));
                                }
                            } else {
                                Toast.makeText(NewFavoritePollActivity.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewFavoritePollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.FAVORITENAME), paramFavoriteName);
                params.put(getResources().getString(R.string.FOODID), paramFID);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void getResult(FoodItem removedFood) {
        pollOptionsAdapter.remove(removedFood);
//        retrievePollFoodOptions();
    }


}
