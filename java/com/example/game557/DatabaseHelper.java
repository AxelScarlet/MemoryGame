package com.example.game557;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GameApp.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    public static final String TABLE_USER = "User";
    public static final String COLUMN_USER_ID = "userID";
    public static final String COLUMN_USERNAME = "userName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Nicknames Table
    public static final String TABLE_NICKNAME = "Nickname";
    public static final String COLUMN_NICKNAME_ID = "nicknameID";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_USER_ID_FK = "userID"; // Foreign key for userID

    // Leaderboard Table
    public static final String TABLE_LEADERBOARD = "Leaderboard";
    public static final String COLUMN_LEADERBOARD_ID = "leaderboardID";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_NICKNAME_ID_FK = "nicknameID"; // Foreign key for nicknameID

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_NICKNAME + " (" +
                COLUMN_NICKNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NICKNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "));");

        db.execSQL("CREATE TABLE " + TABLE_LEADERBOARD + " (" +
                COLUMN_LEADERBOARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SCORE + " INTEGER, " +
                COLUMN_NICKNAME_ID_FK + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_NICKNAME_ID_FK + ") REFERENCES " + TABLE_NICKNAME + "(" + COLUMN_NICKNAME_ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NICKNAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
        onCreate(db);
    }

    // User Management
    public boolean insertUser(String userName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, userName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    public boolean validateLogin(String userName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{userName, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return valid;
    }

    public boolean chkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean chkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Nickname Management
    public boolean insertNickname(String nickname, int userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICKNAME, nickname);
        values.put(COLUMN_USER_ID, userID);
        long result = db.insert(TABLE_NICKNAME, null, values);
        db.close();
        return result != -1;
    }

    public boolean isNicknameExists(String nickname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NICKNAME,
                new String[]{COLUMN_NICKNAME_ID},
                COLUMN_NICKNAME + " = ?",
                new String[]{nickname},
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Get all nicknames for a specific user
    public Cursor getAllNicknames(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NICKNAME + " WHERE " + COLUMN_USER_ID_FK + "=?", new String[]{String.valueOf(userID)});
    }

    // Get all nicknames (no user filter)
    public ArrayList<String> getAllNicknames() {
        ArrayList<String> nicknames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NICKNAME, null); // Get all nicknames

        if (cursor.moveToFirst()) {
            do {
                nicknames.add(cursor.getString(cursor.getColumnIndex(COLUMN_NICKNAME))); // Add nickname to the list
            } while (cursor.moveToNext());
        }
        cursor.close();
        return nicknames;
    }

    public ArrayList<String> getAllNicknamesByID(int userID) {
        ArrayList<String> nicknames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NICKNAME + " WHERE " + COLUMN_USER_ID_FK + "=?", new String[]{String.valueOf(userID)}); // Get all nicknames for the specific user

        if (cursor.moveToFirst()) {
            do {
                nicknames.add(cursor.getString(cursor.getColumnIndex(COLUMN_NICKNAME))); // Add nickname to the list
            } while (cursor.moveToNext());
        }
        cursor.close();
        return nicknames;
    }

    public int getUserId(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + "=?", new String[]{userName});
        int userId = -1; // Default value if not found
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0); // Get the userID
        }
        cursor.close();
        db.close();
        return userId;
    }

    public String getUsernameById(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userID)});
        String username = null; // Default value if not found
        if (cursor.moveToFirst()) {
            username = cursor.getString(0); // Get the username
        }
        cursor.close();
        db.close();
        return username;
    }

    public int getNicknameIdByNickname(String nickname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NICKNAME_ID + " FROM " + TABLE_NICKNAME + " WHERE " + COLUMN_NICKNAME + "=?", new String[]{nickname});
        int nicknameId = -1; // Default value if not found
        if (cursor.moveToFirst()) {
            nicknameId = cursor.getInt(0); // Get the nicknameID
        }
        cursor.close();
        db.close();
        return nicknameId;
    }

    public boolean deleteNickname(String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NICKNAME, COLUMN_NICKNAME + "=?", new String[]{nickname});
        db.close();
        return result > 0; // Return true if at least one row was deleted
    }


    public boolean updateNickname(String oldNickname, String newNickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICKNAME, newNickname);
        int rowsAffected = db.update(TABLE_NICKNAME, values, COLUMN_NICKNAME + "=?", new String[]{oldNickname});
        db.close();
        return rowsAffected > 0; // Return true if at least one row was updated
    }

    // Leaderboard Management
    public boolean insertLeaderboardScore(int score, int nicknameID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_NICKNAME_ID_FK, nicknameID);
        long result = db.insert(TABLE_LEADERBOARD, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateLeaderboardScore(int score, int nicknameID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);

        // Check if the entry exists
        Cursor cursor = db.query(TABLE_LEADERBOARD, new String[]{COLUMN_SCORE},
                COLUMN_NICKNAME_ID_FK + "=?", new String[]{String.valueOf(nicknameID)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Entry exists, update the score
            int result = db.update(TABLE_LEADERBOARD, values,
                    COLUMN_NICKNAME_ID_FK + "=?", new String[]{String.valueOf(nicknameID)});
            cursor.close();
            db.close();
            return result > 0; // Return true if the update was successful
        } else {
            // Entry does not exist, insert a new one
            values.put(COLUMN_NICKNAME_ID_FK, nicknameID); // Add nicknameID to ContentValues
            long result = db.insert(TABLE_LEADERBOARD, null, values);
            cursor.close();
            db.close();
            return result != -1; // Return true if the insert was successful
        }
    }

    // Get all leaderboard entries
    public List<Map<String, String>> getAllLeaderboardEntries() {
        List<Map<String, String>> leaderboardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT n.nickname, l.score FROM " + TABLE_LEADERBOARD + " l " +
                "JOIN " + TABLE_NICKNAME + " n ON l.nicknameID = n.nicknameID ORDER BY l.score DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> leaderboardEntry = new HashMap<>();
                leaderboardEntry.put("nickname", cursor.getString(cursor.getColumnIndex("nickname")));
                leaderboardEntry.put("score", cursor.getString(cursor.getColumnIndex("score")));
                leaderboardList.add(leaderboardEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return leaderboardList;
    }



}
