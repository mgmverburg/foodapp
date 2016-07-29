package nl.pharmit.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by s157218 on 21-7-2016.
 */
public class PollPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_admin);

        FloatingActionButton foodTypeButton = (FloatingActionButton) findViewById(R.id.foodType);
        foodTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                startActivity(new Intent(PollPage.this, FoodTypeActivity.class));
            }
        });

        FloatingActionButton favoritepolls = (FloatingActionButton) findViewById(R.id.favoritePolls);
        favoritepolls.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollPage.this, FavoritePollActivity.class));
            }
        });

        FloatingActionButton createPoll = (FloatingActionButton) findViewById(R.id.createPoll);
        createPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                startActivity(new Intent(PollPage.this, CreatePollActivity.class));
            }
        });
    }
}
