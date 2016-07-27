package nl.pharmit.foodapp;

import org.json.JSONException;

/**
 * Created by s148494 on 26-7-2016.
 */

public interface CustomListener<T> {
    public void getResult(T object) throws JSONException;
}
