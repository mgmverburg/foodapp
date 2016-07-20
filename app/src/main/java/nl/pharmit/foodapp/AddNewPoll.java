package nl.pharmit.foodapp;

import android.app.FragmentManager;
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
    View view;



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_poll_creating_page);
        ImageButton add = (ImageButton) findViewById(R.id.imageButton2);

    }

    public void thePoll(View view) {
        Fragment poll = null;

        if(view == findViewById(R.id.add)) {
            poll = new FragmentPoll();

        }
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.commit();





    }
}
