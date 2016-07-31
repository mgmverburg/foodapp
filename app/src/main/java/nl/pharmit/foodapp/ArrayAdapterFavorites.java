package nl.pharmit.foodapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by s157218 on 31-7-2016.
 */
public class ArrayAdapterFavorites extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    public ArrayAdapterFavorites(Context context, String[] values) {
        super(context, R.layout.row_layout_favorites, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout_favorites, parent, false);
        TextView pollName = (TextView) rowView.findViewById(R.id.pollName);;
        pollName.setText(getItem(position));
        return rowView;
    }
}
