package com.google.android.gms.analytics;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import com.google.android.gms.analytics.i;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
abstract class j<T extends i> {
    Context mContext;
    a<T> xV;

    public interface a<U extends i> {
        void c(String str, int i);

        void d(String str, boolean z);

        U dX();

        void f(String str, String str2);

        void g(String str, String str2);
    }

    public j(Context context, a<T> aVar) {
        this.mContext = context;
        this.xV = aVar;
    }

    private T a(XmlResourceParser xmlResourceParser) throws NumberFormatException {
        try {
            xmlResourceParser.next();
            int eventType = xmlResourceParser.getEventType();
            while (eventType != 1) {
                if (xmlResourceParser.getEventType() == 2) {
                    String lowerCase = xmlResourceParser.getName().toLowerCase();
                    if (lowerCase.equals("screenname")) {
                        String attributeValue = xmlResourceParser.getAttributeValue(null, "name");
                        String strTrim = xmlResourceParser.nextText().trim();
                        if (!TextUtils.isEmpty(attributeValue) && !TextUtils.isEmpty(strTrim)) {
                            this.xV.f(attributeValue, strTrim);
                        }
                    } else if (lowerCase.equals("string")) {
                        String attributeValue2 = xmlResourceParser.getAttributeValue(null, "name");
                        String strTrim2 = xmlResourceParser.nextText().trim();
                        if (!TextUtils.isEmpty(attributeValue2) && strTrim2 != null) {
                            this.xV.g(attributeValue2, strTrim2);
                        }
                    } else if (lowerCase.equals("bool")) {
                        String attributeValue3 = xmlResourceParser.getAttributeValue(null, "name");
                        String strTrim3 = xmlResourceParser.nextText().trim();
                        if (!TextUtils.isEmpty(attributeValue3) && !TextUtils.isEmpty(strTrim3)) {
                            try {
                                this.xV.d(attributeValue3, Boolean.parseBoolean(strTrim3));
                            } catch (NumberFormatException e) {
                                z.T("Error parsing bool configuration value: " + strTrim3);
                            }
                        }
                    } else if (lowerCase.equals("integer")) {
                        String attributeValue4 = xmlResourceParser.getAttributeValue(null, "name");
                        String strTrim4 = xmlResourceParser.nextText().trim();
                        if (!TextUtils.isEmpty(attributeValue4) && !TextUtils.isEmpty(strTrim4)) {
                            try {
                                this.xV.c(attributeValue4, Integer.parseInt(strTrim4));
                            } catch (NumberFormatException e2) {
                                z.T("Error parsing int configuration value: " + strTrim4);
                            }
                        }
                    }
                }
                eventType = xmlResourceParser.next();
            }
        } catch (IOException e3) {
            z.T("Error parsing tracker configuration file: " + e3);
        } catch (XmlPullParserException e4) {
            z.T("Error parsing tracker configuration file: " + e4);
        }
        return (T) this.xV.dX();
    }

    public T w(int i) {
        try {
            return (T) a(this.mContext.getResources().getXml(i));
        } catch (Resources.NotFoundException e) {
            z.W("inflate() called with unknown resourceId: " + e);
            return null;
        }
    }
}
