package mktvsmart.screen.gchat.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import mktvsmart.screen.R;
import mktvsmart.screen.gchat.MessageLongClickListener;
import mktvsmart.screen.gchat.database.ChatDatabaseHelper;
import mktvsmart.screen.gchat.ui.MessagePopupWindow;

/* loaded from: classes.dex */
public class ChatCursorAdapter extends CursorAdapter {
    private static final int VIEW_TYPE = 2;
    private Context mContext;
    private MessageLongClickListener messageLongClickListener;

    private class ViewHolder {
        public TextView content;
        public TextView timestamp;
        public TextView userId;
        public TextView username;

        private ViewHolder() {
        }

        /* synthetic */ ViewHolder(ChatCursorAdapter chatCursorAdapter, ViewHolder viewHolder) {
            this();
        }
    }

    public ChatCursorAdapter(Context context, Cursor cursor, MessagePopupWindow.OnPopupMenuListener listener) {
        super(context, cursor, 2);
        this.mContext = context;
        this.messageLongClickListener = new MessageLongClickListener(this.mContext, listener);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 2;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE_TYPE));
    }

    @Override // android.support.v4.widget.CursorAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Cursor cursor = getCursor();
        if (!this.mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        int userId = cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_USER_ID));
        int messageType = cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE_TYPE));
        long timestamp = cursor.getLong(cursor.getColumnIndex(ChatDatabaseHelper.KEY_TIMESTAMP));
        String username = cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_USERNAME));
        String content = cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_CONTENT));
        if (convertView == null) {
            viewHolder = new ViewHolder(this, null);
            switch (messageType) {
                case 0:
                    convertView = LayoutInflater.from(this.mContext).inflate(R.layout.gchat_message_list_in, parent, false);
                    viewHolder.username = (TextView) convertView.findViewById(R.id.user_name);
                    viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                    viewHolder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
                    viewHolder.userId = (TextView) convertView.findViewById(R.id.user_id);
                    break;
                default:
                    convertView = LayoutInflater.from(this.mContext).inflate(R.layout.gchat_message_list_out, parent, false);
                    viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                    viewHolder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.content.setTag(Integer.valueOf(position));
        viewHolder.content.setOnLongClickListener(this.messageLongClickListener);
        Date date = new Date(1000 * timestamp);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = sdFormatter.format(date);
        if (messageType == 0) {
            viewHolder.timestamp.setText(timeString);
            viewHolder.username.setText(username);
            viewHolder.userId.setText(this.mContext.getResources().getString(R.string.gchat_num_format, Integer.valueOf(userId)));
            viewHolder.content.setText(content);
        } else {
            viewHolder.content.setText(content);
            viewHolder.timestamp.setText(timeString);
        }
        return convertView;
    }

    @Override // android.support.v4.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override // android.support.v4.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
    }
}
