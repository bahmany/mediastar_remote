package mktvsmart.screen.spectrum;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import com.alibaba.fastjson.asm.Opcodes;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class SetValuePop extends PopupWindow {
    private static final int BOTTOM_POSITION = 80;
    private static final int LEFT_POSITION = 0;
    private static String TAG = "POPActivity";
    private static final int TOP_POSITION = 20;
    private View conentView;
    private ViewGroup.LayoutParams layoutParams;
    private Button mAddBtn;
    private ChartVO mChartVO;
    private Context mContext;
    private int mCurrentProgress;
    private int mCurrentValue;
    private TextView mMaxTV;
    private TextView mMinTV;
    private String mName;
    private SeekBar mSeekBar;
    private int mSeekBarLength;
    private Button mSubstactBtn;
    private TextView mTextView;
    private int screenWidth;
    private TextView text;
    private TextMoveLayout textMoveLayout;
    private SettingListener mSListener = null;
    private float moveStep = 0.0f;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.spectrum.SetValuePop.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (v.getId() == R.id.add_btn && SetValuePop.this.mCurrentValue < SetValuePop.this.mChartVO.getmMaxValue()) {
                SetValuePop.this.mCurrentValue++;
                SetValuePop.this.mCurrentProgress = SetValuePop.this.mCurrentValue - SetValuePop.this.mChartVO.getmMinValue();
                SetValuePop.this.text.layout((int) (SetValuePop.this.mCurrentProgress * SetValuePop.this.moveStep), 20, SetValuePop.this.screenWidth, 80);
                SetValuePop.this.text.setText(new StringBuilder(String.valueOf(SetValuePop.this.mCurrentValue)).toString());
                SetValuePop.this.mSeekBar.setProgress(SetValuePop.this.mCurrentProgress);
                SetValuePop.this.setText(SetValuePop.this.mName, SetValuePop.this.mCurrentValue);
                return;
            }
            if (v.getId() == R.id.subtract_btn && SetValuePop.this.mCurrentValue > SetValuePop.this.mChartVO.getmMinValue()) {
                SetValuePop setValuePop = SetValuePop.this;
                setValuePop.mCurrentValue--;
                SetValuePop.this.mCurrentProgress = SetValuePop.this.mCurrentValue - SetValuePop.this.mChartVO.getmMinValue();
                SetValuePop.this.text.layout((int) (SetValuePop.this.mCurrentProgress * SetValuePop.this.moveStep), 20, SetValuePop.this.screenWidth, 80);
                SetValuePop.this.text.setText(new StringBuilder(String.valueOf(SetValuePop.this.mCurrentValue)).toString());
                SetValuePop.this.mSeekBar.setProgress(SetValuePop.this.mCurrentProgress);
                SetValuePop.this.setText(SetValuePop.this.mName, SetValuePop.this.mCurrentValue);
            }
        }
    };
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: mktvsmart.screen.spectrum.SetValuePop.2
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            SetValuePop.this.text.layout((int) (progress * SetValuePop.this.moveStep), 20, SetValuePop.this.screenWidth, 80);
            SetValuePop.this.text.setText(new StringBuilder(String.valueOf(SetValuePop.this.mChartVO.getmMinValue() + progress)).toString());
            SetValuePop.this.setText(SetValuePop.this.mName, SetValuePop.this.mChartVO.getmMinValue() + progress);
        }
    };

    public SetValuePop(Activity context, String string, ChartVO chartVO) {
        this.mContext = context;
        this.mChartVO = chartVO;
        this.mName = string;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.conentView = inflater.inflate(R.layout.modify_value_pop, (ViewGroup) null);
        context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        setContentView(this.conentView);
        this.screenWidth = w;
        initView();
        initData();
        initSeekBar();
        setOnClickListener();
        setWidth((w / 2) + 50);
        setHeight(-2);
        setFocusable(true);
        setOutsideTouchable(true);
        update();
        ColorDrawable dw = new ColorDrawable(R.color.white);
        setBackgroundDrawable(dw);
        setAnimationStyle(R.style.AnimationFade);
        setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: mktvsmart.screen.spectrum.SetValuePop.3
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
            }
        });
    }

    private void initData() {
        this.mCurrentValue = this.mChartVO.getmCurrentValue();
        this.mTextView.setText(this.mName);
        this.mMinTV.setText(new StringBuilder(String.valueOf(this.mChartVO.getmMinValue())).toString());
        this.mMaxTV.setText(new StringBuilder(String.valueOf(this.mChartVO.getmMaxValue())).toString());
        this.mSeekBarLength = this.mChartVO.getmMaxValue() - this.mChartVO.getmMinValue();
        this.mCurrentProgress = this.mCurrentValue - this.mChartVO.getmMinValue();
    }

    public void initSeekBar() {
        this.text = new TextView(this.mContext);
        this.text.setBackgroundColor(Color.rgb(KeyInfo.KEYCODE_X, KeyInfo.KEYCODE_X, KeyInfo.KEYCODE_X));
        this.text.setTextColor(Color.rgb(0, Opcodes.IF_ICMPLT, 229));
        this.text.setTextSize(16.0f);
        this.layoutParams = new ViewGroup.LayoutParams(this.screenWidth, 50);
        this.textMoveLayout.addView(this.text, this.layoutParams);
        this.text.layout(0, 20, this.screenWidth, 80);
        this.mSeekBar.setEnabled(true);
        this.mSeekBar.setMax(this.mSeekBarLength);
        this.mSeekBar.setProgress(this.mCurrentProgress);
        this.moveStep = (float) ((this.screenWidth / this.mSeekBarLength) * 0.35d);
        this.text.layout((int) (this.mCurrentProgress * this.moveStep), 20, this.screenWidth, 80);
        this.text.setText(new StringBuilder(String.valueOf(this.mCurrentValue)).toString());
    }

    private void initView() {
        this.mTextView = (TextView) this.conentView.findViewById(R.id.text_view);
        this.mSeekBar = (SeekBar) this.conentView.findViewById(R.id.progresss);
        this.mMinTV = (TextView) this.conentView.findViewById(R.id.min_text_view);
        this.mMaxTV = (TextView) this.conentView.findViewById(R.id.max_text_view);
        this.mAddBtn = (Button) this.conentView.findViewById(R.id.add_btn);
        this.mSubstactBtn = (Button) this.conentView.findViewById(R.id.subtract_btn);
        this.textMoveLayout = (TextMoveLayout) this.conentView.findViewById(R.id.show_seekbar);
    }

    private void setOnClickListener() {
        this.mSeekBar.setOnSeekBarChangeListener(this.mOnSeekBarChangeListener);
        this.mAddBtn.setOnClickListener(this.mOnClickListener);
        this.mSubstactBtn.setOnClickListener(this.mOnClickListener);
    }

    public void showPopupWindow(View parent) {
        if (!isShowing()) {
            showAtLocation(parent, 17, 0, -200);
        } else {
            dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setText(String str, int value) {
        if (this.mSListener != null) {
            this.mSListener.onSetting(str, value);
        }
    }

    public void setOnSettingListener(SettingListener listener) {
        this.mSListener = listener;
    }
}
