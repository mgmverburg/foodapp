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
//    String[] values;
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

        RequestManager.getInstance(this).getAllFavoritePolls(new CustomListener<List<String>>() {
            @Override
            public void getResult(List<String> result) throws JSONException {
                names.addAll(result);
                favoritePollsAdapter = new ArrayAdapterFavorites(FavoritePollActivity.this, names, FavoritePollActivity.this);
                favoritePolls.setAdapter(favoritePollsAdapter);
            }
        });
        addNewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoritePollActivity.this, NewFavoritePollActivity.class));
            }
        });

    }

    @Override
    public void getResult(String removeFavorite) {
        favoritePollsAdapter.remove(removeFavorite);
        Toast.makeText(FavoritePollActivity.this, "Poll deleted", Toast.LENGTH_SHORT).show();
//        retrievePollFoodOptions();
    }

}
