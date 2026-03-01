package mktvsmart.screen.util;

import android.app.Instrumentation;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import mktvsmart.screen.GMScreenApp;
import org.cybergarage.soap.SOAP;

/* loaded from: classes.dex */
public class AdsControllor {
    private static final int HIDE_COUNT_DOWN_TIME = 1115;
    private static final int HIDE_INTERSTITAL_ADS = 1112;
    private static final int LOAD_INTERSTITAL_ADS = 1113;
    private static final int SHOW_COUNT_DOWN_TIME = 1114;
    private static final int SHOW_INTERSTITAL_ADS = 1111;
    private static final String TAG = AdsControllor.class.getSimpleName();
    private static AdsControllor instance = null;
    private AdCoverWindow mAdCoverView;
    private AdStatus mStatus = AdStatus.CLOSE;
    private int dulTime = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper(), new InnerCallback(this, null));
    private InterstitialAd mInterstitial = new InterstitialAd(GMScreenApp.getAppContext());

    public enum AdStatus {
        OPEN,
        CLOSE,
        LEFT_APP;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static AdStatus[] valuesCustom() {
            AdStatus[] adStatusArrValuesCustom = values();
            int length = adStatusArrValuesCustom.length;
            AdStatus[] adStatusArr = new AdStatus[length];
            System.arraycopy(adStatusArrValuesCustom, 0, adStatusArr, 0, length);
            return adStatusArr;
        }
    }

    public boolean isOpen() {
        return this.mStatus != AdStatus.CLOSE;
    }

    public AdStatus getmStatus() {
        return this.mStatus;
    }

    private AdsControllor() {
        this.mInterstitial.setAdUnitId("ca-app-pub-4741798363812571/6769341049");
        this.mInterstitial.setAdListener(new AdListener() { // from class: mktvsmart.screen.util.AdsControllor.1
            @Override // com.google.android.gms.ads.AdListener
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(AdsControllor.TAG, "onAdLoaded");
            }

            @Override // com.google.android.gms.ads.AdListener
            public void onAdFailedToLoad(int errorCode) {
                Log.e(AdsControllor.TAG, "onAdFailedToLoad errorCode : " + errorCode);
                super.onAdFailedToLoad(errorCode);
            }

            @Override // com.google.android.gms.ads.AdListener
            public void onAdOpened() {
                Log.d(AdsControllor.TAG, "onAdOpened");
                super.onAdOpened();
                AdsControllor.this.mStatus = AdStatus.OPEN;
                if (AdsControllor.this.dulTime >= 0) {
                    AdsControllor.this.mHandler.sendMessage(AdsControllor.this.mHandler.obtainMessage(AdsControllor.SHOW_COUNT_DOWN_TIME, Integer.valueOf(AdsControllor.this.dulTime)));
                }
            }

            @Override // com.google.android.gms.ads.AdListener
            public void onAdClosed() {
                Log.d(AdsControllor.TAG, "onAdClosed");
                super.onAdClosed();
                AdsControllor.this.mHandler.removeMessages(AdsControllor.SHOW_COUNT_DOWN_TIME);
                AdsControllor.this.mHandler.sendEmptyMessage(AdsControllor.HIDE_COUNT_DOWN_TIME);
                AdsControllor.this.mHandler.sendEmptyMessageDelayed(AdsControllor.LOAD_INTERSTITAL_ADS, 1000L);
                AdsControllor.this.mStatus = AdStatus.CLOSE;
                AdsControllor.this.dulTime = -1;
            }

            @Override // com.google.android.gms.ads.AdListener
            public void onAdLeftApplication() {
                Log.d(AdsControllor.TAG, "onAdLeftApplication");
                super.onAdLeftApplication();
                AdsControllor.this.mStatus = AdStatus.LEFT_APP;
                AdsControllor.this.mHandler.removeMessages(AdsControllor.SHOW_COUNT_DOWN_TIME);
                AdsControllor.this.mHandler.sendEmptyMessage(AdsControllor.HIDE_COUNT_DOWN_TIME);
                AdsControllor.this.mHandler.sendEmptyMessageDelayed(AdsControllor.HIDE_INTERSTITAL_ADS, 500L);
            }
        });
        this.mInterstitial.loadAd(new AdRequest.Builder().build());
        Log.d(TAG, "AdRequest.Builder().build()");
        this.mAdCoverView = new AdCoverWindow(GMScreenApp.getAppContext());
    }

    public static synchronized AdsControllor obtain() {
        if (instance == null) {
            instance = new AdsControllor();
        }
        return instance;
    }

    public void showInterstitialAd() {
        showInterstitialAd(30);
    }

    public void showInterstitialAd(int durTime) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(SHOW_INTERSTITAL_ADS, Integer.valueOf(durTime)));
    }

    public void hideInterstitialAd() {
        if (this.mStatus != AdStatus.CLOSE) {
            this.mHandler.sendEmptyMessageDelayed(HIDE_INTERSTITAL_ADS, 200L);
        }
    }

    public boolean isAdLoaded() {
        return this.mInterstitial.isLoaded();
    }

    private class InnerCallback implements Handler.Callback {
        private InnerCallback() {
        }

        /* synthetic */ InnerCallback(AdsControllor adsControllor, InnerCallback innerCallback) {
            this();
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AdsControllor.SHOW_INTERSTITAL_ADS /* 1111 */:
                    if (!AdsControllor.this.mInterstitial.isLoaded()) {
                        Log.d(AdsControllor.TAG, "InterstitialAd not loaded");
                        AdsControllor.this.mInterstitial.loadAd(new AdRequest.Builder().build());
                        break;
                    } else {
                        AdsControllor.this.mInterstitial.show();
                        if (msg.obj != null) {
                            AdsControllor.this.dulTime = ((Integer) msg.obj).intValue();
                            break;
                        }
                    }
                    break;
                case AdsControllor.HIDE_INTERSTITAL_ADS /* 1112 */:
                    Log.d(AdsControllor.TAG, "receive HIDE_INTERSTITAL_ADS");
                    if (AdsControllor.this.mStatus != AdStatus.CLOSE) {
                        Log.d(AdsControllor.TAG, "make a KEYCODE_BACK event");
                        AdsControllor.simulateKeystroke(4);
                        break;
                    }
                    break;
                case AdsControllor.LOAD_INTERSTITAL_ADS /* 1113 */:
                    AdsControllor.this.mInterstitial.loadAd(new AdRequest.Builder().build());
                    break;
                case AdsControllor.SHOW_COUNT_DOWN_TIME /* 1114 */:
                    if (!AdsControllor.this.mAdCoverView.isShowing()) {
                        AdsControllor.this.mAdCoverView.show();
                    }
                    AdsControllor.this.mAdCoverView.setMessage("Please wait " + AdsControllor.this.dulTime + SOAP.XMLNS);
                    if (AdsControllor.this.dulTime == 0) {
                        AdsControllor.this.mHandler.sendEmptyMessage(AdsControllor.HIDE_INTERSTITAL_ADS);
                        break;
                    } else {
                        Handler handler = AdsControllor.this.mHandler;
                        Handler handler2 = AdsControllor.this.mHandler;
                        AdsControllor adsControllor = AdsControllor.this;
                        int i = adsControllor.dulTime;
                        adsControllor.dulTime = i - 1;
                        handler.sendMessageDelayed(handler2.obtainMessage(AdsControllor.SHOW_COUNT_DOWN_TIME, Integer.valueOf(i)), 1000L);
                        break;
                    }
                case AdsControllor.HIDE_COUNT_DOWN_TIME /* 1115 */:
                    if (AdsControllor.this.mAdCoverView.isShowing()) {
                        AdsControllor.this.mAdCoverView.dismiss();
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void simulateKeystroke(final int KeyCode) {
        new Thread(new Runnable() { // from class: mktvsmart.screen.util.AdsControllor.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
