package com.joshboles.dayoff.model;

/**
 * Created by josh on 2/12/14.
 */
public class Contact {

    int _id;
    String _name;
    String _phone;

    // Empty constructor
    public Contact(){

    }

    // constructor
    public Contact(int id, String name, String _phone_number){
        this._id = id;
        this._name = name;
        this._phone = _phone_number;
    }

    // constructor
    public Contact(String name, String _phone_number){
        this._name = name;
        this._phone = _phone_number;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting phone number
    public String getPhoneNumber(){
        return this._phone;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number){
        this._phone = phone_number;
    }


}
