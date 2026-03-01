package com.google.android.gms.tagmanager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tagmanager.ContainerHolder;

/* loaded from: classes.dex */
class n implements ContainerHolder {
    private Status CM;
    private final Looper IB;
    private boolean NM;
    private Container anZ;
    private Container aoa;
    private b aob;
    private a aoc;
    private TagManager aod;

    public interface a {
        void co(String str);

        String nS();

        void nU();
    }

    private class b extends Handler {
        private final ContainerHolder.ContainerAvailableListener aoe;

        public b(ContainerHolder.ContainerAvailableListener containerAvailableListener, Looper looper) {
            super(looper);
            this.aoe = containerAvailableListener;
        }

        public void cp(String str) {
            sendMessage(obtainMessage(1, str));
        }

        protected void cq(String str) {
            this.aoe.onContainerAvailable(n.this, str);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    cq((String) msg.obj);
                    break;
                default:
                    bh.T("Don't know how to handle this message.");
                    break;
            }
        }
    }

    public n(Status status) {
        this.CM = status;
        this.IB = null;
    }

    public n(TagManager tagManager, Looper looper, Container container, a aVar) {
        this.aod = tagManager;
        this.IB = looper == null ? Looper.getMainLooper() : looper;
        this.anZ = container;
        this.aoc = aVar;
        this.CM = Status.Jo;
        tagManager.a(this);
    }

    private void nT() {
        if (this.aob != null) {
            this.aob.cp(this.aoa.nQ());
        }
    }

    public synchronized void a(Container container) {
        if (!this.NM) {
            if (container == null) {
                bh.T("Unexpected null container.");
            } else {
                this.aoa = container;
                nT();
            }
        }
    }

    public synchronized void cm(String str) {
        if (!this.NM) {
            this.anZ.cm(str);
        }
    }

    void co(String str) {
        if (this.NM) {
            bh.T("setCtfeUrlPathAndQuery called on a released ContainerHolder.");
        } else {
            this.aoc.co(str);
        }
    }

    @Override // com.google.android.gms.tagmanager.ContainerHolder
    public synchronized Container getContainer() {
        Container container = null;
        synchronized (this) {
            if (this.NM) {
                bh.T("ContainerHolder is released.");
            } else {
                if (this.aoa != null) {
                    this.anZ = this.aoa;
                    this.aoa = null;
                }
                container = this.anZ;
            }
        }
        return container;
    }

    String getContainerId() {
        if (!this.NM) {
            return this.anZ.getContainerId();
        }
        bh.T("getContainerId called on a released ContainerHolder.");
        return "";
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    String nS() {
        if (!this.NM) {
            return this.aoc.nS();
        }
        bh.T("setCtfeUrlPathAndQuery called on a released ContainerHolder.");
        return "";
    }

    @Override // com.google.android.gms.tagmanager.ContainerHolder
    public synchronized void refresh() {
        if (this.NM) {
            bh.T("Refreshing a released ContainerHolder.");
        } else {
            this.aoc.nU();
        }
    }

    @Override // com.google.android.gms.common.api.Releasable
    public synchronized void release() {
        if (this.NM) {
            bh.T("Releasing a released ContainerHolder.");
        } else {
            this.NM = true;
            this.aod.b(this);
            this.anZ.release();
            this.anZ = null;
            this.aoa = null;
            this.aoc = null;
            this.aob = null;
        }
    }

    @Override // com.google.android.gms.tagmanager.ContainerHolder
    public synchronized void setContainerAvailableListener(ContainerHolder.ContainerAvailableListener listener) {
        if (this.NM) {
            bh.T("ContainerHolder is released.");
        } else if (listener == null) {
            this.aob = null;
        } else {
            this.aob = new b(listener, this.IB);
            if (this.aoa != null) {
                nT();
            }
        }
    }
}
