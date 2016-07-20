package nl.pharmit.foodapp;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by s157218 on 20-7-2016.
 */
public class AddNewPoll extends AppCompatActivity {
    ImageButton add;




    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_poll_creating_page);
        ImageButton add = (ImageButton) findViewById(R.id.imageButton2);

        add.setOnClickListener(new View.OnClickListener() {
            Fragment poll = null;
            public void onClick(View v) {
                 poll= new FragmentPoll();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, poll).commit();

            }


        });

    }

}
