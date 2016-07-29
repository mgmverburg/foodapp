package nl.pharmit.foodapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by s148494 on 20-7-2016.
 */

public class AddUserDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {
    EditText mEditText;
    AddUserDialogListener mListener;
    String groupID;
    Context context;

    public interface AddUserDialogListener {
        public void onDone(boolean restartActivity);
    }

    public static AddUserDialogFragment newInstance(String groupID) {
        AddUserDialogFragment f = new AddUserDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("GID", groupID);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_dialogfragment, container,
                false);
        groupID = getArguments().getString("GID");
        mEditText = (EditText) rootView.findViewById(R.id.enterName);
        getDialog().setTitle("Add user");
        mEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);

        Button Cancel= (Button) rootView.findViewById(R.id.Cancel);
        Cancel.setOnClickListener(onCancel);
        Button OK= (Button) rootView.findViewById(R.id.Add);
        OK.setOnClickListener(onOK);

        // Do something else
        return rootView;
    }

    View.OnClickListener onCancel=
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
//                    mListener.onDone();
                    dismiss();
                }
            };

    View.OnClickListener onOK=
            new View.OnClickListener(){
                @Override public void onClick(View view){
                    addUser();
                }
            };

    public void addUser() {
        final String paramUsername = mEditText.getText().toString();
        final String paramGID = AddUserDialogFragment.this.groupID;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.addUser) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {

                                Toast.makeText(context, jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
                                mListener.onDone(false);
                                dismiss();
                            } else {
                                Toast.makeText(context, jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.USERNAME), paramUsername);
                params.put(getResources().getString(R.string.GROUPID), paramGID);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            mListener = (AddUserDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddUserDialogListener");
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            addUser();
        }
        return false;
    }
}
