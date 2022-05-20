package com.golan.amit.plusminus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlusMinusDbHelper extends SQLiteOpenHelper {

    public static final String DATABASENAME = "plusminus.db";
    public static final String TABLE = "tblgameresults";
    public static final int DATABASEVERSION = 1;
    public static final String ID_COLUMN = "id";
    public static final String WRONG_COLUMN = "wrong";
    public static final String CORRECT_COLUMN = "correct";
    public static final String ROUNDS_COLUMN = "rounds";
    public static final String SCORE_COLUMN = "score";
    public static final String DATETIME_COLUMN = "curr_datetime";

    SQLiteDatabase database;

    public static final String CREATE_TABLE_GAMERESULT =
            "CREATE TABLE IF NOT EXISTS " + TABLE +
                    "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    WRONG_COLUMN + " INTEGER," +
                    CORRECT_COLUMN + " INTEGER," +
                    ROUNDS_COLUMN + " INTEGER," +
                    SCORE_COLUMN + " INTEGER," +
                    DATETIME_COLUMN + " DATE);";

    String[] allColumns = {
            ID_COLUMN, WRONG_COLUMN, CORRECT_COLUMN, ROUNDS_COLUMN, SCORE_COLUMN, DATETIME_COLUMN
    };

    public PlusMinusDbHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(MainActivity.DEBUGTAG, "create string: {" + CREATE_TABLE_GAMERESULT + "}");
        try {
            db.execSQL(CREATE_TABLE_GAMERESULT);
            Log.i(MainActivity.DEBUGTAG, "database created");
        } catch (Exception exc) {
            Log.e(MainActivity.DEBUGTAG, "database creation exception: " + exc);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void open() {
        database = this.getWritableDatabase();
        Log.i(MainActivity.DEBUGTAG, "database connection open");
    }

    public void close() {
        if (database != null) {
            try {
                database.close();
                Log.i(MainActivity.DEBUGTAG, "database connection closed") ;
            } catch (Exception edbc) {
                Log.e(MainActivity.DEBUGTAG, "database connection close exception: " + edbc);
            }
        } else {
            Log.e(MainActivity.DEBUGTAG, "database is null");
        }
    }

    public void insertGameResult(String eCorrect, String eWrong, String eRounds, String eScore) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CORRECT_COLUMN, eCorrect);
        contentValues.put(WRONG_COLUMN, eWrong);
        contentValues.put(ROUNDS_COLUMN, eRounds);
        contentValues.put(SCORE_COLUMN, eScore);
        contentValues.put(DATETIME_COLUMN, currentDate());
        long insertedId = -1;
        try {
            insertedId = database.insert(TABLE, null, contentValues);
            Log.i(MainActivity.DEBUGTAG, "inserted correct, wrong, rounds and score ");
        } catch (Exception eid) {
            Log.e(MainActivity.DEBUGTAG, "insert game result exception: " + eid);
        }
    }

    public void resetTableToScratch() {
        try {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE);
            Log.i(MainActivity.DEBUGTAG, "database dropped");
        } catch (Exception edbd) {
            Log.e(MainActivity.DEBUGTAG, "database drop exception: " + edbd);
        }

        try {
            database.execSQL(CREATE_TABLE_GAMERESULT);
            Log.i(MainActivity.DEBUGTAG, "database re-created");
        } catch (Exception edbc) {
            Log.e(MainActivity.DEBUGTAG, "database re-create exception: " + edbc);
        }
    }

    public void displayDatabaseContent() {
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int correct = cursor.getInt(cursor.getColumnIndex(CORRECT_COLUMN));
                int wrong = cursor.getInt(cursor.getColumnIndex(WRONG_COLUMN));
                int rounds = cursor.getInt(cursor.getColumnIndex(ROUNDS_COLUMN));
                int score = cursor.getInt(cursor.getColumnIndex(SCORE_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));

                String tmpDisplayStr = String.format("id: %d, correct: %d, wrong: %d, rounds: %d and score: %d",
                        id, correct, wrong, rounds, score);
                if (MainActivity.DEBUG)
                    Log.i(MainActivity.DEBUGTAG, tmpDisplayStr);

            }
        } else {
            Log.e(MainActivity.DEBUGTAG, "database is empty, no activity in account");
        }
    }


    public ArrayList<GameResult> getAllGameResults() {
        ArrayList<GameResult> l = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int correct = cursor.getInt(cursor.getColumnIndex(CORRECT_COLUMN));
                int wrong = cursor.getInt(cursor.getColumnIndex(WRONG_COLUMN));
                int rounds = cursor.getInt(cursor.getColumnIndex(ROUNDS_COLUMN));
                int score = cursor.getInt(cursor.getColumnIndex(SCORE_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));

                GameResult gr = new GameResult((int) id, correct, wrong, rounds, score, currentdate);
                l.add(gr);
            }
        }
        return l;
    }


    public ArrayList<GameResult> getAllGameResultsByFilters(String selection, String orderBy) {
        Cursor cursor = database.query(TABLE, allColumns, selection, null, null, null, orderBy);
        ArrayList<GameResult> l = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int correct = cursor.getInt(cursor.getColumnIndex(CORRECT_COLUMN));
                int wrong = cursor.getInt(cursor.getColumnIndex(WRONG_COLUMN));
                int rounds = cursor.getInt(cursor.getColumnIndex(ROUNDS_COLUMN));
                int score = cursor.getInt(cursor.getColumnIndex(SCORE_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));

                GameResult gr = new GameResult((int) id, correct, wrong, rounds, score, currentdate);
                l.add(gr);
            }
        }
        return l;
    }


    public String lastActivityDate() {
        String tmpDate = null;
        String query = "SELECT " + DATETIME_COLUMN + " FROM " + TABLE +
                " ORDER BY " + DATETIME_COLUMN + " DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                tmpDate = cursor.getString(0);
                Log.d(MainActivity.DEBUGTAG, "select last date :" + tmpDate);
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "select last date exception:" + e);
            }
        }
        return tmpDate;
    }


    public long deleteRecordById(long rowId) {
        return database.delete(TABLE, ID_COLUMN + "=" + rowId, null);
    }


    private String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
