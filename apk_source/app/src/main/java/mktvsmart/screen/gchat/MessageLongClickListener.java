package mktvsmart.screen.gchat;

import android.content.Context;
import android.view.View;
import mktvsmart.screen.gchat.ui.MessagePopupWindow;

/* loaded from: classes.dex */
public class MessageLongClickListener implements View.OnLongClickListener {
    private Context mContext;
    private MessagePopupWindow.OnPopupMenuListener mPopupMenuListener;

    public MessageLongClickListener(Context context, MessagePopupWindow.OnPopupMenuListener listener) {
        this.mContext = context;
        this.mPopupMenuListener = listener;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        MessagePopupWindow messagePopupWindow = new MessagePopupWindow(this.mContext, v, this.mPopupMenuListener);
        messagePopupWindow.show();
        return true;
    }
}
