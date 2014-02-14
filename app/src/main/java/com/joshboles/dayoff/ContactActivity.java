package com.joshboles.dayoff;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
        LinearLayout ll;

        Contact dContact;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

            // Grab what's needed from the UI
            lv = (ListView) rootView.findViewById(R.id.contact_listview);
            ll = (LinearLayout) rootView.findViewById(R.id.ll_add);

            updateContacts();

            // Enable up button for action bar
            ActionBar bar = getActivity().getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);

            // OnClick for Adding a new contact
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, 1);
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    deleteContact(cAdapter.getItem(position));
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

            // Check that new contact doesn't already exist.

            Contact contact = new Contact(name, number);
            db.createContact(contact);
            updateContacts();
        }

        public void deleteContact(final Contact contact){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // set title
            builder.setTitle("Delete " + contact.getName() + "?");
            // set dialog message
            builder.setMessage("Click yes to delete");
            builder.setCancelable(false);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.deleteContact(contact.getID());
                    updateContacts();
                }
            });

            // create alert dialog
            AlertDialog alert = builder.create();
            // show it
            alert.show();
        }
    }

}
