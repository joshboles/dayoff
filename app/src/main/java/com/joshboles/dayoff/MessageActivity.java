package com.joshboles.dayoff;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.joshboles.dayoff.helper.DatabaseHelper;
import com.joshboles.dayoff.model.Message;

public class MessageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message, menu);
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

        TextView tvVacation;
        TextView tvLate;
        TextView tvSick;

        Message dbVacation;
        Message dbLate;
        Message dbSick;

        String messageType;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_message, container, false);

            ActionBar bar = getActivity().getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);

            db = new DatabaseHelper(getActivity().getApplicationContext());
            dbVacation = db.getMessage("vacation");
            dbLate= db.getMessage("late");
            dbSick = db.getMessage("sick");

            tvVacation = (TextView) rootView.findViewById(R.id.tv_vacation);
            tvLate = (TextView) rootView.findViewById(R.id.tv_late);
            tvSick = (TextView) rootView.findViewById(R.id.tv_sick);

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
