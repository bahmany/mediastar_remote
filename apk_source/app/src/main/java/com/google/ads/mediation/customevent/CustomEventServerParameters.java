package com.google.ads.mediation.customevent;

import com.google.ads.mediation.MediationServerParameters;
import com.google.android.gms.plus.PlusShare;
import com.hisilicon.multiscreen.http.BuildConfig;

/* loaded from: classes.dex */
public final class CustomEventServerParameters extends MediationServerParameters {

    @MediationServerParameters.Parameter(name = "class_name", required = BuildConfig.DEBUG)
    public String className;

    @MediationServerParameters.Parameter(name = PlusShare.KEY_CALL_TO_ACTION_LABEL, required = BuildConfig.DEBUG)
    public String label;

    @MediationServerParameters.Parameter(name = "parameter", required = com.baoyz.swipemenulistview.BuildConfig.DEBUG)
    public String parameter = null;
}
