package com.google.android.gms.internal;

import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ez
/* loaded from: classes.dex */
class fv {
    private int tc;
    private final List<String> uJ;
    private final List<String> uK;
    private final String uL;
    private final String uM;
    private final String uN;
    private final String uO;
    private final boolean uP;
    private final int uQ;
    private String uR;

    public fv(int i, Map<String, String> map) {
        this.uR = map.get("url");
        this.uM = map.get("base_uri");
        this.uN = map.get("post_parameters");
        this.uP = parseBoolean(map.get("drt_include"));
        this.uL = map.get("activation_overlay_url");
        this.uK = J(map.get("check_packages"));
        this.uQ = parseInt(map.get("request_id"));
        this.uO = map.get(PlaylistSQLiteHelper.COL_TYPE);
        this.uJ = J(map.get("errors"));
        this.tc = i;
    }

    private List<String> J(String str) {
        if (str == null) {
            return null;
        }
        return Arrays.asList(str.split(ClientInfo.SEPARATOR_BETWEEN_VARS));
    }

    private static boolean parseBoolean(String bool) {
        return bool != null && (bool.equals("1") || bool.equals("true"));
    }

    private int parseInt(String i) {
        if (i == null) {
            return 0;
        }
        return Integer.parseInt(i);
    }

    public List<String> cM() {
        return this.uJ;
    }

    public String cN() {
        return this.uN;
    }

    public boolean cO() {
        return this.uP;
    }

    public int getErrorCode() {
        return this.tc;
    }

    public String getType() {
        return this.uO;
    }

    public String getUrl() {
        return this.uR;
    }

    public void setUrl(String url) {
        this.uR = url;
    }
}
