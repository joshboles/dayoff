package com.joshboles.dayoff.model;

/**
 * Created by josh on 2/12/14.
 */
public class Message {

    int _id;
    String _label;
    String _content;

    // Empty constructor
    public Message(){

    }

    // constructor
    public Message(int id, String label, String content){
        this._id = id;
        this._label = label;
        this._content = content;
    }

    // constructor
    public Message(String label, String content){
        this._label = label;
        this._content = content;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting label
    public String getLabel(){
        return this._label;
    }

    // setting label
    public void setLabel(String label){
        this._label = label;
    }

    // getting content
    public String getContent(){
        return this._content;
    }

    // setting content
    public void setContent(String content){
        this._content = content;
    }
}
