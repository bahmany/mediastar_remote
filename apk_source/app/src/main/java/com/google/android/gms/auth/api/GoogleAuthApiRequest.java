package com.google.android.gms.auth.api;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class GoogleAuthApiRequest implements SafeParcelable {
    public static final GoogleAuthApiRequestCreator CREATOR = new GoogleAuthApiRequestCreator();
    public static final String DEFAULT_SCOPE_PREFIX = "oauth2:";
    public static final int HTTP_METHOD_DELETE = 3;
    public static final int HTTP_METHOD_GET = 0;
    public static final int HTTP_METHOD_HEAD = 4;
    public static final int HTTP_METHOD_OPTIONS = 5;
    public static final int HTTP_METHOD_PATCH = 7;
    public static final int HTTP_METHOD_POST = 1;
    public static final int HTTP_METHOD_PUT = 2;
    public static final int HTTP_METHOD_TRACE = 6;
    public static final int VERSION_CODE = 1;
    byte[] DA;
    long DB;
    String Dt;
    Bundle Du;
    String Dv;
    List<String> Dw;
    String Dx;
    int Dy;
    Bundle Dz;
    String name;
    String version;
    final int versionCode;
    String yR;

    GoogleAuthApiRequest(int versionCode, String name, String version, String apiId, String path, Bundle parameters, String accountName, List<String> scopes, String scopePrefix, int httpMethod, Bundle headers, byte[] body, long timeout) {
        this.versionCode = versionCode;
        this.name = name;
        this.version = version;
        this.Dt = apiId;
        this.yR = path;
        this.Du = parameters;
        this.Dv = accountName;
        this.Dw = scopes;
        this.Dx = scopePrefix;
        this.Dy = httpMethod;
        this.Dz = headers;
        this.DA = body;
        this.DB = timeout;
    }

    public GoogleAuthApiRequest(String name, String version, String apiId) {
        this.versionCode = 1;
        this.name = name;
        ay(version);
        this.Dt = apiId;
        this.Du = new Bundle();
        this.Dw = new ArrayList();
        this.Dx = DEFAULT_SCOPE_PREFIX;
        this.Dz = new Bundle();
        this.DA = new byte[0];
    }

    public GoogleAuthApiRequest(String name, String version, String path, int httpMethod) {
        this.versionCode = 1;
        this.name = name;
        ay(version);
        setPath(path);
        T(httpMethod);
        this.Du = new Bundle();
        this.Dw = new ArrayList();
        this.Dx = DEFAULT_SCOPE_PREFIX;
        this.Dz = new Bundle();
        this.DA = new byte[0];
    }

    private void T(int i) {
        if (i < 0 || i > 7) {
            throw new IllegalArgumentException("Invalid HTTP method.");
        }
        this.Dy = i;
    }

    private void ay(String str) {
        if (str.charAt(0) >= '0' && str.charAt(0) <= '9') {
            str = "v" + str;
        }
        this.version = str;
    }

    private void setPath(String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        this.yR = path;
    }

    public void addParameter(String key, String value) {
        if (this.Du.containsKey(key)) {
            this.Du.getStringArrayList(key).add(value);
            return;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(value);
        this.Du.putStringArrayList(key, arrayList);
    }

    public void addScope(String scope) {
        this.Dw.add(scope);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAccountName() {
        return this.Dv;
    }

    public String getApiId() {
        return this.Dt;
    }

    public String getFullScope() {
        String scope = getScope();
        if (scope == null) {
            return null;
        }
        return this.Dx + scope;
    }

    public String getHeader(String key) {
        return this.Dz.getString(key);
    }

    public Bundle getHeaders() {
        return this.Dz;
    }

    public Map<String, String> getHeadersAsMap() {
        HashMap map = new HashMap();
        for (String str : this.Dz.keySet()) {
            map.put(str, this.Dz.getString(str));
        }
        return map;
    }

    public byte[] getHttpBody() {
        return this.DA;
    }

    public JSONObject getHttpBodyAsJson() throws JSONException {
        try {
            return new JSONObject(new String(this.DA, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("GoogleAuthApiRequest", "Unsupported encoding error.");
            return null;
        }
    }

    public int getHttpMethod() {
        return this.Dy;
    }

    public String getName() {
        return this.name;
    }

    public Bundle getParameters() {
        return this.Du;
    }

    public Map<String, List<String>> getParametersAsMap() {
        HashMap map = new HashMap();
        for (String str : this.Du.keySet()) {
            map.put(str, this.Du.getStringArrayList(str));
        }
        return map;
    }

    public String getPath() {
        return this.yR;
    }

    public String getScope() {
        if (this.Dw.size() == 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.Dw.size()) {
                return stringBuffer.toString();
            }
            stringBuffer.append(this.Dw.get(i2));
            if (i2 != this.Dw.size() - 1) {
                stringBuffer.append(" ");
            }
            i = i2 + 1;
        }
    }

    public long getTimeout() {
        return this.DB;
    }

    public String getVersion() {
        return this.version;
    }

    public void putHeader(String key, String value) {
        this.Dz.putString(key, value);
    }

    public void setAccountName(String accountName) {
        this.Dv = accountName;
    }

    public void setHttpBody(String string) {
        try {
            this.DA = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("GoogleAuthApiRequest", "Unsupported encoding error.");
        }
    }

    public void setTimeout(long timeout) {
        this.DB = timeout;
    }

    public String toString() {
        return "{ API: " + this.name + ServiceReference.DELIMITER + this.version + ", Scope: " + getFullScope() + ", Account: " + getAccountName() + " }";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        GoogleAuthApiRequestCreator.a(this, parcel, flags);
    }
}
