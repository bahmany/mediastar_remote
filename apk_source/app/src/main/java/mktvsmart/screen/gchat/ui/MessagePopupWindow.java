package mktvsmart.screen.gchat.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class MessagePopupWindow extends PopupWindow {
    private Context mContext;
    private OnPopupMenuListener mPopupMenuListener;
    private int mPosition;
    private View mView;
    private View.OnClickListener onPopupMenuClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.MessagePopupWindow.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popup_copy /* 2131493198 */:
                    TextView textView = (TextView) MessagePopupWindow.this.mView;
                    ClipboardManager clipBoard = (ClipboardManager) MessagePopupWindow.this.mContext.getSystemService("clipboard");
                    clipBoard.setPrimaryClip(ClipData.newPlainText(null, textView.getText()));
                    break;
                case R.id.popup_delete /* 2131493199 */:
                    if (MessagePopupWindow.this.mPopupMenuListener != null) {
                        MessagePopupWindow.this.mPopupMenuListener.onDeleteClick(MessagePopupWindow.this.mPosition);
                        break;
                    }
                    break;
                case R.id.popup_block /* 2131493200 */:
                    if (MessagePopupWindow.this.mPopupMenuListener != null) {
                        if (MessagePopupWindow.this.mPopupMenuListener.isUserInBlockList(MessagePopupWindow.this.mPosition)) {
                            MessagePopupWindow.this.mPopupMenuListener.onBlockClick(MessagePopupWindow.this.mPosition, false);
                            break;
                        } else {
                            MessagePopupWindow.this.mPopupMenuListener.onBlockClick(MessagePopupWindow.this.mPosition, true);
                            break;
                        }
                    }
                    break;
            }
            if (MessagePopupWindow.this.isShowing()) {
                MessagePopupWindow.this.dismiss();
            }
        }
    };

    public interface OnPopupMenuListener {
        int getMessageType(int i);

        boolean isUserInBlockList(int i);

        void onBlockClick(int i, boolean z);

        void onDeleteClick(int i);
    }

    public MessagePopupWindow(Context context, View view, OnPopupMenuListener listener) {
        this.mContext = context;
        this.mView = view;
        this.mPopupMenuListener = listener;
        this.mPosition = ((Integer) this.mView.getTag()).intValue();
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.gchat_popup_window_layout, (ViewGroup) null);
        setContentView(layout);
        TextView copy = (TextView) layout.findViewById(R.id.popup_copy);
        TextView delete = (TextView) layout.findViewById(R.id.popup_delete);
        TextView block = (TextView) layout.findViewById(R.id.popup_block);
        block.setOnClickListener(this.onPopupMenuClickListener);
        copy.setOnClickListener(this.onPopupMenuClickListener);
        delete.setOnClickListener(this.onPopupMenuClickListener);
        if (this.mPopupMenuListener != null) {
            if (this.mPopupMenuListener.getMessageType(this.mPosition) == 0) {
                block.setVisibility(0);
                if (this.mPopupMenuListener.isUserInBlockList(this.mPosition)) {
                    block.setText(this.mContext.getResources().getString(R.string.str_unblock));
                } else {
                    block.setText(this.mContext.getResources().getString(R.string.str_block));
                }
            } else {
                block.setVisibility(8);
            }
        }
        LinearLayout popupWindowView = (LinearLayout) layout.findViewById(R.id.gchat_popup_window);
        popupWindowView.measure(0, 0);
        setWidth(popupWindowView.getMeasuredWidth());
        setHeight(popupWindowView.getMeasuredHeight());
        setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.gchat_popwindow_bg));
        setOutsideTouchable(true);
        setFocusable(true);
    }

    public void show() {
        int[] location = new int[2];
        this.mView.getLocationOnScreen(location);
        int popupWidth = getWidth();
        int popupHeight = getHeight();
        showAtLocation(this.mView, 0, (location[0] + (this.mView.getWidth() / 2)) - (popupWidth / 2), location[1] - popupHeight);
    }
}
