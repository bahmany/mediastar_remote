package com.hisilicon.dlna.dmc.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/* loaded from: classes.dex */
public class PlaylistProvider extends ContentProvider {
    private static final String AUTHORITY = "com.hisilicon.native.dmc.data.playlistprovider";
    private static final int PLAYLIST_ITEM = 1;
    public static final Uri PLAYLIST_ITEM_URI = Uri.parse("content://com.hisilicon.native.dmc.data.playlistprovider/playlist_item");
    private static final String TAG = PlaylistProvider.class.getName();
    private static final UriMatcher uriMatcher = new UriMatcher(-1);
    private PlaylistSQLiteHelper dbPlaylistHelper;

    static {
        uriMatcher.addURI(AUTHORITY, "playlist_item", 1);
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.e(TAG, "update database");
        switch (uriMatcher.match(uri)) {
            case 1:
                Log.i(TAG, "Delete item, id = " + selectionArgs[0]);
                SQLiteDatabase database = this.dbPlaylistHelper.getWritableDatabase();
                try {
                    int deleted = database.delete(PlaylistSQLiteHelper.TABLE_PLAYLIST_ITEMS, selection, selectionArgs);
                    Log.i(TAG, "Delete item count = " + deleted);
                } finally {
                    if (database != null) {
                        database.close();
                    }
                }
            default:
                return -1;
        }
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return String.valueOf(uriMatcher.match(uri));
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        Uri uri2 = null;
        Log.e(TAG, "insert database");
        switch (uriMatcher.match(uri)) {
            case 1:
                Log.i(TAG, "Insert playlist item " + values.toString());
                SQLiteDatabase database = this.dbPlaylistHelper.getWritableDatabase();
                try {
                    long newId = database.insert(PlaylistSQLiteHelper.TABLE_PLAYLIST_ITEMS, null, values);
                    uri2 = Uri.parse(String.valueOf(PLAYLIST_ITEM_URI.toString()) + "/?newid=" + newId);
                    if (database != null) {
                        database.close();
                    }
                } catch (Throwable th) {
                    if (database != null) {
                        database.close();
                    }
                    throw th;
                }
            default:
                return uri2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003b A[Catch: Exception -> 0x005b, all -> 0x006a, TRY_LEAVE, TryCatch #1 {Exception -> 0x005b, blocks: (B:8:0x0035, B:10:0x0038, B:12:0x003b, B:16:0x0047), top: B:32:0x0035, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0043 A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int bulkInsert(android.net.Uri r11, android.content.ContentValues[] r12) {
        /*
            r10 = this;
            r6 = 0
            java.lang.String r7 = com.hisilicon.dlna.dmc.data.PlaylistProvider.TAG
            java.lang.String r8 = "bulk insert"
            android.util.Log.e(r7, r8)
            android.content.UriMatcher r7 = com.hisilicon.dlna.dmc.data.PlaylistProvider.uriMatcher
            int r7 = r7.match(r11)
            switch(r7) {
                case 1: goto L13;
                default: goto L11;
            }
        L11:
            r3 = r6
        L12:
            return r3
        L13:
            java.lang.String r7 = com.hisilicon.dlna.dmc.data.PlaylistProvider.TAG
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            java.lang.String r9 = "Insert list of item playlist item "
            r8.<init>(r9)
            java.lang.String r9 = r12.toString()
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r8 = r8.toString()
            android.util.Log.i(r7, r8)
            r3 = 0
            com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper r7 = r10.dbPlaylistHelper
            android.database.sqlite.SQLiteDatabase r1 = r7.getWritableDatabase()
            r1.beginTransaction()     // Catch: java.lang.Throwable -> L63
            int r7 = r12.length     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L6a
        L36:
            if (r6 < r7) goto L47
        L38:
            int r6 = r12.length     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L6a
            if (r3 != r6) goto L3e
            r1.setTransactionSuccessful()     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L6a
        L3e:
            r1.endTransaction()     // Catch: java.lang.Throwable -> L63
        L41:
            if (r1 == 0) goto L12
            r1.close()
            goto L12
        L47:
            r0 = r12[r6]     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L6a
            java.lang.String r8 = "PlaylistItems"
            r9 = 0
            long r4 = r1.insertOrThrow(r8, r9, r0)     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L6a
            r8 = 0
            int r8 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r8 <= 0) goto L38
            int r3 = r3 + 1
            int r6 = r6 + 1
            goto L36
        L5b:
            r2 = move-exception
            r2.printStackTrace()     // Catch: java.lang.Throwable -> L6a
            r1.endTransaction()     // Catch: java.lang.Throwable -> L63
            goto L41
        L63:
            r6 = move-exception
            if (r1 == 0) goto L69
            r1.close()
        L69:
            throw r6
        L6a:
            r6 = move-exception
            r1.endTransaction()     // Catch: java.lang.Throwable -> L63
            throw r6     // Catch: java.lang.Throwable -> L63
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hisilicon.dlna.dmc.data.PlaylistProvider.bulkInsert(android.net.Uri, android.content.ContentValues[]):int");
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.dbPlaylistHelper = new PlaylistSQLiteHelper(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case 1:
                Log.i(TAG, "query all playlist item, selection = " + selection);
                SQLiteDatabase database = this.dbPlaylistHelper.getReadableDatabase();
                Cursor result = database.query(PlaylistSQLiteHelper.TABLE_PLAYLIST_ITEMS, projection, selection, selectionArgs, null, null, null);
                return result;
            default:
                return null;
        }
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.e(TAG, "update database");
        int count = -1;
        switch (uriMatcher.match(uri)) {
            case 1:
                Log.i(TAG, "UpdateItem = " + values.toString());
                SQLiteDatabase database = this.dbPlaylistHelper.getWritableDatabase();
                try {
                    count = database.update(PlaylistSQLiteHelper.TABLE_PLAYLIST_ITEMS, values, selection, selectionArgs);
                    if (database != null) {
                        database.close();
                    }
                } catch (Throwable th) {
                    if (database != null) {
                        database.close();
                    }
                    throw th;
                }
            default:
                return count;
        }
    }
}
