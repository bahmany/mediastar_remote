package mktvsmart.screen.gchat.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: classes.dex */
public class ChatDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chat_message_db";
    public static final String KEY_CONTENT = "_content";
    public static final String KEY_MESSAGE_TYPE = "_message_type";
    public static final String KEY_PRIMARY = "_id";
    public static final String KEY_TIMESTAMP = "_timestamp";
    public static final String KEY_USERNAME = "_username";
    public static final String KEY_USER_ID = "_user_id";
    public static final String TABLE_NAME = "message";
    public static final int VERSION = 1;

    public ChatDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ChatDatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public ChatDatabaseHelper(Context context, String name) {
        this(context, name, 1);
    }

    public ChatDatabaseHelper(Context context) {
        this(context, DATABASE_NAME);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase db) throws SQLException {
        db.execSQL("CREATE TABLE IF NOT EXISTS message(_id INTEGER PRIMARY KEY AUTOINCREMENT, _user_id, _message_type, _username, _content, _timestamp);");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException {
        db.execSQL("DROP TABLE IF EXISTS message");
        onCreate(db);
    }
}
