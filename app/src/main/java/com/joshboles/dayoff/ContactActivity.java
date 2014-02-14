package com.joshboles.dayoff;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
            lv = (ListView) rootView.findViewById(R.id.contact_listview);

            // Enable up button for action bar
            ActionBar bar = getActivity().getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);

            updateContacts();

            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ll_add);
            TextView tv = (TextView) rootView.findViewById(R.id.contact_add);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, 1);
                }
            });

            return rootView;
        }

        public void updateContacts(){
            db = new DatabaseHelper(getActivity().getApplicationContext());
            List<Contact> contacts = db.getAllContacts();
            cAdapter = new ContactAdapter(getActivity(), new ArrayList<Contact>());

            lv.setAdapter(cAdapter);
            cAdapter.clear();
            cAdapter.addAll(contacts);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data != null) {
                Uri uri = data.getData();

                if (uri != null) {
                    Cursor c = null;
                    try {
                        c = getActivity().getContentResolver().query(uri, new String[]{
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.TYPE },
                                null, null, null);

                        if (c != null && c.moveToFirst()) {
                            String name = c.getString(0);
                            String number = c.getString(1);
                            int type = c.getInt(2);
                            saveNewContact(type, name, number);
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            }
        }

        public void saveNewContact(int type, String name, String number) {
            Contact contact = new Contact(name, number);
            db.createContact(contact);
            updateContacts();
        }

    }

}
