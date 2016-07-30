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

import java.util.ArrayList;
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

    private RequestManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
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

    public void getAllPollFood(String pollIDparam, final CustomListener<JSONArray> listener) {
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
                                listener.getResult(foodOptions);

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
