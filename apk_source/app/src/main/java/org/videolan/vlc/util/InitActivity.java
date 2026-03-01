package org.videolan.vlc.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

/* loaded from: classes.dex */
public class InitActivity extends Activity {
    public static final String FROM_ME = "fromVLCInitActivity";
    private ProgressDialog mPD;
    private UIHandler uiHandler;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        this.uiHandler = new UIHandler(this);
        new AsyncTask<Object, Object, Boolean>() { // from class: org.videolan.vlc.util.InitActivity.1
            AnonymousClass1() {
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                InitActivity.this.mPD = new ProgressDialog(InitActivity.this);
                InitActivity.this.mPD.setCancelable(false);
                InitActivity.this.mPD.setMessage("Initializing decoders…");
                InitActivity.this.mPD.show();
            }

            @Override // android.os.AsyncTask
            public Boolean doInBackground(Object... params) {
                VLCInstance.setApp(InitActivity.this.getApplication());
                int iResID = InitActivity.this.getResources().getIdentifier("libvlc", "raw", InitActivity.this.getPackageName());
                return Boolean.valueOf(VLCInstance.initialize(InitActivity.this, iResID));
            }

            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean inited) {
                if (inited.booleanValue()) {
                    InitActivity.this.uiHandler.sendEmptyMessage(0);
                } else {
                    InitActivity.this.uiHandler.sendEmptyMessage(1);
                }
            }
        }.execute(new Object[0]);
    }

    /* renamed from: org.videolan.vlc.util.InitActivity$1 */
    class AnonymousClass1 extends AsyncTask<Object, Object, Boolean> {
        AnonymousClass1() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            InitActivity.this.mPD = new ProgressDialog(InitActivity.this);
            InitActivity.this.mPD.setCancelable(false);
            InitActivity.this.mPD.setMessage("Initializing decoders…");
            InitActivity.this.mPD.show();
        }

        @Override // android.os.AsyncTask
        public Boolean doInBackground(Object... params) {
            VLCInstance.setApp(InitActivity.this.getApplication());
            int iResID = InitActivity.this.getResources().getIdentifier("libvlc", "raw", InitActivity.this.getPackageName());
            return Boolean.valueOf(VLCInstance.initialize(InitActivity.this, iResID));
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean inited) {
            if (inited.booleanValue()) {
                InitActivity.this.uiHandler.sendEmptyMessage(0);
            } else {
                InitActivity.this.uiHandler.sendEmptyMessage(1);
            }
        }
    }

    private static class UIHandler extends WeakHandler<InitActivity> {
        public UIHandler(InitActivity owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            InitActivity ctx = getOwner();
            switch (msg.what) {
                case 0:
                    ctx.mPD.dismiss();
                    Intent src = ctx.getIntent();
                    Intent i = new Intent();
                    i.setClassName(src.getStringExtra("package"), src.getStringExtra("className"));
                    i.setData(src.getData());
                    i.putExtras(src);
                    i.putExtra(InitActivity.FROM_ME, true);
                    ctx.startActivity(i);
                    ctx.finish();
                    break;
                case 1:
                    ctx.mPD.setMessage("Initialize failed");
                    sendEmptyMessageDelayed(2, 2000L);
                    break;
                case 2:
                    ctx.mPD.dismiss();
                    ctx.finish();
                    break;
            }
        }
    }
}
