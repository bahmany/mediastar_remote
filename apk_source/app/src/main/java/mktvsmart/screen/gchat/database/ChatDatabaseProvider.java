package mktvsmart.screen.gchat.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/* loaded from: classes.dex */
public class ChatDatabaseProvider extends ContentProvider {
    public static final String AUTHORITY = "mktvsmart.screen.gchat.database";
    private static final UriMatcher MATCHER = new UriMatcher(-1);
    private static final int MESSAGE = 1;
    public static final String MESSAGE_PATH = "message";
    private ChatDatabaseHelper mChatDatabaseHelper;

    static {
        MATCHER.addURI(AUTHORITY, "message", 1);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mChatDatabaseHelper = new ChatDatabaseHelper(getContext());
        return false;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = this.mChatDatabaseHelper.getReadableDatabase();
        switch (MATCHER.match(uri)) {
            case 1:
                Cursor cursor = db.query("message", projection, selection, selectionArgs, null, null, sortOrder);
                if (cursor != null) {
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri.toString());
        }
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case 1:
                return "vnd.android.cursor.dir/message";
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri.toString());
        }
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = this.mChatDatabaseHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case 1:
                long rowId = db.insert("message", null, values);
                if (rowId < 0) {
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, rowId);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri.toString());
        }
    }

    @Override // android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = values.length;
        SQLiteDatabase db = this.mChatDatabaseHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case 1:
                for (ContentValues contentValues : values) {
                    db.insert("message", null, contentValues);
                }
                if (numValues > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numValues;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri.toString());
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.mChatDatabaseHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case 1:
                int count = db.delete("message", selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri.toString());
        }
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.mChatDatabaseHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case 1:
                int count = db.update("message", values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri.toString());
        }
    }
}
