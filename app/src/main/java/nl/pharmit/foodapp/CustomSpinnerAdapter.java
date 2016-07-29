package nl.pharmit.foodapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Michiel on 7/29/2016.
 */
public class CustomSpinnerAdapter extends ArrayAdapter<FoodItem> {

    private int hidingItemIndex = 0;

    public CustomSpinnerAdapter(Context context, int textViewResourceId, List<FoodItem> objects) {
        super(context, textViewResourceId, objects);
//        this.hidingItemIndex = hidingItemIndex;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if (position == hidingItemIndex) {
            TextView tv = new TextView(getContext());
            tv.setHeight(0);
            tv.setVisibility(View.GONE);
            v = tv;
        } else {
            v = super.getDropDownView(position, null, parent);
        }
        return v;
    }
}
