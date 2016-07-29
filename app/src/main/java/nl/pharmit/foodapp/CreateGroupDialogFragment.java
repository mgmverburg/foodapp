package nl.pharmit.foodapp;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by s148494 on 21-7-2016.
 */

public class CreateGroupDialogFragment extends DialogFragment {
    EditText groupNameText;
    AddUserDialogFragment.AddUserDialogListener mListener;
    String username;
    Context context;

//    public interface AddUserDialogListener {
//        public void onDone();
////        public void onDialogNegativeClick(DialogFragment dialog);
//    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;

        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.popup_width_ratio, typedValue, true);
        float myFloatValue = typedValue.getFloat();
//        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        window.setLayout((int) (width*(myFloatValue)), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public static CreateGroupDialogFragment newInstance(String username) {
        CreateGroupDialogFragment f = new CreateGroupDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("username", username);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_group_dialogfragment, container,
                false);
        username = getArguments().getString("username");
        groupNameText = (EditText) rootView.findViewById(R.id.enterGroupname);
        getDialog().setTitle("Create group");
        groupNameText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        groupNameText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        groupNameText.setOnEditorActionListener(this);

        Button Cancel= (Button) rootView.findViewById(R.id.Cancel);
        Cancel.setOnClickListener(onCancel);
        Button OK = (Button) rootView.findViewById(R.id.Create);
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
                    createGroup();
                }
            };

    private void createGroup() {
        final String paramAdmin = this.username;
        final String paramGroupname = groupNameText.getText().toString();
        //making HTTP request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.createGroup) ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        Boolean isError = false;
                        try {
                            jObj = new JSONObject(response);
                            isError = jObj.getBoolean("isError");
                            if (!isError) {
                                String groupID = jObj.getString(getResources().getString(R.string.GROUPID));
                                Toast.makeText(context, jObj.getString(getResources().getString(R.string.successMessage)), Toast.LENGTH_LONG).show();
                                mListener.onDone(true);
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
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.GROUPNAME), paramGroupname);
                params.put(getResources().getString(R.string.ADMIN), paramAdmin);
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
            mListener = (AddUserDialogFragment.AddUserDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddUserDialogListener");
        }
    }

//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        if (EditorInfo.IME_ACTION_DONE == actionId) {
//            addUser();
//        }
//        return false;
//    }
}
