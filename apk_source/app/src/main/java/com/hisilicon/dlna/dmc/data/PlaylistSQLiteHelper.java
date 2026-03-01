package com.hisilicon.dlna.dmc.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: classes.dex */
public class PlaylistSQLiteHelper extends SQLiteOpenHelper {
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_URL = "url";
    private static final String DATABASE_CREATE_PLAYLIST_ITEM = "create table PlaylistItems( _id integer primary key autoincrement, title text not null, url text not null, type text not null, metadata text not null);";
    private static final String DATABASE_NAME = "playlists.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_PLAYLIST_ITEMS = "PlaylistItems";
    public static final String COL_TYPE = "type";
    public static final String COL_METADATA = "metadata";
    public static String[] PLALYLISTITEM_ALLCOLUMNS = {"_id", "title", "url", COL_TYPE, COL_METADATA};

    public PlaylistSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase db) throws SQLException {
        db.execSQL(DATABASE_CREATE_PLAYLIST_ITEM);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException {
        db.execSQL("DROP TABLE IF EXISTS PlaylistItems");
        onCreate(db);
    }
}
