package com.iflytek.cloud.a.d;

import android.content.Context;
import android.text.TextUtils;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.b.c;
import com.iflytek.msc.MSC;
import com.iflytek.msc.MSCSessionInfo;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class a {
    public static Object a = new Object();
    private MSCSessionInfo b = new MSCSessionInfo();

    public static void a(Context context, String str, String str2, com.iflytek.cloud.a.c.a aVar) throws SpeechError, IOException {
        byte[] bytes;
        synchronized (a) {
            String strA = c.a(context, aVar);
            if (!TextUtils.isEmpty(str)) {
                bytes = str.getBytes(XML.CHARSET_UTF8);
            } else if (context != null) {
                String strA2 = c.a(context);
                bytes = TextUtils.isEmpty(strA2) ? null : strA2.getBytes(aVar.n());
            } else {
                bytes = null;
            }
            int iQMSPLogin = MSC.QMSPLogin(bytes, TextUtils.isEmpty(str2) ? null : str2.getBytes(aVar.n()), strA.getBytes(aVar.n()));
            com.iflytek.cloud.a.f.a.a.a("[MSPLogin]ret:" + iQMSPLogin);
            if (iQMSPLogin != 0) {
                throw new SpeechError(iQMSPLogin);
            }
        }
    }

    public static boolean a() {
        boolean z;
        synchronized (a) {
            z = MSC.QMSPLogOut() == 0;
        }
        return z;
    }

    public synchronized byte[] a(Context context, com.iflytek.cloud.a.c.a aVar) throws SpeechError, UnsupportedEncodingException {
        byte[] bArrQMSPDownloadData;
        synchronized (a) {
            String strC = c.c(context, aVar);
            com.iflytek.cloud.a.f.a.a.a("[MSPSession downloadData]enter time:" + System.currentTimeMillis());
            bArrQMSPDownloadData = MSC.QMSPDownloadData(strC.getBytes(aVar.n()), this.b);
            com.iflytek.cloud.a.f.a.a.a("[MSPSession downloadData]leavel:" + this.b.errorcode + ",data len = " + (bArrQMSPDownloadData == null ? 0 : bArrQMSPDownloadData.length));
            int i = this.b.errorcode;
            if (i != 0 || bArrQMSPDownloadData == null) {
                throw new SpeechError(i);
            }
        }
        return bArrQMSPDownloadData;
    }

    public synchronized byte[] a(Context context, com.iflytek.cloud.a.c.a aVar, String str) throws SpeechError, UnsupportedEncodingException {
        byte[] bArrQMSPSearch;
        synchronized (a) {
            String strC = c.c(context, aVar);
            com.iflytek.cloud.a.f.a.a.a("[MSPSession searchResult]enter time:" + System.currentTimeMillis());
            bArrQMSPSearch = MSC.QMSPSearch(strC.getBytes(aVar.n()), str.getBytes(XML.CHARSET_UTF8), this.b);
            com.iflytek.cloud.a.f.a.a.a("[QMSPSearch searchResult]leavel:" + this.b.errorcode + ",data len = " + (bArrQMSPSearch == null ? 0 : bArrQMSPSearch.length));
            int i = this.b.errorcode;
            if (i != 0 || bArrQMSPSearch == null) {
                throw new SpeechError(i);
            }
        }
        return bArrQMSPSearch;
    }

    public synchronized byte[] a(Context context, String str, byte[] bArr, com.iflytek.cloud.a.c.a aVar) throws SpeechError, UnsupportedEncodingException {
        byte[] bArrQMSPUploadData;
        synchronized (a) {
            String strC = c.c(context, aVar);
            com.iflytek.cloud.a.f.a.a.a("[MSPSession uploadData]enter time:" + System.currentTimeMillis());
            bArrQMSPUploadData = MSC.QMSPUploadData(str.getBytes(aVar.n()), bArr, bArr.length, strC.getBytes(XML.CHARSET_UTF8), this.b);
            com.iflytek.cloud.a.f.a.a.a("[MSPSession uploaddData]leavel:" + this.b.errorcode + ",data len = " + (bArrQMSPUploadData == null ? 0 : bArrQMSPUploadData.length));
            int i = this.b.errorcode;
            if (i != 0 || bArrQMSPUploadData == null) {
                throw new SpeechError(i);
            }
        }
        return bArrQMSPUploadData;
    }
}
