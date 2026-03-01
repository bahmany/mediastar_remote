package mktvsmart.screen;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/* loaded from: classes.dex */
public class CommonErrorDialog extends Dialog {
    private TextView mContentText;
    private Context mContext;
    private OnButtonClickListener mOnButtonClickListener;
    private Button mYesBtn;
    private View.OnClickListener mYesClickListener;

    public interface OnButtonClickListener {
        void onClickedConfirm();
    }

    public CommonErrorDialog(Context context) {
        super(context, R.style.dialog);
        this.mYesClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.CommonErrorDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (CommonErrorDialog.this.mOnButtonClickListener != null) {
                    CommonErrorDialog.this.mOnButtonClickListener.onClickedConfirm();
                }
                CommonErrorDialog.this.dismiss();
            }
        };
        this.mContext = context;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View layout = inflater.inflate(R.layout.message_dialog, (ViewGroup) null);
        this.mContentText = (TextView) layout.findViewById(R.id.message_txt);
        this.mYesBtn = (Button) layout.findViewById(R.id.message_btn);
        this.mYesBtn.setOnClickListener(this.mYesClickListener);
        setContentView(layout);
        setCanceledOnTouchOutside(true);
    }

    public void setmContent(String content) {
        this.mContentText.setText(content);
    }

    public void setOnButtonClickListener(OnButtonClickListener l) {
        this.mOnButtonClickListener = l;
    }
}
