package mktvsmart.screen.gchat.database;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class ChatDatabaseAsyncHandler extends AsyncQueryHandler {
    private WeakReference<ChatDatabaseListener> mListener;

    public interface ChatDatabaseListener {
        void onDeleteComplete(int i, Object obj, int i2);

        void onInsertComplete(int i, Object obj, Uri uri);

        boolean onQueryComplete(int i, Object obj, Cursor cursor);

        void onUpdateComplete(int i, Object obj, int i2);
    }

    public ChatDatabaseAsyncHandler(ContentResolver resolver) {
        super(resolver);
    }

    public void setListener(ChatDatabaseListener listener) {
        this.mListener = listener != null ? new WeakReference<>(listener) : null;
    }

    private ChatDatabaseListener getListener() {
        if (this.mListener == null) {
            return null;
        }
        return this.mListener.get();
    }

    public void clearDBListener() {
        this.mListener = null;
    }

    @Override // android.content.AsyncQueryHandler
    public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        super.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override // android.content.AsyncQueryHandler
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        ChatDatabaseListener listener = getListener();
        if (listener != null) {
            boolean close = listener.onQueryComplete(token, cookie, cursor);
            if (cursor != null && !cursor.isClosed() && close) {
                cursor.close();
                return;
            }
            return;
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override // android.content.AsyncQueryHandler
    protected void onDeleteComplete(int token, Object cookie, int result) {
        ChatDatabaseListener listener = getListener();
        if (listener != null) {
            listener.onDeleteComplete(token, cookie, result);
        }
    }

    @Override // android.content.AsyncQueryHandler
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        ChatDatabaseListener listener = getListener();
        if (listener != null) {
            listener.onInsertComplete(token, cookie, uri);
        }
    }

    @Override // android.content.AsyncQueryHandler
    protected void onUpdateComplete(int token, Object cookie, int result) {
        ChatDatabaseListener listener = getListener();
        if (listener != null) {
            listener.onUpdateComplete(token, cookie, result);
        }
    }
}
