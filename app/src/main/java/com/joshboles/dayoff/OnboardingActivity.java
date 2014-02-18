package com.joshboles.dayoff;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joshboles.dayoff.helper.DatabaseHelper;
import com.joshboles.dayoff.model.Contact;
import com.joshboles.dayoff.model.Message;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Enable up button for action bar
        ActionBar bar = getActionBar();
        bar.setIcon(R.drawable.ic_transparent);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ContactOnboardFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.onboarding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment for Contact onboarding
     */
    public static class ContactOnboardFragment extends Fragment {

        DatabaseHelper db;
        ContactAdapter cAdapter;
        ListView lv;
        LinearLayout ll;
        Button bn;

        public ContactOnboardFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboard_contact, container, false);

            // Grab what's needed from the UI
            lv = (ListView) rootView.findViewById(R.id.contact_listview);
            ll = (LinearLayout) rootView.findViewById(R.id.ll_add);
            bn = (Button) rootView.findViewById(R.id.contact_onboard_next);

            updateContacts();

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

            bn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create fragment and give it an argument specifying the list it should show
                    MessageOnboardFragment msgFragment = new MessageOnboardFragment();
                    Bundle args = new Bundle();
                    //args.putString("stateListId", stateListId);
                    msgFragment.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.container, msgFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
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

    /**
     * Fragment for Message onboarding
     */
    public static class MessageOnboardFragment extends Fragment {

        DatabaseHelper db;

        TextView tvVacation;
        TextView tvLate;
        TextView tvSick;

        Message dbVacation;
        Message dbLate;
        Message dbSick;

        String messageType;

        Button buttonDone;

        public MessageOnboardFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboard_message, container, false);

            db = new DatabaseHelper(getActivity().getApplicationContext());
            dbVacation = db.getMessage("vacation");
            dbLate= db.getMessage("late");
            dbSick = db.getMessage("sick");

            tvVacation = (TextView) rootView.findViewById(R.id.tv_vacation);
            tvLate = (TextView) rootView.findViewById(R.id.tv_late);
            tvSick = (TextView) rootView.findViewById(R.id.tv_sick);
            buttonDone = (Button) rootView.findViewById(R.id.message_onboard_done);

            tvVacation.setText(dbVacation.getContent());
            tvLate.setText(dbLate.getContent());
            tvSick.setText(dbSick.getContent());

            tvVacation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editMessage(dbVacation);
                }
            });

            tvLate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editMessage(dbLate);
                }
            });

            tvSick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editMessage(dbSick);
                }
            });

            buttonDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            return rootView;
        }

        private void editMessage(Message message){
            // Save which message is being edited
            messageType = message.getLabel();

            // Build alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String title = "Edit" + messageType;
            builder.setTitle(title);

            // Set up the input
            final EditText input = new EditText(getActivity());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            input.setText(message.getContent());

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Context context = getActivity().getApplicationContext();
                    CharSequence text = "Error saving.";

                    if(input.getText().toString().length() < 1){
                        Toast toast = Toast.makeText(context, "Error: message must not be empty.", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        if(messageType == dbVacation.getLabel()){
                            text = "Changes saved for: Vacation";
                            dbVacation.setContent(input.getText().toString());
                            db.updateMessage(dbVacation);
                            tvVacation.setText(dbVacation.getContent());
                        }

                        if(messageType == dbLate.getLabel()){
                            text = "Changes saved for: Late";
                            dbLate.setContent(input.getText().toString());
                            db.updateMessage(dbLate);
                            tvLate.setText(dbLate.getContent());
                        }

                        if(messageType == dbSick.getLabel()){
                            text = "Changes saved for: Sick";
                            dbSick.setContent(input.getText().toString());
                            db.updateMessage(dbSick);
                            tvSick.setText(dbSick.getContent());
                        }
                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }


    }

}
