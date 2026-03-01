package com.google.android.gms.dynamic;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.LifecycleDelegate;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes.dex */
public abstract class a<T extends LifecycleDelegate> {
    private T RP;
    private Bundle RQ;
    private LinkedList<InterfaceC0029a> RR;
    private final f<T> RS = (f<T>) new f<T>() { // from class: com.google.android.gms.dynamic.a.1
        @Override // com.google.android.gms.dynamic.f
        public void a(T t) {
            a.this.RP = t;
            Iterator it = a.this.RR.iterator();
            while (it.hasNext()) {
                ((InterfaceC0029a) it.next()).b(a.this.RP);
            }
            a.this.RR.clear();
            a.this.RQ = null;
        }
    };

    /* renamed from: com.google.android.gms.dynamic.a$a, reason: collision with other inner class name */
    private interface InterfaceC0029a {
        void b(LifecycleDelegate lifecycleDelegate);

        int getState();
    }

    private void a(Bundle bundle, InterfaceC0029a interfaceC0029a) {
        if (this.RP != null) {
            interfaceC0029a.b(this.RP);
            return;
        }
        if (this.RR == null) {
            this.RR = new LinkedList<>();
        }
        this.RR.add(interfaceC0029a);
        if (bundle != null) {
            if (this.RQ == null) {
                this.RQ = (Bundle) bundle.clone();
            } else {
                this.RQ.putAll(bundle);
            }
        }
        a(this.RS);
    }

    public static void b(FrameLayout frameLayout) throws PackageManager.NameNotFoundException {
        final Context context = frameLayout.getContext();
        final int iIsGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        String strD = GooglePlayServicesUtil.d(context, iIsGooglePlayServicesAvailable);
        String strE = GooglePlayServicesUtil.e(context, iIsGooglePlayServicesAvailable);
        LinearLayout linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        TextView textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        textView.setText(strD);
        linearLayout.addView(textView);
        if (strE != null) {
            Button button = new Button(context);
            button.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
            button.setText(strE);
            linearLayout.addView(button);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.gms.dynamic.a.5
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    context.startActivity(GooglePlayServicesUtil.c(context, iIsGooglePlayServicesAvailable));
                }
            });
        }
    }

    private void cv(int i) {
        while (!this.RR.isEmpty() && this.RR.getLast().getState() >= i) {
            this.RR.removeLast();
        }
    }

    protected void a(FrameLayout frameLayout) throws PackageManager.NameNotFoundException {
        b(frameLayout);
    }

    protected abstract void a(f<T> fVar);

    public T it() {
        return this.RP;
    }

    public void onCreate(final Bundle savedInstanceState) {
        a(savedInstanceState, new InterfaceC0029a() { // from class: com.google.android.gms.dynamic.a.3
            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public void b(LifecycleDelegate lifecycleDelegate) {
                a.this.RP.onCreate(savedInstanceState);
            }

            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public int getState() {
                return 1;
            }
        });
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) throws PackageManager.NameNotFoundException {
        final FrameLayout frameLayout = new FrameLayout(inflater.getContext());
        a(savedInstanceState, new InterfaceC0029a() { // from class: com.google.android.gms.dynamic.a.4
            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public void b(LifecycleDelegate lifecycleDelegate) {
                frameLayout.removeAllViews();
                frameLayout.addView(a.this.RP.onCreateView(inflater, container, savedInstanceState));
            }

            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public int getState() {
                return 2;
            }
        });
        if (this.RP == null) {
            a(frameLayout);
        }
        return frameLayout;
    }

    public void onDestroy() {
        if (this.RP != null) {
            this.RP.onDestroy();
        } else {
            cv(1);
        }
    }

    public void onDestroyView() {
        if (this.RP != null) {
            this.RP.onDestroyView();
        } else {
            cv(2);
        }
    }

    public void onInflate(final Activity activity, final Bundle attrs, final Bundle savedInstanceState) {
        a(savedInstanceState, new InterfaceC0029a() { // from class: com.google.android.gms.dynamic.a.2
            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public void b(LifecycleDelegate lifecycleDelegate) {
                a.this.RP.onInflate(activity, attrs, savedInstanceState);
            }

            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public int getState() {
                return 0;
            }
        });
    }

    public void onLowMemory() {
        if (this.RP != null) {
            this.RP.onLowMemory();
        }
    }

    public void onPause() {
        if (this.RP != null) {
            this.RP.onPause();
        } else {
            cv(5);
        }
    }

    public void onResume() {
        a((Bundle) null, new InterfaceC0029a() { // from class: com.google.android.gms.dynamic.a.7
            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public void b(LifecycleDelegate lifecycleDelegate) {
                a.this.RP.onResume();
            }

            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public int getState() {
                return 5;
            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        if (this.RP != null) {
            this.RP.onSaveInstanceState(outState);
        } else if (this.RQ != null) {
            outState.putAll(this.RQ);
        }
    }

    public void onStart() {
        a((Bundle) null, new InterfaceC0029a() { // from class: com.google.android.gms.dynamic.a.6
            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public void b(LifecycleDelegate lifecycleDelegate) {
                a.this.RP.onStart();
            }

            @Override // com.google.android.gms.dynamic.a.InterfaceC0029a
            public int getState() {
                return 4;
            }
        });
    }

    public void onStop() {
        if (this.RP != null) {
            this.RP.onStop();
        } else {
            cv(4);
        }
    }
}
