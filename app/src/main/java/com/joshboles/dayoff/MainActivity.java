package com.joshboles.dayoff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joshboles.dayoff.helper.DatabaseHelper;
import com.joshboles.dayoff.model.Message;

public class MainActivity extends ActionBarActivity {

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

        DatabaseHelper db;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            db = new DatabaseHelper(getActivity().getApplicationContext());

            ivVacation = (ImageView) rootView.findViewById(R.id.iv_vacation);
            ivLate = (ImageView) rootView.findViewById(R.id.iv_late);
            ivSick = (ImageView) rootView.findViewById(R.id.iv_sick);

            ivVacation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgVacation = db.getMessage("vacation");
                    sendSMS(msgVacation.getContent());
                }
            });

            ivLate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgLate = db.getMessage("late");
                    sendSMS(msgLate.getContent());
                }
            });

            ivSick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgSick = db.getMessage("sick");
                    sendSMS(msgSick.getContent());
                }
            });


            return rootView;
        }

        public void sendSMS(String message){
            String phoneNumber = "6143602177";
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }

}
