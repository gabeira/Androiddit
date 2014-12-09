package br.com.androiddit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gabrielbernardopereira on 12/7/14.
 */
public class RedditDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "androiddit.db";

    public RedditDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold reddits.

        final String SQL_CREATE_REDDITS_TABLE = "CREATE TABLE " + RedditDAO.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                RedditDAO._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                RedditDAO.COLUMN_CREATED + " INTEGER NOT NULL, " +
                RedditDAO.COLUMN_TITLE + " TEXT NOT NULL, " +
                RedditDAO.COLUMN_AUTHOR + " TEXT NOT NULL," +
                RedditDAO.COLUMN_NUM_COMMENTS + " INTEGER NOT NULL, " +
                RedditDAO.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                RedditDAO.COLUMN_URL + " TEXT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_REDDITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RedditDAO.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
