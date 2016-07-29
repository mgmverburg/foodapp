package nl.pharmit.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by s157218 on 21-7-2016.
 */
public class PollPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_type_choice);

        FloatingActionButton history = (FloatingActionButton) findViewById(R.id.foodtype);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                startActivity(new Intent(PollPage.this, HistoryPoll.class));
            }
        });

        FloatingActionButton favoritepolls = (FloatingActionButton) findViewById(R.id.favoritepolls);
        favoritepolls.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollPage.this, FavoritePollActivity.class));
            }
        });
    }
}
