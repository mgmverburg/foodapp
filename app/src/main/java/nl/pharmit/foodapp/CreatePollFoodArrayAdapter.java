package nl.pharmit.foodapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

public class CreatePollFoodArrayAdapter extends ArrayAdapter<FoodItem> {
    private final CustomListener<FoodItem> listener;
    private final List<FoodItem> items;

    public CreatePollFoodArrayAdapter(Context context, CustomListener<FoodItem> listener, List<FoodItem> values) {
        super(context, 0, values);
        this.listener = listener;
        this.items =values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rowlayout, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.label);
        final FoodItem food = getItem(position);
        textView.setText(food.getFoodName());
        // Change the icon for Windows and iPhone

        ImageButton button = (ImageButton) convertView.findViewById(R.id.remove_user);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        CreatePollFoodArrayAdapter.this.listener.getResult(food);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    values.remove(food);
//                    CreatePollFoodArrayAdapter.this.notifyDataSetChanged();
                }
            });

        //if user is itself, then don't show X button
//        if (s.startsWith("Windows7") || s.startsWith("iPhone")
//                || s.startsWith("Solaris")) {
//            imageView.invalidate();
//            imageView.setImageResource(R.drawable.no);
//        }
//        else {
//            imageView.setImageResource(R.drawable.ok);
//        }

        return convertView;
    }

    public List<FoodItem> getItems() {
        return items;
    }
}