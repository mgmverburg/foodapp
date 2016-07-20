package nl.pharmit.foodapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by s157218 on 18-7-2016.
 */
public class GroupPage  extends AppCompatActivity {
    Button button;
    String username;
    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedpreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        username = sharedpreferences.getString(getResources().getString(R.string.USERNAME), null);
        FindUserGroup(username);


    }

    private void FindUserGroup(String username) {
        final String paramUsername = username;
        boolean hasGroup = false;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.getUserGroup) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        String userGroupID;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                userGroupID = jObj.getString(getResources().getString(R.string.GROUPID));
                                setContentView(R.layout.activity_group_page_created);
                                updateGroupInfo(userGroupID);

//                                startActivity(new Intent(GroupPage.this, GroupPageCreated.class));
//
                            } else {
                                setContentView(R.layout.activity_group_page);
                                final Button button = (Button) findViewById(R.id.button);
                                Typeface typeface= Typeface.createFromAsset(getAssets(),"Lato-Regular.ttf");
                                button.setText("CREATE GROUP");
                                button.setTypeface(typeface);
                                button.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v){
                                        startActivity(new Intent(GroupPage.this, GroupPage.class));
                                    }

                                });
//                                Toast.makeText(GroupPageCreated.this, jObj.getString("error_msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GroupPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.USERNAME), paramUsername);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateGroupInfo(String groupID) {
        final String paramGID = groupID;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.getGroup) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                String groupName = jObj.getString(getResources().getString(R.string.GROUPNAME));
                                TextView groupNameText = (TextView) findViewById(R.id.group_name);
                                groupNameText.setText(groupName);
                                JSONArray users = jObj.getJSONArray(getResources().getString(R.string.GROUPMEMBERS));
                                updateListUsers(users);
                                ImageButton invite = (ImageButton) findViewById(R.id.imageButton);
                                invite.setOnClickListener(new View.OnClickListener() {
                                    @Override

                                    public void onClick(View v) {
                                        startActivity(new Intent(GroupPage.this, AddNewPoll.class));
                                    }
                                });
//
                            } else {
                                Toast.makeText(GroupPage.this, jObj.getString("error_msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GroupPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.GROUPID), paramGID);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void updateListUsers(JSONArray members) throws JSONException {
        ListView lv = (ListView) findViewById(R.id.list);
        data = new String[members.length()];
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            data[i] = member.getString("username");
//            data.add("This user" + i);
        }

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(GroupPage.this, data);
        lv.setAdapter(adapter);
    }
}
