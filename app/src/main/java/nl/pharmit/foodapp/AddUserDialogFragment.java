package nl.pharmit.foodapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    public interface AddUserDialogListener {
        public void onDone();
//        public void onDialogNegativeClick(DialogFragment dialog);
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
        View rootView = inflater.inflate(R.layout.add_user_dialogfragment, container,
                false);
        groupID = getArguments().getString("GID");
        mEditText = (EditText) rootView.findViewById(R.id.enterUsername);
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

                                Toast.makeText(getContext(), jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
                                mListener.onDone();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), jObj.getString(getResources().getString(R.string.errorMessage)), Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AddUserDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddUserDialogListener");
        }
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog =  builder.create();
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();



        View dialogView = inflater.inflate(R.layout.add_user_dialogfragment, null);
        builder.setTitle("Add user");
        mEditText = (EditText) dialogView.findViewById(R.id.enterUsername);
        mEditText.requestFocus();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.adduser, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(AddUserDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddUserDialogFragment.this.getDialog().cancel();
                    }
                });


        return dialog;
    }*/

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            addUser();
        }
        return false;
    }
}
