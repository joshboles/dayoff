package com.joshboles.dayoff;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joshboles.dayoff.helper.DatabaseHelper;
import com.joshboles.dayoff.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        DatabaseHelper db;
        ContactAdapter cAdapter;
        ListView lv;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

            ActionBar bar = getActivity().getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);

            db = new DatabaseHelper(getActivity().getApplicationContext());
            List<Contact> contacts = db.getAllContacts();
            cAdapter = new ContactAdapter(getActivity(), new ArrayList<Contact>());

            lv = (ListView) rootView.findViewById(R.id.contact_listview);
            lv.setAdapter(cAdapter);
            cAdapter.clear();
            cAdapter.addAll(contacts);

            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ll_add);
            TextView tv = (TextView) rootView.findViewById(R.id.contact_add);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getActivity().getApplicationContext();
                    CharSequence text = "Todo: Launch Add Contact activity";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });

            return rootView;
        }

    }

}
