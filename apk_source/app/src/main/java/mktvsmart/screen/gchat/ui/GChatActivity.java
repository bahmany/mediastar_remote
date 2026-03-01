package mktvsmart.screen.gchat.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import mktvsmart.screen.R;
import mktvsmart.screen.message.process.MessageProcessor;

/* loaded from: classes.dex */
public class GChatActivity extends FragmentActivity {
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_by_stb);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        MessageProcessor.obtain().removeProcessCallback(this);
    }
}
