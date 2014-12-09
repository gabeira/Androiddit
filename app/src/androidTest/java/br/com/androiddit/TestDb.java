package br.com.androiddit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import br.com.androiddit.data.RedditDAO;
import br.com.androiddit.data.RedditDbHelper;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(RedditDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new RedditDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        RedditDbHelper dbHelper = new RedditDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues redditValues = createRedditValues();

        long redditRowId = db.insert(RedditDAO.TABLE_NAME, null, redditValues);
        assertTrue(redditRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor redditCursor = db.query(
                RedditDAO.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(redditCursor, redditValues);

        dbHelper.close();
    }

    static ContentValues createRedditValues() {
        ContentValues redditValues = new ContentValues();
        redditValues.put(RedditDAO.COLUMN_TITLE, "title");
        redditValues.put(RedditDAO.COLUMN_AUTHOR, "Gabriel");
        redditValues.put(RedditDAO.COLUMN_CREATED, 1411921859);
        redditValues.put(RedditDAO.COLUMN_NUM_COMMENTS, 5);
        redditValues.put(RedditDAO.COLUMN_THUMBNAIL, "http://teste.png");
        redditValues.put(RedditDAO.COLUMN_URL, "http://www.google.com");

        return redditValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            Object expectedValue = entry.getValue();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}