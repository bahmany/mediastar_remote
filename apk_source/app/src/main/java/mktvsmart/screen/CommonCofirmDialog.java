package mktvsmart.screen;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/* loaded from: classes.dex */
public class CommonCofirmDialog extends Dialog {
    private TextView mContentText;
    private Context mContext;
    private Button mNoBtn;
    private View.OnClickListener mNoClickListener;
    private OnButtonClickListener mOnButtonClickListener;
    private TextView mTitleText;
    private Button mYesBtn;
    private View.OnClickListener mYesClickListener;

    public interface OnButtonClickListener {
        void onClickedCancel();

        void onClickedConfirm();
    }

    public CommonCofirmDialog(Context mContext) {
        super(mContext, R.style.dialog);
        this.mYesClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.CommonCofirmDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CommonCofirmDialog.this.mOnButtonClickListener.onClickedConfirm();
                CommonCofirmDialog.this.dismiss();
            }
        };
        this.mNoClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.CommonCofirmDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CommonCofirmDialog.this.mOnButtonClickListener.onClickedCancel();
                CommonCofirmDialog.this.dismiss();
            }
        };
        this.mContext = mContext;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View layout = inflater.inflate(R.layout.confirm_dialog, (ViewGroup) null);
        this.mTitleText = (TextView) layout.findViewById(R.id.confirm_dialog_title);
        this.mContentText = (TextView) layout.findViewById(R.id.confirm_dialog_txt);
        this.mYesBtn = (Button) layout.findViewById(R.id.confirm_dialog_yes_btn);
        this.mNoBtn = (Button) layout.findViewById(R.id.confirm_dialog_no_btn);
        this.mYesBtn.setOnClickListener(this.mYesClickListener);
        this.mNoBtn.setOnClickListener(this.mNoClickListener);
        setContentView(layout);
        setCanceledOnTouchOutside(false);
    }

    public void setmTitle(String title) {
        this.mTitleText.setText(title);
    }

    public void setmContent(String content) {
        this.mContentText.setText(content);
    }

    public void setOnButtonClickListener(OnButtonClickListener l) {
        this.mOnButtonClickListener = l;
    }
}
