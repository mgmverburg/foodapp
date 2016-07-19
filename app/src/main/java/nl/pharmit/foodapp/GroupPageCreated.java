package nl.pharmit.foodapp;

import android.app.ListActivity;
import android.content.Context;
import android.icu.text.BreakIterator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by s157218 on 18-7-2016.
 */
public class GroupPageCreated extends AppCompatActivity {

    private String[] data = new String[55];
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page_created);
        ListView lv = (ListView) findViewById(R.id.list);
        generateListContent();

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, data);
        lv.setAdapter(adapter);
//        lv.setAdapter(new MyListAdapter(this, R.layout.activity_group_page_created, data));


    }

    private void generateListContent() {
        for (int i = 0; i < 55; i++) {
            data[i] = "This user" + i;
//            data.add("This user" + i);
        }
    }
}
