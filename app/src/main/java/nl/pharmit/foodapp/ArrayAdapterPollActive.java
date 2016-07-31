package nl.pharmit.foodapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by s157218 on 31-7-2016.
 */
public class ArrayAdapterPollActive extends ArrayAdapter<FoodItemPollResult> {
    private final Context context;
    private final List<FoodItemPollResult> values;

    public ArrayAdapterPollActive(Context context, List<FoodItemPollResult> values) {
        super(context, R.layout.rowlayoutpollactive, values);
        Collections.sort(values);
        this.context = context;
        this.values = values;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayoutpollactive, parent, false);
        TextView rank = (TextView) rowView.findViewById(R.id.textView);
        TextView foodchoice = (TextView) rowView.findViewById(R.id.foodchoice);
        TextView firstchoices = (TextView) rowView.findViewById(R.id.firstchoicevote);
        TextView secondchoices = (TextView) rowView.findViewById(R.id.secondchoicevotes);

        final FoodItemPollResult food = getItem(position);
//        rank.setText(food.rank);
        Collections.sort(this.values);
        rank.setText(Integer.toString(position + 1));
        firstchoices.setText(Integer.toString(food.getFirstChoiceAmount()));
        secondchoices.setText(Integer.toString(food.getSecondChoiceAmount()));
        foodchoice.setText(food.getFoodName());

        return rowView;
    }
}
