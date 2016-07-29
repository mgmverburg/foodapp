package nl.pharmit.foodapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by s157218 on 18-7-2016.
 */
public class GroupPage extends AppCompatActivity implements AddUserDialogFragment.AddUserDialogListener {
    Button button;
    Context context = this;
    String username, groupID, admin;
    boolean isAdmin;
    FragmentManager fm = getSupportFragmentManager();
    private String[] data;
    SharedPreferences sharedPreferences;
    ToggleButton polltab;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        username = sharedPreferences.getString(getResources().getString(R.string.USERNAME), null);
        FindUserGroup(username);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void FindUserGroup(String username) {
        final String paramUsername = username;
        boolean hasGroup = false;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.getUserGroup),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        String userGroupID, admin;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                GroupPage.this.groupID = jObj.getString(getResources().getString(R.string.GROUPID));
                                sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getResources().getString(R.string.GROUPID), GroupPage.this.groupID);
                                editor.commit();
                                setContentView(R.layout.activity_group_page_created);
                                polltab = (ToggleButton) findViewById(R.id.polltab);
                                polltab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(GroupPage.this, PollPage.class));
                                    }
                                });


                                updateGroupInfo();
                            } else {
                                setup(false);
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

    private void setup(boolean hasGroup) {

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupPage.this, FoodTypeActivity.class));
            }
        });
        if (hasGroup) {

            ImageButton invite = (ImageButton) findViewById(R.id.buttonadd);
            final String alertMessage;
            if (this.username.equals(this.admin)) {
                isAdmin = true;
                alertMessage = "Are you sure you want to disband the group?";
                invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddUserDialogFragment addFragment = AddUserDialogFragment.newInstance(GroupPage.this.groupID);
                        addFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                        // Show DialogFragment
                        addFragment.show(fm, "Dialog Fragment");
//                                        startActivity(new Intent(GroupPage.this, AddNewPoll.class));
                    }
                });
            } else {
                isAdmin = false;
                alertMessage = "Are you sure you want to leave the group?";
                invite.invalidate();
                invite.setVisibility(View.INVISIBLE);

            }
            ImageButton disbandButton = (ImageButton) findViewById(R.id.buttondisband);
            disbandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set title
                    alertDialogBuilder.setTitle("Warning");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(alertMessage)
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (isAdmin) {
                                        deleteGroup();
                                    } else {
                                        MySimpleArrayAdapter.removeUserFromGroup(GroupPage.this.username, GroupPage.this.groupID,
                                                GroupPage.this, true);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
        } else {
            setContentView(R.layout.activity_group_page);
            final Button button = (Button) findViewById(R.id.button);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
            button.setText("CREATE GROUP");
            button.setTypeface(typeface);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateGroupDialogFragment createFragment = CreateGroupDialogFragment.newInstance(GroupPage.this.username);
                    createFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                    // Show DialogFragment
                    createFragment.show(fm, "Dialog Fragment");
//                                        startActivity(new Intent(GroupPage.this, GroupPage.class));
                }

            });
        }
    }

    public void deleteGroup() {
        final String paramGID = this.groupID;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.deleteGroup),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                Toast.makeText(GroupPage.this, jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
                                GroupPage.this.onDone(true);
                            } else {
                                Toast.makeText(GroupPage.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
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


    private void updateGroupInfo() {
        final String paramGID = this.groupID;
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.getGroup),
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
                                GroupPage.this.admin = jObj.getString(getResources().getString(R.string.ADMIN));
                                setup(true);
                                updateListUsers(users);
//
                            } else {
                                Toast.makeText(GroupPage.this, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
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
//        lv.setOnClickListener(null);
        data = new String[members.length()];
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            data[i] = member.getString("username");
//            data.add("This user" + i);
        }

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(GroupPage.this, data, this.groupID, this.admin, this.username);
        lv.setAdapter(adapter);
    }

    @Override
    public void onDone(boolean restartActivity) {
        if (restartActivity) {
            startActivity(new Intent(GroupPage.this, GroupPage.class));
        } else {
            updateGroupInfo();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GroupPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://nl.pharmit.foodapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GroupPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://nl.pharmit.foodapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
