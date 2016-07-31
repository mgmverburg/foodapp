package nl.pharmit.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by s157218 on 28-7-2016.
 */
public class FavoritePollActivity extends AppCompatActivity {
    ListView favoritePolls;
    ArrayAdapterFavorites favoritePollsAdapter;
    String[] values;
    Button addNewFavorite = (Button) findViewById(R.id.addNewPollFav);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poll_favorites);
        favoritePolls = (ListView) findViewById(R.id.favoritePolls);
        favoritePollsAdapter = new ArrayAdapterFavorites(this, values);
        favoritePolls.setAdapter(favoritePollsAdapter);
        addNewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoritePollActivity.this, NewFavoritePollActivity.class));
            }
        });
    }
}
