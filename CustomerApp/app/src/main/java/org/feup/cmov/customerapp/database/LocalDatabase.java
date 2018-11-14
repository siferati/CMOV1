package org.feup.cmov.customerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.feup.cmov.customerapp.dataStructures.Ticket;
import org.feup.cmov.customerapp.dataStructures.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalDatabase extends SQLiteOpenHelper {
    // data base file name
    public static final String DATABASE_NAME = "LocalClientDB.db";

    // schema version 1
    private static final int SCHEMA_VERSION = 1;

    // singleton instance of the database
    private static LocalDatabase sInstance;

    // used set to false
    private static final int USED_FALSE = 0;

    // used set to true
    private static final int USED_TRUE = 1;

    public static synchronized LocalDatabase getInstance(Context context) {
        Log.d("http", "Created singleton");

        if (sInstance == null) {
            sInstance = new LocalDatabase(context.getApplicationContext());
        }

        return sInstance;
    }


    private LocalDatabase(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);

        Log.d("http", "New database");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("http", "Database created");

        db.execSQL("CREATE TABLE tickets (id TEXT unique, userid TEXT, name TEXT, date TEXT, seatnumber TEXT, price TEXT, used TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS tickets");
            onCreate(db);
        }
    }

    public void dropAllTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("tickets", null, null);
    }

    /**
     * Check if the database exist and can be read.
     *
     * @return true if it exists and can be read, false if it doesn't
     */
    public static boolean checkDataBase(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    public synchronized void addTicket(Context context, Ticket ticket) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures consistency of the database.
        db.beginTransaction();
        try {
            User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

            ContentValues values = new ContentValues();
            values.put("id", ticket.getId());
            values.put("userid", user.getId());
            values.put("name", ticket.getName());
            values.put("date", ticket.getDate());

            String seatNumber = Integer.toString(ticket.getSeatNumber());
            values.put("seatNumber", seatNumber);

            String price = Double.toString(ticket.getPrice());
            values.put("price", price);

            String used = Integer.toString(USED_FALSE);
            values.put("used", used);

            db.insertWithOnConflict("tickets", null, values, SQLiteDatabase.CONFLICT_IGNORE);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("http", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }

    }

    public synchronized List<Ticket> getAllTickets(Context context) {
        List<Ticket> tickets = new ArrayList<>();
        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

        String TICKETS_SELECT_QUERY = "SELECT id, name, date, seatNumber, price, used FROM tickets WHERE tickets.id = '" + user.getId() + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TICKETS_SELECT_QUERY, null);

        Log.d("http", "GETTING TICKETS");

        try {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    Log.d("http", "ID: " + id);

                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    String seatNumber = cursor.getString(cursor.getColumnIndex("seatNumber"));
                    String price = cursor.getString(cursor.getColumnIndex("price"));
                    String used = cursor.getString(cursor.getColumnIndex("used"));

                    int sn = Integer.parseInt(seatNumber);
                    double pr = Double.parseDouble(price);
                    Ticket ticket = new Ticket(id, name, date, sn, pr);

                    if (Integer.parseInt(used) == USED_TRUE) {
                        ticket.setUsed(true);
                    }

                    tickets.add(ticket);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("http", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tickets;
    }

    public synchronized void updateTicket(Context context, Ticket ticket) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures consistency of the database.
        db.beginTransaction();
        try {
            User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

            ContentValues values = new ContentValues();
            values.put("id", ticket.getId());
            values.put("userid", user.getId());
            values.put("name", ticket.getName());
            values.put("date", ticket.getDate());

            String seatNumber = Integer.toString(ticket.getSeatNumber());
            values.put("seatNumber", seatNumber);

            String price = Double.toString(ticket.getPrice());
            values.put("price", price);

            String used = Integer.toString(USED_FALSE);
            values.put("used", used);

            String[] args = {ticket.getId()};

            db.update("tickets", null,"id=?", args);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("http", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

}
