package com.apps.orenc.detectandrecognize;

/**
 * Created by orenc on 6/5/15.
 *
 * Class that encapsulates some details about a person.
 */
public class Person {

    private static final String TAG = "Person";

    private int mId; // Not its actual ID, but its ID in the DB.
    private byte[] mPicture;
    private String mFirstName;
    private String mLastName;
    private String mPhone;
    private String mEmail;
    private String mGeneral;

    public Person() {
    }

    public Person(int id, byte[] picture, String firstName, String lastName, String phone, String email, String general) {
        mId = id;
        mPicture = picture;
        mFirstName = firstName;
        mLastName = lastName;
        mPhone = phone;
        mEmail = email;
        mGeneral = general;
    }

    public int getId() {
        return mId;
    }
    public byte[] getPicture() {
        return mPicture;
    }
    public String getFirstName() {
        return mFirstName;
    }
    public String getLastName() {
        return mLastName;
    }
    public String getPhone() {
        return mPhone;
    }
    public String getEmail() {
        return mEmail;
    }
    public String getGeneral() {
        return mGeneral;
    }


    public Person setId(int id) {
        mId = id;
        return this;
    }
    public Person setPicture(byte[] picture) {
        mPicture = picture;
        return this;
    }
    public Person setFirstName(String firstName) {
        mFirstName = firstName;
        return this;
    }
    public Person setLastName(String lastName) {
        mLastName = lastName;
        return this;
    }
    public Person setPhone(String phone) {
        mPhone = phone;
        return this;
    }
    public Person setEmail(String email) {
        mEmail = email;
        return this;
    }
    public Person setGeneral(String general) {
        mGeneral = general;
        return this;
    }
}
