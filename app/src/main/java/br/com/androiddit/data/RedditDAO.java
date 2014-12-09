package br.com.androiddit.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabrielbernardopereira on 12/7/14.
 */
public class RedditDAO implements BaseColumns {

    public static final String LOG_TAG = RedditDAO.class.getSimpleName().toString();

    public static final String TABLE_NAME = "reddits";

    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_REDDIT_ID = "reddit_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_NUM_COMMENTS = "comments";
    public static final String COLUMN_THUMBNAIL = "thumbnail";
    public static final String COLUMN_URL = "url";

    protected SQLiteDatabase db;

    public RedditDAO(Context ctx) {
        db = ctx.openOrCreateDatabase(RedditDbHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    protected RedditDAO() {}

    public long insert(Reddit reddit) {
        ContentValues redditValues = new ContentValues();
        redditValues.put(RedditDAO.COLUMN_TITLE, reddit.getTitle());
        redditValues.put(RedditDAO.COLUMN_AUTHOR, reddit.getAuthor());
        redditValues.put(RedditDAO.COLUMN_CREATED, reddit.getCreated());
        redditValues.put(RedditDAO.COLUMN_NUM_COMMENTS, reddit.getNumberOfComments());
        redditValues.put(RedditDAO.COLUMN_THUMBNAIL, reddit.getThumbnail());
        redditValues.put(RedditDAO.COLUMN_URL, reddit.getUrl());

        long id = insert(redditValues);
        return id;
    }

    public long insert(ContentValues redditValues) {
        long id = db.insert(TABLE_NAME, "", redditValues);
        return id;
    }

    public Cursor getCursor() {
        try {
            // select * from tickets
            return db.query(TABLE_NAME, null, null, null, null, null, null, null);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Erro ao buscar os tickets: " + e.toString());
            return null;
        }
    }

    public List<Reddit> getRedditList() {
        Cursor c = getCursor();
        List<Reddit> reddits = new ArrayList<Reddit>();
        if (c.moveToFirst()) {
            int idxId = c.getColumnIndex(COLUMN_REDDIT_ID);
            int idxTitle = c.getColumnIndex(COLUMN_TITLE);
            int idxAuthor = c.getColumnIndex(COLUMN_AUTHOR);
            int idxCreated = c.getColumnIndex(COLUMN_CREATED);
            int idxNumComments = c.getColumnIndex(COLUMN_NUM_COMMENTS);
            int idxThumb = c.getColumnIndex(COLUMN_THUMBNAIL);
            int idxUrl = c.getColumnIndex(COLUMN_URL);
            do {
                Reddit r = new Reddit();
                reddits.add(r);
                r.setId(c.getLong(idxId));
                r.setTitle(c.getString(idxTitle));
                r.setAuthor(c.getString(idxAuthor));
                r.setCreated(c.getLong(idxCreated));
                r.setNumberOfComments(c.getInt(idxNumComments));
                r.setThumbnail(c.getString(idxThumb));
                r.setUrl(c.getString(idxUrl));
            } while (c.moveToNext());
        }
        return reddits;
    }

    public void setRedditList(List<Reddit> reddits){
        for (Reddit r : reddits) {
            insert(r);
        }
        Log.d(LOG_TAG, "Inseriu: " + reddits.size());

    }

    public Cursor query(SQLiteQueryBuilder queryBuilder, String[] projection, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy) {
        Cursor c = queryBuilder.query(this.db, projection, selection, selectionArgs, groupBy, having, orderBy);
        return c;
    }

    public void closeDB() {
        // fecha o banco de dados
        if (db != null) {
            db.close();
        }
    }
}