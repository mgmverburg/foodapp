package nl.pharmit.foodapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by s157218 on 28-7-2016.
 */
public class FavoritePollActivity extends AppCompatActivity {
    ListView favoritePolls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poll_favorites);
    }
}
