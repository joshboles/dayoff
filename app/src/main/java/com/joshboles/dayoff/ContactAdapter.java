package com.joshboles.dayoff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joshboles.dayoff.model.Contact;

import java.util.List;

/**
 * Created by josh on 2/12/14.
 */
public class ContactAdapter extends ArrayAdapter<Contact>{

    private Context mContext;
    private List<Contact> mContacts;

    public ContactAdapter(Context context, List<Contact> objects) {
        super(context, R.layout.contact_row, objects);
        this.mContext = context;
        this.mContacts = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.contact_row, null);
        }

        Contact cl = mContacts.get(position);

        TextView labelView = (TextView) convertView.findViewById(R.id.contact_row_name);
        TextView numberView = (TextView) convertView.findViewById(R.id.contact_row_number);

        labelView.setText(cl.getName());
        numberView.setText(cl.getPhoneNumber());

        return convertView;
    }

}