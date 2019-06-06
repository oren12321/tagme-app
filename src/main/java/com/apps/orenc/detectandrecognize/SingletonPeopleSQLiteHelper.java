package com.apps.orenc.detectandrecognize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orenc on 6/5/15.
 *
 * Singleton object that handle the people DB.
 */
public class SingletonPeopleSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SingletonPeopleSQLiteHelper";

    // Singleton desing pattern for this class.
    private static SingletonPeopleSQLiteHelper mInstance;
    public static SingletonPeopleSQLiteHelper getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new SingletonPeopleSQLiteHelper(context);
        }
        return mInstance;
    }


    // Structures of the DB: name, tables, fields, queries, etc.
    public static class Constants {

        public static class Database {
            public static final String NAME = "dbPeople";
            public static final int VERSION = 1;
        }

        public static class Tables {

            public static class People {

                public static final String NAME = "tblPeople";

                public static class Columns {
                    public static final String ID = "id";
                    public static final String PICTURE = "picture";
                    public static final String FIRST_NAME = "fname";
                    public static final String LAST_NAME = "lname";
                    public static final String PHONE = "phone";
                    public static final String EMAIL = "email";
                    public static final String GENERAL = "general";
                }
            }
        }

        public static class Queries {

            public static class Create {

                public static final String PEOPLE_TABLE =
                        "CREATE TABLE " + Tables.People.NAME + "("
                        + Tables.People.Columns.ID + " INT PRIMARY KEY,"
                        + Tables.People.Columns.PICTURE + " BLOB NOT NULL,"
                        + Tables.People.Columns.FIRST_NAME + " TEXT,"
                        + Tables.People.Columns.LAST_NAME + " TEXT,"
                        + Tables.People.Columns.PHONE + " TEXT,"
                        + Tables.People.Columns.EMAIL + " TEXT,"
                        + Tables.People.Columns.GENERAL + " TEXT);";


            }

            public static class Drop {
                public static final String PEOPLE_TABLE =
                        "DROP TABLE IF EXISTS " + Tables.People.NAME + ";";
            }

            public static class Select {
                public static final String ALL_PEOPLE =
                        "SELECT * FROM " + Tables.People.NAME + " ORDER BY " + Tables.People.Columns.ID + ";";
            }

        }
    }


    private SingletonPeopleSQLiteHelper(Context context) {
        super(context, Constants.Database.NAME, null, Constants.Database.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Constants.Queries.Create.PEOPLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // When upgrading database, drop the current tables if exits and create them again.

        db.execSQL(Constants.Queries.Drop.PEOPLE_TABLE);

        onCreate(db);
    }


    public void insertPerson(Person person, boolean withNullId) {

        // Get database reference with write permission.
        SQLiteDatabase db = getWritableDatabase();

        // Prepare the inserted record.
        ContentValues values = new ContentValues();
        if(!withNullId) {
            values.put(Constants.Tables.People.Columns.ID, person.getId());
        }
        values.put(Constants.Tables.People.Columns.PICTURE, person.getPicture());
        values.put(Constants.Tables.People.Columns.FIRST_NAME, person.getFirstName());
        values.put(Constants.Tables.People.Columns.LAST_NAME, person.getLastName());
        values.put(Constants.Tables.People.Columns.PHONE, person.getPhone());
        values.put(Constants.Tables.People.Columns.EMAIL, person.getEmail());
        values.put(Constants.Tables.People.Columns.GENERAL, person.getGeneral());

        // Insert the record and close the database connection.
        db.insert(Constants.Tables.People.NAME, null, values);
        db.close();
    }

    public void updatePerson(Person person) {

        // Get database reference with write permission.
        SQLiteDatabase db = getWritableDatabase();

        // Prepare the updated record.
        ContentValues values = new ContentValues();
        values.put(Constants.Tables.People.Columns.ID, person.getId());
        values.put(Constants.Tables.People.Columns.PICTURE, person.getPicture());
        values.put(Constants.Tables.People.Columns.FIRST_NAME, person.getFirstName());
        values.put(Constants.Tables.People.Columns.LAST_NAME, person.getLastName());
        values.put(Constants.Tables.People.Columns.PHONE, person.getPhone());
        values.put(Constants.Tables.People.Columns.EMAIL, person.getEmail());
        values.put(Constants.Tables.People.Columns.GENERAL, person.getGeneral());

        // Update the record and close the database connection.
        db.update(
                Constants.Tables.People.NAME,
                values,
                Constants.Tables.People.Columns.ID + " = ?",
                new String[]{String.valueOf(person.getId())});
        db.close();
    }

    public void deletePerson(Person person) {

        // Get database reference with write permission.
        SQLiteDatabase db = getWritableDatabase();

        // Delete record and close connection.
        db.delete(
                Constants.Tables.People.NAME,
                Constants.Tables.People.Columns.ID + " = ?",
                new String[]{String.valueOf(person.getId())});
        db.close();
    }


    public Person getPerson(int id) {

        Person returnedPerson = null;

        // Get database reference with read permission.
        SQLiteDatabase db = getReadableDatabase();

        // Get cursor to the queried group of records.
        Cursor cursor = db.query(
                Constants.Tables.People.NAME,
                new String[] {
                        Constants.Tables.People.Columns.ID,
                        Constants.Tables.People.Columns.PICTURE,
                        Constants.Tables.People.Columns.FIRST_NAME,
                        Constants.Tables.People.Columns.LAST_NAME,
                        Constants.Tables.People.Columns.PHONE,
                        Constants.Tables.People.Columns.EMAIL,
                        Constants.Tables.People.Columns.GENERAL },
                Constants.Tables.People.Columns.ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        // Check if records found.
        if(cursor != null && cursor.getCount() > 0) {
            // Go to the first record in the group.and return it.
            cursor.moveToFirst();
            returnedPerson = new Person(
                    cursor.getInt(0),
                    cursor.getBlob(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6));

            cursor.close();
        }
        // Close the database connection.
        db.close();

        return returnedPerson;
    }


    public List<Person> getAllPersons() {

        List<Person> persons = null;

        // Get database reference with read permission.
        SQLiteDatabase db = getReadableDatabase();

        // Query all the table.
        Cursor cursor = db.rawQuery(Constants.Queries.Select.ALL_PEOPLE, null);

        // There is data in the table.
        if(cursor != null && cursor.getCount() > 0) {

            // Fill the persons list.
            persons = new ArrayList<>();

            cursor.moveToFirst();

            do {
                persons.add(new Person(
                        cursor.getInt(0),
                        cursor.getBlob(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)));
            } while(cursor.moveToNext());

            cursor.close();
        }

        // Close the dataase connection.
        db.close();

        return persons;
    }

    public void deleteAllPersons() {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(Constants.Tables.People.NAME, null, null);
        db.close();
    }
}
