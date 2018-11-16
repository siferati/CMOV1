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
import org.feup.cmov.customerapp.dataStructures.Voucher;

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
    private static final int AVAILABLE_FALSE = 0;

    // used set to true
    private static final int AVAILABLE_TRUE = 1;

    private static final String TICKETS_TABLE = "tickets";
    private static final String VOUCHERS_TABLE = "vouchers";

    private static final String USER_ID = "userid";

    private static final String TICKET_ID = "id";
    private static final String TICKET_NAME = "name";
    private static final String TICKET_DATE = "date";
    private static final String TICKET_SN = "seatnumber";
    private static final String TICKET_PRICE = "price";
    private static final String TICKET_AVAILABLE = "available";

    private static final String VOUCHER_ID = "id";
    private static final String VOUCHER_TYPE = "type";
    private static final String VOUCHER_DISCOUNT = "discount";
    private static final String VOUCHER_AVAILABLE = "available";

    /**
     * Get static instance of local database
     * @param context - current application context
     * @return static instance of local database
     */
    public static synchronized LocalDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocalDatabase(context.getApplicationContext());
        }

        return sInstance;
    }

    private LocalDatabase(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TICKETS_TABLE + " (" +
                TICKET_ID + " TEXT unique, " +
                USER_ID + " TEXT, " +
                TICKET_NAME + " TEXT, " +
                TICKET_DATE + " TEXT, " +
                TICKET_SN + " INTEGER, " +
                TICKET_PRICE + " DOUBLE, " +
                TICKET_AVAILABLE + " INTEGER);");

        db.execSQL("CREATE TABLE " + VOUCHERS_TABLE + " (" +
                VOUCHER_ID + " TEXT unique, " +
                USER_ID + " TEXT, " +
                VOUCHER_TYPE + " TEXT, " +
                VOUCHER_DISCOUNT + " DOUBLE, " +
                VOUCHER_AVAILABLE + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Drop all database's tables
     */
    public void dropAllTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TICKETS_TABLE, null, null);
        db.delete(VOUCHERS_TABLE, null, null);
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

    /**
     * Adds a ticket to local database
     * @param context - current application context
     * @param ticket - ticket to add to database
     */
    public synchronized void addTicket(Context context, Ticket ticket) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures consistency of the database.
        db.beginTransaction();
        try {
            User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

            ContentValues values = new ContentValues();
            values.put(TICKET_ID, ticket.getId());
            values.put(USER_ID, user.getId());
            values.put(TICKET_NAME, ticket.getName());
            values.put(TICKET_DATE, ticket.getDate());
            values.put(TICKET_SN, ticket.getSeatNumber());
            values.put(TICKET_PRICE, ticket.getPrice());
            values.put(TICKET_AVAILABLE, AVAILABLE_TRUE);

            db.insert(TICKETS_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("http", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Get all tickets from local database, ordered by availability and date
     * @param context - current application context
     * @return list of tickets currently in the local database
     */
    public synchronized List<Ticket> getAllTickets(Context context) {
        List<Ticket> tickets = new ArrayList<>();
        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

        String TICKETS_SELECT_QUERY = "SELECT * FROM " + TICKETS_TABLE +
                " WHERE " + USER_ID + " = '" + user.getId() + "'" +
                " ORDER BY " + TICKET_AVAILABLE + " DESC, " + TICKET_DATE + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(TICKETS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(TICKET_ID));
                    String name = cursor.getString(cursor.getColumnIndex(TICKET_NAME));
                    String date = cursor.getString(cursor.getColumnIndex(TICKET_DATE));
                    int seatNumber = cursor.getInt(cursor.getColumnIndex(TICKET_SN));
                    double price = cursor.getDouble(cursor.getColumnIndex(TICKET_PRICE));
                    int used = cursor.getInt(cursor.getColumnIndex(TICKET_AVAILABLE));

                    Ticket ticket = new Ticket(id, name, date, seatNumber, price);

                    if (used == AVAILABLE_FALSE) {
                        ticket.setAvailable(false);
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

    /**
     * Updates ticket's availability in the database
     * @param ticket - ticket to update
     */
    public synchronized void updateTicket(Ticket ticket) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            int available = -1;
            if(ticket.isAvailable()) {
                available = AVAILABLE_TRUE;
            } else {
                available = AVAILABLE_FALSE;
            }

            values.put(TICKET_AVAILABLE, available);

            String ticketId = ticket.getId();
            String[] args = {ticketId};

            db.update(TICKETS_TABLE, values,"id=?", args);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("http", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public synchronized void addVoucher(Context context, Voucher voucher) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures consistency of the database.
        db.beginTransaction();
        try {
            User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

            ContentValues values = new ContentValues();
            values.put(VOUCHER_ID, voucher.getId());
            values.put(USER_ID, user.getId());
            values.put(VOUCHER_TYPE, voucher.getType());
            values.put(VOUCHER_DISCOUNT, voucher.getDiscount());
            values.put(VOUCHER_AVAILABLE, AVAILABLE_TRUE);

            db.insert(VOUCHERS_TABLE, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("http", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public synchronized List<Voucher> getAllVouchers(Context context) {
        List<Voucher> vouchers = new ArrayList<>();
        User user = User.loadLoggedinUser(User.LOGGEDIN_USER_PATH, context);

        String VOUCHERS_SELECT_QUERY = "SELECT * FROM " + VOUCHERS_TABLE +
                " WHERE " + USER_ID + " = '" + user.getId() + "'" +
                " ORDER BY " + VOUCHER_TYPE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(VOUCHERS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(VOUCHER_ID));
                    String type = cursor.getString(cursor.getColumnIndex(VOUCHER_TYPE));
                    double discount = cursor.getDouble(cursor.getColumnIndex(VOUCHER_DISCOUNT));
                    int available = cursor.getInt(cursor.getColumnIndex(VOUCHER_AVAILABLE));

                    Voucher voucher = new Voucher(id, type, discount);

                    if (available == AVAILABLE_FALSE) {
                        voucher.setAvailable(false);
                    }

                    vouchers.add(voucher);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("http", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return vouchers;
    }

    public synchronized void deleteVoucher(Context context, Voucher voucher) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(VOUCHER_AVAILABLE, voucher.isAvailable());

            String[] args = {voucher.getId()};

            db.delete(VOUCHERS_TABLE, "id=?", args);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("http", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }
}
