package com.example.hiker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HikerManager";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String TABLE_HIKERS = "hikers";
    private static final String COLUMN_HIKE_ID = "hike_id";
    public static final String COLUMN_HIKE_NAME = "hike_name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PARKING = "parking";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CUSTOM1 = "custom1";
    public static final String COLUMN_CUSTOM2 = "custom2";
    public static final String TABLE_OBSERVATIONS = "observations";
    public static final String COLUMN_OBSERVATION_ID = "_id";
    public static final String COLUMN_OBSERVATION_TEXT = "observation_text";
    public static final String COLUMN_ADDITIONAL_COMMENT = "additional_comment";
    public static final String COLUMN_OBSERVATION_TIME = "observation_time";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_HIKERS_TABLE = "CREATE TABLE " + TABLE_HIKERS + "("
                + COLUMN_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HIKE_NAME + " TEXT,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_PARKING + " TEXT,"
                + COLUMN_LENGTH + " TEXT,"
                + COLUMN_DIFFICULTY + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_CUSTOM1 + " TEXT,"
                + COLUMN_CUSTOM2 + " TEXT"
                + ")";
        db.execSQL(CREATE_HIKERS_TABLE);
        String CREATE_OBSERVATIONS_TABLE = "CREATE TABLE " + TABLE_OBSERVATIONS + " (" +
                        COLUMN_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_OBSERVATION_TEXT + " TEXT," +
                        COLUMN_ADDITIONAL_COMMENT + " TEXT," +
                        COLUMN_OBSERVATION_TIME + " TEXT," +
                        COLUMN_HIKE_ID + " INTEGER," +
                        "FOREIGN KEY(" + COLUMN_HIKE_ID + ") REFERENCES " +
                        TABLE_HIKERS + "(" + COLUMN_HIKE_ID + "));";
        db.execSQL(CREATE_OBSERVATIONS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, and recreate the table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
        onCreate(db);

    }

    // Add a new user to the database
    public void insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    // Check if a user with the given credentials exists in the database
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        // Return true if the user exists, false otherwise
        return count > 0;
    }

    public long insertDetails(String hikeName, String location, String date, String parking,
                         String length, String difficulty, String description,
                         String custom1, String custom2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIKE_NAME, hikeName);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_PARKING, parking);
        values.put(COLUMN_LENGTH, length);
        values.put(COLUMN_DIFFICULTY, difficulty);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CUSTOM1, custom1);
        values.put(COLUMN_CUSTOM2, custom2);
        long result = db.insertOrThrow(TABLE_HIKERS, null, values);
        return result;
    }
    public long insertObservation(long hikeId, String observationText, String additionalComment, String observationTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIKE_ID, hikeId);
        values.put(COLUMN_OBSERVATION_TEXT, observationText);
        values.put(COLUMN_ADDITIONAL_COMMENT, additionalComment);
        values.put(COLUMN_OBSERVATION_TIME, observationTime);

        return db.insert(TABLE_OBSERVATIONS, null, values);
    }
    public List<Observation> getAllObservationsForHike(long hikeId) {
        List<Observation> observations = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_OBSERVATIONS,
                new String[]{COLUMN_OBSERVATION_TEXT, COLUMN_OBSERVATION_TIME, COLUMN_ADDITIONAL_COMMENT},
                COLUMN_HIKE_ID + " = ?",
                new String[]{String.valueOf(hikeId)},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String observationText = cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_TEXT));
                @SuppressLint("Range") String observationTime = cursor.getString(cursor.getColumnIndex(COLUMN_OBSERVATION_TIME));
                @SuppressLint("Range") String additionalComment = cursor.getString(cursor.getColumnIndex(COLUMN_ADDITIONAL_COMMENT));

                Observation observation = new Observation(observationText, observationTime, additionalComment);
                observations.add(observation);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return observations;
    }
    @SuppressLint("Range")
    public String getHikeDetailsByName(String searchTerm) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_HIKE_NAME, COLUMN_LOCATION, COLUMN_DATE, COLUMN_PARKING, COLUMN_LENGTH, COLUMN_DIFFICULTY, COLUMN_DESCRIPTION, COLUMN_CUSTOM1, COLUMN_CUSTOM2};
        String selection = COLUMN_HIKE_NAME + " = ?";
        String[] selectionArgs = {searchTerm};
        Cursor result = db.query(TABLE_HIKERS, columns, selection, selectionArgs, null, null, null);

        String hikeDetails = "";

        if (result.moveToFirst()) {
            do { // Lấy thông tin từ Cursor
                String location = result.getString(result.getColumnIndex(COLUMN_LOCATION));
                String date = result.getString(result.getColumnIndex(COLUMN_DATE));
                String parking = result.getString(result.getColumnIndex(COLUMN_PARKING));
                String length = result.getString(result.getColumnIndex(COLUMN_LENGTH));
                String difficulty = result.getString(result.getColumnIndex(COLUMN_DIFFICULTY));
                String description = result.getString(result.getColumnIndex(COLUMN_DESCRIPTION));
                String custom1 = result.getString(result.getColumnIndex(COLUMN_CUSTOM1));
                String custom2 = result.getString(result.getColumnIndex(COLUMN_CUSTOM2));

                // Xử lý dữ liệu tùy thuộc vào nhu cầu của bạn
                hikeDetails += "Location: " + location + "\n";
                hikeDetails += "Date: " + date + "\n";
                hikeDetails += "Parking: " + parking + "\n";
                hikeDetails += "Length: " + length + "\n";
                hikeDetails += "Difficulty: " + difficulty + "\n";
                hikeDetails += "Description: " + description + "\n";
                hikeDetails += "Custom1: " + custom1 + "\n";
                hikeDetails += "Custom2: " + custom2 + "\n";

            } while (result.moveToNext());
        }
        return hikeDetails;

    }
    public List<String> getAllHikeDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> hikeDetailsList = new ArrayList<>();

        String[] columns = {COLUMN_HIKE_NAME, COLUMN_LOCATION, COLUMN_DATE, COLUMN_PARKING, COLUMN_LENGTH, COLUMN_DIFFICULTY, COLUMN_DESCRIPTION, COLUMN_CUSTOM1, COLUMN_CUSTOM2};
        Cursor result = db.query(TABLE_HIKERS, columns, null, null, null, null, null);

        if (result.moveToFirst()) {
            do {
                @SuppressLint("Range") String hikeName = result.getString(result.getColumnIndex(COLUMN_HIKE_NAME));
                // Thêm các thông tin chi tiết cần thiết vào danh sách
                hikeDetailsList.add("Hike Name: " + hikeName);
            } while (result.moveToNext());
        }

        result.close();
        db.close();
        return hikeDetailsList;
    }

    public boolean deleteAllHikeDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HIKERS, null, null);
        db.close();
        return false;
    }
    public boolean deleteHikeDetailsByName(String hikeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HIKERS, COLUMN_HIKE_NAME + "=?", new String[]{hikeName}) > 0;
    }

}

