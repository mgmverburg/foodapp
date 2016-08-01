package nl.pharmit.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
 * Created by s157218 on 28-7-2016.
 */
public class FavoritePollActivity extends AppCompatActivity implements CustomListener<String> {
    ListView favoritePolls;
    ArrayAdapterFavorites favoritePollsAdapter;
    String[] values;
    List<String> names;
    Button addNewFavorite ;
    int requestCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poll_favorites);
        favoritePolls = (ListView) findViewById(R.id.favoritePolls);
        addNewFavorite = (Button) findViewById(R.id.addNewPollFav);

        names = new ArrayList<String>();

        getAllFavoritePolls();
        addNewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoritePollActivity.this, NewFavoritePollActivity.class));
            }
        });

    }

    private void getAllFavoritePolls() {
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.getResources().getString(R.string.rootURL)
                + this.getResources().getString(R.string.getAllFavoritePolls) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                JSONArray favoritePollsArray = jObj.getJSONArray(FavoritePollActivity.this.getResources().getString(R.string.FOODITEMS));
                                final int totalNumberRequests = favoritePollsArray.length();
                                requestCount = 0;
//                                final List<FoodItem> foodChoices = new ArrayList<FoodItem>();
                                for (int i=0;i<favoritePollsArray.length();i++) {
                                    JSONObject favoritePoll = null;
                                    favoritePoll = favoritePollsArray.getJSONObject(i);
                                    final String name = favoritePoll.getString(FavoritePollActivity.this.getResources().getString(R.string.FAVORITENAMERETURN));
                                    requestCount++;
                                    names.add(name);
                                    if (requestCount == totalNumberRequests) {
                                        favoritePollsAdapter = new ArrayAdapterFavorites(FavoritePollActivity.this, names, FavoritePollActivity.this);
                                        favoritePolls.setAdapter(favoritePollsAdapter);

                                    }

                                }
                            } else {
                                Toast.makeText(FavoritePollActivity.this, jObj.getString(FavoritePollActivity.this.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FavoritePollActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void getResult(String removeFavorite) {
        favoritePollsAdapter.remove(removeFavorite);
//        retrievePollFoodOptions();
    }

}
