package nl.pharmit.foodapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePollFoodArrayAdapter extends ArrayAdapter<FoodItem> {
    private final Context context;
    private final List<FoodItem> values;
    private final String pollID;
    private final CustomListener<String> listener;

    public CreatePollFoodArrayAdapter(Context context, CustomListener<String> listener, List<FoodItem> values, String pollID) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
        this.pollID = pollID;
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
        final String rowFoodID = values.get(position).getFoodID();
        ImageButton button = (ImageButton) rowView.findViewById(R.id.remove_user);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePollFoodOption(rowFoodID, CreatePollFoodArrayAdapter.this.pollID, context);
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

     public void removePollFoodOption(String foodID, String pollID, Context contextParam) {
        final String paramFoodID = foodID;
        final String paramPollID = pollID;
        final Context context = contextParam;
//        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL)
                + context.getResources().getString(R.string.removeFoodChoice) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        String message = "";
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                CreatePollFoodArrayAdapter.this.listener.getResult("test");
                                //reload page with data
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(context.getResources().getString(R.string.FOODID), paramFoodID);
                params.put(context.getResources().getString(R.string.POLLID), paramPollID);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}