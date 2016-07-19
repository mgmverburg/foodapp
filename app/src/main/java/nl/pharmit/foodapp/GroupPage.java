package nl.pharmit.foodapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by s157218 on 18-7-2016.
 */
public class GroupPage  extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page);
        final Button button = (Button) findViewById(R.id.button);
        Typeface typeface= Typeface.createFromAsset(getAssets(),"Lato-Regular.ttf");
        button.setText("CREATE GROUP");
        button.setTypeface(typeface);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(GroupPage.this, GroupPageCreated.class));
            }

        });

    }
}
