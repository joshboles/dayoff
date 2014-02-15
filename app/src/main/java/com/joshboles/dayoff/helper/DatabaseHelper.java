package com.joshboles.dayoff.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joshboles.dayoff.model.Contact;
import com.joshboles.dayoff.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josh on 2/12/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Table Names
    private static final String TABLE_CONTACT = "contacts";
    private static final String TABLE_MESSAGE = "messages";

    // Common column names
    private static final String KEY_ID = "id";

    // CONTACTS Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "number";

    // MESSAGES Table - column names
    private static final String KEY_LABEL = "label";
    private static final String KEY_CONTENT = "content";

    // Table Create Statements
    // Contact table create statement
    private static final String CREATE_TABLE_CONTACT = "CREATE TABLE "
            + TABLE_CONTACT + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_NUMBER + " TEXT" + ")";

    // Messages table create statement
    private static final String CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LABEL + " TEXT,"
            + KEY_CONTENT + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_CONTACT);
        db.execSQL(CREATE_TABLE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);

        // create new tables
        onCreate(db);
    }

    // Creating contacts
    public long createContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_NUMBER, contact.getPhoneNumber());

        // insert row
        long contact_id = db.insert(TABLE_CONTACT, null, values);

        return contact_id;
    }

    // Getting all contacts
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Contact ct = new Contact();
                ct.setID(c.getInt((c.getColumnIndex(KEY_ID))));
                ct.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                ct.setPhoneNumber(c.getString(c.getColumnIndex(KEY_NUMBER)));

                // adding to contact list
                contacts.add(ct);
            } while (c.moveToNext());
        }

        return contacts;
    }

    //Deleting a contact
    public void deleteContact(long contact_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, KEY_ID + " = ?",
                new String[] { String.valueOf(contact_id) });
    }

    // Creating Messages (probably won't be used by users)
    public long createMessage(Message message){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LABEL, message.getLabel());
        values.put(KEY_CONTENT, message.getContent());

        // Insert Row
        long message_id = db.insert(TABLE_MESSAGE, null, values);

        return message_id;
    }

    // Get single message (used for sending)
    public Message getMessage(String message_label) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE + " WHERE "
                + KEY_LABEL + " = '" + message_label + "'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Message m = new Message();
        m.setID(c.getInt(c.getColumnIndex(KEY_ID)));
        m.setLabel((c.getString(c.getColumnIndex(KEY_LABEL))));
        m.setContent((c.getString(c.getColumnIndex(KEY_CONTENT))));

        return m;
    }

    public int getMessageCount(){
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updating a message
    public int updateMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, message.getContent());

        // updating row
        return db.update(TABLE_MESSAGE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(message.getID()) });
    }

    //
    public void wipeMessages(){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_MESSAGE;
        db.rawQuery(deleteQuery, null);
    }

    // Closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}