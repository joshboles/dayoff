package com.joshboles.dayoff;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.joshboles.dayoff.helper.DatabaseHelper;
import com.joshboles.dayoff.model.Contact;
import com.joshboles.dayoff.model.Message;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_contacts) {
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_messages) {
            Intent intent = new Intent(this, MessageActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ImageView ivVacation;
        ImageView ivLate;
        ImageView ivSick;

        Message msgVacation;
        Message msgLate;
        Message msgSick;

        String msgType;
        Integer toastText;

        DatabaseHelper db;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ActionBar bar = getActivity().getActionBar();
            if (bar != null) {
                bar.setIcon(R.drawable.ic_transparent);
            }

            db = new DatabaseHelper(getActivity().getApplicationContext());

            ivVacation = (ImageView) rootView.findViewById(R.id.iv_vacation);
            ivLate = (ImageView) rootView.findViewById(R.id.iv_late);
            ivSick = (ImageView) rootView.findViewById(R.id.iv_sick);

            ivVacation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Resources resources = getActivity().getResources();
                    String str = resources.getString(R.string.sms_prompt_vacation);
                    msgType = "vacation";
                    msgVacation = db.getMessage("vacation");
                    sendSMS(str, msgVacation);
                }
            });

            ivLate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Resources resources = getActivity().getResources();
                    String str = resources.getString(R.string.sms_prompt_late);
                    msgType = "late";
                    msgLate = db.getMessage("late");
                    sendSMS(str, msgLate);
                }
            });

            ivSick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Resources resources = getActivity().getResources();
                    String str = resources.getString(R.string.sms_prompt_sick);
                    msgType = "sick";
                    msgSick = db.getMessage("sick");
                    sendSMS(str, msgSick);
                }
            });


            return rootView;
        }

        public void sendSMS(String prompt, final Message message){
            if(db.getAllContacts().size() != 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // set title
                builder.setTitle(prompt);
                builder.setMessage("Click yes to send.");
                builder.setCancelable(false);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<Contact> contacts = db.getAllContacts();
                        for(Contact c : contacts){
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(c.getPhoneNumber(), null, message.getContent(), null, null);
                        }

                        // Set toast based on what message was sent.
                        if(msgType == "vacation"){
                            toastText = R.string.send_vacation_success;
                        } else if (msgType == "late"){
                            toastText = R.string.send_late_success;
                        } else if (msgType == "sick") {
                            toastText = R.string.send_sick_success;
                        }

                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                // create alert dialog
                AlertDialog alert = builder.create();
                // show it
                alert.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // set title
                builder.setTitle("No contacts found. Add some?");
                //builder.setMessage("");
                builder.setCancelable(false);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), ContactActivity.class);
                        startActivity(intent);
                    }
                });

                // create alert dialog
                AlertDialog alert = builder.create();
                // show it
                alert.show();
            }
        }
    }

}
