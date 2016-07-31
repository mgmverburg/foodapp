package nl.pharmit.foodapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginRegistrationActivity extends AppCompatActivity {

    private EditText loginFormUsername;
    private EditText loginFormPassword;
    SharedPreferences sharedPreferences;

    //TODO progress dialog to show that something is being executed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFormUsername = (EditText) findViewById(R.id.formUsername);
        loginFormPassword = (EditText) findViewById(R.id.formPassword);
        Button loginButton = (Button) findViewById(R.id.btnLogin);
        Button registerButton = (Button) findViewById(R.id.btnRegister);

        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Store values at the time of the register button click.
                String username = loginFormUsername.getText().toString();
                String password = loginFormPassword.getText().toString();
                // Check for empty data in the form
                if (!username.isEmpty() && password.length() >= 4) {
                    // login user
                    attemptRegister(username, password);
                } else {
                    // Prompt user to enter credentials
                    setError(username, password);
//                    Toast.makeText(getApplicationContext(),
//                            "Please enter your desired username and password", Toast.LENGTH_LONG)
//                            .show();
                }


                //below is in case we want to have a different view for registering
//                startActivity(new Intent(LoginRegistrationActivity.this, RegistrationActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Store values at the time of the login button click.
                String username = loginFormUsername.getText().toString();
                String password = loginFormPassword.getText().toString();
                // Check for empty data in the form
                if (!username.isEmpty() && !password.isEmpty()) {
                    // login user
                    attemptLogin(username, password);
                } else {
                    setError(username,password);
                    // Prompt user to enter credentials
//                    Toast.makeText(getApplicationContext(),
//                            "Please enter your username and password", Toast.LENGTH_LONG)
//                            .show();
                }

            }
        });
    }

    private void setError(String username, String password) {
        if (username.isEmpty()) {
            loginFormUsername.setError("Username is required!");
        } else {
            loginFormUsername.setError(null);
        }
        if (password.isEmpty()) {
            loginFormPassword.setError("Password is required");
        } else if (password.length() < 4) {
            loginFormPassword.setError("Password of at least 4 characters is required!");
        } else {
            loginFormPassword.setError(null);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     //     * If there are form errors (invalid username, missing fields, etc.), the
     //     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(String username, String password) {
        final String paramUsername = username;
        final String paramPassword = password;
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.session), Context.MODE_PRIVATE);
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.loginFile) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        String loggedInUsername = "";
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                loggedInUsername = jObj.getString(getResources().getString(R.string.USERNAME));
                                Toast.makeText(LoginRegistrationActivity.this, "Welcome " + loggedInUsername, Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getResources().getString(R.string.USERNAME), loggedInUsername);
                                editor.commit();
                                startActivity(new Intent(LoginRegistrationActivity.this, GroupPage.class));
                            } else {
                                Toast.makeText(LoginRegistrationActivity.this, jObj.getString("error_msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginRegistrationActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.USERNAME), paramUsername);
                params.put(getResources().getString(R.string.PASSWORD), paramPassword);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void attemptRegister(String username, String password) {
        final String paramUsername = username;
        final String paramPassword = password;
        //making HTTP request
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.registerFile),
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://foodapp-mverapp.rhcloud.com/Register.php",
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
                                message = jObj.getString("success_msg");
                                Toast.makeText(LoginRegistrationActivity.this, message, Toast.LENGTH_LONG).show();
                                //When done, return to the login page
//                                finish();

                            } else {
                                message = jObj.getString("error_msg");
                                Toast.makeText(LoginRegistrationActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(LoginRegistrationActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginRegistrationActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.USERNAME), paramUsername);
                params.put(getResources().getString(R.string.PASSWORD), paramPassword);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

