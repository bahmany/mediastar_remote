package mktvsmart.screen.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class PasswordDialog extends Dialog {
    private EditText edit;
    private Button inputPswCancelBtn;
    private OnTextChangeListener listener;
    private TextView name;

    public interface OnTextChangeListener {
        void onTextChanged(CharSequence charSequence, int i, int i2, int i3);
    }

    public PasswordDialog(Context context) {
        super(context, R.style.dialog);
        setContentView(R.layout.input_passowrd_dialog);
        this.name = (TextView) findViewById(R.id.input_password_title);
        this.edit = (EditText) findViewById(R.id.input_password_edittext);
        this.inputPswCancelBtn = (Button) findViewById(R.id.input_psw_cancel_btn);
        initListener();
    }

    private void initListener() {
        this.edit.addTextChangedListener(new TextWatcher() { // from class: mktvsmart.screen.widget.PasswordDialog.1
            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (PasswordDialog.this.listener != null) {
                    PasswordDialog.this.listener.onTextChanged(s, start, before, count);
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
        this.inputPswCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.widget.PasswordDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PasswordDialog.this.dismiss();
            }
        });
        setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: mktvsmart.screen.widget.PasswordDialog.3
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                PasswordDialog.this.dismiss();
            }
        });
    }

    @Override // android.app.Dialog
    public void setTitle(int resId) {
        this.name.setText(resId);
    }

    public void setTitle(String text) {
        this.name.setText(text);
    }

    public OnTextChangeListener getOnTextChangeListener() {
        return this.listener;
    }

    public void setOnTextChangeListener(OnTextChangeListener listener) {
        this.listener = listener;
    }
}
