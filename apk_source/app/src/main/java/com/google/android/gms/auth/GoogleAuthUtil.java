package com.google.android.gms.auth;

import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.Cif;
import com.google.android.gms.internal.r;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/* loaded from: classes.dex */
public final class GoogleAuthUtil {
    public static final int CHANGE_TYPE_ACCOUNT_ADDED = 1;
    public static final int CHANGE_TYPE_ACCOUNT_REMOVED = 2;
    public static final int CHANGE_TYPE_ACCOUNT_RENAMED_FROM = 3;
    public static final int CHANGE_TYPE_ACCOUNT_RENAMED_TO = 4;
    private static final ComponentName Dn;
    private static final ComponentName Do;
    private static final Intent Dp;
    private static final Intent Dq;
    public static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    public static final String KEY_ANDROID_PACKAGE_NAME;
    public static final String KEY_CALLER_UID;
    public static final String KEY_REQUEST_ACTIONS = "request_visible_actions";

    @Deprecated
    public static final String KEY_REQUEST_VISIBLE_ACTIVITIES = "request_visible_actions";
    public static final String KEY_SUPPRESS_PROGRESS_SCREEN = "suppressProgressScreen";

    private static class a extends Handler {
        private final Context mD;

        a(Context context) {
            super(Looper.myLooper() == null ? Looper.getMainLooper() : Looper.myLooper());
            this.mD = context;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws Resources.NotFoundException, PackageManager.NameNotFoundException {
            if (msg.what != 1) {
                Log.wtf("GoogleAuthUtil", "Don't know how to handle this message: " + msg.what);
                return;
            }
            int iIsGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mD);
            if (GooglePlayServicesUtil.isUserRecoverableError(iIsGooglePlayServicesAvailable)) {
                GooglePlayServicesUtil.showErrorNotification(iIsGooglePlayServicesAvailable, this.mD);
            }
        }
    }

    static {
        KEY_CALLER_UID = Build.VERSION.SDK_INT >= 11 ? "callerUid" : "callerUid";
        KEY_ANDROID_PACKAGE_NAME = Build.VERSION.SDK_INT >= 14 ? "androidPackageName" : "androidPackageName";
        Dn = new ComponentName(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, "com.google.android.gms.auth.GetToken");
        Do = new ComponentName(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, "com.google.android.gms.recovery.RecoveryService");
        Dp = new Intent().setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE).setComponent(Dn);
        Dq = new Intent().setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE).setComponent(Do);
    }

    private GoogleAuthUtil() {
    }

    private static void D(Context context) throws PackageManager.NameNotFoundException, GoogleAuthException {
        try {
            GooglePlayServicesUtil.D(context);
        } catch (GooglePlayServicesNotAvailableException e) {
            throw new GoogleAuthException(e.getMessage());
        } catch (GooglePlayServicesRepairableException e2) {
            throw new GooglePlayServicesAvailabilityException(e2.getConnectionStatusCode(), e2.getMessage(), e2.getIntent());
        }
    }

    private static String a(Context context, String str, String str2, Bundle bundle) throws Resources.NotFoundException, IOException, GoogleAuthException {
        if (bundle == null) {
            bundle = new Bundle();
        }
        try {
            return getToken(context, str, str2, bundle);
        } catch (GooglePlayServicesAvailabilityException e) {
            int connectionStatusCode = e.getConnectionStatusCode();
            if (b(context, connectionStatusCode)) {
                a aVar = new a(context.getApplicationContext());
                aVar.sendMessageDelayed(aVar.obtainMessage(1), 30000L);
            } else {
                GooglePlayServicesUtil.showErrorNotification(connectionStatusCode, context);
            }
            throw new UserRecoverableNotifiedException("User intervention required. Notification has been pushed.");
        } catch (UserRecoverableAuthException e2) {
            throw new UserRecoverableNotifiedException("User intervention required. Notification has been pushed.");
        }
    }

    private static boolean aw(String str) {
        return "NetworkError".equals(str) || "ServiceUnavailable".equals(str) || "Timeout".equals(str);
    }

    private static boolean ax(String str) {
        return "BadAuthentication".equals(str) || "CaptchaRequired".equals(str) || "DeviceManagementRequiredOrSyncDisabled".equals(str) || "NeedPermission".equals(str) || "NeedsBrowser".equals(str) || "UserCancel".equals(str) || "AppDownloadRequired".equals(str) || Cif.DM_SYNC_DISABLED.fu().equals(str) || Cif.DM_ADMIN_BLOCKED.fu().equals(str) || Cif.DM_ADMIN_PENDING_APPROVAL.fu().equals(str) || Cif.DM_STALE_SYNC_REQUIRED.fu().equals(str) || Cif.DM_DEACTIVATED.fu().equals(str) || Cif.DM_REQUIRED.fu().equals(str);
    }

    private static boolean b(Context context, int i) {
        if (i == 1) {
            try {
                if (context.getPackageManager().getApplicationInfo(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, 8192).enabled) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return false;
    }

    public static void clearToken(Context context, String token) throws PackageManager.NameNotFoundException, GoogleAuthException, IOException {
        Context applicationContext = context.getApplicationContext();
        n.aU("Calling this from your main thread can lead to deadlock");
        D(applicationContext);
        Bundle bundle = new Bundle();
        String str = context.getApplicationInfo().packageName;
        bundle.putString("clientPackageName", str);
        if (!bundle.containsKey(KEY_ANDROID_PACKAGE_NAME)) {
            bundle.putString(KEY_ANDROID_PACKAGE_NAME, str);
        }
        com.google.android.gms.common.a aVar = new com.google.android.gms.common.a();
        try {
            if (!applicationContext.bindService(Dp, aVar, 1)) {
                throw new IOException("Could not bind to service with the given context.");
            }
            try {
                Bundle bundleA = r.a.a(aVar.fX()).a(token, bundle);
                String string = bundleA.getString(Cif.Ev);
                if (bundleA.getBoolean("booleanResult")) {
                } else {
                    throw new GoogleAuthException(string);
                }
            } catch (RemoteException e) {
                Log.i("GoogleAuthUtil", "GMS remote exception ", e);
                throw new IOException("remote exception");
            } catch (InterruptedException e2) {
                throw new GoogleAuthException("Interrupted");
            }
        } finally {
            applicationContext.unbindService(aVar);
        }
    }

    public static List<AccountChangeEvent> getAccountChangeEvents(Context ctx, int eventIndex, String accountName) throws PackageManager.NameNotFoundException, GoogleAuthException, IOException {
        n.b(accountName, (Object) "accountName must be provided");
        n.aU("Calling this from your main thread can lead to deadlock");
        Context applicationContext = ctx.getApplicationContext();
        D(applicationContext);
        com.google.android.gms.common.a aVar = new com.google.android.gms.common.a();
        try {
            if (!applicationContext.bindService(Dp, aVar, 1)) {
                throw new IOException("Could not bind to service with the given context.");
            }
            try {
                try {
                    return r.a.a(aVar.fX()).a(new AccountChangeEventsRequest().setAccountName(accountName).setEventIndex(eventIndex)).getEvents();
                } catch (RemoteException e) {
                    Log.i("GoogleAuthUtil", "GMS remote exception ", e);
                    throw new IOException("remote exception");
                }
            } catch (InterruptedException e2) {
                throw new GoogleAuthException("Interrupted");
            }
        } finally {
            applicationContext.unbindService(aVar);
        }
    }

    public static String getAccountId(Context ctx, String accountName) throws PackageManager.NameNotFoundException, GoogleAuthException, IOException {
        n.b(accountName, (Object) "accountName must be provided");
        n.aU("Calling this from your main thread can lead to deadlock");
        D(ctx.getApplicationContext());
        return getToken(ctx, accountName, "^^_account_id_^^", new Bundle());
    }

    public static String getAppCert(Context context, String packageNameToCertify) {
        return "spatula";
    }

    public static String getToken(Context context, String accountName, String scope) throws IOException, GoogleAuthException {
        return getToken(context, accountName, scope, new Bundle());
    }

    public static String getToken(Context context, String accountName, String scope, Bundle extras) throws PackageManager.NameNotFoundException, GoogleAuthException, IOException {
        Context applicationContext = context.getApplicationContext();
        n.aU("Calling this from your main thread can lead to deadlock");
        D(applicationContext);
        Bundle extras2 = extras == null ? new Bundle() : new Bundle(extras);
        String str = context.getApplicationInfo().packageName;
        extras2.putString("clientPackageName", str);
        if (!extras2.containsKey(KEY_ANDROID_PACKAGE_NAME)) {
            extras2.putString(KEY_ANDROID_PACKAGE_NAME, str);
        }
        com.google.android.gms.common.a aVar = new com.google.android.gms.common.a();
        try {
            if (!applicationContext.bindService(Dp, aVar, 1)) {
                throw new IOException("Could not bind to service with the given context.");
            }
            try {
                try {
                    Bundle bundleA = r.a.a(aVar.fX()).a(accountName, scope, extras2);
                    String string = bundleA.getString("authtoken");
                    if (!TextUtils.isEmpty(string)) {
                        return string;
                    }
                    String string2 = bundleA.getString("Error");
                    Intent intent = (Intent) bundleA.getParcelable("userRecoveryIntent");
                    if (ax(string2)) {
                        throw new UserRecoverableAuthException(string2, intent);
                    }
                    if (aw(string2)) {
                        throw new IOException(string2);
                    }
                    throw new GoogleAuthException(string2);
                } catch (RemoteException e) {
                    Log.i("GoogleAuthUtil", "GMS remote exception ", e);
                    throw new IOException("remote exception");
                }
            } catch (InterruptedException e2) {
                throw new GoogleAuthException("Interrupted");
            }
        } finally {
            applicationContext.unbindService(aVar);
        }
    }

    public static String getTokenWithNotification(Context context, String accountName, String scope, Bundle extras) throws IOException, GoogleAuthException {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putBoolean("handle_notification", true);
        return a(context, accountName, scope, extras);
    }

    public static String getTokenWithNotification(Context context, String accountName, String scope, Bundle extras, Intent callback) throws URISyntaxException, IOException, GoogleAuthException {
        h(callback);
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelable("callback_intent", callback);
        extras.putBoolean("handle_notification", true);
        return a(context, accountName, scope, extras);
    }

    public static String getTokenWithNotification(Context context, String accountName, String scope, Bundle extras, String authority, Bundle syncBundle) throws IOException, GoogleAuthException {
        if (TextUtils.isEmpty(authority)) {
            throw new IllegalArgumentException("Authority cannot be empty or null.");
        }
        if (extras == null) {
            extras = new Bundle();
        }
        if (syncBundle == null) {
            syncBundle = new Bundle();
        }
        ContentResolver.validateSyncExtrasBundle(syncBundle);
        extras.putString("authority", authority);
        extras.putBundle("sync_extras", syncBundle);
        extras.putBoolean("handle_notification", true);
        return a(context, accountName, scope, extras);
    }

    private static void h(Intent intent) throws URISyntaxException {
        if (intent == null) {
            throw new IllegalArgumentException("Callback cannot be null.");
        }
        try {
            Intent.parseUri(intent.toUri(1), 1);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Parameter callback contains invalid data. It must be serializable using toUri() and parseUri().");
        }
    }

    @Deprecated
    public static void invalidateToken(Context context, String token) {
        AccountManager.get(context).invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, token);
    }
}
