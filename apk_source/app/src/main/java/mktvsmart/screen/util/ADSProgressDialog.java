package mktvsmart.screen.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import java.text.NumberFormat;
import mktvsmart.screen.GMScreenApp;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class ADSProgressDialog extends AlertDialog {
    private static final int AUTO_DISMISS = 1;
    public static final int STYLE_HORIZONTAL = 1;
    public static final int STYLE_SPINNER = 0;
    private static final int UPDATE_VIEW = 0;
    private AdView adView;
    private FrameLayout mAdSpaceFrame;
    private Context mContext;
    private boolean mHasStarted;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private boolean mIndeterminate;
    private Drawable mIndeterminateDrawable;
    private int mMax;
    private CharSequence mMessage;
    private TextView mMessageView;
    private ProgressBar mProgress;
    private Drawable mProgressDrawable;
    private TextView mProgressNumber;
    private String mProgressNumberFormat;
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat;
    private int mProgressStyle;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private Handler mViewUpdateHandler;
    private static final String TAG = ADSProgressDialog.class.getSimpleName();
    private static AdView cacheAdView = initCacheAdView();

    public ADSProgressDialog(Context context) {
        super(context);
        this.mProgressStyle = 0;
        this.adView = null;
        this.mViewUpdateHandler = new InnerWeakHandler(this);
        this.mContext = context;
        initFormats();
        if (cacheAdView == null) {
            cacheAdView = initCacheAdView();
        }
        this.adView = cacheAdView;
        cacheAdView = initCacheAdView();
    }

    private static AdView initCacheAdView() {
        AdView cacheAdView2 = new AdView(GMScreenApp.getAppContext());
        cacheAdView2.setAdUnitId("ca-app-pub-4741798363812571/2199540641");
        cacheAdView2.setAdSize(AdSize.BANNER);
        cacheAdView2.setAdListener(new GsAdListenner("AD of ADSProgressDialog"));
        cacheAdView2.loadAd(new AdRequest.Builder().build());
        return cacheAdView2;
    }

    public ADSProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mProgressStyle = 0;
        this.adView = null;
        this.mViewUpdateHandler = new InnerWeakHandler(this);
        this.mContext = context;
        initFormats();
    }

    private void initFormats() {
        this.mProgressNumberFormat = "%1d/%2d";
        this.mProgressPercentFormat = NumberFormat.getPercentInstance();
        this.mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    @Override // android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        if (this.mProgressStyle == 1) {
            View view = inflater.inflate(R.layout.ads_alert_progress_dialog, (ViewGroup) null);
            this.mProgress = (ProgressBar) view.findViewById(R.id.progress);
            this.mProgressNumber = (TextView) view.findViewById(R.id.progress_number);
            this.mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
            this.mAdSpaceFrame = (FrameLayout) view.findViewById(R.id.ad_space);
            this.mAdSpaceFrame.addView(this.adView, -2, -2);
            setView(view);
        } else {
            View view2 = inflater.inflate(R.layout.ads_progress_layout, (ViewGroup) null);
            this.mProgress = (ProgressBar) view2.findViewById(R.id.progress);
            this.mMessageView = (TextView) view2.findViewById(R.id.message);
            this.mAdSpaceFrame = (FrameLayout) view2.findViewById(R.id.ad_space);
            this.mAdSpaceFrame.addView(this.adView, -2, -2);
            setView(view2);
        }
        if (this.mMax > 0) {
            setMax(this.mMax);
        }
        if (this.mProgressVal > 0) {
            setProgress(this.mProgressVal);
        }
        if (this.mSecondaryProgressVal > 0) {
            setSecondaryProgress(this.mSecondaryProgressVal);
        }
        if (this.mIncrementBy > 0) {
            incrementProgressBy(this.mIncrementBy);
        }
        if (this.mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(this.mIncrementSecondaryBy);
        }
        if (this.mProgressDrawable != null) {
            setProgressDrawable(this.mProgressDrawable);
        }
        if (this.mIndeterminateDrawable != null) {
            setIndeterminateDrawable(this.mIndeterminateDrawable);
        }
        if (this.mMessage != null) {
            setMessage(this.mMessage);
        }
        setIndeterminate(this.mIndeterminate);
        onProgressChanged();
        super.onCreate(savedInstanceState);
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        this.mHasStarted = true;
    }

    @Override // android.app.Dialog
    protected void onStop() {
        super.onStop();
        this.mHasStarted = false;
    }

    public void setProgress(int value) {
        if (this.mHasStarted) {
            this.mProgress.setProgress(value);
            onProgressChanged();
        } else {
            this.mProgressVal = value;
        }
    }

    public void setSecondaryProgress(int secondaryProgress) {
        if (this.mProgress != null) {
            this.mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            this.mSecondaryProgressVal = secondaryProgress;
        }
    }

    public int getProgress() {
        return this.mProgress != null ? this.mProgress.getProgress() : this.mProgressVal;
    }

    public int getSecondaryProgress() {
        return this.mProgress != null ? this.mProgress.getSecondaryProgress() : this.mSecondaryProgressVal;
    }

    public int getMax() {
        return this.mProgress != null ? this.mProgress.getMax() : this.mMax;
    }

    public void setMax(int max) {
        if (this.mProgress != null) {
            this.mProgress.setMax(max);
            onProgressChanged();
        } else {
            this.mMax = max;
        }
    }

    public void incrementProgressBy(int diff) {
        if (this.mProgress != null) {
            this.mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            this.mIncrementBy += diff;
        }
    }

    public void incrementSecondaryProgressBy(int diff) {
        if (this.mProgress != null) {
            this.mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            this.mIncrementSecondaryBy += diff;
        }
    }

    public void setProgressDrawable(Drawable d) {
        if (this.mProgress != null) {
            this.mProgress.setProgressDrawable(d);
        } else {
            this.mProgressDrawable = d;
        }
    }

    public void setIndeterminateDrawable(Drawable d) {
        if (this.mProgress != null) {
            this.mProgress.setIndeterminateDrawable(d);
        } else {
            this.mIndeterminateDrawable = d;
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        if (this.mProgress != null) {
            this.mProgress.setIndeterminate(indeterminate);
        } else {
            this.mIndeterminate = indeterminate;
        }
    }

    public boolean isIndeterminate() {
        return this.mProgress != null ? this.mProgress.isIndeterminate() : this.mIndeterminate;
    }

    @Override // android.app.AlertDialog
    public void setMessage(CharSequence message) {
        System.out.println("message = " + ((Object) message));
        if (this.mProgress != null) {
            if (this.mProgressStyle == 1) {
                super.setMessage(message);
                return;
            } else {
                this.mMessageView.setText(message);
                return;
            }
        }
        this.mMessage = message;
    }

    public void setProgressStyle(int style) {
        this.mProgressStyle = style;
    }

    public void setProgressNumberFormat(String format) {
        this.mProgressNumberFormat = format;
        onProgressChanged();
    }

    public void setProgressPercentFormat(NumberFormat format) {
        this.mProgressPercentFormat = format;
        onProgressChanged();
    }

    private void onProgressChanged() {
        if (this.mProgressStyle == 1 && this.mViewUpdateHandler != null && !this.mViewUpdateHandler.hasMessages(0)) {
            this.mViewUpdateHandler.sendEmptyMessage(0);
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        if (this.mViewUpdateHandler.hasMessages(1)) {
            this.mViewUpdateHandler.removeMessages(1);
        }
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
        Log.i("ADSProgressDialog", "show() @ " + this);
        if (this.mViewUpdateHandler.hasMessages(1)) {
            Log.i("ADSProgressDialog", "remove AUTO_DISMISS msg");
            this.mViewUpdateHandler.removeMessages(1);
        }
    }

    public void show(long lTimeOut, Runnable timeoutRun) {
        super.show();
        Log.i("ADSProgressDialog", "show(long lTimeOut, Runnable timeoutRun)");
        if (this.mViewUpdateHandler.hasMessages(1)) {
            Log.i("ADSProgressDialog", "remove AUTO_DISMISS msg");
            this.mViewUpdateHandler.removeMessages(1);
        }
        this.mViewUpdateHandler.sendMessageDelayed(this.mViewUpdateHandler.obtainMessage(1, timeoutRun), lTimeOut);
    }

    static class InnerWeakHandler extends WeakHandler<ADSProgressDialog> {
        public InnerWeakHandler(ADSProgressDialog owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            ADSProgressDialog owner = getOwner();
            if (owner != null) {
                switch (msg.what) {
                    case 0:
                        if (owner.mProgressStyle == 1) {
                            int progress = owner.mProgress.getProgress();
                            int max = owner.mProgress.getMax();
                            if (owner.mProgressNumberFormat == null) {
                                owner.mProgressNumber.setText("");
                            } else {
                                String format = owner.mProgressNumberFormat;
                                owner.mProgressNumber.setText(String.format(format, Integer.valueOf(progress), Integer.valueOf(max)));
                            }
                            if (owner.mProgressPercentFormat == null) {
                                owner.mProgressPercent.setText("");
                                break;
                            } else {
                                double percent = progress / max;
                                SpannableString tmp = new SpannableString(owner.mProgressPercentFormat.format(percent));
                                tmp.setSpan(new StyleSpan(1), 0, tmp.length(), 33);
                                owner.mProgressPercent.setText(tmp);
                                break;
                            }
                        }
                        break;
                    case 1:
                        if (owner.isShowing()) {
                            owner.dismiss();
                            Runnable run = (Runnable) msg.obj;
                            run.run();
                            break;
                        }
                        break;
                }
            }
        }
    }
}
