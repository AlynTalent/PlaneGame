package edu.fsu.cs.mobile.planegame;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by sam12 on 8/1/2017.
 */

public class ScoreProvider extends ContentProvider{
    public final static String DBNAME = "HighScores";
    public final static String SCORE_TABLE = "scores";
    public final static String SCORE_ID = "_id";
    public final static String SCORE_NAME = "name";
    public final static String SCORE_POINTS = "points";

    public static final Uri CONTENT_URI = Uri.parse(
            "content://edu.fsu.cs.mobile.planegame.ScoreProvider/" + SCORE_TABLE);

    private static final String SQL_CREATE = "CREATE TABLE "
            + SCORE_TABLE + " ( "
            + SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SCORE_NAME + " STRING,"
            + SCORE_POINTS + " INTEGER )";

    private DatabaseHelper mHelper;
    private static UriMatcher sUriMatcher;

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return mHelper.getReadableDatabase().query(SCORE_TABLE, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values){

        //String _id = values.getAsString(RUN_ID);
        //String steps = values.getAsString(RUN_STEPS);
        //String distance = values.getAsString(RUN_DISTANCE);

        Log.i("Insert", "About to insert");


        long id = mHelper.getWritableDatabase().insert(SCORE_TABLE, null, values);
        Log.i("Insert", "Inserted into database");
        return Uri.withAppendedPath(CONTENT_URI, "" + id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        return mHelper.getWritableDatabase().delete(SCORE_TABLE, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        return mHelper.getWritableDatabase().update(SCORE_TABLE, values, selection, selectionArgs);
    }


    protected static final class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context){
            super(context, DBNAME, null, 2);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            //db.execSQL("DROP TABLE IF EXISTS " + RUN_TABLE);
            db.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SCORE_TABLE);
            db.execSQL(SQL_CREATE);
        }
    }
}
