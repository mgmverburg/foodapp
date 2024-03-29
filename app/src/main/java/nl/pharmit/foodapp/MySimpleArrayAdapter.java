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
import java.util.Map;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final String groupID;
    private final String admin;
    private final String loggedInUser;

    public MySimpleArrayAdapter(Context context, String[] values, String groupID, String admin, String loggedInUser) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
        this.groupID = groupID;
        this.admin = admin;
        this.loggedInUser = loggedInUser;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);

        textView.setText(values[position]);
        // Change the icon for Windows and iPhone
        final String rowUsername = values[position];
        ImageButton button = (ImageButton) rowView.findViewById(R.id.remove_user);
        if (admin.equals(rowUsername) || !admin.equals(this.loggedInUser)) {
            button.invalidate();

            if (admin.equals(rowUsername)) {
                button.setImageResource(R.drawable.admin);
            } else {
                button.setVisibility(View.INVISIBLE);
            }
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeUserFromGroup(rowUsername, MySimpleArrayAdapter.this.groupID, context, false);
                }
            });
        }

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

    static public void removeUserFromGroup(String username, String groupID, Context contextParam, boolean leaveGroupParam) {
        final String paramUsername = username;
        final String paramGroupID = groupID;
        final Context context = contextParam;
        final Boolean leaveGroup = leaveGroupParam;
//        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.rootURL)
                + context.getResources().getString(R.string.removeUser) ,
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
                                if (leaveGroup) {
                                    message = "You successfully left the group";
                                } else {
                                    message = jObj.getString(context.getResources().getString(R.string.successMessage));
                                }
                                Toast.makeText(context, message , Toast.LENGTH_LONG).show();
                                ((AddUserDialogFragment.AddUserDialogListener)context).onDone(leaveGroup);
                                //reload page with data
                            } else {
                                Toast.makeText(context, jObj.getString(context.getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
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
                params.put(context.getResources().getString(R.string.USERNAME), paramUsername);
                params.put(context.getResources().getString(R.string.GROUPID), paramGroupID);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}