package nl.pharmit.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s157218 on 24-7-2016.
 */
public class FoodTypeActivity extends AppCompatActivity implements AddFoodDialogFragment.AddFoodDialogListener{
    ListView lv;
    Button addFood;
    List<FoodItem> foodTypes = new ArrayList<FoodItem>();
    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_type);

        lv = (ListView) findViewById(R.id.listView);
        addFood = (Button) findViewById(R.id.button3);
        updateFoodTypes();
    }

    private void updateFoodTypes() {
        RequestManager.getInstance(this).retrieveAllFoodTypes(new CustomListener<List<FoodItem>>()
        {
            @Override
            public void getResult(List<FoodItem> result)
            {
                if (!result.isEmpty())
                {
                    foodTypes = new ArrayList<FoodItem>();
                    foodTypes.addAll(result);
                    ArrayAdapterFoodType adapter = new ArrayAdapterFoodType(FoodTypeActivity.this, foodTypes, new CustomListener<String>() {
                        @Override
                        public void getResult(String object) throws JSONException {
                            updateFoodTypes();
                        }
                    });
                    lv.setAdapter(adapter);
                    addFood.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddFoodDialogFragment addFragment = new AddFoodDialogFragment();
                            addFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                            // Show DialogFragment
                            addFragment.show(fm, "Dialog Fragment");
//                                        startActivity(new Intent(GroupPage.this, AddNewPoll.class));
                        }
                    });
                    //needs to be replaced with possible loading from saved polls options
//                    retrievePollFoodOptions();
                }
            }
        });
    }

    @Override
    public void onDone(boolean restartActivity) {
        if (restartActivity) {
            startActivity(new Intent(FoodTypeActivity.this, GroupPage.class));
        } else {
            updateFoodTypes();
        }
    }
}
