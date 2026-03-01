package com.google.android.gms.dynamic;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.dynamic.c;

/* loaded from: classes.dex */
public final class b extends c.a {
    private Fragment Sb;

    private b(Fragment fragment) {
        this.Sb = fragment;
    }

    public static b a(Fragment fragment) {
        if (fragment != null) {
            return new b(fragment);
        }
        return null;
    }

    @Override // com.google.android.gms.dynamic.c
    public void d(d dVar) {
        this.Sb.registerForContextMenu((View) e.f(dVar));
    }

    @Override // com.google.android.gms.dynamic.c
    public void e(d dVar) {
        this.Sb.unregisterForContextMenu((View) e.f(dVar));
    }

    @Override // com.google.android.gms.dynamic.c
    public Bundle getArguments() {
        return this.Sb.getArguments();
    }

    @Override // com.google.android.gms.dynamic.c
    public int getId() {
        return this.Sb.getId();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean getRetainInstance() {
        return this.Sb.getRetainInstance();
    }

    @Override // com.google.android.gms.dynamic.c
    public String getTag() {
        return this.Sb.getTag();
    }

    @Override // com.google.android.gms.dynamic.c
    public int getTargetRequestCode() {
        return this.Sb.getTargetRequestCode();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean getUserVisibleHint() {
        return this.Sb.getUserVisibleHint();
    }

    @Override // com.google.android.gms.dynamic.c
    public d getView() {
        return e.k(this.Sb.getView());
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isAdded() {
        return this.Sb.isAdded();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isDetached() {
        return this.Sb.isDetached();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isHidden() {
        return this.Sb.isHidden();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isInLayout() {
        return this.Sb.isInLayout();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isRemoving() {
        return this.Sb.isRemoving();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isResumed() {
        return this.Sb.isResumed();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isVisible() {
        return this.Sb.isVisible();
    }

    @Override // com.google.android.gms.dynamic.c
    public d iu() {
        return e.k(this.Sb.getActivity());
    }

    @Override // com.google.android.gms.dynamic.c
    public c iv() {
        return a(this.Sb.getParentFragment());
    }

    @Override // com.google.android.gms.dynamic.c
    public d iw() {
        return e.k(this.Sb.getResources());
    }

    @Override // com.google.android.gms.dynamic.c
    public c ix() {
        return a(this.Sb.getTargetFragment());
    }

    @Override // com.google.android.gms.dynamic.c
    public void setHasOptionsMenu(boolean hasMenu) {
        this.Sb.setHasOptionsMenu(hasMenu);
    }

    @Override // com.google.android.gms.dynamic.c
    public void setMenuVisibility(boolean menuVisible) {
        this.Sb.setMenuVisibility(menuVisible);
    }

    @Override // com.google.android.gms.dynamic.c
    public void setRetainInstance(boolean retain) {
        this.Sb.setRetainInstance(retain);
    }

    @Override // com.google.android.gms.dynamic.c
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.Sb.setUserVisibleHint(isVisibleToUser);
    }

    @Override // com.google.android.gms.dynamic.c
    public void startActivity(Intent intent) {
        this.Sb.startActivity(intent);
    }

    @Override // com.google.android.gms.dynamic.c
    public void startActivityForResult(Intent intent, int requestCode) {
        this.Sb.startActivityForResult(intent, requestCode);
    }
}
