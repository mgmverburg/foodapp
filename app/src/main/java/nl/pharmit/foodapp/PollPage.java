package nl.pharmit.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ToggleButton;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by s157218 on 21-7-2016.
 */
public class PollPage extends AppCompatActivity {
    ToggleButton polltab2;
    ToggleButton grouptab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_admin);
        polltab2 = (ToggleButton) findViewById(R.id.polltab2);
        grouptab2 = (ToggleButton) findViewById(R.id.grouptab2);

        polltab2.setChecked(true);
        polltab2.setEnabled(false);
        grouptab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollPage.this, GroupPage.class));
                polltab2.setChecked(false);
            }
        });

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
