package mktvsmart.screen.gchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.util.List;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;

/* loaded from: classes.dex */
public class ChatMessageManager {
    public static final Uri CONTENT_URI = Uri.parse("content://mktvsmart.screen.gchat.database/message");
    private ChatDatabaseAsyncHandler mAsyncHandler;

    public ChatMessageManager(Context context) {
        this.mAsyncHandler = new ChatDatabaseAsyncHandler(context.getContentResolver());
    }

    public void deleteAllAsync() {
        this.mAsyncHandler.startDelete(0, null, CONTENT_URI, null, null);
    }

    public void insertAsync(DataConvertChatMsgModel message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChatDatabaseHelper.KEY_USER_ID, Integer.valueOf(message.getUserID()));
        contentValues.put(ChatDatabaseHelper.KEY_MESSAGE_TYPE, Integer.valueOf(message.getMsgType()));
        contentValues.put(ChatDatabaseHelper.KEY_USERNAME, message.getUsername());
        contentValues.put(ChatDatabaseHelper.KEY_CONTENT, message.getContent());
        contentValues.put(ChatDatabaseHelper.KEY_TIMESTAMP, Long.valueOf(message.getTimestamp()));
        this.mAsyncHandler.startInsert(0, null, CONTENT_URI, contentValues);
    }

    public void deleteAsync(DataConvertChatMsgModel message) {
        String selection = "_user_id=" + message.getUserID() + " AND " + ChatDatabaseHelper.KEY_TIMESTAMP + "=" + message.getTimestamp() + " AND " + ChatDatabaseHelper.KEY_CONTENT + "=?";
        this.mAsyncHandler.startDelete(0, null, CONTENT_URI, selection, new String[]{message.getContent()});
    }

    public static Cursor getAllMessage(Context context) {
        return query(context, null, null, null, "_id ASC");
    }

    public static Cursor query(Context context, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.query(CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    public static void insert(Context context, DataConvertChatMsgModel message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChatDatabaseHelper.KEY_USER_ID, Integer.valueOf(message.getUserID()));
        contentValues.put(ChatDatabaseHelper.KEY_MESSAGE_TYPE, Integer.valueOf(message.getMsgType()));
        contentValues.put(ChatDatabaseHelper.KEY_USERNAME, message.getUsername());
        contentValues.put(ChatDatabaseHelper.KEY_CONTENT, message.getContent());
        contentValues.put(ChatDatabaseHelper.KEY_TIMESTAMP, Long.valueOf(message.getTimestamp()));
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.insert(CONTENT_URI, contentValues);
    }

    public static void bulkInsert(Context context, List<DataConvertChatMsgModel> messages) {
        if (messages != null && !messages.isEmpty()) {
            int length = messages.size();
            ContentValues[] values = new ContentValues[length];
            for (int i = 0; i < length; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ChatDatabaseHelper.KEY_USER_ID, Integer.valueOf(messages.get(i).getUserID()));
                contentValues.put(ChatDatabaseHelper.KEY_MESSAGE_TYPE, Integer.valueOf(messages.get(i).getMsgType()));
                contentValues.put(ChatDatabaseHelper.KEY_USERNAME, messages.get(i).getUsername());
                contentValues.put(ChatDatabaseHelper.KEY_CONTENT, messages.get(i).getContent());
                contentValues.put(ChatDatabaseHelper.KEY_TIMESTAMP, Long.valueOf(messages.get(i).getTimestamp()));
                values[i] = contentValues;
            }
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.bulkInsert(CONTENT_URI, values);
        }
    }

    public static void update(Context context, DataConvertChatMsgModel message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChatDatabaseHelper.KEY_USER_ID, Integer.valueOf(message.getUserID()));
        contentValues.put(ChatDatabaseHelper.KEY_MESSAGE_TYPE, Integer.valueOf(message.getMsgType()));
        contentValues.put(ChatDatabaseHelper.KEY_USERNAME, message.getUsername());
        contentValues.put(ChatDatabaseHelper.KEY_CONTENT, message.getContent());
        contentValues.put(ChatDatabaseHelper.KEY_TIMESTAMP, Long.valueOf(message.getTimestamp()));
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.update(CONTENT_URI, contentValues, null, null);
    }

    public static void delete(Context context, DataConvertChatMsgModel message) {
        String whereClause = "_user_id=" + message.getUserID() + " AND " + ChatDatabaseHelper.KEY_TIMESTAMP + "=" + message.getTimestamp() + " AND " + ChatDatabaseHelper.KEY_CONTENT + "=?";
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(CONTENT_URI, whereClause, new String[]{message.getContent()});
    }

    public static void deleteAll(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(CONTENT_URI, null, null);
    }
}
