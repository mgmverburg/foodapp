package nl.pharmit.foodapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CreatePollFoodArrayAdapter extends ArrayAdapter<FoodItem> {
    private final Context context;
    private final List<FoodItem> values;
    private final CustomListener<String> listener;

    public CreatePollFoodArrayAdapter(Context context, CustomListener<String> listener, List<FoodItem> values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
        this.listener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);

        textView.setText(values.get(position).getFoodName());
        // Change the icon for Windows and iPhone
        final FoodItem food = values.get(position);
        ImageButton button = (ImageButton) rowView.findViewById(R.id.remove_user);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    values.remove(food);
                    CreatePollFoodArrayAdapter.this.notifyDataSetChanged();
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

        return rowView;
    }
}