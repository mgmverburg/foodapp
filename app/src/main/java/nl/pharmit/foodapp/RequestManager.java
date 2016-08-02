package nl.pharmit.foodapp;

import android.content.Context;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by s148494 on 26-7-2016.
 */

public class RequestManager {

    private static RequestManager instance = null;
    public RequestQueue requestQueue;
    Context context;
    DateFormat dateFormat;
    Calendar calendar;
    int requestCount;

    private RequestManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar = Calendar.getInstance();
    }

    public static synchronized RequestManager getInstance(Context context)
    {
        if (null == instance)
            instance = new RequestManager(context);
        return instance;
    }

    //this is so you don't need to pass context each time
    public static synchronized RequestManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }


    public void getActivePoll(String groupIDparam, final CustomListener<JSONObject> listener ) {
        final String paramGID = groupIDparam;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.context.getResources().getString(R.string.rootURL)
                + RequestManager.this.context.getResources().getString(R.string.getActivePoll) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONObject poll = jObj.getJSONObject(RequestManager.this.context.getResources().getString(R.string.POLLINFO));
                                String deadlineTime = poll.getString(context.getResources().getString(R.string.DEADLINETIME));
                                try {
                                    calendar.setTime(dateFormat.parse(deadlineTime));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                int deadlineHour = calendar.get(Calendar.HOUR_OF_DAY);
                                int deadlineMinute = calendar.get(Calendar.MINUTE);

                                int currentHour = calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                int currentMinute = calendar.getInstance().get(Calendar.MINUTE);

//                                String PID = jObj.getString(RequestManager.this.context.getResources().getString(R.string.POLLID));
                                listener.getResult(poll);
//
                            } else {
                                listener.getResult(null);
//                                Toast.makeText(RequestManager.this.context, jObj.getString(RequestManager.this.context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RequestManager.this.context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(RequestManager.this.context.getResources().getString(R.string.GROUPID), paramGID);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }



    public void getAllPollFood(String pollIDparam, final CustomListener<List<FoodItem>> listener) {
        final String paramPID = pollIDparam;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL)
                + context.getResources().getString(R.string.getFoodOptions) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONArray foodOptions = jObj.getJSONArray(context.getResources().getString(R.string.FOODITEMS));
                                final int totalNumberRequests = foodOptions.length();
                                requestCount = 0;
                                final List<FoodItem> foodChoices = new ArrayList<FoodItem>();
                                for (int i=0;i<foodOptions.length();i++) {
                                    JSONObject foodOption = null;
                                    foodOption = foodOptions.getJSONObject(i);
                                    final String foodID = foodOption.getString(context.getResources().getString(R.string.FOODID));
                                    RequestManager.this.getFood(foodID, new CustomListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject object) throws JSONException {
                                            requestCount++;
                                            String foodName = object.getString(context.getResources().getString(R.string.FOODNAME));
                                            foodChoices.add(new FoodItem(foodID, foodName));
                                            if (requestCount == totalNumberRequests) {
                                                listener.getResult(foodChoices);
                                            }
                                        }
                                    });

                                }
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(context.getResources().getString(R.string.POLLID), paramPID);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getFavoritePoll(String name, final CustomListener<List<FoodItem>> listener) {
        final List<FoodItem> foodOptions = new ArrayList<FoodItem>();
        final String paramFavoriteName = name;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL)
                + context.getResources().getString(R.string.getFavoritePoll) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONArray foodOptionsArray = jObj.getJSONArray(context.getResources().getString(R.string.FOODITEMS));
                                final int totalNumberRequests = foodOptionsArray.length();
                                requestCount = 0;
//                                final List<FoodItem> foodChoices = new ArrayList<FoodItem>();
                                for (int i=0;i<foodOptionsArray.length();i++) {
                                    JSONObject foodOption = null;
                                    foodOption = foodOptionsArray.getJSONObject(i);
                                    final String foodID = foodOption.getString(context.getResources().getString(R.string.FOODID));
                                    RequestManager.this.getFood(foodID, new CustomListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject object) throws JSONException {
                                            requestCount++;
                                            String foodName = object.getString(context.getResources().getString(R.string.FOODNAME));
                                            foodOptions.add(new FoodItem(foodID, foodName));
                                            if (requestCount == totalNumberRequests) {
                                                listener.getResult(foodOptions);
                                            }
                                        }
                                    });

                                }
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(context.getResources().getString(R.string.FAVORITENAME), paramFavoriteName);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getAllFavoritePolls(final CustomListener<List<String>> listener) {
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL)
                + context.getResources().getString(R.string.getAllFavoritePolls) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONArray favoritePollsArray = jObj.getJSONArray(context.getResources().getString(R.string.FOODITEMS));
                                final int totalNumberRequests = favoritePollsArray.length();
                                requestCount = 0;
                                final List<String> favorites = new ArrayList<String>();
                                for (int i=0;i<favoritePollsArray.length();i++) {
                                    JSONObject favoritePoll = null;
                                    favoritePoll = favoritePollsArray.getJSONObject(i);
                                    final String name = favoritePoll.getString(context.getResources().getString(R.string.FAVORITENAMERETURN));
                                    requestCount++;
                                    favorites.add(name);
                                    if (requestCount == totalNumberRequests) {
                                        listener.getResult(favorites);


                                    }

                                }
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getFood(String foodID, final CustomListener<JSONObject> listener) {
        final String paramFID = foodID;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL) +
                context.getResources().getString(R.string.getFood) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");

                            if (!isError) {
                                listener.getResult(jObj);
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(context.getResources().getString(R.string.FOODID), paramFID);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void retrieveAllFoodTypes(final CustomListener<List<FoodItem>> listener) {
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL)
                + context.getResources().getString(R.string.getAllFood) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONArray foodOptions = jObj.getJSONArray(context.getResources().getString(R.string.FOODITEMS));
                                List<FoodItem> foodTypes = new ArrayList<FoodItem>();
                                if (foodOptions != null) {
                                    for (int i=0;i < foodOptions.length();i++){
                                        JSONObject foodOption = foodOptions.getJSONObject(i);
                                        String foodName = foodOption.getString(context.getResources().getString(R.string.FOODNAME));
                                        String foodID = foodOption.getString(context.getResources().getString(R.string.FOODID));
                                        foodTypes.add(new FoodItem(foodID, foodName));
                                    }
                                }
                                listener.getResult(foodTypes);
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    public void addUser(String username, String paramGroupID, final CustomListener<String> listener) {
        final String paramUsername = username;
        final String paramGID = paramGroupID;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL) + context.getResources().getString(R.string.addUser) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {

//                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
                                listener.getResult("");
//                                dismiss();
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(context.getResources().getString(R.string.USERNAME), paramUsername);
                params.put(context.getResources().getString(R.string.GROUPID), paramGID);
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }
}
