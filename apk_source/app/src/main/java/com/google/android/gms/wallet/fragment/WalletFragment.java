package com.google.android.gms.wallet.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
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
import com.google.android.gms.internal.oq;
import com.google.android.gms.internal.or;
import com.google.android.gms.internal.oy;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

/* loaded from: classes.dex */
public final class WalletFragment extends Fragment {
    private WalletFragmentOptions atJ;
    private WalletFragmentInitParams atK;
    private MaskedWalletRequest atL;
    private MaskedWallet atM;
    private Boolean atN;
    private b atS;
    private boolean mCreated = false;
    private final com.google.android.gms.dynamic.b atT = com.google.android.gms.dynamic.b.a(this);
    private final c atU = new c();
    private a atV = new a(this);
    private final Fragment Sb = this;

    public interface OnStateChangedListener {
        void onStateChanged(WalletFragment walletFragment, int i, int i2, Bundle bundle);
    }

    static class a extends or.a {
        private OnStateChangedListener atW;
        private final WalletFragment atX;

        a(WalletFragment walletFragment) {
            this.atX = walletFragment;
        }

        @Override // com.google.android.gms.internal.or
        public void a(int i, int i2, Bundle bundle) {
            if (this.atW != null) {
                this.atW.onStateChanged(this.atX, i, i2, bundle);
            }
        }

        public void a(OnStateChangedListener onStateChangedListener) {
            this.atW = onStateChangedListener;
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
            Button button = new Button(WalletFragment.this.Sb.getActivity());
            button.setText(R.string.wallet_buy_button_place_holder);
            int iA = -1;
            int iA2 = -2;
            if (WalletFragment.this.atJ != null && (fragmentStyle = WalletFragment.this.atJ.getFragmentStyle()) != null) {
                DisplayMetrics displayMetrics = WalletFragment.this.Sb.getResources().getDisplayMetrics();
                iA = fragmentStyle.a("buyButtonWidth", displayMetrics, -1);
                iA2 = fragmentStyle.a("buyButtonHeight", displayMetrics, -2);
            }
            button.setLayoutParams(new ViewGroup.LayoutParams(iA, iA2));
            button.setOnClickListener(this);
            frameLayout.addView(button);
        }

        @Override // com.google.android.gms.dynamic.a
        protected void a(f<b> fVar) throws PackageManager.NameNotFoundException {
            Activity activity = WalletFragment.this.Sb.getActivity();
            if (WalletFragment.this.atS == null && WalletFragment.this.mCreated && activity != null) {
                try {
                    oq oqVarA = oy.a(activity, WalletFragment.this.atT, WalletFragment.this.atJ, WalletFragment.this.atV);
                    WalletFragment.this.atS = new b(oqVarA);
                    WalletFragment.this.atJ = null;
                    fVar.a(WalletFragment.this.atS);
                    if (WalletFragment.this.atK != null) {
                        WalletFragment.this.atS.initialize(WalletFragment.this.atK);
                        WalletFragment.this.atK = null;
                    }
                    if (WalletFragment.this.atL != null) {
                        WalletFragment.this.atS.updateMaskedWalletRequest(WalletFragment.this.atL);
                        WalletFragment.this.atL = null;
                    }
                    if (WalletFragment.this.atM != null) {
                        WalletFragment.this.atS.updateMaskedWallet(WalletFragment.this.atM);
                        WalletFragment.this.atM = null;
                    }
                    if (WalletFragment.this.atN != null) {
                        WalletFragment.this.atS.setEnabled(WalletFragment.this.atN.booleanValue());
                        WalletFragment.this.atN = null;
                    }
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Activity activity = WalletFragment.this.Sb.getActivity();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity), activity, -1);
        }
    }

    public static WalletFragment newInstance(WalletFragmentOptions options) {
        WalletFragment walletFragment = new WalletFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extraWalletFragmentOptions", options);
        walletFragment.Sb.setArguments(bundle);
        return walletFragment;
    }

    public int getState() {
        if (this.atS != null) {
            return this.atS.getState();
        }
        return 0;
    }

    public void initialize(WalletFragmentInitParams initParams) {
        if (this.atS != null) {
            this.atS.initialize(initParams);
            this.atK = null;
        } else {
            if (this.atK != null) {
                Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
                return;
            }
            this.atK = initParams;
            if (this.atL != null) {
                Log.w("WalletFragment", "updateMaskedWalletRequest() was called before initialize()");
            }
            if (this.atM != null) {
                Log.w("WalletFragment", "updateMaskedWallet() was called before initialize()");
            }
        }
    }

    @Override // android.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.atS != null) {
            this.atS.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) throws Resources.NotFoundException {
        WalletFragmentOptions walletFragmentOptions;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(WalletFragmentOptions.class.getClassLoader());
            WalletFragmentInitParams walletFragmentInitParams = (WalletFragmentInitParams) savedInstanceState.getParcelable("walletFragmentInitParams");
            if (walletFragmentInitParams != null) {
                if (this.atK != null) {
                    Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
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
        } else if (this.Sb.getArguments() != null && (walletFragmentOptions = (WalletFragmentOptions) this.Sb.getArguments().getParcelable("extraWalletFragmentOptions")) != null) {
            walletFragmentOptions.Z(this.Sb.getActivity());
            this.atJ = walletFragmentOptions;
        }
        this.mCreated = true;
        this.atU.onCreate(savedInstanceState);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.atU.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mCreated = false;
    }

    @Override // android.app.Fragment
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        if (this.atJ == null) {
            this.atJ = WalletFragmentOptions.a(activity, attrs);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("attrKeyWalletFragmentOptions", this.atJ);
        this.atU.onInflate(activity, bundle, savedInstanceState);
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        this.atU.onPause();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.atU.onResume();
        FragmentManager fragmentManager = this.Sb.getActivity().getFragmentManager();
        Fragment fragmentFindFragmentByTag = fragmentManager.findFragmentByTag(GooglePlayServicesUtil.GMS_ERROR_DIALOG);
        if (fragmentFindFragmentByTag != null) {
            fragmentManager.beginTransaction().remove(fragmentFindFragmentByTag).commit();
            GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.Sb.getActivity()), this.Sb.getActivity(), -1);
        }
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.setClassLoader(WalletFragmentOptions.class.getClassLoader());
        this.atU.onSaveInstanceState(outState);
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

    @Override // android.app.Fragment
    public void onStart() {
        super.onStart();
        this.atU.onStart();
    }

    @Override // android.app.Fragment
    public void onStop() {
        super.onStop();
        this.atU.onStop();
    }

    public void setEnabled(boolean enabled) {
        if (this.atS == null) {
            this.atN = Boolean.valueOf(enabled);
        } else {
            this.atS.setEnabled(enabled);
            this.atN = null;
        }
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.atV.a(listener);
    }

    public void updateMaskedWallet(MaskedWallet maskedWallet) {
        if (this.atS == null) {
            this.atM = maskedWallet;
        } else {
            this.atS.updateMaskedWallet(maskedWallet);
            this.atM = null;
        }
    }

    public void updateMaskedWalletRequest(MaskedWalletRequest request) {
        if (this.atS == null) {
            this.atL = request;
        } else {
            this.atS.updateMaskedWalletRequest(request);
            this.atL = null;
        }
    }
}
