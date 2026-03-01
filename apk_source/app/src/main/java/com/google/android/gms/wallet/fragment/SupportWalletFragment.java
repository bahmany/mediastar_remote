package com.google.android.gms.wallet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import com.google.android.gms.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.dynamic.e;
import com.google.android.gms.dynamic.f;
import com.google.android.gms.dynamic.h;
import com.google.android.gms.internal.oq;
import com.google.android.gms.internal.or;
import com.google.android.gms.internal.oy;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

/* loaded from: classes.dex */
public final class SupportWalletFragment extends Fragment {
    private b atF;
    private WalletFragmentOptions atJ;
    private WalletFragmentInitParams atK;
    private MaskedWalletRequest atL;
    private MaskedWallet atM;
    private Boolean atN;
    private boolean mCreated = false;
    private final h atG = h.a(this);
    private final c atH = new c();
    private a atI = new a(this);
    private final Fragment Ll = this;

    public interface OnStateChangedListener {
        void onStateChanged(SupportWalletFragment supportWalletFragment, int i, int i2, Bundle bundle);
    }

    static class a extends or.a {
        private OnStateChangedListener atO;
        private final SupportWalletFragment atP;

        a(SupportWalletFragment supportWalletFragment) {
            this.atP = supportWalletFragment;
        }

        @Override // com.google.android.gms.internal.or
        public void a(int i, int i2, Bundle bundle) {
            if (this.atO != null) {
                this.atO.onStateChanged(this.atP, i, i2, bundle);
            }
        }

        public void a(OnStateChangedListener onStateChangedListener) {
            this.atO = onStateChangedListener;
        }
    }

    private static class b implements LifecycleDelegate {
        private final oq atQ;

        private b(oq oqVar) {
            this.atQ = oqVar;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getState() {
            try {
                return this.atQ.getState();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initialize(WalletFragmentInitParams startParams) {
            try {
                this.atQ.initialize(startParams);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            try {
                this.atQ.onActivityResult(requestCode, resultCode, data);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setEnabled(boolean enabled) {
            try {
                this.atQ.setEnabled(enabled);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateMaskedWallet(MaskedWallet maskedWallet) {
            try {
                this.atQ.updateMaskedWallet(maskedWallet);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateMaskedWalletRequest(MaskedWalletRequest request) {
            try {
                this.atQ.updateMaskedWalletRequest(request);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onCreate(Bundle savedInstanceState) {
            try {
                this.atQ.onCreate(savedInstanceState);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            try {
                return (View) e.f(this.atQ.onCreateView(e.k(inflater), e.k(container), savedInstanceState));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onDestroy() {
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onDestroyView() {
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onInflate(Activity activity, Bundle attrs, Bundle savedInstanceState) {
            try {
                this.atQ.a(e.k(activity), (WalletFragmentOptions) attrs.getParcelable("extraWalletFragmentOptions"), savedInstanceState);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onLowMemory() {
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onPause() {
            try {
                this.atQ.onPause();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onResume() {
            try {
                this.atQ.onResume();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onSaveInstanceState(Bundle outState) {
            try {
                this.atQ.onSaveInstanceState(outState);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onStart() {
            try {
                this.atQ.onStart();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onStop() {
            try {
                this.atQ.onStop();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class c extends com.google.android.gms.dynamic.a<b> implements View.OnClickListener {
        private c() {
        }

        @Override // com.google.android.gms.dynamic.a
        protected void a(FrameLayout frameLayout) {
            WalletFragmentStyle fragmentStyle;
            Button button = new Button(SupportWalletFragment.this.Ll.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            int iA = -1;
            int iA2 = -2;
            if (SupportWalletFragment.this.atJ != null && (fragmentStyle = SupportWalletFragment.this.atJ.getFragmentStyle()) != null) {
                DisplayMetrics displayMetrics = SupportWalletFragment.this.Ll.getResources().getDisplayMetrics();
                iA = fragmentStyle.a("buyButtonWidth", displayMetrics, -1);
                iA2 = fragmentStyle.a("buyButtonHeight", displayMetrics, -2);
            }
            button.setLayoutParams(new ViewGroup.LayoutParams(iA, iA2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        @Override // com.google.android.gms.dynamic.a
        protected void a(f<b> fVar) throws PackageManager.NameNotFoundException {
            FragmentActivity activity = SupportWalletFragment.this.Ll.getActivity();
            if (SupportWalletFragment.this.atF == null && SupportWalletFragment.this.mCreated && activity != null) {
                try {
                    oq oqVarA = oy.a(activity, SupportWalletFragment.this.atG, SupportWalletFragment.this.atJ, SupportWalletFragment.this.atI);
                    SupportWalletFragment.this.atF = new b(oqVarA);
                    SupportWalletFragment.this.atJ = null;
                    fVar.a(SupportWalletFragment.this.atF);
                    if (SupportWalletFragment.this.atK != null) {
                        SupportWalletFragment.this.atF.initialize(SupportWalletFragment.this.atK);
                        SupportWalletFragment.this.atK = null;
                    }
                    if (SupportWalletFragment.this.atL != null) {
                        SupportWalletFragment.this.atF.updateMaskedWalletRequest(SupportWalletFragment.this.atL);
                        SupportWalletFragment.this.atL = null;
                    }
                    if (SupportWalletFragment.this.atM != null) {
                        SupportWalletFragment.this.atF.updateMaskedWallet(SupportWalletFragment.this.atM);
                        SupportWalletFragment.this.atM = null;
                    }
                    if (SupportWalletFragment.this.atN != null) {
                        SupportWalletFragment.this.atF.setEnabled(SupportWalletFragment.this.atN.booleanValue());
                        SupportWalletFragment.this.atN = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            FragmentActivity activity = SupportWalletFragment.this.Ll.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }
    }

    public static SupportWalletFragment newInstance(WalletFragmentOptions options) {
        SupportWalletFragment supportWalletFragment = new SupportWalletFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extraWalletFragmentOptions", options);
        supportWalletFragment.Ll.setArguments(bundle);
        return supportWalletFragment;
    }

    public int getState() {
        if (this.atF != null) {
            return this.atF.getState();
        }
        return 0;
    }

    public void initialize(WalletFragmentInitParams initParams) {
        if (this.atF != null) {
            this.atF.initialize(initParams);
            this.atK = null;
        } else {
            if (this.atK != null) {
                Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
                return;
            }
            this.atK = initParams;
            if (this.atL != null) {
                Log.w("SupportWalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.atM != null) {
                Log.w("SupportWalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.atF != null) {
            this.atF.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) throws Resources.NotFoundException {
        WalletFragmentOptions walletFragmentOptions;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) savedInstanceState.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.atK != null) {
                    Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
                }
                this.atK = walletFragmentInitParams;
            }
            if (this.atL == null) {
                this.atL = (MaskedWalletRequest) savedInstanceState.getParcelable("maskedWalletRequest");
            }
            if (this.atM == null) {
                this.atM = (MaskedWallet) savedInstanceState.getParcelable("maskedWallet");
            }
            if (savedInstanceState.containsKey("walletFragmentOptions")) {
                this.atJ = (WalletFragmentOptions) savedInstanceState.getParcelable("walletFragmentOptions");
            }
            if (savedInstanceState.containsKey("enabled")) {
                this.atN = Boolean.valueOf(savedInstanceState.getBoolean("enabled"));
            }
        } else if (this.Ll.getArguments() != null && (walletFragmentOptions = (WalletFragmentOptions) this.Ll.getArguments().getParcelable("extraWalletFragmentOptions")) != null) {
            walletFragmentOptions.Z(this.Ll.getActivity());
            this.atJ = walletFragmentOptions;
        }
        this.mCreated = true;
        this.atH.onCreate(savedInstanceState);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.atH.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mCreated = false;
    }

    @Override // android.support.v4.app.Fragment
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        if (this.atJ == null) {
            this.atJ = WalletFragmentOptions.a(activity, attrs);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("attrKeyWalletFragmentOptions", this.atJ);
        this.atH.onInflate(activity, bundle, savedInstanceState);
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        this.atH.onPause();
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.atH.onResume();
        FragmentManager supportFragmentManager = this.Ll.getActivity().getSupportFragmentManager();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag(GooglePlayServicesUtil.GMS_ERROR_DIALOG);
        if (fragmentFindFragmentByTag != null) {
            supportFragmentManager.beginTransaction().remove(fragmentFindFragmentByTag).commit();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.Ll.getActivity()), this.Ll.getActivity(), -1);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.setClassLoader(WalletFragmentOptions.class.getClassLoader());
        this.atH.onSaveInstanceState(outState);
        if (this.atK != null) {
            outState.putParcelable("walletFragmentInitParams", this.atK);
            this.atK = null;
        }
        if (this.atL != null) {
            outState.putParcelable("maskedWalletRequest", this.atL);
            this.atL = null;
        }
        if (this.atM != null) {
            outState.putParcelable("maskedWallet", this.atM);
            this.atM = null;
        }
        if (this.atJ != null) {
            outState.putParcelable("walletFragmentOptions", this.atJ);
            this.atJ = null;
        }
        if (this.atN != null) {
            outState.putBoolean("enabled", this.atN.booleanValue());
            this.atN = null;
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        this.atH.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        this.atH.onStop();
    }

    public void setEnabled(boolean enabled) {
        if (this.atF == null) {
            this.atN = Boolean.valueOf(enabled);
        } else {
            this.atF.setEnabled(enabled);
            this.atN = null;
        }
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.atI.a(listener);
    }

    public void updateMaskedWallet(MaskedWallet maskedWallet) {
        if (this.atF == null) {
            this.atM = maskedWallet;
        } else {
            this.atF.updateMaskedWallet(maskedWallet);
            this.atM = null;
        }
    }

    public void updateMaskedWalletRequest(MaskedWalletRequest request) {
        if (this.atF == null) {
            this.atL = request;
        } else {
            this.atF.updateMaskedWalletRequest(request);
            this.atL = null;
        }
    }
}
