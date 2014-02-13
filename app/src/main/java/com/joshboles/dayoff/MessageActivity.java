package com.joshboles.dayoff;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_message, container, false);

            ActionBar bar = getActivity().getActionBar();
            bar.setDisplayHomeAsUpEnabled(true);

            db = new DatabaseHelper(getActivity().getApplicationContext());
            Message dbVacation = db.getMessage("vacation");
            Message dbLate= db.getMessage("late");
            Message dbSick = db.getMessage("sick");

            tvVacation = (TextView) rootView.findViewById(R.id.tv_vacation);
            tvLate = (TextView) rootView.findViewById(R.id.tv_late);
            tvSick = (TextView) rootView.findViewById(R.id.tv_sick);

            tvVacation.setText(dbVacation.getContent());
            tvLate.setText(dbLate.getContent());
            tvSick.setText(dbSick.getContent());

            return rootView;
        }
    }

}
