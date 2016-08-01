package nl.pharmit.foodapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by s157218 on 31-7-2016.
 */
public class ArrayAdapterFavorites extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;
    CustomListener<String> listener;

    public ArrayAdapterFavorites(Context context, List<String> values, CustomListener<String> listener) {
        super(context, R.layout.row_layout_favorites, values);
        this.context = context;
        this.values = values;
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView pollName = (TextView) rowView.findViewById(R.id.label);
        final String name = getItem(position);
        pollName.setText(name);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewFavoritePollActivity.class);
                Bundle b = new Bundle();
                b.putString("name", name); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                context.startActivity(intent);
//                finish();
            }
        });

        ImageButton button = (ImageButton) rowView.findViewById(R.id.remove_user);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFavoritePoll(name);

            }
        });
        return rowView;
    }

    private void removeFavoritePoll(String name) {
        final String paramFavoriteName = name;
//        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL)
                + context.getResources().getString(R.string.removeFavoritePoll) ,
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
                                try {
                                    listener.getResult(paramFavoriteName);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                params.put(context.getResources().getString(R.string.FAVORITENAME), paramFavoriteName);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
