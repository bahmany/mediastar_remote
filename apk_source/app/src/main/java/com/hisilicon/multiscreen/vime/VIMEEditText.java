package com.hisilicon.multiscreen.vime;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/* loaded from: classes.dex */
public class VIMEEditText extends EditText {
    private EditTextWatcher mEtWatcher;

    public VIMEEditText(Context context) {
        super(context);
        this.mEtWatcher = null;
    }

    public VIMEEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEtWatcher = null;
    }

    public VIMEEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mEtWatcher = null;
    }

    public void addWatcher(EditTextWatcher watcher) {
        this.mEtWatcher = watcher;
    }

    @Override // android.widget.TextView
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (this.mEtWatcher != null) {
            this.mEtWatcher.onTextChanged(text, start, lengthBefore, lengthAfter);
        }
    }

    @Override // android.widget.TextView
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (this.mEtWatcher != null) {
            this.mEtWatcher.onSelectionChanged(selStart, selEnd);
        }
    }
}
