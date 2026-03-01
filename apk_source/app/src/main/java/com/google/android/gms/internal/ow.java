package com.google.android.gms.internal;

import android.app.PendingIntent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.Payments;
import com.google.android.gms.wallet.Wallet;

/* loaded from: classes.dex */
public class ow implements Payments {

    /* renamed from: com.google.android.gms.internal.ow$1 */
    class AnonymousClass1 extends Wallet.b {
        final /* synthetic */ int ady;

        AnonymousClass1(int i) {
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ox oxVar) throws PendingIntent.CanceledException {
            oxVar.fH(i);
            b((AnonymousClass1) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.ow$2 */
    class AnonymousClass2 extends Wallet.b {
        final /* synthetic */ int ady;
        final /* synthetic */ MaskedWalletRequest aug;

        AnonymousClass2(MaskedWalletRequest maskedWalletRequest, int i) {
            maskedWalletRequest = maskedWalletRequest;
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ox oxVar) throws PendingIntent.CanceledException {
            oxVar.a(maskedWalletRequest, i);
            b((AnonymousClass2) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.ow$3 */
    class AnonymousClass3 extends Wallet.b {
        final /* synthetic */ int ady;
        final /* synthetic */ FullWalletRequest auh;

        AnonymousClass3(FullWalletRequest fullWalletRequest, int i) {
            fullWalletRequest = fullWalletRequest;
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ox oxVar) throws PendingIntent.CanceledException {
            oxVar.a(fullWalletRequest, i);
            b((AnonymousClass3) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.ow$4 */
    class AnonymousClass4 extends Wallet.b {
        final /* synthetic */ int ady;
        final /* synthetic */ String aui;
        final /* synthetic */ String auj;

        AnonymousClass4(String str, String str2, int i) {
            str = str;
            str = str2;
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ox oxVar) throws PendingIntent.CanceledException {
            oxVar.d(str, str, i);
            b((AnonymousClass4) Status.Jo);
        }
    }

    /* renamed from: com.google.android.gms.internal.ow$5 */
    class AnonymousClass5 extends Wallet.b {
        final /* synthetic */ NotifyTransactionStatusRequest auk;

        AnonymousClass5(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
            notifyTransactionStatusRequest = notifyTransactionStatusRequest;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(ox oxVar) {
            oxVar.a(notifyTransactionStatusRequest);
            b((AnonymousClass5) Status.Jo);
        }
    }

    @Override // com.google.android.gms.wallet.Payments
    public void changeMaskedWallet(GoogleApiClient googleApiClient, String googleTransactionId, String merchantTransactionId, int requestCode) {
        googleApiClient.a((GoogleApiClient) new Wallet.b() { // from class: com.google.android.gms.internal.ow.4
            final /* synthetic */ int ady;
            final /* synthetic */ String aui;
            final /* synthetic */ String auj;

            AnonymousClass4(String googleTransactionId2, String merchantTransactionId2, int requestCode2) {
                str = googleTransactionId2;
                str = merchantTransactionId2;
                i = requestCode2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ox oxVar) throws PendingIntent.CanceledException {
                oxVar.d(str, str, i);
                b((AnonymousClass4) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.wallet.Payments
    public void checkForPreAuthorization(GoogleApiClient googleApiClient, int requestCode) {
        googleApiClient.a((GoogleApiClient) new Wallet.b() { // from class: com.google.android.gms.internal.ow.1
            final /* synthetic */ int ady;

            AnonymousClass1(int requestCode2) {
                i = requestCode2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ox oxVar) throws PendingIntent.CanceledException {
                oxVar.fH(i);
                b((AnonymousClass1) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.wallet.Payments
    public void loadFullWallet(GoogleApiClient googleApiClient, FullWalletRequest request, int requestCode) {
        googleApiClient.a((GoogleApiClient) new Wallet.b() { // from class: com.google.android.gms.internal.ow.3
            final /* synthetic */ int ady;
            final /* synthetic */ FullWalletRequest auh;

            AnonymousClass3(FullWalletRequest request2, int requestCode2) {
                fullWalletRequest = request2;
                i = requestCode2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ox oxVar) throws PendingIntent.CanceledException {
                oxVar.a(fullWalletRequest, i);
                b((AnonymousClass3) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.wallet.Payments
    public void loadMaskedWallet(GoogleApiClient googleApiClient, MaskedWalletRequest request, int requestCode) {
        googleApiClient.a((GoogleApiClient) new Wallet.b() { // from class: com.google.android.gms.internal.ow.2
            final /* synthetic */ int ady;
            final /* synthetic */ MaskedWalletRequest aug;

            AnonymousClass2(MaskedWalletRequest request2, int requestCode2) {
                maskedWalletRequest = request2;
                i = requestCode2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ox oxVar) throws PendingIntent.CanceledException {
                oxVar.a(maskedWalletRequest, i);
                b((AnonymousClass2) Status.Jo);
            }
        });
    }

    @Override // com.google.android.gms.wallet.Payments
    public void notifyTransactionStatus(GoogleApiClient googleApiClient, NotifyTransactionStatusRequest request) {
        googleApiClient.a((GoogleApiClient) new Wallet.b() { // from class: com.google.android.gms.internal.ow.5
            final /* synthetic */ NotifyTransactionStatusRequest auk;

            AnonymousClass5(NotifyTransactionStatusRequest request2) {
                notifyTransactionStatusRequest = request2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(ox oxVar) {
                oxVar.a(notifyTransactionStatusRequest);
                b((AnonymousClass5) Status.Jo);
            }
        });
    }
}
