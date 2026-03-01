package com.iflytek.cloud.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.resource.Resource;
import com.iflytek.cloud.ui.a.e;
import com.iflytek.cloud.ui.a.f;

/* loaded from: classes.dex */
public final class a extends e implements View.OnClickListener {
    public static int a = 9;
    private LinearLayout d;
    private f e;
    private RotateAnimation f;
    private SpeechRecognizer g;
    private RecognizerDialogListener h;
    private long i;
    private RecognizerListener j;
    private volatile int k;

    /* renamed from: com.iflytek.cloud.ui.a$a, reason: collision with other inner class name */
    public class C0149a extends ClickableSpan {
        private String b;

        public C0149a(String str) {
            this.b = str;
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View view) {
            try {
                Context context = view.getContext();
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(this.b));
                intent.addFlags(268435456);
                intent.putExtra("com.android.browser.application_id", context.getPackageName());
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
        }
    }

    public a(Context context, InitListener initListener) {
        super(context.getApplicationContext());
        this.e = null;
        this.f = null;
        this.i = 0L;
        this.j = new b(this);
        this.g = SpeechRecognizer.createRecognizer(context.getApplicationContext(), initListener);
        a();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(SpeechError speechError) {
        try {
            LinearLayout linearLayout = (LinearLayout) this.d.findViewWithTag("error");
            a((TextView) linearLayout.findViewWithTag("errtxt"), speechError);
            linearLayout.findViewWithTag("errview").setBackgroundDrawable(com.iflytek.cloud.ui.a.a.a(getContext(), "warning"));
            setTag(speechError);
            this.k = 3;
            k();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void g() {
        com.iflytek.cloud.a.f.a.a.a("startRecognizing");
        long j = this.i;
        this.i = SystemClock.elapsedRealtime();
        if (this.i - j < 300) {
            return;
        }
        int iStartListening = this.g.startListening(this.j);
        if (iStartListening != 0) {
            a(new SpeechError(iStartListening));
        } else {
            i();
        }
    }

    private void h() {
        if (this.d != null) {
            this.d.destroyDrawingCache();
            this.d = null;
        }
        this.e = null;
        System.gc();
    }

    private void i() {
        if (this.e == null) {
            this.e = new f(getContext().getApplicationContext());
        }
        this.k = 1;
        k();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j() {
        try {
            ((FrameLayout) this.d.findViewWithTag("waiting")).findViewWithTag("control").startAnimation(this.f);
            this.k = 2;
            k();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void k() {
        FrameLayout frameLayout = (FrameLayout) this.d.findViewWithTag("waiting");
        TextView textView = (TextView) this.d.findViewWithTag("title");
        LinearLayout linearLayout = (LinearLayout) this.d.findViewWithTag("error");
        TextView textView2 = (TextView) frameLayout.findViewWithTag("tips");
        if (this.k == 1) {
            linearLayout.setVisibility(8);
            textView.setVisibility(0);
            frameLayout.setVisibility(8);
            textView.setText(Resource.getTitle(2));
            this.e.a(0);
            this.e.invalidate();
            this.e.setVisibility(0);
            return;
        }
        if (this.k == 2) {
            textView.setVisibility(8);
            this.e.setVisibility(8);
            frameLayout.setVisibility(0);
            textView2.setVisibility(0);
            textView2.setText(Resource.getTitle(3));
            return;
        }
        if (this.k == 3) {
            textView.setVisibility(8);
            this.e.setVisibility(8);
            frameLayout.setVisibility(8);
            linearLayout.setVisibility(0);
        }
    }

    public void a() {
        try {
            Context applicationContext = getContext().getApplicationContext();
            View viewA = com.iflytek.cloud.ui.a.a.a(applicationContext, "recognize", this);
            viewA.setBackgroundDrawable(com.iflytek.cloud.ui.a.a.a(applicationContext.getApplicationContext(), "voice_bg.9"));
            this.d = (LinearLayout) viewA.findViewWithTag("container");
            com.iflytek.cloud.a.f.f.a(this);
            this.e = new f(applicationContext.getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
            layoutParams.bottomMargin = 20;
            this.d.addView(this.e, 1, layoutParams);
            ((FrameLayout) this.d.findViewWithTag("waiting")).findViewWithTag("control").setBackgroundDrawable(com.iflytek.cloud.ui.a.a.a(getContext(), "waiting"));
            this.f = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
            this.f.setRepeatCount(-1);
            this.f.setInterpolator(new LinearInterpolator());
            this.f.setDuration(700L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void a(TextView textView, SpeechError speechError) {
        textView.setText(Html.fromHtml(speechError.getHtmlDescription(true)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.bringToFront();
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int length = text.length();
            Spannable spannable = (Spannable) textView.getText();
            URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, length, URLSpan.class);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            spannableStringBuilder.clearSpans();
            for (URLSpan uRLSpan : uRLSpanArr) {
                spannableStringBuilder.setSpan(new C0149a(uRLSpan.getURL()), spannable.getSpanStart(uRLSpan), spannable.getSpanEnd(uRLSpan), 34);
            }
            int length2 = speechError.getHtmlDescription(false).length();
            int length3 = speechError.getHtmlDescription(true).length() - "<br>".length();
            spannableStringBuilder.setSpan(new ForegroundColorSpan(com.iflytek.cloud.ui.a.a.a()[0]), 0, length2, 34);
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(com.iflytek.cloud.ui.a.a.b()[0], true), 0, length2, 33);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(com.iflytek.cloud.ui.a.a.a()[1]), length2 + 1, length3 + 1, 34);
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(com.iflytek.cloud.ui.a.a.b()[1], true), length2 + 1, length, 34);
            textView.setText(spannableStringBuilder);
        }
    }

    public void a(RecognizerDialogListener recognizerDialogListener) {
        this.h = recognizerDialogListener;
        setOnClickListener(this);
    }

    public void a(String str, String str2) {
        this.g.setParameter(str, str2);
    }

    @Override // com.iflytek.cloud.ui.a.e
    public void b() {
        super.b();
        g();
    }

    @Override // com.iflytek.cloud.ui.a.e
    public void c() {
        this.g.cancel();
        super.c();
    }

    @Override // com.iflytek.cloud.ui.a.e
    protected boolean d() {
        if (!super.d()) {
            return false;
        }
        h();
        return this.g.destroy();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (this.k) {
            case 1:
                this.g.stopListening();
                j();
                break;
            case 3:
                if (view.getTag() != null && ((SpeechError) view.getTag()).getErrorCode() == 20001) {
                    e();
                    break;
                } else {
                    g();
                    break;
                }
                break;
        }
    }
}
