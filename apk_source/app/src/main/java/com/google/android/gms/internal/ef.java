package com.google.android.gms.internal;

import android.text.TextUtils;
import android.util.Base64;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@ez
/* loaded from: classes.dex */
public class ef {
    public static PublicKey F(String str) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(str, 0)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e2) {
            gs.T("Invalid key specification.");
            throw new IllegalArgumentException(e2);
        }
    }

    public static boolean a(PublicKey publicKey, String str, String str2) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(str.getBytes());
            if (signature.verify(Base64.decode(str2, 0))) {
                return true;
            }
            gs.T("Signature verification failed.");
            return false;
        } catch (InvalidKeyException e) {
            gs.T("Invalid key specification.");
            return false;
        } catch (NoSuchAlgorithmException e2) {
            gs.T("NoSuchAlgorithmException.");
            return false;
        } catch (SignatureException e3) {
            gs.T("Signature exception.");
            return false;
        }
    }

    public static boolean b(String str, String str2, String str3) {
        if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str) && !TextUtils.isEmpty(str3)) {
            return a(F(str), str2, str3);
        }
        gs.T("Purchase verification failed: missing data.");
        return false;
    }
}
