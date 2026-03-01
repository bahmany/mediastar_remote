package mktvsmart.screen.spectrum;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import com.alibaba.fastjson.asm.Opcodes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.R;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;

/* loaded from: classes.dex */
public class GsSpectrumActivity extends Activity {
    private static final int DISMISS_DIALOG_MSG = 0;
    private static final int GET_SETTINGS_INFO_MSG = 2;
    private static final int GET_SPEC_INFO_MSG = 3;
    private static final int HOLD = 0;
    private static final int MAX_CENT_VALUE = 2126;
    private static final int MAX_REF_VALUE = 100;
    private static final int MAX_SPAN_VALUE = 1200;
    private static final int MIN_CENT_VALUE = 974;
    private static final int MIN_REF_VALUE = 40;
    private static final int MIN_SPAN_VALUE = 48;
    private static final int MIN_YAXIS_VALUE = 0;
    private static final int OSD_LENGTH = 1024;
    private static final int SET_22K = 2;
    private static final int SET_DisEqC = 3;
    private static final int SET_VH = 1;
    private static final int SHOW_DIALOG_MSG = 1;
    private static final int SPE_FREQ_MAX = 2150;
    private static final int SPE_FREQ_MIN = 950;
    private static final String STATUS = "status";
    private static final String STATU_H = "H";
    private static final String STATU_LNB1 = "LNB1";
    private static final String STATU_LNB2 = "LNB2";
    private static final String STATU_LNB3 = "LNB3";
    private static final String STATU_LNB4 = "LNB4";
    private static final String STATU_OFF = "OFF";
    private static final String STATU_ON = "ON";
    private static final String STATU_V = "V";
    private static final float TEN = 10.0f;
    private static final String TITLE = "title";
    private DecimalFormat df;
    private TextView m22KStatusText;
    private Button mBackBtn;
    private int mCent;
    private Button mCentDownBtn;
    private TextView mCentText;
    private Button mCentUpBtn;
    private ChartVO mChartVo;
    private TextView mDisEqCStatusText;
    private TextView mDiscripText;
    private SlidingDrawer mDrawer;
    private double mDx;
    private int mEndFre;
    private TextView mHoldStatusText;
    private Button mImageView;
    private LineChart mLineChart;
    private LineData mLineData;
    private ListView mListView;
    private SetValuePop mMySetValuePop;
    private int mRef;
    private Button mRefDownBtn;
    private TextView mRefText;
    private Button mRefUpBtn;
    private int mSpan;
    private Button mSpanDownBtn;
    private TextView mSpanText;
    private Button mSpanUpBtn;
    private CurSpeInfoManager mSpeInfoManager;
    private SpeRequestManager mSpectrumRequestManager;
    private int mStartFre;
    private TextView mVHStatusText;
    private XAxis mXAxis;
    private YAxis mYLeftAxis;
    private YAxis mYRightAxis;
    private MessageProcessor msgProc;
    private ADSProgressDialog waitDialog;
    private String TAG = "GsSpectrumActivity";
    private int mDvbsSpeVH = 0;
    private int mDvbsSpe22k = 0;
    private int mDvbSpecDiseqc = 0;
    private int mSetRef = 0;
    private int mSetCent = 0;
    private int mSetSpan = 0;
    private boolean mHoldFlag = false;
    private int mShowCentDetail = 0;
    public Handler myHandler = new Handler() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) throws UnsupportedEncodingException {
            switch (msg.what) {
                case 0:
                    GsSpectrumActivity.this.dismissWaitDialog();
                    break;
                case 1:
                    GsSpectrumActivity.this.showWaitDialog(msg.arg1);
                    break;
                case 2:
                    GsSpectrumActivity.this.initData();
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.setListView();
                    break;
                case 3:
                    if (!GsSpectrumActivity.this.mHoldFlag) {
                        GsSpectrumActivity.this.mLineData = GsSpectrumActivity.this.getLineData();
                        GsSpectrumActivity.this.showChart(GsSpectrumActivity.this.mLineChart, GsSpectrumActivity.this.mLineData);
                        GsSpectrumActivity.this.showCentDetails();
                        GsSpectrumActivity.this.mSpectrumRequestManager.sendSpeInfoRequest(1024);
                        break;
                    }
                    break;
            }
        }
    };
    private SlidingDrawer.OnDrawerOpenListener mOnDrawerOpenListener = new SlidingDrawer.OnDrawerOpenListener() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.2
        @Override // android.widget.SlidingDrawer.OnDrawerOpenListener
        public void onDrawerOpened() {
            GsSpectrumActivity.this.mImageView.setBackgroundResource(R.drawable.pullin);
        }
    };
    private SlidingDrawer.OnDrawerCloseListener mOnDrawerCloseListener = new SlidingDrawer.OnDrawerCloseListener() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.3
        @Override // android.widget.SlidingDrawer.OnDrawerCloseListener
        public void onDrawerClosed() {
            GsSpectrumActivity.this.mImageView.setBackgroundResource(R.drawable.pullout);
        }
    };
    private OnChartValueSelectedListener mOnChartValueSelectedListener = new OnChartValueSelectedListener() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.4
        @Override // com.github.mikephil.charting.listener.OnChartValueSelectedListener
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
            GsSpectrumActivity.this.mShowCentDetail = e.getXIndex();
            GsSpectrumActivity.this.showCentDetails();
        }

        @Override // com.github.mikephil.charting.listener.OnChartValueSelectedListener
        public void onNothingSelected() {
        }
    };
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.5
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) throws UnsupportedEncodingException {
            switch (position) {
                case 0:
                    GsSpectrumActivity.this.mHoldStatusText = (TextView) view.findViewById(R.id.status);
                    if (GsSpectrumActivity.this.mHoldStatusText.getText().equals(GsSpectrumActivity.STATU_OFF)) {
                        GsSpectrumActivity.this.mHoldFlag = true;
                        GsSpectrumActivity.this.mHoldStatusText.setText(GsSpectrumActivity.STATU_ON);
                        break;
                    } else {
                        GsSpectrumActivity.this.mHoldFlag = false;
                        GsSpectrumActivity.this.mSpectrumRequestManager.sendSpeInfoRequest(1024);
                        GsSpectrumActivity.this.mHoldStatusText.setText(GsSpectrumActivity.STATU_OFF);
                        break;
                    }
                case 1:
                    GsSpectrumActivity.this.mVHStatusText = (TextView) view.findViewById(R.id.status);
                    if (GsSpectrumActivity.this.mVHStatusText.getText().equals(GsSpectrumActivity.STATU_H)) {
                        GsSpectrumActivity.this.mVHStatusText.setText(GsSpectrumActivity.STATU_V);
                        GsSpectrumActivity.this.mDvbsSpeVH = 1;
                    } else {
                        GsSpectrumActivity.this.mVHStatusText.setText(GsSpectrumActivity.STATU_H);
                        GsSpectrumActivity.this.mDvbsSpeVH = 0;
                    }
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeVHRequest(GsSpectrumActivity.this.mDvbsSpeVH);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_vh, 0));
                    break;
                case 2:
                    GsSpectrumActivity.this.m22KStatusText = (TextView) view.findViewById(R.id.status);
                    if (GsSpectrumActivity.this.m22KStatusText.getText().equals(GsSpectrumActivity.STATU_OFF)) {
                        GsSpectrumActivity.this.m22KStatusText.setText(GsSpectrumActivity.STATU_ON);
                        GsSpectrumActivity.this.mDvbsSpe22k = 1;
                    } else {
                        GsSpectrumActivity.this.m22KStatusText.setText(GsSpectrumActivity.STATU_OFF);
                        GsSpectrumActivity.this.mDvbsSpe22k = 0;
                    }
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpe22kRequset(GsSpectrumActivity.this.mDvbsSpe22k);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_22k, 0));
                    break;
                case 3:
                    GsSpectrumActivity.this.mDisEqCStatusText = (TextView) view.findViewById(R.id.status);
                    if (GsSpectrumActivity.this.mDisEqCStatusText.getText().equals(GsSpectrumActivity.STATU_LNB1)) {
                        GsSpectrumActivity.this.mDisEqCStatusText.setText(GsSpectrumActivity.STATU_LNB2);
                        GsSpectrumActivity.this.mDvbSpecDiseqc = 2;
                    } else if (GsSpectrumActivity.this.mDisEqCStatusText.getText().equals(GsSpectrumActivity.STATU_LNB2)) {
                        GsSpectrumActivity.this.mDisEqCStatusText.setText(GsSpectrumActivity.STATU_LNB3);
                        GsSpectrumActivity.this.mDvbSpecDiseqc = 3;
                    } else if (GsSpectrumActivity.this.mDisEqCStatusText.getText().equals(GsSpectrumActivity.STATU_LNB3)) {
                        GsSpectrumActivity.this.mDisEqCStatusText.setText(GsSpectrumActivity.STATU_LNB4);
                        GsSpectrumActivity.this.mDvbSpecDiseqc = 4;
                    } else if (GsSpectrumActivity.this.mDisEqCStatusText.getText().equals(GsSpectrumActivity.STATU_LNB4)) {
                        GsSpectrumActivity.this.mDisEqCStatusText.setText(GsSpectrumActivity.STATU_LNB1);
                        GsSpectrumActivity.this.mDvbSpecDiseqc = 1;
                    }
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeDiseqcRequest(GsSpectrumActivity.this.mDvbSpecDiseqc);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_diseqc, 0));
                    break;
            }
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.6
        @Override // android.view.View.OnClickListener
        public void onClick(View v) throws UnsupportedEncodingException {
            switch (v.getId()) {
                case R.id.back_btn /* 2131492959 */:
                    GsSpectrumActivity.this.finish();
                    break;
                case R.id.cent_up /* 2131492967 */:
                    GsSpectrumActivity.this.mCent = Integer.valueOf(GsSpectrumActivity.this.mCentText.getText().toString()).intValue();
                    GsSpectrumActivity.this.mCent++;
                    GsSpectrumActivity.this.adjustCurSpeSpan();
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(GsSpectrumActivity.this.mSpan, GsSpectrumActivity.this.mCent);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_span_and_cent_fre, 0));
                    break;
                case R.id.cent_text /* 2131492968 */:
                    if (!GsSpectrumActivity.this.mCentText.getText().toString().equals("")) {
                        GsSpectrumActivity.this.setPopWindow(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_cent), GsSpectrumActivity.MAX_CENT_VALUE, GsSpectrumActivity.MIN_CENT_VALUE, GsSpectrumActivity.this.mCent, v);
                        break;
                    }
                    break;
                case R.id.cent_down /* 2131492969 */:
                    GsSpectrumActivity.this.mCent = Integer.valueOf(GsSpectrumActivity.this.mCentText.getText().toString()).intValue();
                    GsSpectrumActivity gsSpectrumActivity = GsSpectrumActivity.this;
                    gsSpectrumActivity.mCent--;
                    GsSpectrumActivity.this.adjustCurSpeSpan();
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(GsSpectrumActivity.this.mSpan, GsSpectrumActivity.this.mCent);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_span_and_cent_fre, 0));
                    break;
                case R.id.span_up /* 2131492970 */:
                    GsSpectrumActivity.this.mSpan = Integer.valueOf(GsSpectrumActivity.this.mSpanText.getText().toString()).intValue();
                    GsSpectrumActivity.this.mSpan++;
                    GsSpectrumActivity.this.adjustCurCentFre();
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(GsSpectrumActivity.this.mSpan, GsSpectrumActivity.this.mCent);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_span_and_cent_fre, 0));
                    break;
                case R.id.span_text /* 2131492971 */:
                    if (!GsSpectrumActivity.this.mSpanText.getText().toString().equals("")) {
                        GsSpectrumActivity.this.setPopWindow(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_span), GsSpectrumActivity.MAX_SPAN_VALUE, 48, GsSpectrumActivity.this.mSpan, v);
                        break;
                    }
                    break;
                case R.id.span_down /* 2131492972 */:
                    GsSpectrumActivity.this.mSpan = Integer.valueOf(GsSpectrumActivity.this.mSpanText.getText().toString()).intValue();
                    GsSpectrumActivity gsSpectrumActivity2 = GsSpectrumActivity.this;
                    gsSpectrumActivity2.mSpan--;
                    GsSpectrumActivity.this.adjustCurCentFre();
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(GsSpectrumActivity.this.mSpan, GsSpectrumActivity.this.mCent);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_span_and_cent_fre, 0));
                    break;
                case R.id.ref_up /* 2131492973 */:
                    GsSpectrumActivity.this.mRef = Integer.valueOf(GsSpectrumActivity.this.mRefText.getText().toString()).intValue();
                    GsSpectrumActivity.this.mRef++;
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeRefRequest(GsSpectrumActivity.this.mRef);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_ref, 0));
                    break;
                case R.id.ref_text /* 2131492974 */:
                    if (!GsSpectrumActivity.this.mRefText.getText().toString().equals("")) {
                        GsSpectrumActivity.this.setPopWindow(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_ref), 100, 40, GsSpectrumActivity.this.mRef, v);
                        break;
                    }
                    break;
                case R.id.ref_down /* 2131492975 */:
                    GsSpectrumActivity.this.mRef = Integer.valueOf(GsSpectrumActivity.this.mRefText.getText().toString()).intValue();
                    GsSpectrumActivity gsSpectrumActivity3 = GsSpectrumActivity.this;
                    gsSpectrumActivity3.mRef--;
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeRefRequest(GsSpectrumActivity.this.mRef);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_ref, 0));
                    break;
            }
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws UnsupportedEncodingException {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_gmscreen_line_charts);
        setMessageProcess();
        this.mSpectrumRequestManager = SpeRequestManager.getInstance();
        this.mSpeInfoManager = CurSpeInfoManager.getInstance();
        this.mSpectrumRequestManager.sendSpeSettingRequest();
        this.mSpectrumRequestManager.sendSpeInfoRequest(1024);
        this.myHandler.sendMessage(this.myHandler.obtainMessage(1, R.string.loading_data, 0));
        initView();
        setListeners();
        initLineChart();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWaitDialog(int stringId) {
        this.waitDialog = DialogBuilder.showProgressDialog((Activity) this, stringId, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_load_data_fail);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissWaitDialog() {
        if (this.waitDialog.isShowing()) {
            this.waitDialog.dismiss();
        }
    }

    private void initLineChartData() {
        this.mYLeftAxis.setAxisMaxValue(this.mRef);
        this.mYRightAxis.setAxisMaxValue(this.mRef);
    }

    private void initLineChart() {
        XAxisValueFormatter customX = new MyXAxisValueFormatter();
        MyMarkerView mv = new MyMarkerView(this, R.layout.my_mark_view);
        this.mLineChart.setDrawBorders(true);
        this.mLineChart.setDescription("");
        this.mLineChart.setNoDataTextDescription("You need to provide data for the chart.");
        this.mLineChart.setDrawGridBackground(false);
        this.mLineChart.setTouchEnabled(true);
        this.mLineChart.setDragEnabled(true);
        this.mLineChart.setScaleEnabled(true);
        this.mLineChart.setPinchZoom(false);
        this.mLineChart.setHighlightPerTapEnabled(true);
        this.mLineChart.setMarkerView(mv);
        this.mLineChart.setOnChartValueSelectedListener(this.mOnChartValueSelectedListener);
        this.mXAxis = this.mLineChart.getXAxis();
        this.mYLeftAxis = this.mLineChart.getAxisLeft();
        this.mYRightAxis = this.mLineChart.getAxisRight();
        this.mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        this.mXAxis.setValueFormatter(customX);
        this.mXAxis.setSpaceBetweenLabels(3);
        this.mXAxis.setDrawGridLines(false);
        this.mXAxis.setDrawAxisLine(true);
        this.mYLeftAxis.setDrawGridLines(false);
        this.mYLeftAxis.setStartAtZero(false);
        this.mYLeftAxis.setAxisMinValue(0.0f);
        this.mYRightAxis.setDrawGridLines(false);
        this.mYRightAxis.setStartAtZero(false);
        this.mYRightAxis.setAxisMinValue(0.0f);
        Legend mLegend = this.mLineChart.getLegend();
        mLegend.setEnabled(false);
    }

    private void initView() {
        this.mLineChart = (LineChart) findViewById(R.id.spread_line_chart);
        this.mBackBtn = (Button) findViewById(R.id.back_btn);
        this.mCentUpBtn = (Button) findViewById(R.id.cent_up);
        this.mCentDownBtn = (Button) findViewById(R.id.cent_down);
        this.mSpanUpBtn = (Button) findViewById(R.id.span_up);
        this.mSpanDownBtn = (Button) findViewById(R.id.span_down);
        this.mRefUpBtn = (Button) findViewById(R.id.ref_up);
        this.mRefDownBtn = (Button) findViewById(R.id.ref_down);
        this.mCentText = (TextView) findViewById(R.id.cent_text);
        this.mSpanText = (TextView) findViewById(R.id.span_text);
        this.mRefText = (TextView) findViewById(R.id.ref_text);
        this.mDiscripText = (TextView) findViewById(R.id.descrip_tv);
        this.mDrawer = (SlidingDrawer) findViewById(R.id.slidingdraw);
        this.mImageView = (Button) findViewById(R.id.handle);
        this.mListView = (ListView) findViewById(R.id.content);
        this.mChartVo = new ChartVO();
        this.df = new DecimalFormat("######0.0");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTextViewText() {
        this.mCentText.setText(new StringBuilder(String.valueOf(this.mCent)).toString());
        this.mSpanText.setText(new StringBuilder(String.valueOf(this.mSpan)).toString());
        this.mRefText.setText(new StringBuilder(String.valueOf(this.mRef)).toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCentDetails() {
        if (this.mShowCentDetail != 0) {
            this.mDiscripText.setText("[Fre=" + this.df.format((this.mShowCentDetail * this.mDx) + this.mSpeInfoManager.getCurrentStartFre()) + "MHz,Strength=" + this.df.format(this.mSpeInfoManager.getCurrentRetDbuv()[this.mShowCentDetail] / TEN) + "dBuv]");
        } else {
            this.mDiscripText.setText("[Fre=" + this.df.format(this.mSpeInfoManager.getCurrentSpeCentFre()) + "MHz,Strength=" + this.df.format(this.mSpeInfoManager.getCurrentRetDbuv()[this.mSpeInfoManager.getCurrentRetDbuv().length / 2] / TEN) + "dBuv]");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initData() {
        this.mCent = this.mSpeInfoManager.getCurrentSpeCentFre();
        this.mSpan = this.mSpeInfoManager.getCurrentSpeSpan();
        this.mRef = this.mSpeInfoManager.getCurrentSpeRef();
        this.mDvbsSpeVH = this.mSpeInfoManager.getCurrentSpeVH();
        this.mDvbsSpe22k = this.mSpeInfoManager.getCurrentSpe22kOn();
        this.mDvbSpecDiseqc = this.mSpeInfoManager.getCurrentSpeDiseqc();
    }

    private void caculateParams() {
        this.mStartFre = this.mCent - (this.mSpan / 2);
        this.mEndFre = this.mCent + (this.mSpan / 2);
        this.mDx = this.mSpan / this.mSpeInfoManager.getCurrentRetDbuv().length;
    }

    private void setListeners() {
        this.mBackBtn.setOnClickListener(this.mOnClickListener);
        this.mCentUpBtn.setOnClickListener(this.mOnClickListener);
        this.mCentDownBtn.setOnClickListener(this.mOnClickListener);
        this.mSpanUpBtn.setOnClickListener(this.mOnClickListener);
        this.mSpanDownBtn.setOnClickListener(this.mOnClickListener);
        this.mRefUpBtn.setOnClickListener(this.mOnClickListener);
        this.mRefDownBtn.setOnClickListener(this.mOnClickListener);
        this.mCentText.setOnClickListener(this.mOnClickListener);
        this.mSpanText.setOnClickListener(this.mOnClickListener);
        this.mRefText.setOnClickListener(this.mOnClickListener);
        this.mListView.setOnItemClickListener(this.mOnItemClickListener);
        this.mDrawer.setOnDrawerOpenListener(this.mOnDrawerOpenListener);
        this.mDrawer.setOnDrawerCloseListener(this.mOnDrawerCloseListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPopWindow(final String str, int maxValue, int minValue, int currentValue, View v) {
        this.mSetRef = this.mRef;
        this.mSetCent = this.mCent;
        this.mSetSpan = this.mSpan;
        this.mChartVo.setmMaxValue(maxValue);
        this.mChartVo.setmMinValue(minValue);
        this.mChartVo.setmCurrentValue(currentValue);
        this.mMySetValuePop = new SetValuePop(this, str, this.mChartVo);
        this.mMySetValuePop.showPopupWindow(v);
        this.mMySetValuePop.setOnSettingListener(new SettingListener() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.7
            @Override // mktvsmart.screen.spectrum.SettingListener
            public void onSetting(String name, int value) {
                if (name.equals(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_cent))) {
                    GsSpectrumActivity.this.mSetCent = value;
                } else if (!name.equals(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_span))) {
                    GsSpectrumActivity.this.mSetRef = value;
                } else {
                    GsSpectrumActivity.this.mSetSpan = value;
                }
            }
        });
        this.mMySetValuePop.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.8
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() throws UnsupportedEncodingException {
                if (str.equals(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_ref)) && GsSpectrumActivity.this.mSetRef != 0 && GsSpectrumActivity.this.mSetRef != GsSpectrumActivity.this.mRef) {
                    GsSpectrumActivity.this.mRef = GsSpectrumActivity.this.mSetRef;
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeRefRequest(GsSpectrumActivity.this.mRef);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_ref, 0));
                    return;
                }
                if (str.equals(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_span)) && GsSpectrumActivity.this.mSetSpan != 0 && GsSpectrumActivity.this.mSetSpan != GsSpectrumActivity.this.mSpan) {
                    GsSpectrumActivity.this.mSpan = GsSpectrumActivity.this.mSetSpan;
                    GsSpectrumActivity.this.adjustCurCentFre();
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(GsSpectrumActivity.this.mSpan, GsSpectrumActivity.this.mCent);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_span_and_cent_fre, 0));
                    return;
                }
                if (str.equals(GsSpectrumActivity.this.getResources().getString(R.string.spectrum_cent)) && GsSpectrumActivity.this.mSetCent != 0 && GsSpectrumActivity.this.mSetCent != GsSpectrumActivity.this.mCent) {
                    GsSpectrumActivity.this.mCent = GsSpectrumActivity.this.mSetCent;
                    GsSpectrumActivity.this.adjustCurSpeSpan();
                    GsSpectrumActivity.this.setTextViewText();
                    GsSpectrumActivity.this.mSpectrumRequestManager.sendSetSpeSpanAndCentFreRequest(GsSpectrumActivity.this.mSpan, GsSpectrumActivity.this.mCent);
                    GsSpectrumActivity.this.myHandler.sendMessage(GsSpectrumActivity.this.myHandler.obtainMessage(1, R.string.set_span_and_cent_fre, 0));
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustCurSpeSpan() {
        if (this.mSpan > Math.min(2150 - this.mCent, this.mCent - 950) * 2) {
            this.mSpan = Math.min(2150 - this.mCent, this.mCent - 950) * 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustCurCentFre() {
        if (this.mCent < (this.mSpan / 2) + SPE_FREQ_MIN) {
            this.mCent = (this.mSpan / 2) + SPE_FREQ_MIN;
        } else if (this.mCent > 2150 - (this.mSpan / 2)) {
            this.mCent = 2150 - (this.mSpan / 2);
        }
    }

    private String getDvbSpecDiseqC(int i) {
        switch (i) {
            case 1:
                return STATU_LNB1;
            case 2:
                return STATU_LNB2;
            case 3:
                return STATU_LNB3;
            case 4:
                return STATU_LNB4;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setListView() {
        String statu_vh = this.mDvbsSpeVH == 0 ? STATU_H : STATU_V;
        String statu_22k = this.mDvbsSpe22k == 0 ? STATU_OFF : STATU_ON;
        Object statu_diseqc = getDvbSpecDiseqC(this.mDvbSpecDiseqc);
        Object[] statues = {STATU_OFF, statu_vh, statu_22k, statu_diseqc};
        Object[] titles = {getResources().getString(R.string.hold), getResources().getString(R.string.set_vh), getResources().getString(R.string.set_22k), getResources().getString(R.string.set_diseqc)};
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("title", titles[i]);
            map.put(STATUS, statues[i]);
            list.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.ordered_item, new String[]{"title", STATUS}, new int[]{R.id.title, R.id.status});
        this.mListView.setAdapter((ListAdapter) adapter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showChart(LineChart lineChart, LineData lineData) {
        initLineChartData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LineData getLineData() {
        this.mCent = Integer.valueOf(this.mCentText.getText().toString()).intValue();
        this.mSpan = Integer.valueOf(this.mSpanText.getText().toString()).intValue();
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();
        caculateParams();
        for (int i = 0; i < this.mSpeInfoManager.getCurrentRetDbuv().length; i++) {
            xVals.add(new StringBuilder(String.valueOf(this.mStartFre + (i * this.mDx))).toString());
        }
        for (int i2 = 0; i2 < this.mSpeInfoManager.getCurrentRetDbuv().length; i2++) {
            yVals.add(new Entry(this.mSpeInfoManager.getCurrentRetDbuv()[i2] / TEN, i2));
        }
        LineDataSet set = new LineDataSet(yVals, " ");
        set.setDrawCubic(true);
        set.setCubicIntensity(0.8f);
        set.setDrawFilled(true);
        set.setFillColor(-16776961);
        set.setFillAlpha(Opcodes.FCMPG);
        set.setLineWidth(1.8f);
        set.setDrawCircles(false);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setHighLightColor(ViewCompat.MEASURED_STATE_MASK);
        set.setValueTextSize(TEN);
        LineData data = new LineData(xVals, set);
        data.setValueTextSize(9.0f);
        data.setDrawValues(false);
        return data;
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.recycle();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_REQUEST_SPECTRUM_INFO, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.9
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                GsSpectrumActivity.this.mSpeInfoManager.setCurrentSpectrumInfo(recvData);
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(0);
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(3);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_REF, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.10
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(0);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_22K, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.11
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(0);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_VH, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.12
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(0);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_SPAN_AND_CENT_FRE, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.13
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(0);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_DISEQC, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.14
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(0);
            }
        });
        this.msgProc.setOnMessageProcess(302, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.spectrum.GsSpectrumActivity.15
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                GsSpectrumActivity.this.mSpeInfoManager.setCurrentSpectrumSetting(recvData);
                GsSpectrumActivity.this.myHandler.sendEmptyMessage(2);
            }
        });
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        dismissWaitDialog();
        this.msgProc.recycle();
        this.msgProc.removeProcessCallback(null);
    }
}
