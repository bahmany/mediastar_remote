package com.google.android.gms.appstate;

import com.google.android.gms.common.internal.m;

/* loaded from: classes.dex */
public final class a implements AppState {
    private final int CO;
    private final String CP;
    private final byte[] CQ;
    private final boolean CR;
    private final String CS;
    private final byte[] CT;

    public a(AppState appState) {
        this.CO = appState.getKey();
        this.CP = appState.getLocalVersion();
        this.CQ = appState.getLocalData();
        this.CR = appState.hasConflict();
        this.CS = appState.getConflictVersion();
        this.CT = appState.getConflictData();
    }

    static int a(AppState appState) {
        return m.hashCode(Integer.valueOf(appState.getKey()), appState.getLocalVersion(), appState.getLocalData(), Boolean.valueOf(appState.hasConflict()), appState.getConflictVersion(), appState.getConflictData());
    }

    static boolean a(AppState appState, Object obj) {
        if (!(obj instanceof AppState)) {
            return false;
        }
        if (appState == obj) {
            return true;
        }
        AppState appState2 = (AppState) obj;
        return m.equal(Integer.valueOf(appState2.getKey()), Integer.valueOf(appState.getKey())) && m.equal(appState2.getLocalVersion(), appState.getLocalVersion()) && m.equal(appState2.getLocalData(), appState.getLocalData()) && m.equal(Boolean.valueOf(appState2.hasConflict()), Boolean.valueOf(appState.hasConflict())) && m.equal(appState2.getConflictVersion(), appState.getConflictVersion()) && m.equal(appState2.getConflictData(), appState.getConflictData());
    }

    static String b(AppState appState) {
        return m.h(appState).a("Key", Integer.valueOf(appState.getKey())).a("LocalVersion", appState.getLocalVersion()).a("LocalData", appState.getLocalData()).a("HasConflict", Boolean.valueOf(appState.hasConflict())).a("ConflictVersion", appState.getConflictVersion()).a("ConflictData", appState.getConflictData()).toString();
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: fp, reason: merged with bridge method [inline-methods] */
    public AppState freeze() {
        return this;
    }

    @Override // com.google.android.gms.appstate.AppState
    public byte[] getConflictData() {
        return this.CT;
    }

    @Override // com.google.android.gms.appstate.AppState
    public String getConflictVersion() {
        return this.CS;
    }

    @Override // com.google.android.gms.appstate.AppState
    public int getKey() {
        return this.CO;
    }

    @Override // com.google.android.gms.appstate.AppState
    public byte[] getLocalData() {
        return this.CQ;
    }

    @Override // com.google.android.gms.appstate.AppState
    public String getLocalVersion() {
        return this.CP;
    }

    @Override // com.google.android.gms.appstate.AppState
    public boolean hasConflict() {
        return this.CR;
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    public String toString() {
        return b(this);
    }
}
