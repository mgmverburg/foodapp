package nl.pharmit.foodapp;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

public class AddFoodDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {
    EditText mEditText;
    AddFoodDialogListener mListener;
    Context context;

    public interface AddFoodDialogListener {
        public void onDone(boolean restartActivity);
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_dialogfragment, container,
                false);
        mEditText = (EditText) rootView.findViewById(R.id.enterName);
        mEditText.setHint("Enter the food name");
        getDialog().setTitle("Add food type");
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
                    addFood();
                }
            };

    public void addFood() {
        final String paramFoodName = mEditText.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.rootURL) + getResources().getString(R.string.addFood) ,
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
                params.put(getResources().getString(R.string.FOODNAME), paramFoodName);
                params.put(getResources().getString(R.string.FOODDESCRIPTION), "");
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
            mListener = (AddFoodDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddUserDialogListener");
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            addFood();
        }
        return false;
    }
}
