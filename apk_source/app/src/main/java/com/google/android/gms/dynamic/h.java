package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.android.gms.dynamic.c;

/* loaded from: classes.dex */
public final class h extends c.a {
    private Fragment Ll;

    private h(Fragment fragment) {
        this.Ll = fragment;
    }

    public static h a(Fragment fragment) {
        if (fragment != null) {
            return new h(fragment);
        }
        return null;
    }

    @Override // com.google.android.gms.dynamic.c
    public void d(d dVar) {
        this.Ll.registerForContextMenu((View) e.f(dVar));
    }

    @Override // com.google.android.gms.dynamic.c
    public void e(d dVar) {
        this.Ll.unregisterForContextMenu((View) e.f(dVar));
    }

    @Override // com.google.android.gms.dynamic.c
    public Bundle getArguments() {
        return this.Ll.getArguments();
    }

    @Override // com.google.android.gms.dynamic.c
    public int getId() {
        return this.Ll.getId();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean getRetainInstance() {
        return this.Ll.getRetainInstance();
    }

    @Override // com.google.android.gms.dynamic.c
    public String getTag() {
        return this.Ll.getTag();
    }

    @Override // com.google.android.gms.dynamic.c
    public int getTargetRequestCode() {
        return this.Ll.getTargetRequestCode();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean getUserVisibleHint() {
        return this.Ll.getUserVisibleHint();
    }

    @Override // com.google.android.gms.dynamic.c
    public d getView() {
        return e.k(this.Ll.getView());
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isAdded() {
        return this.Ll.isAdded();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isDetached() {
        return this.Ll.isDetached();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isHidden() {
        return this.Ll.isHidden();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isInLayout() {
        return this.Ll.isInLayout();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isRemoving() {
        return this.Ll.isRemoving();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isResumed() {
        return this.Ll.isResumed();
    }

    @Override // com.google.android.gms.dynamic.c
    public boolean isVisible() {
        return this.Ll.isVisible();
    }

    @Override // com.google.android.gms.dynamic.c
    public d iu() {
        return e.k(this.Ll.getActivity());
    }

    @Override // com.google.android.gms.dynamic.c
    public c iv() {
        return a(this.Ll.getParentFragment());
    }

    @Override // com.google.android.gms.dynamic.c
    public d iw() {
        return e.k(this.Ll.getResources());
    }

    @Override // com.google.android.gms.dynamic.c
    public c ix() {
        return a(this.Ll.getTargetFragment());
    }

    @Override // com.google.android.gms.dynamic.c
    public void setHasOptionsMenu(boolean hasMenu) {
        this.Ll.setHasOptionsMenu(hasMenu);
    }

    @Override // com.google.android.gms.dynamic.c
    public void setMenuVisibility(boolean menuVisible) {
        this.Ll.setMenuVisibility(menuVisible);
    }

    @Override // com.google.android.gms.dynamic.c
    public void setRetainInstance(boolean retain) {
        this.Ll.setRetainInstance(retain);
    }

    @Override // com.google.android.gms.dynamic.c
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.Ll.setUserVisibleHint(isVisibleToUser);
    }

    @Override // com.google.android.gms.dynamic.c
    public void startActivity(Intent intent) {
        this.Ll.startActivity(intent);
    }

    @Override // com.google.android.gms.dynamic.c
    public void startActivityForResult(Intent intent, int requestCode) {
        this.Ll.startActivityForResult(intent, requestCode);
    }
}
