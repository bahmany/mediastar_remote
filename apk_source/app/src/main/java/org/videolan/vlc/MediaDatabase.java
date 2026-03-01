package org.videolan.vlc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.videolan.vlc.util.VLCInstance;

/* loaded from: classes.dex */
public class MediaDatabase {
    private static /* synthetic */ int[] $SWITCH_TABLE$org$videolan$vlc$MediaDatabase$mediaColumn = null;
    private static final int CHUNK_SIZE = 50;
    private static final String DB_NAME = "vlc_database";
    private static final int DB_VERSION = 20;
    private static final String DIR_ROW_PATH = "path";
    private static final String DIR_TABLE_NAME = "directories_table";
    private static final String MEDIA_ALBUM = "album";
    private static final String MEDIA_ALBUMARTIST = "albumartist";
    private static final String MEDIA_ARTIST = "artist";
    private static final String MEDIA_ARTWORKURL = "artwork_url";
    private static final String MEDIA_AUDIOTRACK = "audio_track";
    private static final String MEDIA_DISCNUMBER = "disc_number";
    private static final String MEDIA_GENRE = "genre";
    private static final String MEDIA_HEIGHT = "height";
    private static final String MEDIA_LAST_MODIFIED = "last_modified";
    private static final String MEDIA_LENGTH = "length";
    public static final String MEDIA_LOCATION = "_id";
    private static final String MEDIA_PICTURE = "picture";
    private static final String MEDIA_SPUTRACK = "spu_track";
    private static final String MEDIA_TABLE_NAME = "media_table";
    private static final String MEDIA_TIME = "time";
    public static final String MEDIA_TITLE = "title";
    private static final String MEDIA_TRACKNUMBER = "track_number";
    private static final String MEDIA_TYPE = "type";
    private static final String MEDIA_VIRTUAL_TABLE_NAME = "media_table_fts";
    private static final String MEDIA_WIDTH = "width";
    private static final String MRL_DATE = "date";
    private static final String MRL_TABLE_NAME = "mrl_table";
    private static final String MRL_TABLE_SIZE = "100";
    private static final String MRL_URI = "uri";
    private static final String NETWORK_FAV_TABLE_NAME = "fav_table";
    private static final String NETWORK_FAV_TITLE = "title";
    private static final String NETWORK_FAV_URI = "uri";
    private static final String PLAYLIST_MEDIA_ID = "id";
    private static final String PLAYLIST_MEDIA_MEDIALOCATION = "media_location";
    private static final String PLAYLIST_MEDIA_ORDER = "playlist_order";
    private static final String PLAYLIST_MEDIA_PLAYLISTNAME = "playlist_name";
    private static final String PLAYLIST_MEDIA_TABLE_NAME = "playlist_media_table";
    private static final String PLAYLIST_NAME = "name";
    private static final String PLAYLIST_TABLE_NAME = "playlist_table";
    private static final String SEARCHHISTORY_DATE = "date";
    private static final String SEARCHHISTORY_KEY = "key";
    private static final String SEARCHHISTORY_TABLE_NAME = "searchhistory_table";
    public static final String TAG = "VLC/MediaDatabase";
    private static MediaDatabase instance;
    private SQLiteDatabase mDb;

    public enum mediaColumn {
        MEDIA_TABLE_NAME,
        MEDIA_PATH,
        MEDIA_TIME,
        MEDIA_LENGTH,
        MEDIA_TYPE,
        MEDIA_PICTURE,
        MEDIA_TITLE,
        MEDIA_ARTIST,
        MEDIA_GENRE,
        MEDIA_ALBUM,
        MEDIA_ALBUMARTIST,
        MEDIA_WIDTH,
        MEDIA_HEIGHT,
        MEDIA_ARTWORKURL,
        MEDIA_AUDIOTRACK,
        MEDIA_SPUTRACK,
        MEDIA_TRACKNUMBER,
        MEDIA_DISCNUMBER,
        MEDIA_LAST_MODIFIED;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static mediaColumn[] valuesCustom() {
            mediaColumn[] mediacolumnArrValuesCustom = values();
            int length = mediacolumnArrValuesCustom.length;
            mediaColumn[] mediacolumnArr = new mediaColumn[length];
            System.arraycopy(mediacolumnArrValuesCustom, 0, mediacolumnArr, 0, length);
            return mediacolumnArr;
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$org$videolan$vlc$MediaDatabase$mediaColumn() {
        int[] iArr = $SWITCH_TABLE$org$videolan$vlc$MediaDatabase$mediaColumn;
        if (iArr == null) {
            iArr = new int[mediaColumn.valuesCustom().length];
            try {
                iArr[mediaColumn.MEDIA_ALBUM.ordinal()] = 10;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[mediaColumn.MEDIA_ALBUMARTIST.ordinal()] = 11;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[mediaColumn.MEDIA_ARTIST.ordinal()] = 8;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[mediaColumn.MEDIA_ARTWORKURL.ordinal()] = 14;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[mediaColumn.MEDIA_AUDIOTRACK.ordinal()] = 15;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[mediaColumn.MEDIA_DISCNUMBER.ordinal()] = 18;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[mediaColumn.MEDIA_GENRE.ordinal()] = 9;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[mediaColumn.MEDIA_HEIGHT.ordinal()] = 13;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[mediaColumn.MEDIA_LAST_MODIFIED.ordinal()] = 19;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[mediaColumn.MEDIA_LENGTH.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[mediaColumn.MEDIA_PATH.ordinal()] = 2;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[mediaColumn.MEDIA_PICTURE.ordinal()] = 6;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[mediaColumn.MEDIA_SPUTRACK.ordinal()] = 16;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[mediaColumn.MEDIA_TABLE_NAME.ordinal()] = 1;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[mediaColumn.MEDIA_TIME.ordinal()] = 3;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[mediaColumn.MEDIA_TITLE.ordinal()] = 7;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[mediaColumn.MEDIA_TRACKNUMBER.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                iArr[mediaColumn.MEDIA_TYPE.ordinal()] = 5;
            } catch (NoSuchFieldError e18) {
            }
            try {
                iArr[mediaColumn.MEDIA_WIDTH.ordinal()] = 12;
            } catch (NoSuchFieldError e19) {
            }
            $SWITCH_TABLE$org$videolan$vlc$MediaDatabase$mediaColumn = iArr;
        }
        return iArr;
    }

    private MediaDatabase(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        this.mDb = helper.getWritableDatabase();
    }

    public static synchronized MediaDatabase getInstance() {
        if (instance == null) {
            instance = new MediaDatabase(VLCInstance.getAppContext());
        }
        return instance;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, MediaDatabase.DB_NAME, (SQLiteDatabase.CursorFactory) null, 20);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public SQLiteDatabase getWritableDatabase() {
            SQLiteDatabase db;
            try {
                return super.getWritableDatabase();
            } catch (SQLiteException e) {
                try {
                    db = SQLiteDatabase.openOrCreateDatabase(VLCInstance.getAppContext().getDatabasePath(MediaDatabase.DB_NAME), (SQLiteDatabase.CursorFactory) null);
                } catch (SQLiteException e2) {
                    Log.w(MediaDatabase.TAG, "SQLite database could not be created! Media library cannot be saved.");
                    db = SQLiteDatabase.create(null);
                }
                int version = db.getVersion();
                if (version != 20) {
                    db.beginTransaction();
                    try {
                        if (version == 0) {
                            onCreate(db);
                        } else {
                            onUpgrade(db, version, 20);
                        }
                        db.setVersion(20);
                        db.setTransactionSuccessful();
                        return db;
                    } finally {
                        db.endTransaction();
                    }
                }
                return db;
            }
        }

        public void dropMediaTableQuery(SQLiteDatabase db) throws SQLException {
            try {
                db.execSQL("DROP TABLE media_table;");
                db.execSQL("DROP TABLE media_table_fts;");
            } catch (SQLiteException e) {
                Log.w(MediaDatabase.TAG, "SQLite tables could not be dropped! Maybe they were missing...");
            }
        }

        public void createMediaTableQuery(SQLiteDatabase db) throws SQLException {
            db.execSQL("CREATE TABLE IF NOT EXISTS media_table (_id TEXT PRIMARY KEY NOT NULL, time INTEGER, length INTEGER, type INTEGER, picture BLOB, title TEXT, artist TEXT, genre TEXT, album TEXT, albumartist TEXT, width INTEGER, height INTEGER, artwork_url TEXT, audio_track INTEGER, spu_track INTEGER, track_number INTEGER, disc_number INTEGER, last_modified INTEGER);");
            db.execSQL("PRAGMA recursive_triggers='ON'");
            db.execSQL("CREATE VIRTUAL TABLE media_table_fts USING FTS3 (_id, title, artist, genre, album, albumartist);");
            db.execSQL(" CREATE TRIGGER media_insert_trigger AFTER INSERT ON media_table BEGIN INSERT INTO media_table_fts (_id, title, artist, genre, album, albumartist ) VALUES (new._id, new.title, new.artist, new.genre, new.album, new.albumartist); END;");
            db.execSQL(" CREATE TRIGGER media_delete_trigger AFTER DELETE ON media_table BEGIN DELETE FROM media_table_fts WHERE _id = old._id; END;");
        }

        private void createPlaylistTablesQuery(SQLiteDatabase db) throws SQLException {
            db.execSQL("CREATE TABLE IF NOT EXISTS playlist_table (name VARCHAR(200) PRIMARY KEY NOT NULL);");
            db.execSQL("CREATE TABLE IF NOT EXISTS playlist_media_table (id INTEGER PRIMARY KEY AUTOINCREMENT, playlist_name VARCHAR(200) NOT NULL,media_location TEXT NOT NULL,playlist_order INTEGER NOT NULL);");
        }

        private void createMRLTableQuery(SQLiteDatabase db) throws SQLException {
            db.execSQL("CREATE TABLE IF NOT EXISTS mrl_table (uri TEXT PRIMARY KEY NOT NULL,date DATETIME NOT NULL);");
            db.execSQL(" CREATE TRIGGER mrl_history_trigger AFTER INSERT ON mrl_table BEGIN  DELETE FROM mrl_table where uri NOT IN (SELECT uri from mrl_table ORDER BY date DESC LIMIT 100); END");
        }

        public void dropMRLTableQuery(SQLiteDatabase db) throws SQLException {
            try {
                db.execSQL("DROP TABLE mrl_table;");
            } catch (SQLiteException e) {
                Log.w(MediaDatabase.TAG, "SQLite tables could not be dropped! Maybe they were missing...");
            }
        }

        private void createNetworkFavTableQuery(SQLiteDatabase db) throws SQLException {
            db.execSQL("CREATE TABLE IF NOT EXISTS fav_table (uri TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL);");
        }

        public void dropNetworkFavTableQuery(SQLiteDatabase db) throws SQLException {
            try {
                db.execSQL("DROP TABLE fav_table;");
            } catch (SQLiteException e) {
                Log.w(MediaDatabase.TAG, "SQLite tables could not be dropped! Maybe they were missing...");
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) throws SQLException {
            db.execSQL("CREATE TABLE IF NOT EXISTS directories_table (path TEXT PRIMARY KEY NOT NULL);");
            createMediaTableQuery(db);
            createPlaylistTablesQuery(db);
            db.execSQL("CREATE TABLE IF NOT EXISTS searchhistory_table (key VARCHAR(200) PRIMARY KEY NOT NULL, date DATETIME NOT NULL);");
            createMRLTableQuery(db);
            createNetworkFavTableQuery(db);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException {
            dropMediaTableQuery(db);
            createMediaTableQuery(db);
            for (int i = oldVersion + 1; i <= newVersion; i++) {
                switch (i) {
                    case 9:
                        db.execSQL("DROP TABLE playlist_media_table;");
                        db.execSQL("DROP TABLE playlist_table;");
                        createPlaylistTablesQuery(db);
                        break;
                    case 11:
                        createMRLTableQuery(db);
                        break;
                    case 13:
                        createNetworkFavTableQuery(db);
                        break;
                    case 17:
                        dropMRLTableQuery(db);
                        createMRLTableQuery(db);
                        break;
                    case 18:
                        dropNetworkFavTableQuery(db);
                        createNetworkFavTableQuery(db);
                        break;
                }
            }
        }
    }

    public String[] getPlaylists() {
        ArrayList<String> playlists = new ArrayList<>();
        Cursor c = this.mDb.query(PLAYLIST_TABLE_NAME, new String[]{PLAYLIST_NAME}, null, null, null, null, null);
        while (c.moveToNext()) {
            playlists.add(c.getString(c.getColumnIndex(PLAYLIST_NAME)));
        }
        c.close();
        return (String[]) playlists.toArray(new String[playlists.size()]);
    }

    public boolean playlistAdd(String name) {
        if (name.length() >= 200 || playlistExists(name)) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(PLAYLIST_NAME, name);
        long res = this.mDb.insert(PLAYLIST_TABLE_NAME, "NULL", values);
        return res != -1;
    }

    public void playlistDelete(String name) {
        this.mDb.delete(PLAYLIST_TABLE_NAME, "name=?", new String[]{name});
        this.mDb.delete(PLAYLIST_MEDIA_TABLE_NAME, "playlist_name=?", new String[]{name});
    }

    public boolean playlistExists(String name) {
        Cursor c = this.mDb.query(PLAYLIST_TABLE_NAME, new String[]{PLAYLIST_NAME}, "name= ?", new String[]{name}, null, null, "1");
        int count = c.getCount();
        c.close();
        return count > 0;
    }

    public String[] playlistGetItems(String playlistName) {
        if (!playlistExists(playlistName)) {
            return null;
        }
        Cursor c = this.mDb.query(PLAYLIST_MEDIA_TABLE_NAME, new String[]{PLAYLIST_MEDIA_MEDIALOCATION}, "playlist_name= ?", new String[]{playlistName}, null, null, "playlist_order ASC");
        int count = c.getCount();
        String[] ret = new String[count];
        int i = 0;
        while (c.moveToNext()) {
            ret[i] = c.getString(c.getColumnIndex(PLAYLIST_MEDIA_MEDIALOCATION));
            i++;
        }
        c.close();
        return ret;
    }

    public void playlistInsertItem(String playlistName, int position, String mrl) {
        playlistShiftItems(playlistName, position, 1);
        ContentValues values = new ContentValues();
        values.put(PLAYLIST_MEDIA_PLAYLISTNAME, playlistName);
        values.put(PLAYLIST_MEDIA_MEDIALOCATION, mrl);
        values.put(PLAYLIST_MEDIA_ORDER, Integer.valueOf(position));
        this.mDb.insert(PLAYLIST_MEDIA_TABLE_NAME, "NULL", values);
    }

    private void playlistShiftItems(String playlistName, int position, int factor) {
        Cursor c = this.mDb.query(PLAYLIST_MEDIA_TABLE_NAME, new String[]{PLAYLIST_MEDIA_ID, PLAYLIST_MEDIA_ORDER}, "playlist_name=? AND playlist_order >= ?", new String[]{playlistName, String.valueOf(position)}, null, null, "playlist_order ASC");
        while (c.moveToNext()) {
            ContentValues cv = new ContentValues();
            int ii = c.getInt(c.getColumnIndex(PLAYLIST_MEDIA_ORDER)) + factor;
            Log.d(TAG, "ii = " + ii);
            cv.put(PLAYLIST_MEDIA_ORDER, Integer.valueOf(ii));
            this.mDb.update(PLAYLIST_MEDIA_TABLE_NAME, cv, "id=?", new String[]{c.getString(c.getColumnIndex(PLAYLIST_MEDIA_ID))});
        }
        c.close();
    }

    public void playlistRemoveItem(String playlistName, int position) {
        this.mDb.delete(PLAYLIST_MEDIA_TABLE_NAME, "playlist_name=? AND playlist_order=?", new String[]{playlistName, Integer.toString(position)});
        playlistShiftItems(playlistName, position + 1, -1);
    }

    public boolean playlistRename(String playlistName, String newPlaylistName) {
        if (!playlistExists(playlistName) || playlistExists(newPlaylistName)) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(PLAYLIST_NAME, newPlaylistName);
        this.mDb.update(PLAYLIST_TABLE_NAME, values, "name =?", new String[]{playlistName});
        ContentValues values2 = new ContentValues();
        values2.put(PLAYLIST_MEDIA_PLAYLISTNAME, newPlaylistName);
        this.mDb.update(PLAYLIST_MEDIA_TABLE_NAME, values2, "playlist_name =?", new String[]{playlistName});
        return true;
    }

    private static void safePut(ContentValues values, String key, String value) {
        if (value == null) {
            values.putNull(key);
        } else {
            values.put(key, value);
        }
    }

    public synchronized void addMedia(MediaWrapper media) {
        ContentValues values = new ContentValues();
        values.put("_id", media.getLocation());
        values.put(MEDIA_TIME, Long.valueOf(media.getTime()));
        values.put(MEDIA_LENGTH, Long.valueOf(media.getLength()));
        values.put("type", Integer.valueOf(media.getType()));
        values.put("title", media.getTitle());
        safePut(values, MEDIA_ARTIST, media.getArtist());
        safePut(values, MEDIA_GENRE, media.getGenre());
        safePut(values, MEDIA_ALBUM, media.getAlbum());
        safePut(values, MEDIA_ALBUMARTIST, media.getAlbumArtist());
        values.put(MEDIA_WIDTH, Integer.valueOf(media.getWidth()));
        values.put(MEDIA_HEIGHT, Integer.valueOf(media.getHeight()));
        values.put(MEDIA_ARTWORKURL, media.getArtworkURL());
        values.put(MEDIA_AUDIOTRACK, Integer.valueOf(media.getAudioTrack()));
        values.put(MEDIA_SPUTRACK, Integer.valueOf(media.getSpuTrack()));
        values.put(MEDIA_TRACKNUMBER, Integer.valueOf(media.getTrackNumber()));
        values.put(MEDIA_DISCNUMBER, Integer.valueOf(media.getDiscNumber()));
        values.put(MEDIA_LAST_MODIFIED, Long.valueOf(media.getLastModified()));
        this.mDb.replace(MEDIA_TABLE_NAME, "NULL", values);
    }

    public synchronized boolean mediaItemExists(String location) {
        boolean exists;
        try {
            Cursor cursor = this.mDb.query(MEDIA_TABLE_NAME, new String[]{"_id"}, "_id=?", new String[]{location}, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Query failed");
            exists = false;
        }
        return exists;
    }

    private synchronized HashSet<File> getMediaFiles() {
        HashSet<File> files;
        files = new HashSet<>();
        Cursor cursor = this.mDb.query(MEDIA_TABLE_NAME, new String[]{"_id"}, null, null, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                File file = new File(cursor.getString(0));
                files.add(file);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return files;
    }

    public synchronized Cursor queryMedia(String query) {
        String[] queryColumns;
        queryColumns = new String[]{"_id", "title"};
        return this.mDb.query(MEDIA_VIRTUAL_TABLE_NAME, queryColumns, "media_table_fts MATCH ?", new String[]{String.valueOf(query) + "*"}, null, null, null, null);
    }

    public synchronized ArrayList<String> searchMedia(String filter) {
        ArrayList<String> mediaList;
        mediaList = new ArrayList<>();
        Cursor cursor = queryMedia(filter);
        if (cursor.moveToFirst()) {
            do {
                mediaList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mediaList;
    }

    public synchronized HashMap<String, MediaWrapper> getMedias() {
        HashMap<String, MediaWrapper> medias;
        int count;
        medias = new HashMap<>();
        int chunk_count = 0;
        do {
            count = 0;
            Cursor cursor = this.mDb.rawQuery(String.format(Locale.US, "SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s LIMIT %d OFFSET %d", "_id", MEDIA_TIME, MEDIA_LENGTH, "type", "title", MEDIA_ARTIST, MEDIA_GENRE, MEDIA_ALBUM, MEDIA_ALBUMARTIST, MEDIA_WIDTH, MEDIA_HEIGHT, MEDIA_ARTWORKURL, MEDIA_AUDIOTRACK, MEDIA_SPUTRACK, MEDIA_TRACKNUMBER, MEDIA_DISCNUMBER, MEDIA_LAST_MODIFIED, MEDIA_TABLE_NAME, 50, Integer.valueOf(chunk_count * 50)), null);
            if (cursor.moveToFirst()) {
                do {
                    try {
                        String location = cursor.getString(0);
                        MediaWrapper media = new MediaWrapper(location, cursor.getLong(1), cursor.getLong(2), cursor.getInt(3), null, cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getInt(10), cursor.getString(11), cursor.getInt(12), cursor.getInt(13), cursor.getInt(14), cursor.getInt(15), cursor.getLong(16));
                        medias.put(media.getLocation(), media);
                        count++;
                    } catch (IllegalStateException e) {
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            chunk_count++;
        } while (count == 50);
        return medias;
    }

    public synchronized HashMap<String, Long> getVideoTimes(Context context) {
        HashMap<String, Long> times;
        int count;
        times = new HashMap<>();
        int chunk_count = 0;
        do {
            count = 0;
            Cursor cursor = this.mDb.rawQuery(String.format(Locale.US, "SELECT %s,%s FROM %s WHERE %s=%d LIMIT %d OFFSET %d", "_id", MEDIA_TIME, MEDIA_TABLE_NAME, "type", 0, 50, Integer.valueOf(chunk_count * 50)), null);
            if (cursor.moveToFirst()) {
                do {
                    String location = cursor.getString(0);
                    long time = cursor.getLong(1);
                    times.put(location, Long.valueOf(time));
                    count++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            chunk_count++;
        } while (count == 50);
        return times;
    }

    public synchronized MediaWrapper getMedia(String location) throws Throwable {
        MediaWrapper mediaWrapper;
        try {
            try {
                Cursor cursor = this.mDb.query(MEDIA_TABLE_NAME, new String[]{MEDIA_TIME, MEDIA_LENGTH, "type", "title", MEDIA_ARTIST, MEDIA_GENRE, MEDIA_ALBUM, MEDIA_ALBUMARTIST, MEDIA_WIDTH, MEDIA_HEIGHT, MEDIA_ARTWORKURL, MEDIA_AUDIOTRACK, MEDIA_SPUTRACK, MEDIA_TRACKNUMBER, MEDIA_DISCNUMBER, MEDIA_LAST_MODIFIED}, "_id=?", new String[]{location}, null, null, null);
                MediaWrapper media = cursor.moveToFirst() ? new MediaWrapper(location, cursor.getLong(0), cursor.getLong(1), cursor.getInt(2), null, cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getInt(11), cursor.getInt(12), cursor.getInt(13), cursor.getInt(14), cursor.getLong(15)) : null;
                try {
                    cursor.close();
                    mediaWrapper = media;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (IllegalArgumentException e) {
                mediaWrapper = null;
            }
            return mediaWrapper;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public synchronized Bitmap getPicture(Context context, String location) {
        byte[] blob;
        Bitmap blob2;
        byte[] blob3;
        Cursor cursor = this.mDb.query(MEDIA_TABLE_NAME, new String[]{MEDIA_PICTURE}, "_id=?", new String[]{location}, null, null, null);
        if (cursor.moveToFirst() && (blob3 = cursor.getBlob(0)) != null && blob3.length > 1 && blob3.length < 500000) {
            try {
                blob2 = BitmapFactory.decodeByteArray(blob3, blob, blob3.length);
            } catch (OutOfMemoryError e) {
                blob2 = null;
            } finally {
            }
        }
        cursor.close();
        return blob2;
    }

    public synchronized void removeMedia(String location) {
        this.mDb.delete(MEDIA_TABLE_NAME, "_id=?", new String[]{location});
    }

    public void removeMedias(Set<String> locations) {
        this.mDb.beginTransaction();
        try {
            for (String location : locations) {
                this.mDb.delete(MEDIA_TABLE_NAME, "_id=?", new String[]{location});
            }
            this.mDb.setTransactionSuccessful();
        } finally {
            this.mDb.endTransaction();
        }
    }

    public synchronized void updateMedia(String location, mediaColumn col, Object object) {
        if (location != null) {
            ContentValues values = new ContentValues();
            switch ($SWITCH_TABLE$org$videolan$vlc$MediaDatabase$mediaColumn()[col.ordinal()]) {
                case 3:
                    if (object != null) {
                        values.put(MEDIA_TIME, (Long) object);
                        break;
                    }
                    break;
                case 4:
                    if (object != null) {
                        values.put(MEDIA_LENGTH, (Long) object);
                        break;
                    }
                    break;
                case 6:
                    if (object != null) {
                        Bitmap picture = (Bitmap) object;
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        values.put(MEDIA_PICTURE, out.toByteArray());
                        break;
                    } else {
                        values.put(MEDIA_PICTURE, new byte[1]);
                        break;
                    }
                case 15:
                    if (object != null) {
                        values.put(MEDIA_AUDIOTRACK, (Integer) object);
                        break;
                    }
                    break;
                case 16:
                    if (object != null) {
                        values.put(MEDIA_SPUTRACK, (Integer) object);
                        break;
                    }
                    break;
            }
            this.mDb.update(MEDIA_TABLE_NAME, values, "_id=?", new String[]{location});
        }
    }

    public synchronized void addDir(String path) {
        if (!mediaDirExists(path)) {
            ContentValues values = new ContentValues();
            values.put(DIR_ROW_PATH, path);
            this.mDb.insert(DIR_TABLE_NAME, null, values);
        }
    }

    public synchronized void removeDir(String path) {
        this.mDb.delete(DIR_TABLE_NAME, "path=?", new String[]{path});
    }

    public synchronized void recursiveRemoveDir(String path) {
        for (File f : getMediaDirs()) {
            String dirPath = f.getPath();
            if (dirPath.startsWith(path)) {
                this.mDb.delete(DIR_TABLE_NAME, "path=?", new String[]{dirPath});
            }
        }
    }

    public synchronized List<File> getMediaDirs() {
        List<File> paths;
        paths = new ArrayList<>();
        Cursor cursor = this.mDb.query(DIR_TABLE_NAME, new String[]{DIR_ROW_PATH}, null, null, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                File dir = new File(cursor.getString(0));
                paths.add(dir);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paths;
    }

    private synchronized boolean mediaDirExists(String path) {
        boolean exists;
        Cursor cursor = this.mDb.query(DIR_TABLE_NAME, new String[]{DIR_ROW_PATH}, "path=?", new String[]{path}, null, null, null);
        exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public synchronized void addSearchhistoryItem(String key) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(SEARCHHISTORY_KEY, key);
        values.put("date", dateFormat.format(date));
        this.mDb.replace(SEARCHHISTORY_TABLE_NAME, null, values);
    }

    public synchronized ArrayList<String> getSearchhistory(int size) {
        ArrayList<String> history;
        history = new ArrayList<>();
        Cursor cursor = this.mDb.query(SEARCHHISTORY_TABLE_NAME, new String[]{SEARCHHISTORY_KEY}, null, null, null, null, "date DESC", Integer.toString(size));
        while (cursor.moveToNext()) {
            history.add(cursor.getString(0));
        }
        cursor.close();
        return history;
    }

    public synchronized void clearSearchHistory() {
        this.mDb.delete(SEARCHHISTORY_TABLE_NAME, null, null);
    }

    public synchronized void addMrlhistoryItem(String uri) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put("uri", uri);
        values.put("date", dateFormat.format(date));
        this.mDb.replace(MRL_TABLE_NAME, null, values);
    }

    public synchronized ArrayList<String> getMrlhistory() {
        ArrayList<String> history;
        history = new ArrayList<>();
        Cursor cursor = this.mDb.query(MRL_TABLE_NAME, new String[]{"uri"}, null, null, null, null, "date DESC", MRL_TABLE_SIZE);
        while (cursor.moveToNext()) {
            history.add(cursor.getString(0));
        }
        cursor.close();
        return history;
    }

    public synchronized void deleteMrlUri(String uri) {
        this.mDb.delete(MRL_TABLE_NAME, "uri=?", new String[]{uri});
    }

    public synchronized void clearMrlHistory() {
        this.mDb.delete(MRL_TABLE_NAME, null, null);
    }

    public synchronized void addNetworkFavItem(String mrl, String title) {
        ContentValues values = new ContentValues();
        values.put("uri", Uri.encode(mrl));
        values.put("title", Uri.encode(title));
        this.mDb.replace(NETWORK_FAV_TABLE_NAME, null, values);
    }

    public synchronized boolean networkFavExists(String mrl) {
        boolean exists;
        Cursor cursor = this.mDb.query(NETWORK_FAV_TABLE_NAME, new String[]{"uri"}, "uri=?", new String[]{Uri.encode(mrl)}, null, null, null);
        exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public synchronized ArrayList<MediaWrapper> getAllNetworkFav() {
        ArrayList<MediaWrapper> favs;
        favs = new ArrayList<>();
        Cursor cursor = this.mDb.query(NETWORK_FAV_TABLE_NAME, new String[]{"uri", "title"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            MediaWrapper mw = new MediaWrapper(Uri.decode(cursor.getString(0)));
            mw.setTitle(Uri.decode(cursor.getString(1)));
            mw.setType(3);
            favs.add(mw);
        }
        cursor.close();
        return favs;
    }

    public synchronized void deleteNetworkFav(String uri) {
        this.mDb.delete(NETWORK_FAV_TABLE_NAME, "uri=?", new String[]{Uri.encode(uri)});
    }

    public synchronized void clearNetworkFavTable() {
        this.mDb.delete(NETWORK_FAV_TABLE_NAME, null, null);
    }

    public synchronized void emptyDatabase() {
        this.mDb.delete(MEDIA_TABLE_NAME, null, null);
    }

    public static void setPicture(MediaWrapper m, Bitmap p) {
        Log.d(TAG, "Setting new picture for " + m.getTitle());
        try {
            getInstance().updateMedia(m.getLocation(), mediaColumn.MEDIA_PICTURE, p);
        } catch (SQLiteFullException e) {
            Log.d(TAG, "SQLiteFullException while setting picture");
        }
        m.setPictureParsed(true);
    }
}
